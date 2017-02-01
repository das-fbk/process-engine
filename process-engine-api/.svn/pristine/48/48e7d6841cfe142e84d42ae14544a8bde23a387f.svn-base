package eu.fbk.das.process.engine.api.domain;

/**
 * This class represents service action labeled with input or output action
 * 
 * @author Heorhi
 *
 */
public class ServiceTransition {
    protected String from;
    protected String to;
    protected String action;
    protected ServiceDiagramActionType type;
    private String receiveVar;
    private String transitionType;

    public String getFrom() {
	return from;
    }

    public String getTo() {
	return to;
    }

    public String getAction() {
	return action;
    }

    public ServiceDiagramActionType getType() {
	return type;
    }

    public ServiceTransition(String from, String to, String action,
	    ServiceDiagramActionType type) {
	this.from = from;
	this.to = to;
	this.action = action;
	this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
	ServiceTransitionGlobal transition = null;
	try {
	    transition = (ServiceTransitionGlobal) obj;
	} catch (ClassCastException e) {
	    return false;
	}

	if (transition.getFrom().equals(from) && transition.getTo().equals(to)
		&& transition.getAction().equals(action)
		&& transition.getType().equals(type)) {
	    return true;
	} else {
	    return false;
	}

    }

    @Override
    public String toString() {
	return "(" + from + "--"
		+ ((type == ServiceDiagramActionType.IN) ? "!" : "?") + action
		+ "->" + to + ")";
    }

    public void setReceiveVar(String receiveVar) {
	this.receiveVar = receiveVar;

    }

    public String getReceiveVar() {
	return receiveVar;
    }

    public void setTransitionType(String type) {
	this.transitionType = type;
    }

    public String getTransitionType() {
	return transitionType;
    }
}
