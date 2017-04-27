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
		ProcessDiagram branch = new ProcessDiagram();
		// // Martina
		// pe.addProcVar(proc, "planner", "global");

		// boolean branchExecuted = false;
		//
		// if (pe.getRunningBranch(father) != null) {
		// branch = pe.getRunningBranch(father);
		// if (branch.getFather().getCurrentActivity().getName()
		// .equals(current.getName())
		// && branch.isEnded()) {
		// branchExecuted = true;
		// }
		// }

		// if (!branchExecuted) {

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
				// pe.addRunningBranch(pid, branch);
				/*****************
				 * GESTIONE BRANCH DELLO SWITCH COME REFINEMENT ATTIVITA
				 * ASTRATTA
				 *****************************/
				pe.registerProcess(branch, proc);
				pe.addRunningRefinements(proc, branch);
				/***************************************************************************/
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
			// pe.addRunningBranch(pid, branch);
			/***************** UGUALE A SOPRA - TESTARE MEGLIO *****************************/
			pe.registerProcess(branch, proc);
			pe.addRunningRefinements(proc, branch);
			/***************************************************************************/
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
		// DomainObjectInstance fatherDoi =
		// pe.getDomainObjectInstance(father);
		// List<DomainObjectInstance> fatherCorrelated =
		// pe.getCorrelated(fatherDoi);
		// if (fatherCorrelated != null &&
		// !fatherCorrelated.isEmpty()) {
		// for (DomainObjectInstance fc : fatherCorrelated) {
		// pe.createCorrelation(branch.getpid(), fc);
		// }
		// }

		// } else {
		// logger.debug("[" + proc.getpid()
		// + "] Execution of the selected switch branch terminated");
		// current.setExecuted(true);
		// // add the completed activity to the execution history
		// proc.addExecuteActivity(current);
		// // handle effects
		// handleEffect(pe, proc, current);
		//
		// // terminate branch
		// if (branch != null && branch.isRunning()) {
		// branch.setRunning(false);
		// pe.removeRunningBranch(branch);
		// }
		// }
	}
}
