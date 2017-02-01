package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ReplyActivity;

public class ReplyActivityHandler extends AbstractHandler {

    private static final Logger logger = LogManager
	    .getLogger(ReplyActivityHandler.class);

    @Override
    public void handle(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current) {
	logger.debug("[" + proc.getpid() + "] receive for " + current.getName());
	ReplyActivity receive = (ReplyActivity) current;
	boolean prec = handlePrecondition(pe, proc, current);
	if (!prec) {
	    logger.debug("[" + proc.getpid()
		    + "] Precondizioni non soddisfatte, receive non eseguita");
	    return;
	}
	String msg = receive.getName();
	if (thereisamessageforme(pe, proc, msg)) {
	    receive.setExecuted(true);
	    // add the completed activity to the execution history
	    proc.addExecuteActivity(receive);

	    logger.debug("[" + proc.getpid() + "] receive executed");
	    handleEffect(pe, proc, current);
	} else {
	    logger.debug("[" + proc.getpid()
		    + "] receive waiting for a message");
	}
    }

    private boolean thereisamessageforme(ProcessEngine pe, ProcessDiagram proc,
	    String msg) {
	DomainObjectInstance doi = pe.getDomainObjectInstance(proc);
	if (!pe.getMsg(doi).isEmpty()) {
	    if (pe.getMsg(doi).contains(msg)) {
		pe.removeMessage(doi, msg);
		return true;
	    }
	}
	return false;
    }

}
