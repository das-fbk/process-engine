package eu.fbk.das.composer.api.exceptions;

/**
 * This exception is thrown when invalid transition is going to be added to the service 
 * (e.g., tranitions from/to non-existing states and/or labeled with non-existing action)
 * @author Heorhi
 *
 */
public class InvalidServiceTransitionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
}
