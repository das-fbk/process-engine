package eu.fbk.das.process.engine.api.domain;

public class ConcreteActivity extends ProcessActivity {

    private String returnsTo;

    public ConcreteActivity() {
	super();
	this.setConcrete(true);
    }

    public ConcreteActivity(int SourceState, int TargetState, String name,
	    String returnsTo) {
	super(SourceState, TargetState, name, ProcessActivityType.CONCRETE);
	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.name = name;
	this.returnsTo = returnsTo;

    }

    @Override
    public boolean isConcrete() {
	return true;
    }

    public String getReturnsTo() {
	return returnsTo;
    }

    public void setReturnsTo(String returnsTo) {
	this.returnsTo = returnsTo;
    }

    @Override
    public String toString() {
	return "[name=" + this.name + "]";
    }
}
