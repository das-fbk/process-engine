package eu.fbk.das.process.engine.api.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowCurrentStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "process", namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable")
public class ProcessDiagram {

    private static final Logger logger = LogManager
	    .getLogger(ProcessDiagram.class);

    /**
     * Identifier of a process instance
     */
    @XmlTransient
    private int pid;
    @XmlAttribute
    private String name;
    @XmlTransient
    private Set<Integer> states;
    @XmlTransient
    private int initialState;
    @XmlTransient
    private Set<Integer> finalStates;
    @XmlTransient
    private boolean isRunning = false;
    @XmlTransient
    private boolean isEnded = false;

    private List<ProcessActivity> executionHistory = new ArrayList<ProcessActivity>();

    public List<ProcessActivity> getExecutionHistory() {
	return executionHistory;
    }

    public void setExecutionHistory(List<ProcessActivity> executionHistory) {
	this.executionHistory = executionHistory;
    }

    public void addExecuteActivity(ProcessActivity act) {
	this.executionHistory.add(act);

    }

    public void setEnded(boolean isEnded) {
	this.isEnded = isEnded;
    }

    public boolean getEnded() {
	return isEnded;
    }

    private ProcessDiagram father;

    public ProcessDiagram getFather() {
	return father;
    }

    public void setFather(ProcessDiagram father) {
	this.father = father;
    }

    public Set<Integer> getFinalStates() {
	return finalStates;
    }

    public void setFinalStates(Set<Integer> finalStates) {
	this.finalStates = finalStates;
    }

    @XmlTransient
    private boolean terminated;

    public boolean isTerminated() {
	return terminated;
    }

    public void setTerminated(boolean terminated) {
	this.terminated = terminated;
    }

    @XmlElementWrapper(name = "sequence", namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable")
    @XmlElements({
	    @XmlElement(name = "switch", type = eu.fbk.das.process.engine.api.domain.SwitchActivity.class, namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable"),
	    @XmlElement(name = "reply", type = eu.fbk.das.process.engine.api.domain.ReplyActivity.class, namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable"),
	    @XmlElement(name = "invoke", type = eu.fbk.das.process.engine.api.domain.InvokeActivty.class, namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable"),
	    @XmlElement(name = "pick", type = eu.fbk.das.process.engine.api.domain.PickActivity.class, namespace = "http://docs.oasis-open.org/wsbpel/2.0/process/executable"),

    })
    private List<ProcessActivity> activities;

    public boolean isRunning() {
	return isRunning;
    }

    public void setRunning(boolean isRunning) {
	this.isRunning = isRunning;
    }

    @XmlTransient
    private int currentState;

    private ProcessActivity currentActivity;

    /** default constructor */
    public ProcessDiagram() {
	if (activities == null) {
	    activities = new ArrayList<ProcessActivity>();
	}
    }

    public ProcessDiagram(int pid, Set<Integer> states, int initialState,
	    List<ProcessActivity> all) throws InvalidFlowInitialStateException,
	    InvalidFlowActivityException, FlowDuplicateActivityException {
	this.pid = pid;

	for (int i = 0; i < all.size(); i++) {
	    states.add(i);
	}
	this.states = new HashSet<Integer>(states);
	if (this.states.contains(initialState))
	    this.initialState = initialState;
	else
	    throw new InvalidFlowInitialStateException();
	this.activities = new ArrayList<ProcessActivity>(all);

	for (ProcessActivity act : all) {

	    if (this.states.contains(act.getSource())
		    && this.states.contains(act.getTarget())) {

		// //activity duplication control
		boolean isAdded = false;
		for (Object activ : this.activities)
		    if (activ.equals(act)) {
			isAdded = true;
			break;
		    }
		if (!isAdded)
		    this.activities.add(act);
	    } else {
		throw new InvalidFlowActivityException();
	    }
	}

    }

    /**
     * Create a processDiagram with a given list of activities
     * 
     * @param all
     *            , list of activities
     * 
     *            Note: processDigram in order to be run by
     *            {@link ProcessEngine} need a processId and a proper set of
     *            states
     */
    public ProcessDiagram(List<ProcessActivity> all) {
	this.activities = all;
    }

    public ProcessDiagram(String string, List<ProcessActivity> all,
	    Set<Integer> states, int procId)
	    throws InvalidFlowInitialStateException,
	    InvalidFlowActivityException {
	this.states = new HashSet<Integer>(states);

	this.activities = new ArrayList<ProcessActivity>(all);
	this.pid = procId;

	for (ProcessActivity act : all) {

	    if (this.states.contains(act.getSource())
		    && this.states.contains(act.getTarget())) {

		// //activity duplication control
		boolean isAdded = false;
		for (Object activ : this.activities)
		    if (activ.equals(act)) {
			isAdded = true;
			break;
		    }
		if (!isAdded)
		    this.activities.add(act);
	    } else {
		throw new InvalidFlowActivityException();
	    }
	}

    }

    public void addState(int i) {
	states.add(i);
    }

    public int getpid() {
	return pid;
    }

    public ProcessActivity getCurrentActivity() {
	return currentActivity;
    }

    public void setCurrentActivity(ProcessActivity currentActivity) {
	this.currentActivity = currentActivity;
    }

    public Set<Integer> getStates() {
	return states;
    }

    public int getInitialState() {
	return initialState;
    }

    public List<ProcessActivity> getActivities() {
	return activities;
    }

    public boolean FinalState(int state) {
	int finale = 0;
	boolean result = false;

	for (ProcessActivity act : this.activities) {

	    if (act.getSource() != state) {
		// System.out.println(state+" is not final");
	    } else {
		// System.out.println(state+" is final");
		finale++;
	    }
	}

	if (finale == 0) {
	    result = true;
	} else {
	    result = false;
	}
	return result;
    }

    public int getCurrentState() {
	return currentState;
    }

    public String delete_prefix(String string) {
	String[] arr = string.split("\\.|_");
	String result = arr[arr.length - 1];
	return result;
    }

    public void setCurrentState(int currentState)
	    throws InvalidFlowCurrentStateException {
	if (!states.contains(currentState))
	    throw new InvalidFlowCurrentStateException();
	this.currentState = currentState;
    }

    public ProcessActivity getFirstActivity() {
	ProcessActivity result = null;

	for (int i = 0; i < this.activities.size(); i++) {
	    boolean first = true;
	    for (ProcessActivity act : this.activities) {
		if (this.getActivities().get(i).getSource() == act.getTarget()) {
		    first = false;
		}
	    }
	    if (first) {
		result = this.getActivities().get(i);
	    }
	}
	return result;
    }

    /**
     * simulates the execution of a flow activity
     * 
     * @param activity
     *            to be executed
     * @return true if execution is successful, false if execution is not
     *         possible due to the current state of a flow
     * @throws InvalidServiceActionException
     * @throws InvalidServiceCurrentStateException
     */
    public boolean executeActivity(String activity)
	    throws InvalidFlowActivityException,
	    InvalidFlowCurrentStateException {
	boolean isExecuted = false;
	Set<ProcessActivity> activities = new HashSet<ProcessActivity>();

	if (!activities.contains(activity))
	    throw new InvalidFlowActivityException();
	for (ProcessActivity act : activities)
	    if ((act.getSource() == currentState)
		    && act.getName().equals(activity)) {
		setCurrentState(act.getTarget());
		isExecuted = true;
		break;
	    }
	return isExecuted;
    }

    public void refine(ProcessActivity activity,
	    List<ProcessActivity> refinement) {

	this.getActivities().remove(activity);
	for (int i = 0; i < refinement.size(); i++) {
	    this.getActivities().add(refinement.get(i));
	}

	for (Object act : this.getActivities()) {
	    System.out.println(((ProcessActivity) act).getName());
	}

    }

    @Override
    public String toString() {
	String str = "";
	str += "pid:" + pid + "\n";
	str += "states:" + states + "\n";
	str += "initial state:" + initialState + "\n";
	str += "current state:" + currentState + "\n";
	str += "activities:" + activities + "\n";

	return str;
    }

    public List<ProcessActivity> getNextActivity() {

	List<ProcessActivity> actList = new ArrayList<ProcessActivity>();

	// case in which we start a new flow
	if (this.currentActivity == null) {

	    int initialState = this.getInitialState();

	    for (ProcessActivity act : this.getActivities()) {
		if (act.getSource() == initialState) {
		    actList.add(act);
		}
	    }

	}
	// we are in the middle of the flow execution
	else {
	    int target = this.currentActivity.getTarget();
	    for (ProcessActivity act : this.getActivities()) {
		if (act.getSource() == target) {
		    actList.add(act);
		}

	    }
	}
	if (actList.isEmpty()) {
	    this.terminated = true;
	}
	return actList;

    }

    public boolean isEnded() {
	return isEnded;
	// commentato da Alberto, per il collegamento alla terminazione del
	// processo
	// boolean result = false;
	//
	// if (FinalState(this.currentState)) {
	// result = true;
	// } else {
	// result = false;
	// }
	// return result;
    }

    public static ProcessDiagram AdaptationToFlowModel(
	    List<ServiceTransitionGlobal> adaptationProcess,
	    String initialState, int pid, List<String> abstracts,
	    List<String> concretes) throws InvalidFlowInitialStateException,
	    InvalidFlowActivityException {

	Set<Integer> states = new HashSet<Integer>();
	int order = 0;
	List<ProcessActivity> actList = new ArrayList<ProcessActivity>();
	constructSequenceFromState(initialState, adaptationProcess, actList,
		order, states, pid, abstracts, concretes);
	ProcessDiagram model = new ProcessDiagram("refinement", actList,
		states, pid);
	return model;
    }

    private static void constructSequenceFromState(String pointer,
	    List<ServiceTransitionGlobal> adaptationProcess,
	    List<ProcessActivity> actList, int order, Set<Integer> states,
	    int pid, List<String> abstracts, List<String> concretes) {

	List<ServiceTransitionGlobal> currentTransitions = getTransitionsFromState(
		pointer, adaptationProcess);
	if (currentTransitions.size() == 1) {
	    if (multipleTransitionsToState(pointer, adaptationProcess)) {
		// end of pick branch
		return;
	    }
	    ServiceTransitionGlobal t = currentTransitions.iterator().next();
	    addActivityInSequence(t, actList, order, abstracts, concretes);
	    Integer from = Integer.parseInt(t.getFrom());
	    Integer to = Integer.parseInt(t.getTo());
	    states.add(from);
	    states.add(to);
	    order++;
	    constructSequenceFromState(t.getTo(), adaptationProcess, actList,
		    order, states, pid, abstracts, concretes);
	}

	if (currentTransitions.size() > 1) {
	    // System.out.println("Two or more transitions...");
	    // handle pick
	    List<ProcessActivity> activities = new ArrayList<ProcessActivity>();

	    System.out.println("Creating a pick...");
	    List<OnMessageActivity> onMessages = new ArrayList<OnMessageActivity>();
	    for (ServiceTransitionGlobal tr : currentTransitions) {
		OnMessageActivity onMsg = new OnMessageActivity();
		onMsg.setOnMessage(true);
		onMsg.setName(tr.getAction());

		try {
		    onMsg.setBranch(AdaptationToFlowModel(adaptationProcess,
			    tr.getTo(), pid, abstracts, concretes));
		} catch (InvalidFlowInitialStateException e) {
		    logger.error(e.getMessage(), e);
		} catch (InvalidFlowActivityException e) {
		    logger.error(e.getMessage(), e);
		}

		onMessages.add(onMsg);
	    }

	    Integer pickTo = getPickToState(onMessages.get(0).getBranch());
	    if (pickTo == -1
		    && onMessages.get(0).getBranch().getActivities().size() == 0) {
		pickTo = Integer.parseInt(currentTransitions.get(0).getTo());
	    }

	    Integer pickfrom = Integer.parseInt(currentTransitions.get(0)
		    .getFrom());

	    PickActivity nextAct = new PickActivity(pickfrom, pickTo, "PICK",
		    onMessages);
	    nextAct.setPick(true);
	    actList.add(nextAct);
	    states.add(pickfrom);
	    states.add(pickTo);
	    order++;
	    constructSequenceFromState(pickTo.toString(), adaptationProcess,
		    actList, order, states, pid, abstracts, concretes);

	}

    }

    private static int getPickToState(ProcessDiagram branch) {

	int finalstate = -1;
	Set<Integer> states = branch.getStates();
	int nstates = states.size();
	for (int i = 0; i < nstates; i++) {
	    boolean isfinal = true;
	    int current = states.iterator().next();
	    for (ProcessActivity act : branch.getActivities()) {
		if (act.getSource() == current) {
		    isfinal = false;
		}
	    }
	    if (isfinal) {
		finalstate = current;
		break;
	    }
	}
	return finalstate;

    }

    private static List<ServiceTransitionGlobal> getTransitionsFromState(
	    String pointer, List<ServiceTransitionGlobal> adaptationProcess) {
	int range = numOFSucc(pointer, adaptationProcess);
	List<ServiceTransitionGlobal> result = new ArrayList<ServiceTransitionGlobal>(
		range);

	for (int i = 0; i < adaptationProcess.size(); i++) {

	    if (adaptationProcess.get(i).getFrom().equals(pointer)) {
		result.add(adaptationProcess.get(i));
	    }
	}
	return result;
    }

    private static boolean multipleTransitionsToState(String pointer,
	    List<ServiceTransitionGlobal> adaptationProcess) {
	int range = numOFPrec(pointer, adaptationProcess);

	boolean result = false;
	if (range > 1) {
	    result = true;
	}

	return result;
    }

    public static int numOFSucc(String pointer,
	    List<ServiceTransitionGlobal> transitions) {
	int result = 0;
	for (int i = 0; i < transitions.size(); i++) {

	    if (transitions.get(i).getFrom().equals(pointer)) {
		result++;
	    }

	}
	return result;
    }

    public static int numOFPrec(String pointer,
	    List<ServiceTransitionGlobal> transitions) {
	int result = 0;
	for (int i = 0; i < transitions.size(); i++) {

	    if (transitions.get(i).getTo().equals(pointer)) {
		result++;
	    }

	}
	return result;
    }

    private static void addActivityInSequence(ServiceTransitionGlobal t,
	    List<ProcessActivity> activityList, int order,
	    List<String> abstracts, List<String> concretes) {

	ProcessActivity act = null;

	act = createActivity(t, abstracts, concretes);

	act.setSid(t.getSid());
	act.setServiceType(t.getServiceType());
	act.setOrder(order);
	act.setSource(Integer.parseInt(t.getFrom()));
	act.setTarget(Integer.parseInt(t.getTo()));
	activityList.add(act);
    }

    public static ProcessActivity createActivity(ServiceTransition transition,
	    List<String> abstracts, List<String> concretes) {

	ProcessActivity act = new ProcessActivity();

	String type = transition.getType().toString();

	boolean isAbstractAction = abstracts.contains(transition.getAction());
	boolean isConcreteAction = concretes.contains(transition.getAction());

	// activity type
	if (type.equals("IN") && !isAbstractAction) {
	    act = new InvokeActivty();
	    act.setSend(true);
	    act.setType(ProcessActivityType.INVOKE);
	    act.setName(transition.getAction());
	} else if (type.equals("IN") && isAbstractAction) {
	    act = new AbstractActivity();
	    act.setAbstract(true);
	    act.setType(ProcessActivityType.ABSTRACT);
	    act.setName(transition.getAction());
	} else if (type.equals("OUT") && isConcreteAction) {
	    act = new ConcreteActivity();
	    act.setConcrete(true);
	    act.setType(ProcessActivityType.CONCRETE);
	    act.setName(transition.getAction());
	}
	if (type.equals("OUT") && !isConcreteAction) {
	    act = new ReplyActivity();
	    act.setReceive(true);
	    act.setType(ProcessActivityType.REPLY);
	    act.setName(transition.getAction());
	}
	return act;

    }

    public void setPid(int pid) {
	if (pid == 34) {
	    System.out.println();
	}
	this.pid = pid;
    }

    public void setActivities(List<ProcessActivity> activities) {
	this.activities = activities;
    }

    public String getName() {
	return name;
    }

    public void addActivity(ProcessActivity act) {
	this.activities.add(act);
    }

    public void addAllActivity(List<ProcessActivity> acts) {
	if (activities == null) {
	    this.activities = new ArrayList<ProcessActivity>();
	}
	this.activities.addAll(acts);
    }

    public void setName(String name) {
	this.name = name;

    }

    public ProcessActivity findProcessActivity(
	    eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d) {
	for (ProcessActivity act : getActivities()) {
	    if (act.getName().equals(d.getProcess().getCurentActivity())) {
		return act;
	    }
	}
	return null;
    }

}
