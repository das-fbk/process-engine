package eu.fbk.das.process.engine.api.domain;

import java.util.List;

import eu.fbk.das.process.engine.api.jaxb.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.DomainObject.State;
import eu.fbk.das.process.engine.api.jaxb.DomainProperty;
import eu.fbk.das.process.engine.api.jaxb.Fragment;
import eu.fbk.das.process.engine.api.jaxb.Process;

/**
 * Define an entity (Domain Object) and collect all information about it in one
 * place
 * 
 * {@link DomainObject}, {@link Process}, {@link Fragment},
 * {@link DomainProperty}
 */
public class DomainObjectDefinition {

	private DomainObject domainObject;
	private List<Fragment> fragments;

	private List<DomainProperty> properties;
	private State state;
	private Process process;
	private boolean isRole;

	public static final DomainObjectDefinition NONE = new DomainObjectDefinition();

	public void setDomainObject(DomainObject e) {
		this.domainObject = e;
	}

	public DomainObject getDomainObject() {
		return domainObject;
	}

	public List<Fragment> getFragments() {
		return fragments;
	}

	public void setFragments(List<Fragment> fragments) {
		this.fragments = fragments;
	}

	public List<DomainProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<DomainProperty> properties) {
		this.properties = properties;
	}

	// //martina
	public void setState(State state) {
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void setProcess(Process process) {
		this.process = process;

	}

	public Process getProcess() {
		return process;
	}

	public boolean isRole() {
		return isRole;
	}

	public void setRole(boolean isRole) {
		this.isRole = isRole;
	}

	@Override
	public String toString() {
		return "[name=" + getProcess().getName() + "]";
	}

}
