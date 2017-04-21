package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.WhileActivity;
import eu.fbk.das.process.engine.api.jaxb.ClauseType;
import eu.fbk.das.process.engine.api.jaxb.WhileType.VarCondition;

public class WhileActivityHandler extends AbstractHandler {

	private static final Logger logger = LogManager
			.getLogger(WhileActivityHandler.class);

	@Override
	public void handle(ProcessEngine pe, ProcessDiagram proc,
			ProcessActivity current) {
		logger.debug("[" + proc.getpid() + "] Esecuzione While "
				+ current.getName());
		boolean prec = handlePrecondition(pe, proc, current);
		if (!prec) {
			logger.debug("[" + proc.getpid()
					+ "] Precondizioni non soddisfatte, while non eseguita");
			return;
		}

		ProcessDiagram father = proc;
		WhileActivity act = (WhileActivity) current;
		// check condition defined with variables
		boolean VarConditionEval = true;
		VarCondition varCond = act.getCondition();
		if (varCond != null) {
			if (!pe.checkVarCondition(varCond.getName(), varCond.getValue(),
					father)) {
				VarConditionEval = false;
			}
		}
		// check conditions using context (Knowledge of domainProperty)
		boolean ContextEval = true;
		ClauseType varCon = act.getContextCondition();
		if (varCon != null && !pe.checkContext(proc, varCon)) {
			ContextEval = false;
		}

		if (VarConditionEval && ContextEval) {
			logger.debug("[" + proc.getpid() + "] Condizione while true");
			ProcessDiagram branch = act.getDefaultBranch();
			// if (branch.getpid() == ProcessEngine.DEFAULT_PID) {
			branch.setPid(pe.getPid());
			// }
			logger.debug("[" + proc.getpid()
					+ "] Branch while attivo con pid [" + branch.getpid() + "]");

			branch.setCurrentActivity(branch.getFirstActivity());
			pe.addRunningBranch(branch.getpid(), branch);

			branch.setFather(proc);
			proc.setRunning(false);
			branch.setRunning(true);
			current.setRunning(false);
		} else {
			logger.debug("[" + proc.getpid() + "] Condizione while false");
			current.setExecuted(true);
			// add the completed activity to the execution history
			proc.addExecuteActivity(current);
			// handle effects
			handleEffect(pe, proc, current);

			// terminate branch
			ProcessDiagram branch = act.getDefaultBranch();
			if (branch != null && branch.isRunning()) {
				branch.setRunning(false);
				pe.removeRunningBranch(branch);
			}

		}

	}
}
