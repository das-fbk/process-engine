package eu.fbk.das.process.engine.api.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import eu.fbk.das.process.engine.api.jaxb.EffectType;
import eu.fbk.das.process.engine.api.jaxb.PreconditionType;
import eu.fbk.das.process.engine.api.jaxb.VariableType;

@XmlAccessorType(XmlAccessType.NONE)
public class ProcessActivity {

	@XmlElement(name = "name")
	protected String name;
	@XmlElement(name = "order")
	private int order;
	// private List<Variable> variables;

	private int source;
	private int target;

	// service id for the activity
	private String sid;

	// refers to variables in the activities of processes (type ActivityType in
	// process)
	private List<VariableType> actionVariables;

	// refers to variables in the activities of fragments (type
	// ServiceActionType in fragment)
	private List<VariableType> serviceActionVariables;

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	private boolean isService;
	private boolean isSend;
	private boolean isReceive;
	private boolean isHuman;
	private boolean isEvent;
	private boolean isAbstract;
	private boolean isIF;
	private boolean isPick;
	private boolean isConcrete;
	private boolean isDefault;
	private boolean isSwitch;
	private boolean isWhile;
	private boolean isOnMessage;
	private boolean highlighted;

	private PreconditionType precondition;
	private EffectType effect;

	private String serviceType;

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public boolean isHighlighted() {
		return highlighted;
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}

	public boolean isSwitch() {
		return isSwitch;
	}

	public void setSwitch(boolean isSwitch) {
		this.isSwitch = isSwitch;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	// public PreconditionType getCompGoal() {
	// return compGoal;
	// }

	// public void setCompGoal(PreconditionType compGoal) {
	// this.compGoal = compGoal;
	// }

	private ProcessActivityType type;

	private int id;

	private boolean isExecuted = false;

	private boolean isRunning = false;

	private boolean isStopped = false;

	public boolean isStopped() {
		return isStopped;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

	private String from;
	private boolean isScope;

	public ProcessActivity() {
	}

	public ProcessActivity(String name) {
		this.name = name;

	}

	public ProcessActivity(int SourceState, int TargetState, String name,
			ProcessActivityType type) {
		this.name = name;
		this.type = type;

	}

	public boolean isConcrete() {
		return isConcrete;
	}

	public void setConcrete(boolean isConcrete) {
		this.isConcrete = isConcrete;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSource() {
		return source;
	}

	public ProcessActivityType getType() {
		return type;
	}

	public boolean isPick() {
		return isPick;
	}

	public void setPick(boolean isPick) {
		this.isPick = isPick;
	}

	public void setType(ProcessActivityType type) {
		this.type = type;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public ProcessActivity getCopyOfActivity() {
		return new ProcessActivity(this.name);
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public void setIF(boolean isIF) {
		this.isIF = isIF;
	}

	public void setOnMessage(boolean isOnMsg) {
		this.isOnMessage = isOnMsg;
	}

	public boolean containsActivity(ProcessActivity act) {
		return this.equals(act);
	}

	public void setReceive(boolean isReceive) {
		this.isReceive = isReceive;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFrom() {
		return this.from;
	}

	public String getName() {
		return this.name;
	}

	// public List<Variable> getVariables() {
	// return this.variables;
	// }

	public void setName(String name) {
		this.name = name;
	}

	// public void setVariables(List<Variable> variables) {
	// this.variables = variables;
	// }

	public boolean isEvent() {
		return this.isEvent;
	}

	public void setEvent(boolean isEvent) {
		this.isEvent = isEvent;
	}

	public boolean isAbstract() {
		return this.isAbstract;
	}

	public boolean isSend() {
		return this.isSend;
	}

	public boolean isHuman() {
		return this.isHuman;
	}

	public boolean isIF() {
		return this.isIF;
	}

	public void setHuman(boolean isHuman) {
		this.isHuman = isHuman;
	}

	public boolean isReceive() {
		return this.isReceive;
	}

	public void fillInAttributes(ProcessActivity clone) {
		clone.name = this.name;
		clone.isService = this.isService;
		clone.isExecuted = this.isExecuted;
		clone.isRunning = this.isRunning;
		clone.from = this.from;
		clone.isSend = this.isSend;
		clone.isReceive = this.isReceive;
		clone.isIF = this.isIF;

		clone.isReceive = this.isReceive;
		clone.isSwitch = this.isSwitch;
		clone.isPick = this.isPick;
		clone.isConcrete = this.isConcrete;
		clone.isAbstract = this.isAbstract;
		clone.isScope = this.isScope;
		clone.isWhile = this.isWhile;

		// if (variables != null) {
		// List<Variable> vars = new ArrayList<Variable>();
		// for (Variable v : variables) {
		// Variable vClone = v.clone();
		// vars.add(vClone);
		//
		// }
		// clone.setVariables(vars);
		// }
	}

	@Override
	public ProcessActivity clone() {

		ProcessActivity clone = new ProcessActivity();
		fillInAttributes(clone);
		return clone;
	}

	public boolean isExecuted() {
		return isExecuted;
	}

	public void setExecuted(boolean executed) {
		this.isExecuted = executed;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public void setPrecondition(PreconditionType precondition) {
		this.precondition = precondition;
	}

	public void setEffect(EffectType effect) {
		this.effect = effect;
	}

	public PreconditionType getPrecondition() {
		return precondition;
	}

	public EffectType getEffect() {
		return effect;
	}

	public void setWhile(boolean isWhile) {
		this.isWhile = isWhile;

	}

	public boolean isWhile() {
		return isWhile;
	}

	public boolean isScope() {
		return isScope;
	}

	public void setScope(boolean isScope) {
		this.isScope = isScope;
	}

	@Override
	public String toString() {
		return "[name=" + name + "]";
	}

	public List<VariableType> getActionVariables() {
		return actionVariables;
	}

	public void setActionVariables(List<VariableType> actionVariables) {
		this.actionVariables = actionVariables;
	}

	public List<VariableType> getServiceActionVariables() {
		return serviceActionVariables;
	}

	public void setServiceActionVariables(
			List<VariableType> serviceActionVariables) {
		this.serviceActionVariables = serviceActionVariables;
	}

}
