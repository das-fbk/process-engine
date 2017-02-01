package eu.fbk.das.process.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.ExecutableActivityInterface;

/**
 * Register executable handlers (implementation of
 * {@link AbstractExecutableActivityInterface}) definition and build instances
 * 
 */
public class ExecutableHandlerManager {

    private static final Logger logger = LogManager
	    .getLogger(ExecutableHandlerManager.class);

    private Map<String, AbstractExecutableActivityInterface> executableHandlersDefinition = new HashMap<String, AbstractExecutableActivityInterface>();
    private Map<Integer, ArrayList<AbstractExecutableActivityInterface>> executableHandlers = new HashMap<Integer, ArrayList<AbstractExecutableActivityInterface>>();

    /**
     * Register into manager with given name handler as definition
     * 
     * @param activityName
     * @param handler
     */
    public void register(String activityName,
	    AbstractExecutableActivityInterface handler) {
	executableHandlersDefinition.put(activityName, handler);
    }

    /**
     * Get instance of given type for pid, create istance using type and clone
     * method
     * 
     * @param type
     * @param pid
     * @return instance of given type for process id (pid), null if no
     *         definition found or error
     * 
     * @see Cloneable
     * @see AbstractExecutableActivityInterface,
     *      {@link ExecutableActivityInterface}
     */
    public ExecutableActivityInterface getInstance(String name, int pid) {
	if (name == null) {
	    logger.error("Impossible to find an executable handler with null name, please provide a valid activity name");
	    return null;
	}
	// if present, use it
	if (containsKey(pid)) {
	    return getInstance(pid, name);
	}
	// build a new instance
	if (containsDefinition(name)) {
	    AbstractExecutableActivityInterface def = getDefinition(name);
	    AbstractExecutableActivityInterface other = createInstance(pid, def);
	    return other;
	}
	// no definition, error
	return null;
    }

    private AbstractExecutableActivityInterface createInstance(int pid,
	    AbstractExecutableActivityInterface def) {
	AbstractExecutableActivityInterface other;
	try {
	    other = (AbstractExecutableActivityInterface) def.clone();
	} catch (CloneNotSupportedException | NullPointerException e) {
	    logger.error(e.getMessage(), e);
	    return null;
	}
	registerInstance(pid, other);
	return other;
    }

    private ExecutableActivityInterface getInstance(int pid, String name) {
	AbstractExecutableActivityInterface def = executableHandlersDefinition
		.get(name);
	if (def == null) {
	    return null;
	}
	ArrayList<AbstractExecutableActivityInterface> hs = executableHandlers
		.get(pid);
	for (AbstractExecutableActivityInterface a : hs) {
	    if (def != null) {
		if (a.getClass().getName().equals(def.getClass().getName())) {
		    return a;
		}
	    }

	}
	// build a new instance of given definition
	AbstractExecutableActivityInterface other = createInstance(pid, def);
	return other;
    }

    private boolean containsKey(int pid) {
	return executableHandlers.containsKey(pid);
    }

    private boolean containsDefinition(String name) {
	return executableHandlersDefinition.containsKey(name);
    }

    private AbstractExecutableActivityInterface getDefinition(String name) {
	return executableHandlersDefinition.get(name);
    }

    private void registerInstance(int pid,
	    AbstractExecutableActivityInterface other) {
	if (executableHandlers.get(pid) == null) {
	    executableHandlers.put(pid,
		    new ArrayList<AbstractExecutableActivityInterface>());
	}
	if (!executableHandlers.get(pid).contains(other)) {
	    executableHandlers.get(pid).add(other);
	}
    }

}
