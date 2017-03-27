package eu.fbk.das.process.engine.api;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ServiceDiagram;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.jaxb.ActionVariableTypeValues;
import eu.fbk.das.process.engine.api.jaxb.DomainObject.State;
import eu.fbk.das.process.engine.api.jaxb.VariableType;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance.DomainObjectInstanceKnowledge.Dp;

public class DomainObjectInstance {

	private ProcessDiagram process;
	private String id;
	private List<ServiceDiagram> serviceDiagrams;
	// il nome del modello del domainObject dal quale questa istanza e' generata
	private String type;
	private List<ObjectDiagram> internal = new ArrayList<ObjectDiagram>();
	private List<ObjectDiagram> external = new ArrayList<ObjectDiagram>();
	private String lon;
	private String lat;
	private Integer onTurn;
	private boolean role;
	private boolean singleton;
	private String ensemble;
	private String correlations;
	private String selectedRoute;
	private Integer pickupPoint;
	private String routes;
	private State state;

	public void setProcess(ProcessDiagram process) {
		this.process = process;
	}

	public ProcessDiagram getProcess() {
		return process;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void updateKnowledge(Dp updated)
			throws InvalidObjectCurrentStateException {
		if (internal == null) {
			internal = new ArrayList<ObjectDiagram>();
		}
		for (ObjectDiagram od : internal) {
			if (od.getOid().equals(updated.getName())) {
				od.setCurrentState(updated.getCurrentState());
			}
		}
		// TODO: also external, for first storyboard
		for (ObjectDiagram od : external) {
			if (od.getOid().equals(updated.getName())) {
				od.setCurrentState(updated.getCurrentState());
			}
		}

	}

	public void setFragments(List<ServiceDiagram> serviceDiagrams) {
		this.serviceDiagrams = serviceDiagrams;
	}

	public List<ServiceDiagram> getFragments() {
		return serviceDiagrams;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setInternalKnowledge(List<ObjectDiagram> internal) {
		this.internal = internal;
	}

	public void setExternalKnowledge(List<ObjectDiagram> external) {
		this.external = external;
	}

	public List<ObjectDiagram> getInternalKnowledge() {
		return internal;
	}

	public List<ObjectDiagram> getExternalKnowledge() {
		return external;
	}

	/**
	 * @return true if know a domain property
	 */
	public boolean know(String domainProperty) {
		for (ObjectDiagram i : internal) {
			if (i.getType().equals(domainProperty)) {
				return true;
			}
		}
		for (ObjectDiagram i : external) {
			if (i.getType().equals(domainProperty)) {
				return true;
			}
		}
		return false;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public void setLon(String lon) {
		this.lon = lon;

	}

	public String getLat() {
		return lat;
	}

	public String getLon() {
		return lon;
	}

	public void setOnTurn(Integer onTurn) {
		this.onTurn = onTurn;
	}

	public Integer getOnTurn() {
		return onTurn;
	}

	public boolean isRole() {
		return role;
	}

	public void setRole(boolean role) {
		this.role = role;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public String toString() {
		return "[id=" + id + "]";
	}

	public void setEnsemble(String ensemble) {
		this.ensemble = ensemble;
	}

	public String getEnsemble() {
		return ensemble;
	}

	public void setCorrelations(String correlations) {
		this.correlations = correlations;
	}

	public String getCorrelations() {
		return correlations;
	}

	public void setSelectedRoute(String selectedRoute) {
		this.selectedRoute = selectedRoute;
	}

	public String getSelectedRoute() {
		return selectedRoute;
	}

	public Integer getPickupPoint() {
		return pickupPoint;
	}

	public void setPickupPoint(Integer pickupPoint) {
		this.pickupPoint = pickupPoint;
	}

	public void setRoutes(String routes) {
		this.routes = routes;
	}

	public String getRoutes() {
		return routes;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return this.state;
	}

	public boolean hasVariableWithName(String name) {
		boolean response = false;
		if (this.getState() != null) {
			for (VariableType stateVar : this.getState().getStateVariable()) {
				if (stateVar.getName().equalsIgnoreCase(name)) {
					return response = true;
				}
			}
		}
		return response;
	}

	public int getIndexOfVariableWithName(String name) {
		int index = -1;
		if (this.getState() != null) {
			for (VariableType stateVar : this.getState().getStateVariable()) {
				if (stateVar.getName().equalsIgnoreCase(name)) {
					return index = this.getState().getStateVariable()
							.indexOf(stateVar);
				}
			}
		}
		return index;
	}

	public ActionVariableTypeValues getTypeOfVariableWithName(String name) {
		ActionVariableTypeValues response = ActionVariableTypeValues.DEFAULT;
		for (VariableType stateVar : this.getState().getStateVariable()) {
			if (stateVar.getName().equalsIgnoreCase(name)) {
				return response = stateVar.getType();
			}
		}
		return response;
	}

	public boolean hasSameVariablesOfCurrentActivity(ProcessActivity current) {
		boolean response = true;
		if (current.getServiceActionVariables() != null) {
			for (VariableType actionVar : current.getServiceActionVariables()) {
				if (!this.hasVariableWithName(actionVar.getName())) {
					return false;
				}
			}
		}
		return response;
	}

	public Element getStateVariableContentByName(String varName) {
		Element stateVarContent = null;
		int index = this.getIndexOfVariableWithName(varName);
		if (index != -1) {
			stateVarContent = (Element) this.getState().getStateVariable()
					.get(index).getContent();
		}
		return stateVarContent;
	}

	public void setStateVariableContentByVarName(String varName,
			Element varContent) {
		int index = this.getIndexOfVariableWithName(varName);
		if (index != -1) {
			this.getState().getStateVariable().get(index)
					.setContent(varContent);
		}
	}

	public void extendInternalState(List<VariableType> extendedState) {
		if (extendedState != null) {
			if (this.getState() != null) {
				for (VariableType var : extendedState) {
					boolean found = false;
					for (VariableType stateVar : this.getState()
							.getStateVariable()) {
						if (stateVar.getName().equalsIgnoreCase(var.getName())) {
							found = true;
							break;
						}
					}
					if (!found) {
						this.getState().getStateVariable().add(var);
					}
				}
			} else {
				State s = new State();
				s.getStateVariable().addAll(extendedState);
				this.setState(s);
			}
		}
	}
}
