package eu.fbk.das.process.engine.api.composition.element;

/**
 * This class represents transitions in object diagrams
 * 
 * @author Heorhi
 *
 */
public class ObjectTransition {

    private String from;
    private String to;
    private String event;

    public String getFrom() {
	return from;
    }

    public String getTo() {
	return to;
    }

    public String getEvent() {
	return event;
    }

    public ObjectTransition(String from, String to, String event) {
	this.from = from;
	this.to = to;
	this.event = event;
    }

    @Override
    public boolean equals(Object obj) {
	ObjectTransition transition = null;
	try {
	    transition = (ObjectTransition) obj;
	} catch (ClassCastException e) {
	    return false;
	}

	if (transition.getFrom().equals(from) && transition.getTo().equals(to)
		&& transition.getEvent().equals(event)) {
	    return true;
	} else {
	    return false;
	}

    }

    @Override
    public String toString() {
	return "(" + from + "--" + event + "->" + to + ")";
    }

}
