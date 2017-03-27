package eu.fbk.das.process.engine.impl.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.OnMessageActivity;
import eu.fbk.das.process.engine.api.domain.PickActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.jaxb.VariableType;

public class PickActivityHandler extends AbstractHandler {

	private static final Logger logger = LogManager
			.getLogger(PickActivityHandler.class);

	@Override
	public void handle(ProcessEngine pe, ProcessDiagram proc,
			ProcessActivity current) {
		logger.debug("[" + proc.getpid() + "] Pick Execution");
		DomainObjectInstance doi = pe.getDomainObjectInstance(proc);

		boolean foundOnMsg = false;
		ProcessDiagram branch = null;
		for (OnMessageActivity OMact : ((PickActivity) current).getOnMessages()) {

			if (!pe.getMsgAndVariables(doi).isEmpty()) {

				List<Map<String, List<VariableType>>> mapList = pe
						.getMsgAndVariables(doi);
				Iterator<Map<String, List<VariableType>>> iter = mapList
						.iterator();
				while (iter.hasNext()) {
					List<String> msgSet = (List<String>) (iter.next()).keySet();
					Iterator<String> it = msgSet.iterator();
					while (it.hasNext()) {
						String msg = it.next();
						if (msg.equalsIgnoreCase(OMact.getName())) {
							foundOnMsg = true;
							logger.debug("[" + proc.getpid()
									+ "] Pick: on message: " + msg);
							branch = OMact.getBranch();
							// rimuovere il msg una volta trovato
							iter.remove();
						}
					}
				}
			}
			if (foundOnMsg) {
				if (branch != null && branch.getActivities().size() > 0) {
					branch.setCurrentActivity(branch.getFirstActivity());
					// this.addRunningOMBranch(proc.getpid(), branch);
					branch.setPid(pe.getPid());
					branch.setFather(proc);
					branch.setRunning(true);
					current.setRunning(false);
					proc.setRunning(false);
					pe.addRunningBranch(branch.getpid(), branch);

					// add correlation between branch and father
					// pe.createCorrelation(branch.getpid(), proc.getpid());
					// craere la correlazione tra il branch e chi e' in
					// correlazione col padre che crea questo branch
					// TODO: non serve piu' ?
					// List<Integer> fatherCorrelated = pe.getCorrelated(proc
					// .getpid());
					// if (fatherCorrelated != null &&
					// !fatherCorrelated.isEmpty()) {
					// for (Integer fc : fatherCorrelated) {
					// pe.createCorrelation(branch.getpid(), fc);
					// }
					// }

				} else {
					current.setExecuted(true);
					if (!proc.getNextActivity().isEmpty()) {
						proc.setCurrentActivity(proc.getNextActivity().get(0));
					} else {
						proc.setRunning(false);
						proc.setEnded(true);
					}
				}
				foundOnMsg = false;
			}
		}
		if (proc.isTerminated() && pe.getRunningBranch(proc) != null
				&& pe.getRunningBranch(proc).isEnded()) {
			current.setRunning(false);
			current.setExecuted(true);
			proc.setEnded(true);
			System.out.println();
		}
	}
}
