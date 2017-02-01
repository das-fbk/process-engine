package eu.fbk.das.composer.api.exceptions;

/**
 * This exception is thrown when invalid initial transition is going to be added to the service 
 * (initial state is not among service states)
 * @author Heorhi
 *
 */
public class InvalidServiceInitialStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}
