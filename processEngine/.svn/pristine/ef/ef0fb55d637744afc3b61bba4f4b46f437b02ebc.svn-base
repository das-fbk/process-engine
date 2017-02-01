package eu.fbk.das.process.engine.api.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.composer.api.exceptions.InvalidServiceActionException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceCurrentStateException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceInitialStateException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceTransitionException;
import eu.fbk.das.composer.api.exceptions.ServiceDuplicateActionException;

/**
 * This class represents a service
 * 
 * @author Heorhi
 *
 */
public class ServiceDiagram {

    private static final Logger logger = LogManager
	    .getLogger(ServiceDiagram.class);

    /**
     * Identifier of a service instance
     */
    private String sid;

    /**
     * Service type
     */
    private String type;

    /**
     * Entity that can work with the service
     */
    private String consumerEntityType;

    private Set<String> states;
    private Set<String> initialStates;

    /**
     * inputs contains all input (controllable) actions of a service
     */
    private Set<String> inputs;
    /**
     * outputs contains all output (uncontrollable) actions of a service
     */
    private Set<String> outputs;

    private Set<String> abstracts;

    private Set<ServiceTransition> transitions;

    private String currentState;

    private HashSet<String> concretes;

    /**
     * @param sid
     * @param type
     * @param states
     * @param initialStates
     * @param inputs
     * @param outputs
     * @param concretes
     * @param transitions
     * @param isCyclic
     *            is the resulting service cyclic or not
     * @throws InvalidServiceInitialStateException
     * @throws InvalidServiceTransitionException
     * @throws ServiceDuplicateActionException
     */
    public ServiceDiagram(String sid, String type, String entity,
	    Set<String> states, Set<String> initialStates, Set<String> inputs,
	    Set<String> outputs, Set<String> abstracts, Set<String> concretes,
	    Set<ServiceTransition> transitions, boolean isCyclic)
	    throws InvalidServiceInitialStateException,
	    InvalidServiceTransitionException, ServiceDuplicateActionException {

	this.sid = sid;
	this.type = type;
	this.consumerEntityType = entity;
	this.states = new HashSet<String>(states);

	if (this.states.containsAll(initialStates))
	    this.initialStates = new HashSet<String>(initialStates);
	else
	    throw new InvalidServiceInitialStateException();
	this.inputs = new HashSet<String>(inputs);
	this.outputs = new HashSet<String>(outputs);
	this.concretes = new HashSet<String>(concretes);

	this.transitions = new HashSet<ServiceTransition>();
	for (ServiceTransition tr : transitions) {
	    if (tr.getType() == ServiceDiagramActionType.ABSTRACT) {
		System.out.println("da gestire");
	    }
	    if (this.states.contains(tr.getFrom())
		    && this.states.contains(tr.getTo())
		    && ((tr.getType() == ServiceDiagramActionType.IN && this.inputs
			    .contains(tr.getAction())) || ((tr.getType() == ServiceDiagramActionType.OUT && this.outputs
			    .contains(tr.getAction())))
		    // || (tr
		    // .getType() == ServiceDiagramActionType.CONCRETE &&
		    // concretes
		    // .contains(tr.getAction()))
		    )) {

		// Transitition duplication control
		boolean isAdded = false;
		for (ServiceTransition trans : this.transitions)
		    if (trans.equals(tr)) {
			isAdded = true;
			break;
		    }
		if (!isAdded)
		    this.transitions.add(tr);
	    } else {
		InvalidServiceTransitionException e = new InvalidServiceTransitionException();
		logger.error(tr, e);
		// System.err.println(tr);
		throw e;
	    }
	}

	Set<String> intersection = new HashSet<String>(inputs);
	intersection.retainAll(outputs);
	if (!intersection.isEmpty())
	    throw new ServiceDuplicateActionException();

	// making service diagrams cyclic
	if (isCyclic) {
	    List<String> finalStates = new ArrayList<String>();
	    for (String state : this.states) {
		boolean isFinal = true;
		for (ServiceTransition tr : this.transitions)
		    if (tr.getFrom().equals(state)) {
			isFinal = false;
			break;
		    }
		if (isFinal)
		    finalStates.add(state);
	    }
	    List<ServiceTransition> toRemove = new ArrayList<ServiceTransition>();
	    List<ServiceTransition> toAdd = new ArrayList<ServiceTransition>();
	    for (ServiceTransition tr : this.transitions) {
		if (finalStates.contains(tr.getTo())) {
		    toRemove.add(tr);
		    for (String iState : this.initialStates) {
			toAdd.add(new ServiceTransition(tr.getFrom(), iState,
				tr.getAction(), tr.getType()));
		    }
		}
	    }
	    this.transitions.removeAll(toRemove);
	    this.transitions.addAll(toAdd);
	    this.states.removeAll(finalStates);
	}

	this.inputs.add("RESET");
	for (String state : this.states) {
	    if (!this.initialStates.contains(state)) {
		for (String iState : this.initialStates) {
		    this.transitions.add(new ServiceTransition(state, iState,
			    "RESET", ServiceDiagramActionType.IN));
		}
	    }
	}

	// reset to inputs, just be sure
	this.abstracts = new HashSet<String>();
	if (abstracts != null)
	    this.abstracts.addAll(abstracts);
	this.concretes = new HashSet<String>();
	if (concretes != null)
	    this.concretes.addAll(concretes);

    }

    public String getSid() {
	return sid;
    }

    public String getType() {
	return type;
    }

    public Set<String> getStates() {
	return states;
    }

    public Set<String> getInitialStates() {
	return initialStates;
    }

    public Set<String> getInputs() {
	return inputs;
    }

    public Set<String> getOutputs() {
	return outputs;
    }

    public Set<String> getAbstracts() {
	return abstracts;
    }

    public Set<ServiceTransition> getTransitions() {
	return transitions;
    }

    public String getCurrentState() {
	return currentState;
    }

    public void setCurrentState(String currentState)
	    throws InvalidServiceCurrentStateException {
	if (!states.contains(currentState))
	    throw new InvalidServiceCurrentStateException();
	this.currentState = currentState;
    }

    public String getConsumerEntityType() {
	return consumerEntityType;
    }

    /**
     * simulates the execution of a service action
     * 
     * @param action
     *            action to be executed
     * @return true if execution is successful, false if execution is not
     *         possible due to the current state of a service
     * @throws InvalidServiceActionException
     * @throws InvalidServiceCurrentStateException
     */
    public boolean executeAction(String action)
	    throws InvalidServiceActionException,
	    InvalidServiceCurrentStateException {
	boolean isExecuted = false;
	Set<String> actions = new HashSet<String>();
	actions.addAll(inputs);
	actions.addAll(outputs);

	if (!actions.contains(action))
	    throw new InvalidServiceActionException();
	for (ServiceTransition transition : transitions)
	    if (transition.getFrom().equals(currentState)
		    && transition.getAction().equals(action)) {
		setCurrentState(transition.getTo());
		isExecuted = true;
		break;
	    }
	return isExecuted;
    }

    @Override
    public String toString() {
	String str = "";
	str += "sid:" + sid + "\n";
	str += "type:" + type + "\n";
	str += "states:" + states + "\n";
	str += "initial states:" + initialStates + "\n";
	str += "current state:" + currentState + "\n";
	str += "inputs:" + inputs + "\n";
	str += "outputs:" + outputs + "\n";
	str += "abstracts:" + abstracts + "\n";
	str += "transitions:" + transitions + "\n";

	return str;
    }

    @Override
    public ServiceDiagram clone() {
	ServiceDiagram sd = null;
	try {
	    Set<String> states = new HashSet<String>();
	    Set<String> initialStates = new HashSet<String>();
	    Set<String> inputs = new HashSet<String>();
	    Set<String> outputs = new HashSet<String>();
	    Set<String> abstracts = new HashSet<String>();
	    Set<String> concretes = new HashSet<String>();
	    Set<ServiceTransition> transitions = new HashSet<ServiceTransition>();
	    for (String state : this.states) {
		states.add(state);
	    }
	    for (String is : this.initialStates) {
		initialStates.add(is);
	    }
	    for (String i : this.inputs) {
		inputs.add(i);
	    }
	    for (String o : this.outputs) {
		outputs.add(o);
	    }
	    for (String a : this.abstracts) {
		abstracts.add(a);
	    }
	    for (String a : this.concretes) {
		concretes.add(a);
	    }
	    for (ServiceTransition tr : this.transitions) {
		// we do not copy RESET actions since it will be regenerated
		// automacally in the constructor
		if (!tr.getAction().equals("RESET"))
		    transitions.add(new ServiceTransition(tr.getFrom(), tr
			    .getTo(), tr.getAction(), tr.getType()));
	    }
	    sd = new ServiceDiagram(this.sid, this.type,
		    this.consumerEntityType, states, initialStates, inputs,
		    outputs, abstracts, concretes, transitions, false);
	    sd.setCurrentState(currentState);
	} catch (InvalidServiceInitialStateException e) {
	    logger.error(e.getMessage(), e);
	} catch (InvalidServiceTransitionException e) {
	    logger.error(e.getMessage(), e);
	} catch (ServiceDuplicateActionException e) {
	    logger.error(e.getMessage(), e);
	} catch (InvalidServiceCurrentStateException e) {
	    logger.error(e.getMessage(), e);
	}
	return sd;
    }

    public HashSet<String> getConcretes() {
	return concretes;
    }

}
