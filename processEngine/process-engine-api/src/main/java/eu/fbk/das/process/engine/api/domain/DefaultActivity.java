package eu.fbk.das.process.engine.api.domain;


public class DefaultActivity extends ProcessActivity {

    private ProcessDiagram DefaultBranch;

    public ProcessDiagram getDefaultBranch() {
	return DefaultBranch;
    }

    public void setDefaultBranch(ProcessDiagram defaultBranch) {
	DefaultBranch = defaultBranch;
    }

    public DefaultActivity() {
	super();
	this.setDefault(true);

    }

    public DefaultActivity(int SourceState, int TargetState,
	    ProcessDiagram DefBranch) {

	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.DefaultBranch = DefBranch;

    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }
}
