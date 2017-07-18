package eu.fbk.das.process.engine.impl.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.composer.api.exceptions.InvalidServiceInitialStateException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceTransitionException;
import eu.fbk.das.composer.api.exceptions.ServiceDuplicateActionException;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.ConcreteActivity;
import eu.fbk.das.process.engine.api.domain.DefaultActivity;
import eu.fbk.das.process.engine.api.domain.IFActivity;
import eu.fbk.das.process.engine.api.domain.InvokeActivty;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ObjectTransition;
import eu.fbk.das.process.engine.api.domain.OnMessageActivity;
import eu.fbk.das.process.engine.api.domain.PickActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ReplyActivity;
import eu.fbk.das.process.engine.api.domain.ScopeActivity;
import eu.fbk.das.process.engine.api.domain.ServiceDiagram;
import eu.fbk.das.process.engine.api.domain.ServiceDiagramActionType;
import eu.fbk.das.process.engine.api.domain.ServiceTransition;
import eu.fbk.das.process.engine.api.domain.SwitchActivity;
import eu.fbk.das.process.engine.api.domain.WhileActivity;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectTransitionException;
import eu.fbk.das.process.engine.api.jaxb.AbstractType;
import eu.fbk.das.process.engine.api.jaxb.ActionTypeValues;
import eu.fbk.das.process.engine.api.jaxb.ActivityType;
import eu.fbk.das.process.engine.api.jaxb.ConcreteType;
import eu.fbk.das.process.engine.api.jaxb.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.DomainObject.DomainKnowledge.ExternalDomainProperty;
import eu.fbk.das.process.engine.api.jaxb.DomainObject.DomainKnowledge.InternalDomainProperty;
import eu.fbk.das.process.engine.api.jaxb.DomainProperty;
import eu.fbk.das.process.engine.api.jaxb.DomainPropertyEventType;
import eu.fbk.das.process.engine.api.jaxb.Fragment;
import eu.fbk.das.process.engine.api.jaxb.Fragment.Action;
import eu.fbk.das.process.engine.api.jaxb.Fragment.State;
import eu.fbk.das.process.engine.api.jaxb.Fragment.Transition;
import eu.fbk.das.process.engine.api.jaxb.GoalType;
import eu.fbk.das.process.engine.api.jaxb.InvokeType;
import eu.fbk.das.process.engine.api.jaxb.PickType;
import eu.fbk.das.process.engine.api.jaxb.PreconditionType;
import eu.fbk.das.process.engine.api.jaxb.Process;
import eu.fbk.das.process.engine.api.jaxb.ReceiveType;
import eu.fbk.das.process.engine.api.jaxb.ScopeType;
import eu.fbk.das.process.engine.api.jaxb.SwitchType;
import eu.fbk.das.process.engine.api.jaxb.SwitchType.Default;
import eu.fbk.das.process.engine.api.jaxb.WhileType;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario;

/**
 * Parser utility class for {@link ProcessDiagram}, {@link ProcessActivity} ..
 * 
 */
public class Parser {

	private static final Logger logger = LogManager.getLogger(Parser.class);

	public Parser() {
	}

	public ProcessDiagram convertToProcessDiagram(
			List<ActivityType> activities, String processName)
			throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException {
		ProcessDiagram pd = new ProcessDiagram();
		pd.addAllActivity(parseActivities(activities));
		// TODO: manca il setting delle variabili, per ora non gestite
		pd.setName(processName);
		return pd;
	}

	public List<ProcessActivity> parseActivities(List<ActivityType> activities)
			throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException {
		List<ProcessActivity> result = new ArrayList<ProcessActivity>();
		int a = 0;
		for (ActivityType processActivity : activities) {
			Set<Integer> states = new HashSet<Integer>();
			Integer initialState = 0;
			states.add(initialState);
			// action variables are here considered only for the ReceiveType,
			// the InvokeType and the abstractTpe activities
			if (isReceiveType(processActivity)) {
				eu.fbk.das.process.engine.api.domain.ReplyActivity act = new eu.fbk.das.process.engine.api.domain.ReplyActivity(
						a, a + 1, processActivity.getName());
				act.setReceive(true);
				act.setEffect(processActivity.getEffect());
				act.setPrecondition(processActivity.getPrecondition());
				// act.setActionVariables(processActivity.getActionVariable());
				act.setActionVariables(VariableUtils.cloneList(processActivity
						.getActionVariable()));
				result.add(act);
			}
			if (isInvokeType(processActivity)) {
				eu.fbk.das.process.engine.api.domain.InvokeActivty act = new eu.fbk.das.process.engine.api.domain.InvokeActivty(
						a, a + 1, processActivity.getName());
				act.setSend(true);
				act.setEffect(processActivity.getEffect());
				act.setPrecondition(processActivity.getPrecondition());
				// act.setActionVariables(processActivity.getActionVariable());
				if (act.getName().equalsIgnoreCase("DVM_DataPatternResponse")) {
					System.out.println();
				}
				act.setActionVariables(VariableUtils.cloneList(processActivity
						.getActionVariable()));
				result.add(act);
			}
			if (isSwitchType(processActivity)) {
				ProcessActivity act = this.parseSwitchActivity(processActivity,
						a, states, ProcessEngine.DEFAULT_PID);
				act.setSwitch(true);
				result.add(act);
			}
			if (isConcreteType(processActivity)) {
				ConcreteActivity act = (ConcreteActivity) parseSimpleActivity(
						processActivity, a, states);
				act.setPrecondition(processActivity.getPrecondition());
				act.setEffect(processActivity.getEffect());
				result.add(act);
			}
			if (isPickType(processActivity)) {
				ProcessActivity act = parsePickActivity(processActivity, a,
						states, ProcessEngine.DEFAULT_PID);
				result.add(act);
			}
			if (isAbstractType(processActivity)) {
				ProcessActivity act = parseSimpleActivity(processActivity, a,
						states);
				AbstractActivity absact = (AbstractActivity) act;
				absact.setEffect(processActivity.getEffect());
				absact.setPrecondition(processActivity.getPrecondition());
				// act.setActionVariables(processActivity.getActionVariable());
				act.setActionVariables(VariableUtils.cloneList(processActivity
						.getActionVariable()));
				result.add(act);
			}
			if (isWhileType(processActivity)) {
				ProcessActivity act = parseWhileActivity(processActivity, a,
						states, ProcessEngine.DEFAULT_PID);
				act.setPrecondition(processActivity.getPrecondition());
				act.setEffect(processActivity.getEffect());
				result.add(act);
			}
			if (isScopeType(processActivity)) {
				ProcessActivity act = parseScopeActivity(processActivity, a,
						states, ProcessEngine.DEFAULT_PID);
				act.setPrecondition(processActivity.getPrecondition());
				act.setEffect(processActivity.getEffect());
				result.add(act);
			}
			a++;
		}
		return result;
	}

	private ProcessActivity parseWhileActivity(ActivityType processActivity,
			int sourcest, Set<Integer> states, int ProcessID)
			throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException {
		WhileType wa = (WhileType) processActivity;
		ProcessDiagram defbranch = null;
		if (wa.getActivity() != null) {

			int sourcedef = 0;
			int DefInitialState = sourcedef;
			Set<Integer> DefStates = new HashSet<Integer>();
			DefStates.add(DefInitialState);

			int lastSourcedef = 0;
			List<ProcessActivity> DefbranchActs = new ArrayList<ProcessActivity>();
			for (ActivityType act : wa.getActivity()) {
				ProcessActivity br = null;
				if (isSwitchType(act)) {
					br = this.parseSwitchActivity(act, sourcedef, DefStates,
							ProcessID);
				} else if (isPickType(act)) {
					br = this.parsePickActivity(act, sourcedef, DefStates,
							ProcessID);
				} else if (isWhileType(act)) {
					br = this.parseWhileActivity(act, sourcedef, DefStates,
							ProcessID);
					br.setSource(sourcedef);
					br.setTarget(sourcedef + 1);
				} else if (isScopeType(act)) {
					br = this.parseScopeActivity(act, sourcedef, DefStates,
							ProcessID);
					br.setSource(sourcedef);
					br.setTarget(sourcedef + 1);
				} else {
					br = this.parseSimpleActivity(act, sourcedef, DefStates);
				}
				// common to all activities
				br.setPrecondition(act.getPrecondition());
				br.setEffect(act.getEffect());
				DefbranchActs.add(br);

				lastSourcedef = sourcedef;
				sourcedef++;
				DefStates.add(sourcedef);
			}
			defbranch = new ProcessDiagram(ProcessID, DefStates,
					DefInitialState, DefbranchActs);

		}
		WhileActivity result = new WhileActivity(sourcest, sourcest + 1,
				wa.getName());
		if (wa.getVarCondition() != null) {
			result.setCondition(wa.getVarCondition());
		}
		if (wa.getContextCondition() != null) {
			result.setContextCondition(wa.getContextCondition());
		}
		result.addDefaultBranch(defbranch);

		return result;
	}

	private ProcessActivity parseScopeActivity(ActivityType processActivity,
			int sourcest, Set<Integer> states, int ProcessID)
			throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException {
		ScopeType sa = (ScopeType) processActivity;
		List<ProcessActivity> activities = new ArrayList<ProcessActivity>();

		ScopeActivity result = new ScopeActivity(sourcest, sourcest + 1,
				sa.getName());

		if (result.getName().equals("RM_scope1")) {
			System.out.println();
		}

		if (result.getName().equals("RM_scope2")) {
			System.out.println();
		}

		if (sa.getActivity() != null) {
			int sourcedef = 0;
			int DefInitialState = sourcedef;
			Set<Integer> DefStates = new HashSet<Integer>();
			DefStates.add(DefInitialState);
			int lastSourcedef = 0;

			for (ActivityType act : sa.getActivity()) {
				ProcessActivity br = null;
				if (isSwitchType(act)) {
					br = this.parseSwitchActivity(act, sourcedef, DefStates,
							ProcessID);
				} else if (isPickType(act)) {
					br = this.parsePickActivity(act, sourcedef, DefStates,
							ProcessID);
				} else if (isWhileType(act)) {
					br = this.parseWhileActivity(act, sourcedef, DefStates,
							ProcessID);
					// if (lastSourcedef == 0) {
					// br.setSource(lastSourcedef);
					// br.setTarget(lastSourcedef + 1);
					// } else {
					// br.setSource(lastSourcedef + 1);
					// br.setTarget(lastSourcedef + 2);
					// }
					br.setSource(sourcedef);
					br.setTarget(sourcedef + 1);

				} else if (isScopeType(act)) {
					br = this.parseScopeActivity(act, sourcedef, DefStates,
							ProcessID);
					br.setSource(sourcedef);
					br.setTarget(sourcedef + 1);
				} else {
					br = this.parseSimpleActivity(act, sourcedef, DefStates);
				}
				// common to all activities
				br.setPrecondition(act.getPrecondition());
				br.setEffect(act.getEffect());

				lastSourcedef = sourcedef;
				sourcedef++;
				DefStates.add(sourcedef);
				activities.add(br);
			}
			ProcessDiagram branch = new ProcessDiagram(ProcessID, DefStates,
					DefInitialState, activities);
			result.setBranch(branch);
		}

		result.setEventHandler(sa.getEventHandler());
		return result;
	}

	private boolean isWhileType(ActivityType processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.WhileType");
	}

	public boolean isAbstractType(Object processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.AbstractType");
	}

	public boolean isPickType(Object processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.PickType");
	}

	public boolean isConcreteType(Object processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.ConcreteType");
	}

	public boolean isSwitchType(Object processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.SwitchType");
	}

	public boolean isInvokeType(Object processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.InvokeType");
	}

	public boolean isReceiveType(Object processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.ReceiveType");
	}

	private boolean isScopeType(ActivityType processActivity) {
		return processActivity.getClass().getName()
				.equals("eu.fbk.das.process.engine.api.jaxb.ScopeType");
	}

	public ProcessActivity parseSwitchActivity(java.lang.Object object,
			int sourcest, Set<Integer> states, int ProcessID)
			throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException {

		String name = ((SwitchType) object).getName();
		List<IFActivity> IFs = new ArrayList<IFActivity>();
		DefaultActivity def = new DefaultActivity();

		// Default branch
		Default d = ((SwitchType) object).getDefault();
		List<ActivityType> seqActs = d.getBranch().getActivity();
		int sourcedef = 0;
		int DefInitialState = sourcedef;
		Set<Integer> DefStates = new HashSet<Integer>();
		DefStates.add(DefInitialState);

		List<ProcessActivity> DefbranchActs = new ArrayList<ProcessActivity>();
		for (java.lang.Object act : seqActs) {
			if (isSwitchType(act)) {
				ProcessActivity br = this.parseSwitchActivity(act, sourcedef,
						DefStates, ProcessID);
				DefbranchActs.add(br);

			} else if (isPickType(act)) {
				ProcessActivity br = this.parsePickActivity(act, sourcedef,
						DefStates, ProcessID);
				DefbranchActs.add(br);

			} else {
				ProcessActivity br = this.parseSimpleActivity(act, sourcedef,
						DefStates);
				DefbranchActs.add(br);
			}
			sourcedef++;
			DefStates.add(sourcedef);

		}
		ProcessDiagram Defbranch = new ProcessDiagram(ProcessID, DefStates,
				DefInitialState, DefbranchActs);
		def.setDefaultBranch(Defbranch);

		// IFs management

		// for (int j = 0; j < ((SwitchType) object).getIf().size(); j++) {
		List<SwitchType.If> ListofIFs = ((SwitchType) object).getIf();

		for (SwitchType.If currentIF : ListofIFs) {
			// inside the IF

			int sourceif = 0;
			int IFInitialState = sourceif;
			Set<Integer> ifStates = new HashSet<Integer>();
			ifStates.add(IFInitialState);
			IFActivity ifAct = new IFActivity();

			SwitchType.If.Branch branch = currentIF.getBranch();

			List<PreconditionType> conditions = new ArrayList<PreconditionType>();
			List<SwitchType.If.VarCondition> varConditions = new ArrayList<SwitchType.If.VarCondition>();
			for (java.lang.Object cond : currentIF
					.getContextConditionOrVarCondition()) {
				if (cond.getClass()
						.getName()
						.equals("eu.fbk.das.process.engine.api.composition.jaxb.PreconditionType")) {
					conditions.add((PreconditionType) cond);
				} else if (cond
						.getClass()
						.getName()
						.startsWith(
								"eu.fbk.das.process.engine.api.jaxb.SwitchType")) {
					varConditions.add((SwitchType.If.VarCondition) cond);
				}
			}

			// .equals("eu.fbk.das.process.engine.api.jaxb.SwitchType.If.VarCondition"))
			// {

			List<ProcessActivity> IfBranchActivities = new ArrayList<ProcessActivity>();
			List<ActivityType> activs = branch.getActivity();
			// inside to a single branch
			for (java.lang.Object act : activs) {
				if (act.getClass()
						.getName()
						.equals("eu.fbk.das.process.engine.api.jaxb.SwitchType")) {
					ProcessActivity a = this.parseSwitchActivity(act, sourceif,
							ifStates, ProcessID);
					IfBranchActivities.add(a);

				} else {
					ProcessActivity a = this.parseSimpleActivity(act, sourceif,
							ifStates);
					IfBranchActivities.add(a);
				}
				sourceif++;
				ifStates.add(sourceif);
			}

			ProcessDiagram branchAct = new ProcessDiagram(ProcessID, ifStates,
					IFInitialState, IfBranchActivities);
			ifAct.setBranch(branchAct);
			ifAct.setConditions(conditions);
			ifAct.setVarConditions(varConditions);
			IFs.add(ifAct);
		}

		// }
		SwitchActivity sw = new SwitchActivity(sourcest, sourcest + 1, name,
				IFs, def);
		states.add(sourcest + 1);
		return sw;
	}

	public ProcessActivity parsePickActivity(java.lang.Object object,
			int sourcest, Set<Integer> states, int ProcessID)
			throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException {
		List<OnMessageActivity> OnMsgs = new ArrayList<OnMessageActivity>();

		// OnMsgss management
		List<PickType.OnMessage> ListofOnMsgs = ((PickType) object)
				.getOnMessage();

		for (PickType.OnMessage currentOnMsg : ListofOnMsgs) {
			// inside the onMessage

			int sourceif = 0;
			int OMInitialState = sourceif;
			Set<Integer> OMStates = new HashSet<Integer>();
			OMStates.add(OMInitialState);
			OnMessageActivity OMAct = new OnMessageActivity();
			OMAct.setOnMessage(true);
			OMAct.setName(currentOnMsg.getName());

			List<ProcessActivity> OMBranchActivities = new ArrayList<ProcessActivity>();
			List<ActivityType> activs = currentOnMsg.getActivity();
			// inside to a single branch
			for (java.lang.Object act : activs) {
				if (act.getClass()
						.getName()
						.equals("eu.fbk.das.process.engine.api.jaxb.SwitchType")) {
					ProcessActivity a = this.parseSwitchActivity(act, sourceif,
							OMStates, ProcessID);
					OMBranchActivities.add(a);

				} else if (act.getClass().getName()
						.equals("eu.fbk.das.process.engine.api.jaxb.PickType")) {
					ProcessActivity a = this.parsePickActivity(act, sourceif,
							OMStates, ProcessID);
					OMBranchActivities.add(a);

				} else if (act.getClass().getName()
						.equals("eu.fbk.das.process.engine.api.jaxb.WhileType")) {
					ProcessActivity a = this.parseWhileActivity(
							(ActivityType) act, sourceif, OMStates, ProcessID);
					OMBranchActivities.add(a);

				} else if (act.getClass().getName()
						.equals("eu.fbk.das.process.engine.api.jaxb.ScopeType")) {
					ProcessActivity a = this.parseScopeActivity(
							(ActivityType) act, sourceif, OMStates, ProcessID);
					OMBranchActivities.add(a);
				}

				else {
					ProcessActivity a = this.parseSimpleActivity(act, sourceif,
							OMStates);
					a.setEffect(((ActivityType) act).getEffect());
					a.setPrecondition(((ActivityType) act).getPrecondition());
					OMBranchActivities.add(a);
				}
				sourceif++;
				OMStates.add(sourceif);
			}

			ProcessDiagram branchAct = new ProcessDiagram(ProcessID, OMStates,
					OMInitialState, OMBranchActivities);
			OMAct.setBranch(branchAct);
			OnMsgs.add(OMAct);
		}

		PickActivity pick = new PickActivity(sourcest, sourcest + 1, "PICK",
				OnMsgs);
		pick.setPick(true);
		states.add(sourcest + 1);
		return pick;
	}

	public ProcessActivity parseSimpleActivity(Object object, int sourcest,
			Set<Integer> states) {

		ProcessActivity result = new ProcessActivity();
		if (isReceiveType(object)) {
			String name = ((ReceiveType) object).getName();
			ReplyActivity act = new ReplyActivity(sourcest, sourcest + 1, name);
			sourcest++;
			act.setReceive(true);
			act.setEffect(((ActivityType) object).getEffect());
			act.setPrecondition(((ActivityType) object).getPrecondition());
			act.setActionVariables(VariableUtils
					.cloneList(((ActivityType) object).getActionVariable()));
			states.add(sourcest);
			result = act;
		}

		if (isAbstractType(object)) {
			String name = ((AbstractType) object).getName();
			GoalType goal = ((AbstractType) object).getGoal();
			AbstractActivity act = new AbstractActivity(sourcest, sourcest + 1,
					name, goal);
			if (((AbstractType) object).getType() != null) {
				act.setAbstractType(((AbstractType) object).getType());
				act.setReceiveVar(((AbstractType) object).getReceiveVar());
			}
			act.setAbstract(true);
			sourcest++;
			states.add(sourcest);
			result = act;
		}
		if (isConcreteType(object)) {
			String name = ((ConcreteType) object).getName();
			ConcreteActivity act = new ConcreteActivity(sourcest, sourcest + 1,
					name, ((ConcreteType) object).getReturnsTo());
			act.setConcrete(true);
			result = act;
		}
		if (isInvokeType(object)) {
			String name = ((InvokeType) object).getName();
			InvokeActivty act = new InvokeActivty(sourcest, sourcest + 1, name);
			sourcest++;
			act.setSend(true);
			act.setEffect(((ActivityType) object).getEffect());
			act.setPrecondition(((ActivityType) object).getPrecondition());
			act.setActionVariables(VariableUtils
					.cloneList(((ActivityType) object).getActionVariable()));
			states.add(sourcest);
			result = act;

		}
		return result;
	}

	public DomainObject parseDomainObject(File f) {
		DomainObject e = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(DomainObject.class);
			e = (DomainObject) jaxbContext.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e1) {
			logger.error(e1);
		}
		return e;
	}

	public DomainProperty parseDomainProperties(File f) {
		DomainProperty dp = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(DomainProperty.class);
			dp = (DomainProperty) jaxbContext.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e) {
			logger.error(e);
		}
		return dp;
	}

	public Process parseProcess(File f) {
		Process p = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Process.class);
			p = (Process) jaxbContext.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e) {
			logger.error(e);
		}
		return p;
	}

	public List<ObjectDiagram> convertInternalToObjectDiagrams(
			List<InternalDomainProperty> list, List<String> dirs) {
		if (list == null) {
			return null;
		}
		try {
			List<ObjectDiagram> result = new ArrayList<ObjectDiagram>();
			// internal
			for (InternalDomainProperty dp : list) {
				File f = FileUtil.findFile(dp.getName(), dirs);
				if (f != null) {
					DomainProperty d = parseDomainProperties(f);
					result.add(convertToObjectDiagram(d));
				}
			}
			return result;
		} catch (InvalidObjectInitialStateException
				| InvalidObjectTransitionException
				| InvalidObjectCurrentStateException e) {
			logger.error(e);
		}

		return null;
	}

	public List<ObjectDiagram> convertExternalToObjectDiagrams(
			List<ExternalDomainProperty> list, List<String> dirs) {
		if (list == null) {
			return null;
		}
		try {
			List<ObjectDiagram> result = new ArrayList<ObjectDiagram>();
			// external
			for (ExternalDomainProperty dp : list) {
				File f = FileUtil.findFile(dp.getName(), dirs);
				if (f != null) {
					DomainProperty d = parseDomainProperties(f);
					result.add(convertToObjectDiagram(d));
				}
			}
			return result;
		} catch (InvalidObjectInitialStateException
				| InvalidObjectTransitionException
				| InvalidObjectCurrentStateException e) {
			logger.error(e);
		}
		return null;
	}

	public ObjectDiagram convertToObjectDiagram(DomainProperty dp)
			throws InvalidObjectInitialStateException,
			InvalidObjectTransitionException,
			InvalidObjectCurrentStateException {

		String initial = "";
		Set<String> initialStates = new HashSet<String>();
		Set<String> states = new HashSet<String>();
		for (DomainProperty.State st : dp.getState()) {
			states.add(st.getValue());
			if (st != null && st.isIsInitial() != null && st.isIsInitial()) {
				initialStates.add(st.getValue());
				initial = st.getValue();
			}
		}
		Set<String> events = new HashSet<String>();
		for (DomainPropertyEventType e : dp.getEvent()) {
			events.add(e.getValue());
		}
		Set<ObjectTransition> transitions = new HashSet<ObjectTransition>();
		for (DomainProperty.Transition t : dp.getTransition()) {
			ObjectTransition ot = new ObjectTransition(t.getFrom().getValue(),
					t.getTo().getValue(), t.getEvent().getValue());
			transitions.add(ot);
		}
		ObjectDiagram od = new ObjectDiagram(dp.getId(), dp.getId(), states,
				initialStates, events, transitions);
		od.setCurrentState(initial);
		return od;
	}

	public eu.fbk.das.process.engine.api.jaxb.scenario.Scenario parse(File f) {
		Scenario s = null;
		try {
			s = (Scenario) JAXBContext.newInstance(Scenario.class)
					.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return s;
	}

	public DomainObject parseDomainObject(URL f) {
		DomainObject e = null;
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance(DomainObject.class);
			e = (DomainObject) jaxbContext.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e1) {
			logger.error(e1);
		}
		return e;
	}

	public Process parseProcess(URL f) {
		Process p = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Process.class);
			p = (Process) jaxbContext.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e) {
			logger.error(e);
		}
		return p;
	}

	public Fragment parseFragment(File f) {
		Fragment frag = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Fragment.class);
			frag = (Fragment) jaxbContext.createUnmarshaller().unmarshal(f);
		} catch (JAXBException e) {
			logger.error(e);
		}
		return frag;
	}

	public List<ServiceDiagram> convertToServiceDiagram(List<Fragment> fragments)
			throws InvalidServiceInitialStateException,
			InvalidServiceTransitionException, ServiceDuplicateActionException {
		List<ServiceDiagram> response = new ArrayList<ServiceDiagram>();
		for (Fragment f : fragments) {
			Set<String> initialStates = new HashSet<String>();
			for (State s : f.getState()) {
				if (s.isIsInitial() != null && s.isIsInitial()) {
					initialStates.add(s.getName());
				}
			}
			// Set setA = new HashMap(String, List<String>);
			Set<String> inputs = new HashSet<String>();
			Set<String> outputs = new HashSet<String>();
			Set<String> abstracts = new HashSet<String>();
			Set<String> concretes = new HashSet<String>();
			for (Action act : f.getAction()) {
				if (act.getActionType() == ActionTypeValues.INPUT) {
					inputs.add(act.getName());
				}
				if (act.getActionType() == ActionTypeValues.OUTPUT) {
					outputs.add(act.getName());
				}
				if (act.getActionType() == ActionTypeValues.CONCRETE) {
					concretes.add(act.getName());
					outputs.add(act.getName());
				}
				if (act.getActionType() == ActionTypeValues.ABSTRACT) {
					abstracts.add(act.getName());
					inputs.add(act.getName());
				}

			}
			Set<ServiceTransition> transitions = new HashSet<ServiceTransition>();
			for (Transition t : f.getTransition()) {
				ServiceTransition st = new ServiceTransition(t
						.getInitialState().getValue(), t.getFinalState()
						.getValue(), t.getAction().getName(),
						getServiceDiagramActionType(t.getAction().getName(),
								f.getAction()));
				transitions.add(st);
			}
			Set<String> states = new HashSet<String>();
			for (State s : f.getState()) {
				states.add(s.getName());
			}

			ServiceDiagram service = new ServiceDiagram(f.getId(), null,
					f.getConsumerEntityType(), states, initialStates, inputs,
					outputs, abstracts, concretes, transitions, false);

			response.add(service);
		}
		return response;
	}

	// private ProcessDiagram convertToProcessDiagram(ServiceDiagram
	// seviceDiagram) {
	// ProcessDiagram procDiag = new ProcessDiagram();
	// return procDiag;
	// }

	private ServiceDiagramActionType getServiceDiagramActionType(String name,
			List<Action> action) {
		for (Action act : action) {
			if (act.getName().equals(name)) {
				switch (act.getActionType()) {
				case ABSTRACT:
					return ServiceDiagramActionType.IN;
				case INPUT:
					return ServiceDiagramActionType.IN;
				case OUTPUT:
					return ServiceDiagramActionType.OUT;
				case CONCRETE:
					return ServiceDiagramActionType.OUT;
				default:
					return ServiceDiagramActionType.IN;
				}
			}
		}
		return ServiceDiagramActionType.IN;
	}

	// public ServiceDiagram convertToObjectDiagrams(List<DomainProperty>
	// property) {
	//
	// private ObjectDiagram convertToObjectDiagram(DomainProperty dp)
	//
	// return null;
	// }

}
