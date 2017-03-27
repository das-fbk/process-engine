package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.IFActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.SwitchActivity;
import eu.fbk.das.process.engine.api.jaxb.SwitchType;

public class SwitchActivityHandler extends AbstractHandler {

	private static final Logger logger = LogManager
			.getLogger(SwitchActivityHandler.class);

	// private Map<ProcessDiagram, List<ProcVar>> processVarMap = new
	// HashMap<ProcessDiagram, List<ProcVar>>();

	@Override
	public void handle(ProcessEngine pe, ProcessDiagram proc,
			ProcessActivity current) {
		logger.debug("[" + proc.getpid() + "] Esecuzione Switch");
		boolean foundIf = false;
		ProcessDiagram father = proc;
		ProcessDiagram branch = null;
//		// Martina
//		pe.addProcVar(proc, "planner", "local");
		for (IFActivity ifact : ((SwitchActivity) current).getIFs()) {

			boolean VarConditionEval = true;
			boolean ContextConditionEval = true;

			if (ifact.getVarConditions().size() > 0) {
				for (SwitchType.If.VarCondition varCond : ifact
						.getVarConditions()) {

					if (!pe.checkVarCondition(varCond.getName(),
							varCond.getValue(), father)) {
						VarConditionEval = false;
					}

				}
			}

			if (ContextConditionEval && VarConditionEval) {
				branch = ifact.getBranch();
				int pid = pe.getPid();
				branch.setPid(pid);
				branch.setCurrentActivity(branch.getFirstActivity());
				pe.addRunningBranch(pid, branch);

				branch.setFather(proc);
				proc.setRunning(false);
				branch.setRunning(true);
				current.setRunning(false);
				foundIf = true;
				// break;
			}

		}
		// if not executed if, execute def
		if (!foundIf) {
			branch = ((SwitchActivity) current).getDEF().getDefaultBranch();
			int pid = pe.getPid();
			branch.setPid(pid);
			branch.setCurrentActivity(branch.getFirstActivity());
			pe.addRunningBranch(pid, branch);
			branch.setFather(proc);
			proc.setRunning(false);
			branch.setRunning(true);
			current.setRunning(false);
		}

		// add correlation between branch and father
		// pe.createCorrelation(branch.getpid(), proc.getpid());
		// craere la correlazione tra il branch e chi e' in
		// correlazione col padre che crea questo branch
		// TODO: non serve piu' ?
		// DomainObjectInstance fatherDoi = pe.getDomainObjectInstance(father);
		// List<DomainObjectInstance> fatherCorrelated =
		// pe.getCorrelated(fatherDoi);
		// if (fatherCorrelated != null &&
		// !fatherCorrelated.isEmpty()) {
		// for (DomainObjectInstance fc : fatherCorrelated) {
		// pe.createCorrelation(branch.getpid(), fc);
		// }
		// }
	}
}
