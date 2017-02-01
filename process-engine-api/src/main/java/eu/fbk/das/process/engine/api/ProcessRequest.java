package eu.fbk.das.process.engine.api;

import eu.fbk.das.process.engine.api.domain.ProcessActivityType;
import eu.fbk.das.process.engine.api.jaxb.ActivityType;

public class ProcessRequest {

    private int pid;
    private String name;
    private ProcessActivityType type;
    private DomainObjectInstance doi;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public ProcessActivityType getType() {
	return type;
    }

    public void setType(ProcessActivityType type) {
	this.type = type;
    }

    public int getPid() {
	return pid;
    }

    public void setPid(int pidFather) {
	this.pid = pidFather;
    }

    public void setDomainObjectInstance(DomainObjectInstance doi) {
	this.doi = doi;
    }

    public DomainObjectInstance getDomainObjectInstance() {
	return doi;
    }

    @Override
    public String toString() {
	return "[pid=" + pid + ",name=" + name + ",type=" + type + ",doi="
		+ doi.getId() + " ]";
    }

    public boolean sameTypeOf(ActivityType act) {
	switch (act.getClass().getCanonicalName()) {
	case "eu.fbk.das.process.engine.api.jaxb.ReceiveType":
	    return getType() == ProcessActivityType.REPLY ? true : false;
	case "eu.fbk.das.process.engine.api.jaxb.InvokeType":
	    return getType() == ProcessActivityType.INVOKE ? true : false;
	default:
	    break;
	}
	return false;
    }

}
