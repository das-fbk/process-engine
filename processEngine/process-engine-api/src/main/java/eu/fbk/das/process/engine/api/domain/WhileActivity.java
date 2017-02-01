package eu.fbk.das.process.engine.api.domain;

import eu.fbk.das.process.engine.api.jaxb.ClauseType;
import eu.fbk.das.process.engine.api.jaxb.WhileType.VarCondition;

public class WhileActivity extends ProcessActivity {

    private VarCondition condition;
    private ProcessDiagram defaultBranch;
    private ClauseType contexCondition;

    public WhileActivity() {
    }

    public WhileActivity(String name) {
	super(name);

    }

    public WhileActivity(int SourceState, int TargetState, String name) {
	super(SourceState, TargetState, name, ProcessActivityType.WHILE);
	this.name = name;
	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.setWhile(true);

    }

    @Override
    public WhileActivity getCopyOfActivity() {
	return new WhileActivity(this.name);
    }

    public void setCondition(VarCondition cond) {
	this.condition = cond;
    }

    public VarCondition getCondition() {
	return condition;
    }

    public void addDefaultBranch(ProcessDiagram defbranch) {
	this.defaultBranch = defbranch;

    }

    public ProcessDiagram getDefaultBranch() {
	return defaultBranch;
    }

    public void setContextCondition(ClauseType contextCondition) {
	this.contexCondition = contextCondition;
    }

    public ClauseType getContextCondition() {
	return contexCondition;
    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }

}
