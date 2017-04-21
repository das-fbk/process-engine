package eu.fbk.das.process.engine.api.domain;

import java.util.List;

import eu.fbk.das.process.engine.api.AdaptationProblem;
import eu.fbk.das.process.engine.api.jaxb.GoalType;

public class AbstractActivity extends ProcessActivity {

    private List<ProcessActivity> children;
    private GoalType goal;
    private AdaptationProblem problem;
    private String receiveGoal;
    private String abstractType;
    private String receiveVar;

    public List<ProcessActivity> getChildren() {
	return children;
    }

    public void setChildren(List<ProcessActivity> children) {
	this.children = children;
    }

    public AbstractActivity() {
	super();
	goal = new GoalType();
	this.setAbstract(true);
    }

    public AbstractActivity(int SourceState, int TargetState, String name,
	    GoalType goal) {
	super(SourceState, TargetState, name, ProcessActivityType.ABSTRACT);
	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.goal = goal;
	this.name = name;
    }

    @Override
    public boolean isAbstract() {
	return true;
    }

    public void addActivity(ProcessActivity activity) {
	children.add(activity);
    }

    public GoalType getGoal() {
	return goal;
    }

    public void setGoal(GoalType goal) {
	this.goal = goal;
    }

    public List<ProcessActivity> getActivities() {
	return children;
    }

    public void setAdaptationProblem(AdaptationProblem problem) {
	this.problem = problem;

    }

    public AdaptationProblem getProblem() {
	return problem;
    }

    public void setReceiveGoal(String receiveGoal) {
	this.receiveGoal = receiveGoal;
    }

    public String getReceiveGoal() {
	return receiveGoal;
    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }

    public void setAbstractType(String type) {
	this.abstractType = type;
    }

    public void setReceiveVar(String receiveVar) {
	this.receiveVar = receiveVar;

    }

    public String getAbstractType() {
	return abstractType;
    }

    public String getReceiveVar() {
	return receiveVar;
    }

}
