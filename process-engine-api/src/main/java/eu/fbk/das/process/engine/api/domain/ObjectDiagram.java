package eu.fbk.das.process.engine.api.domain;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectEventException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectTransitionException;

/**
 * This class represents object diagrams
 * 
 * @author Heorhi
 *
 */
public class ObjectDiagram {

	private static final Logger logger = LogManager
			.getLogger(ObjectDiagram.class);

	/**
	 * Identifier of an object diagram instance
	 */
	private String oid;
	/**
	 * Object diagram type
	 */
	private String type;

	private Set<String> states;
	private Set<String> initialStates;
	private Set<String> events;
	private Set<ObjectTransition> transitions;

	/**
	 * Indicates the current state of the diagram
	 */
	private String currentState;

	// public static void main(String args[]) {
	// Parser parser = new Parser(Orchestrator.XSD_HOME, Orchestrator.XML_HOME);
	// String type = "ST_StatusNormal";
	// String path = "objects";
	// ObjectDiagram od = parser.parseObjectDiagram("id1", type);
	//
	// String emissionPath = od.emit2Dot(path);
	//
	// emissionPath = emissionPath.substring(0, emissionPath.length() - 4)
	// + ".ps";
	//
	// try {
	// File f = new File(emissionPath);
	// System.out.println(f.getCanonicalPath());
	// Runtime.getRuntime().exec(
	// "C:\\Program Files\\Ghostgum\\gsview\\gsview32.exe \""
	// + f.getCanonicalPath() + "\"");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	public ObjectDiagram(String oid, String type, Set<String> states,
			Set<String> initialStates, Set<String> events,
			Set<ObjectTransition> transitions)
			throws InvalidObjectInitialStateException,
			InvalidObjectTransitionException {
		this.oid = oid;
		this.type = type;
		this.states = new HashSet<String>(states);
		if (this.states.containsAll(initialStates))
			this.initialStates = new HashSet<String>(initialStates);
		else
			throw new InvalidObjectInitialStateException();
		this.events = new HashSet<String>(events);

		this.transitions = new HashSet<ObjectTransition>();
		for (ObjectTransition transition : transitions) {
			if (states.contains(transition.getFrom())
					&& states.contains(transition.getTo())
					&& events.contains(transition.getEvent())) {

				// Transitition duplication control
				boolean isAdded = false;
				for (ObjectTransition tr : this.transitions)
					if (tr.equals(transition)) {
						isAdded = true;
						break;
					}
				if (!isAdded)
					this.transitions.add(transition);
			} else {
				System.out.println("Invalid Transition: " + transition);
				throw new InvalidObjectTransitionException();
			}
		}
	}

	public String getOid() {
		return oid;
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

	public Set<String> getEvents() {
		return events;
	}

	public Set<ObjectTransition> getTransitions() {
		return transitions;
	}

	public String getCurrentState() {
		return currentState;
	}

	public void setCurrentState(String currentState)
			throws InvalidObjectCurrentStateException {
		if (!states.contains(currentState)) {
			System.out.println(states);
			throw new InvalidObjectCurrentStateException();
		}
		this.currentState = currentState;
	}

	/**
	 * This method simulates publishing of an event to an object
	 * 
	 * @param event
	 *            event to publish (must be amoung object events)
	 * @throws InvalidObjectEventException
	 * @throws InvalidObjectCurrentStateException
	 * @return true if one of transitions has been triggered, false if
	 *         publishing was successful but no transitions have bben triggered
	 *         by it
	 */
	public boolean publishEvent(String event)
			throws InvalidObjectEventException,
			InvalidObjectCurrentStateException {
		boolean isPublished = false;

		if (events.contains(event)) {
			for (ObjectTransition transition : transitions) {
				if (transition.getFrom().equals(currentState)
						&& transition.getEvent().equals(event)) {
					setCurrentState(transition.getTo());
					isPublished = true;

					break;
				}
			}
		}
		return isPublished;
	}

	@Override
	public String toString() {
		String str = "";
		str += "oid:" + oid + "\n";
		str += "type:" + type + "\n";
		str += "states:" + states + "\n";
		str += "initial states:" + initialStates + "\n";
		str += "current state:" + currentState + "\n";
		str += "events:" + events + "\n";
		str += "transitions:" + transitions + "\n";
		return str;
	}

	@Override
	public ObjectDiagram clone() {
		ObjectDiagram od = null;
		try {
			Set<String> states = new HashSet<String>();
			Set<String> initialStates = new HashSet<String>();
			Set<String> events = new HashSet<String>();
			Set<ObjectTransition> transitions = new HashSet<ObjectTransition>();
			for (String state : this.states) {
				states.add(state);
			}
			for (String state : this.initialStates) {
				initialStates.add(state);
			}
			for (String event : this.events) {
				events.add(event);
			}
			for (ObjectTransition tr : this.transitions) {
				transitions.add(new ObjectTransition(tr.getFrom(), tr.getTo(),
						tr.getEvent()));
			}
			od = new ObjectDiagram(this.oid, this.type, states, initialStates,
					events, transitions);
			od.setCurrentState(currentState);
		} catch (InvalidObjectInitialStateException e) {
			logger.error(e.getMessage(), e);
		} catch (InvalidObjectTransitionException e) {
			logger.error(e.getMessage(), e);
		} catch (InvalidObjectCurrentStateException e) {
			logger.error(e.getMessage(), e);
		}
		return od;
	}

	// public String emit2Dot(String path) {
	// // creating a command file
	// FileWriter writer;
	// try {
	// // creating directory
	// File dir = new File(path);
	// if (!dir.exists())
	// dir.mkdir();
	// writer = new FileWriter(path + "\\" + oid + "_" + type + ".dot");
	// writer.write("digraph plan_fsm {\nratio=\"1.3\";\nmargin=\"0.2\";\nsize=\"8.0,11.4\";\n");
	// for (ObjectTransition tr : transitions) {
	// writer.write("\"" + tr.getFrom() + "\" -> \"" + tr.getTo()
	// + "\" [" + "label=\"" + oid + "_" + tr.getEvent()
	// + "\"]\n");
	// }
	// for (String state : states) {
	// writer.write("\"" + state + "\"\n");
	// }
	// writer.write("}");
	// writer.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// DotParser.dot2ps(
	// "C:\\Users\\Heorhi\\Program Files\\Graphviz\\bin\\dot.exe",
	// path + "\\" + oid + "_" + type + ".dot", path + "\\" + oid
	// + "_" + type + ".ps");
	// return path + "\\" + oid + "_" + type + ".dot";
	// }

}
