package eu.fbk.das.process.engine.api.exceptions;

import eu.fbk.das.process.engine.api.ProcessEngine;

/**
 * Generic abstract class for {@link ProcessEngine}
 */
public abstract class ProcessEngineException extends Exception {

    public ProcessEngineException(String message) {
	super(message);
    }

    private static final long serialVersionUID = 2201911138735833514L;

}
