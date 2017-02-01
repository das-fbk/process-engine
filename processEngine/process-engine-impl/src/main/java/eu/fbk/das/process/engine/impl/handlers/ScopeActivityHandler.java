package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ScopeActivity;

public class ScopeActivityHandler extends AbstractHandler {

    private static final Logger logger = LogManager
	    .getLogger(ScopeActivityHandler.class);

    @Override
    public void handle(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current) {
	ScopeActivity act = (ScopeActivity) current;
	logger.debug("[" + proc.getpid() + "] Esecuzione Scope "
		+ act.getName());

	if (current.getName().equals("RM_scope2")) {
	    System.out.println();
	}
	boolean prec = handlePrecondition(pe, proc, current);
	if (!prec) {
	    logger.debug("[" + proc.getpid()
		    + "] Precondizioni non soddisfatte, scope non eseguita");
	    return;
	}

	ProcessDiagram refinement = act.getBranch();
	if (refinement == null) {
	    logger.error("Refinement process not found");
	    return;
	}
	if (refinement.isTerminated()) {
	    logger.debug("[" + proc.getpid() + "]Scope completato con successo");
	    current.setRunning(false);
	    current.setExecuted(true);
	    return;
	}
	if (refinement.getpid() == ProcessEngine.DEFAULT_PID) {
	    refinement.setPid(pe.getPid());
	}

	pe.registerProcess(refinement, proc);
	// we can remove the refinement from the awaiting list
	refinement.setFather(proc);
	if (refinement.getStates() != null && refinement.getStates().size() > 0) {
	    logger.debug("[" + proc.getpid()
		    + "] Process in pause, waiting for refinement "
		    + refinement.getpid());
	    current.setRunning(false);
	    // proc.setEnded(true);
	    proc.setRunning(false);
	    refinement.setCurrentActivity(refinement.getFirstActivity());
	    refinement.setRunning(true);
	    refinement.setFather(proc);
	    pe.addRunningRefinements(proc, refinement);
	} else {
	    logger.warn("[" + proc.getpid() + "] Refinement is empty");
	    current.setRunning(false);
	    current.setExecuted(true);
	}

    }
}
