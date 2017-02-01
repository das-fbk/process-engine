package eu.fbk.das.process.engine.impl.handlers;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * A generic process's activity handler
 */
public interface Handler {

    public void handle(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current);

    public boolean handlePrecondition(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current);

    public void handleEffect(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current);

}
