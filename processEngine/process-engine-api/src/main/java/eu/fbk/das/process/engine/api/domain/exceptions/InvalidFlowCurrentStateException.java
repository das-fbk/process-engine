package eu.fbk.das.process.engine.api.domain.exceptions;

/**
 * This exception is thrown when invalid current state is going to be set for the flow
 * (current state to be set does not belong to flow states)
 * 
 *
 */
public class InvalidFlowCurrentStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
