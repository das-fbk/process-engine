package eu.fbk.das.process.engine.api.domain;

public class InvokeActivty extends ProcessActivity {

    public InvokeActivty() {
    }

    public InvokeActivty(String name) {
	super(name);

    }

    public InvokeActivty(int SourceState, int TargetState, String name) {
	super(SourceState, TargetState, name, ProcessActivityType.INVOKE);
	this.name = name;
	this.setSource(SourceState);
	this.setTarget(TargetState);

    }

    @Override
    public InvokeActivty getCopyOfActivity() {
	return new InvokeActivty(this.name);
    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }

}
