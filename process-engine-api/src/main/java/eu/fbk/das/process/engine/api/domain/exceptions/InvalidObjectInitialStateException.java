package eu.fbk.das.process.engine.api.domain.exceptions;

/**
 * This exception is thrown when invalid initial transition is going to be added to the object diagram 
 * (initial state is not among object states)
 * @author Heorhi
 *
 */
public class InvalidObjectInitialStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
