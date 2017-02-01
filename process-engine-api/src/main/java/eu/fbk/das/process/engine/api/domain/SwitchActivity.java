package eu.fbk.das.process.engine.api.domain;

import java.util.List;

public class SwitchActivity extends ProcessActivity {

    private List<IFActivity> IFs;

    private DefaultActivity DEF;

    public SwitchActivity() {
    }

    public SwitchActivity(String name) {
	super(name);

    }

    public SwitchActivity(int SourceState, int TargetState, String name,
	    List<IFActivity> ifs, DefaultActivity def) {
	super(SourceState, TargetState, name, ProcessActivityType.SWITCH);
	this.name = name;
	this.IFs = ifs;
	this.DEF = def;
	this.setSource(SourceState);
	this.setTarget(TargetState);
	this.setSwitch(true);

    }

    public List<IFActivity> getIFs() {
	return IFs;
    }

    public void setIFs(List<IFActivity> iFs) {
	IFs = iFs;
    }

    public DefaultActivity getDEF() {
	return DEF;
    }

    public void setDEF(DefaultActivity dEF) {
	DEF = dEF;
    }

    @Override
    public SwitchActivity getCopyOfActivity() {
	return new SwitchActivity(this.name);
    }

    @Override
    public String toString() {
	return "[name=" + name + "]";
    }

}
