package eu.fbk.das.process.engine.api.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class PickActivity extends ProcessActivity {

    @XmlElement(name = "onMessages")
    private List<OnMessageActivity> OnMessages;

    public PickActivity() {
    }

    public PickActivity(String name) {
	super(name);

    }

    public PickActivity(int SourceState, int TargetState, String name,
	    List<OnMessageActivity> OnMsgs) {
	super(SourceState, TargetState, name, ProcessActivityType.PICK);
	this.name = name;
	this.OnMessages = OnMsgs;
	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.setPick(true);
    }

    public List<OnMessageActivity> getOnMessages() {
	return this.OnMessages;
    }

    public void setOnMessages(List<OnMessageActivity> onMsgs) {
	this.OnMessages = onMsgs;
    }

    @Override
    public PickActivity getCopyOfActivity() {
	return new PickActivity(this.name);
    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }
}
