package eu.fbk.das.process.engine.api.composition.element;

public class ServiceTransitionGlobal extends ServiceTransition {
    private String sid;

    /**
     * contains service type
     */
    private String serviceType;

    public ServiceTransitionGlobal(String from, String to, String action,
	    ServiceDiagramActionType type, String sid, String serviceType) {
	super(from, to, action, type);
	this.sid = sid;
	this.serviceType = serviceType;
    }

    @Override
    public boolean equals(Object obj) {
	ServiceTransitionGlobal tr = (ServiceTransitionGlobal) obj;
	return super.equals(obj) && this.sid.equals(tr.sid);
    }

    @Override
    public String toString() {
	return "(" + from + "--"
		+ ((type == ServiceDiagramActionType.IN) ? "!" : "?") + sid
		+ "_" + action + "->" + to + ")[" + serviceType + "]";
    }

    public String getSid() {
	return sid;
    }

    public String getServiceType() {
	return serviceType;
    }

}
