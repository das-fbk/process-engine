package eu.fbk.das.process.engine.api;

/**
 * Abstract (doing nothing) implementation for
 * {@link ExecutableActivityInterface} with also {@link Cloneable}'s clone
 * method
 */
public abstract class AbstractExecutableActivityInterface implements
		ExecutableActivityInterface, Cloneable {

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
