package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

public abstract class AbstractHandler implements Handler {

    private static final Logger logger = LogManager
	    .getLogger(AbstractHandler.class);

    public boolean handlePrecondition(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current) {
	if (!current.isSwitch() && !current.isPick()) {
	    if (current.getPrecondition() != null) {
		return pe.checkPrecondition(proc, current);
	    }
	}
	return true;
    }

    public void handleEffect(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current) {
	if (current.getEffect() != null
		&& current.getEffect().getEvent() != null) {
	    logger.debug("[" + proc.getpid() + "] applicazione effetti");
	    pe.applyEffect(proc, current.getEffect());
	}
    }
}
