package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.ProcessRequest;
import eu.fbk.das.process.engine.api.domain.InvokeActivty;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivityType;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * Handle an Invoke Activity
 * 
 * @see InvokeActivty
 *
 */
public class InvokeActivityHandler extends AbstractHandler {

    private static final Logger logger = LogManager
	    .getLogger(InvokeActivityHandler.class);

    @Override
    public void handle(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current) {
	logger.debug("[" + proc.getpid()
		+ "] invoke for a process with name : " + current.getName());
	boolean prec = handlePrecondition(pe, proc, current);
	if (!prec) {
	    logger.debug("[" + proc.getpid()
		    + "] Precondizioni non soddisfatte, invoke non eseguita");
	    return;
	}
	ProcessRequest pr = new ProcessRequest();
	pr.setPid(proc.getpid());
	pr.setDomainObjectInstance(pe.getDomainObjectInstance(proc));
	pr.setName(current.getName());
	pr.setType(ProcessActivityType.REPLY);
	pe.addRequest(pr);

	current.setExecuted(true);
	// add the completed activity to the execution history
	proc.addExecuteActivity(current);

	// handle effects
	handleEffect(pe, proc, current);
    }

}
