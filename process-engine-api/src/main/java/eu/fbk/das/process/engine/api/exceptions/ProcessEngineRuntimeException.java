package eu.fbk.das.process.engine.api.exceptions;

/**
 * An exception thrown by ProcessEngine
 */
public class ProcessEngineRuntimeException extends ProcessEngineException {

    public ProcessEngineRuntimeException(String message) {
	super(message);
    }

    private static final long serialVersionUID = -5534859455089555407L;

}
