package eu.fbk.das.process.engine.api.domain;

import java.util.List;

import eu.fbk.das.process.engine.api.jaxb.EventHandlerType;

public class ScopeActivity extends ProcessActivity {

    private List<EventHandlerType> eventHandler;
    private ProcessDiagram branch; // a branch for a scope is just a list of
				   // activities to monitor

    public ScopeActivity() {
    }

    public ScopeActivity(String name) {
	super(name);

    }

    public ScopeActivity(int SourceState, int TargetState, String name) {
	super(SourceState, TargetState, name, ProcessActivityType.SCOPE);
	this.name = name;
	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.setScope(true);

    }

    @Override
    public ScopeActivity getCopyOfActivity() {
	return new ScopeActivity(this.name);
    }

    public void setEventHandler(List<EventHandlerType> eventHandler) {
	this.eventHandler = eventHandler;
    }

    public List<EventHandlerType> getEventHandler() {
	return eventHandler;
    }

    public void setBranch(ProcessDiagram branch) {
	this.branch = branch;

    }

    public ProcessDiagram getBranch() {
	return branch;
    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }

}
