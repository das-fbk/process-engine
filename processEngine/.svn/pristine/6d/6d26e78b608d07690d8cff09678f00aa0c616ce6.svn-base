package eu.fbk.das.process.engine.api.domain;

import java.util.List;

public class IFActivity extends ProcessActivity {

    private List<eu.fbk.das.process.engine.api.jaxb.PreconditionType> conditions;
    private List<eu.fbk.das.process.engine.api.jaxb.SwitchType.If.VarCondition> varConditions;

    public void setVarConditions(
	    List<eu.fbk.das.process.engine.api.jaxb.SwitchType.If.VarCondition> varConditions) {
	this.varConditions = varConditions;
    }

    public List<eu.fbk.das.process.engine.api.jaxb.SwitchType.If.VarCondition> getVarConditions() {
	return varConditions;
    }

    private ProcessDiagram branch;

    public IFActivity() {
	super();
	this.setIF(true);
    }

    public IFActivity(
	    int SourceState,
	    int TargetState,
	    List<eu.fbk.das.process.engine.api.jaxb.PreconditionType> contextConditions,
	    List<eu.fbk.das.process.engine.api.jaxb.SwitchType.If.VarCondition> varConds,
	    ProcessDiagram branch) {

	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.conditions = contextConditions;
	this.varConditions = varConds;
	this.branch = branch;

    }

    public ProcessDiagram getBranch() {
	return branch;
    }

    public void setBranch(ProcessDiagram branch) {
	this.branch = branch;
    }

    public List<eu.fbk.das.process.engine.api.jaxb.PreconditionType> getConditions() {
	return conditions;
    }

    public void setConditions(
	    List<eu.fbk.das.process.engine.api.jaxb.PreconditionType> conditions) {
	this.conditions = conditions;
    }

}
