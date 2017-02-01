package eu.fbk.das.process.engine.api.domain;

public class ReplyActivity extends ProcessActivity {

    public ReplyActivity() {
    }

    public ReplyActivity(String name) {
	super(name);

    }

    public ReplyActivity(int SourceState, int TargetState, String name) {
	super(SourceState, TargetState, name, ProcessActivityType.REPLY);
	this.name = name;
	this.setSource(SourceState);
	this.setTarget(TargetState);

    }

    @Override
    public ReplyActivity getCopyOfActivity() {
	return new ReplyActivity(this.name);
    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }

}
