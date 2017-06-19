package eu.fbk.das.process.engine.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import com.google.common.collect.ArrayListMultimap;

import eu.fbk.das.adaptation.api.AdaptationManagerInterface;
import eu.fbk.das.domainobject.executable.test.TestServicesSingleton;
//import eu.fbk.das.domainobject.executable.test.TestRefinementSingleton;
//import eu.fbk.das.adaptation.api.AdaptationManagerInterface;
import eu.fbk.das.process.engine.api.AbstractExecutableActivityInterface;
import eu.fbk.das.process.engine.api.AdaptationProblem;
import eu.fbk.das.process.engine.api.AdaptationResult;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.ExecutableActivityInterface;
import eu.fbk.das.process.engine.api.ProcVar;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.ProcessRequest;
import eu.fbk.das.process.engine.api.ScopeManager;
import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivityType;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ServiceDiagram;
import eu.fbk.das.process.engine.api.domain.WhileActivity;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectEventException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.ClauseType;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point.DomainProperty;
import eu.fbk.das.process.engine.api.jaxb.EffectType;
import eu.fbk.das.process.engine.api.jaxb.EffectType.Event;
import eu.fbk.das.process.engine.api.jaxb.GoalType;
import eu.fbk.das.process.engine.api.jaxb.VariableType;
import eu.fbk.das.process.engine.impl.handlers.AbstractActivityHandler;
import eu.fbk.das.process.engine.impl.handlers.ConcreteActivityHandler;
import eu.fbk.das.process.engine.impl.handlers.Handler;
import eu.fbk.das.process.engine.impl.handlers.InvokeActivityHandler;
import eu.fbk.das.process.engine.impl.handlers.PickActivityHandler;
import eu.fbk.das.process.engine.impl.handlers.ReplyActivityHandler;
import eu.fbk.das.process.engine.impl.handlers.ScopeActivityHandler;
import eu.fbk.das.process.engine.impl.handlers.SwitchActivityHandler;
import eu.fbk.das.process.engine.impl.handlers.WhileActivityHandler;

public class ProcessEngineImpl implements ProcessEngine {

	private static final Logger logger = LogManager
			.getLogger(ProcessEngineImpl.class);

	public int journeySegmentsNumber = 0;

	private Map<Integer, ProcessDiagram> processes = new HashMap<Integer, ProcessDiagram>();

	private ArrayListMultimap<DomainObjectInstance, Map<String, List<VariableType>>> msgQueue = ArrayListMultimap
			.create();

	public HashMap<Integer, List<ProcessDiagram>> runningBranches = new HashMap<Integer, List<ProcessDiagram>>();

	private static int pidCounter = 1;

	private Map<ProcessActivityType, Handler> handlers = new HashMap<ProcessActivityType, Handler>();

	private Map<ProcessDiagram, List<ProcVar>> processVarMap = new HashMap<ProcessDiagram, List<ProcVar>>();

	private List<Integer> runninBranchesToRemove = new ArrayList<Integer>();

	private DomainObjectManagerInterface dom;

	private CorrelationManager cm;

	private Map<ProcessDiagram, DomainObjectInstance> processToDoi = new HashMap<ProcessDiagram, DomainObjectInstance>();

	/* mapping pid -- adaptation problem for vertical and horizontal adaptation */
	private ArrayListMultimap<Integer, String> aWaitingList = ArrayListMultimap
			.create();

	private AdaptationManagerInterface adaptationManager;

	private String workingFolder;

	private ExecutableHandlerManager ehm;

	private RefinementManager rm;

	private ProcessRequestManager prm;

	private ScopeManager scopeManager;

	private TestServicesSingleton testServices = TestServicesSingleton
			.getInstance();;

	private Map<String, ArrayList<?>> r2rAlternativesMap = new HashMap<String, ArrayList<?>>();
	private Map<String, ArrayList<?>> viaggiaAlternativesMap = new HashMap<String, ArrayList<?>>();

	public ProcessEngineImpl(DomainObjectManagerInterface em,
			AdaptationManagerInterface am, String folder) {
		this.dom = em;
		this.adaptationManager = am;
		this.workingFolder = folder;
		this.ehm = new ExecutableHandlerManager();
		this.rm = new RefinementManager(this);
		this.prm = new ProcessRequestManager(this, dom);
		this.cm = new CorrelationManager(this);
		register(ProcessActivityType.INVOKE, new InvokeActivityHandler());
		register(ProcessActivityType.REPLY, new ReplyActivityHandler());
		register(ProcessActivityType.SWITCH, new SwitchActivityHandler());
		register(ProcessActivityType.CONCRETE, new ConcreteActivityHandler());
		register(ProcessActivityType.PICK, new PickActivityHandler());
		register(ProcessActivityType.ABSTRACT, new AbstractActivityHandler());
		register(ProcessActivityType.WHILE, new WhileActivityHandler());
		register(ProcessActivityType.SCOPE, new ScopeActivityHandler());
		removeAndCreateCompositionsFolder();

		testServices = TestServicesSingleton.getInstance();
	}

	/**
	 * Register handler for a given type
	 * 
	 * @param type
	 * @param handler
	 */
	private void register(ProcessActivityType type, Handler handler) {
		handlers.put(type, handler);
	}

	@Override
	public void remove(ProcessDiagram process) {
		if (process != null && processes.containsKey(process.getpid())) {

			// stampa lo stato dopo l'esecuzione del processo, subito prima di
			// rimuoverlo
			DomainObjectInstance curentDoi = this
					.getDomainObjectInstance(process);
			List<VariableType> state;
			if (curentDoi.getState() != null) {
				state = curentDoi.getState().getStateVariable();
				logger.warn("AFTER EXECUTION STATE OF THE DO: "
						+ curentDoi.getId() + " = ");
				for (VariableType var : state) {
					Element e = (Element) (var.getContent());
					if (e.getFirstChild() != null) {
						String value = e.getFirstChild().getNodeValue();
						logger.warn("Variable: " + var.getName() + " = "
								+ value);
					} else {
						logger.warn("Variable: " + var.getName() + " = " + "");
					}
				}
			}
			// clean the DO state before removing its process
			// DomainObjectInstance doi = this.getDomainObjectInstance(process);
			// DomainObjectDefinition dod = this
			// .getDomainObjectDefinition(process);
			// State s = dod.getState();
			// List<VariableType> modelVar = new ArrayList<VariableType>();
			// if (s != null) {
			// modelVar = s.getStateVariable();
			// }
			// for (int i = 0; i < modelVar.size(); i++) {
			// Element var = DocumentVariableUtils.newElement(modelVar.get(i)
			// .getName(), "");
			// modelVar.get(i).setContent(var);
			// }
			// State cleanedState = new State();
			// cleanedState.getStateVariable().addAll(modelVar);
			// doi.setState(cleanedState);
			//
			// // RESET INTERNAL KNOWLEDGE
			// ObjectDiagram internalKnowledge =
			// doi.getInternalKnowledge().get(0);
			// try {
			// internalKnowledge.setCurrentState("INITIAL");
			// } catch (InvalidObjectCurrentStateException e) {
			// e.printStackTrace();
			// }
			// // RESET EXTERNAL KNOWLEDGE
			// ListIterator<ObjectDiagram> it = doi.getExternalKnowledge()
			// .listIterator();
			// while (it.hasNext()) {
			// ObjectDiagram od = it.next();
			// try {
			// od.setCurrentState("INITIAL");
			// } catch (InvalidObjectCurrentStateException e) {
			// e.printStackTrace();
			// }
			// }

			// DomainObjectInstance doi = this.getDomainObjectInstance(process);
			processes.remove(process.getpid());
			dom.remove(process);
			/*************************************************/
			// deleting corelates reises synchronization exception
			// if (process.getName().contains("PROC_")) {
			// if (cm.hasCorrelation(doi)) {
			// List<DomainObjectInstance> correlates = Collections
			// .synchronizedList(cm.get(doi));
			// // correlates = cm.get(doi);
			// for (DomainObjectInstance c : correlates) {
			// cm.remove(doi, c);
			// }
			// }
			// // dom.getInstances().remove(doi);
			// }
			/*************************************************/

			// TEST
			// if (doi.getId().contains("ViaggiaTrento")) {
			// // this gives the corelates of the DO doi
			// List<DomainObjectInstance> correlates = cm.get(doi);
			// // ListIterator<DomainObjectInstance> it = correlates
			// // .listIterator();
			// // while (it.hasNext()) {
			// for (int i = 0; i < correlates.size(); i++) {
			// // ripulire la proprietà esterna che mi lega al co-relato
			// // che
			// // sto per eliminare
			// // DomainObjectInstance corr = it.next();
			// DomainObjectInstance corr = correlates.get(i);
			// if (corr != null) {
			// ListIterator<ObjectDiagram> iter = corr
			// .getExternalKnowledge().listIterator();
			// boolean found = false;
			// while (iter.hasNext() && !found) {
			// ObjectDiagram od = iter.next();
			// int index = corr.getExternalKnowledge().indexOf(od);
			// if (od.getOid().equals(
			// doi.getInternalKnowledge().get(0).getOid())) {
			// // try {
			// // corr.getExternalKnowledge().get(index)
			// // .setCurrentState("INITIAL");
			// // } catch (InvalidObjectCurrentStateException
			// // e) {
			// // e.printStackTrace();
			// // }
			// // corr.getExternalKnowledge().remove(index);
			// // cm.remove(doi, corr);
			// // cm.remove(corr, doi);
			// // found = true;
			// }
			// }
			// }
			// }
			// }

			logger.debug("Process with pid " + process.getpid() + " removed");
		} else {
			logger.warn("Process null cannot be removed");
		}
	}

	@Override
	public void removeAll() {
		processes.clear();
		dom.clearProcessDefinition();
		logger.debug("all processes removed");
	}

	/**
	 * From given {@link DomainObjectInstance} start corresponding
	 * {@link ProcessDiagram}, with a new process id (pid) and put inside
	 * process engine running's processes
	 * 
	 * @param doi
	 *            - {@link DomainObjectInstance}
	 * @return pid of running {@link ProcessDiagram} or -1 if error
	 */
	@Override
	public int start(DomainObjectInstance doi)
			throws ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		if (doi == null || doi.getProcess() == null) {
			logger.warn("Input not valid, impossible to start a process");
			return -1;
		}
		logger.info("Starting process");

		ProcessDiagram process = doi.getProcess();
		process.setPid(getPid());
		process.setRunning(true);
		process.setEnded(false);
		process.setTerminated(false);
		process.setCurrentActivity(process.getActivities().get(0));
		for (ProcessActivity act : process.getActivities()) {
			act.setExecuted(false);
			act.setRunning(false);
			act.setStopped(false);
		}
		// e lo aggiungo alla lista dei processi da eseguire
		processes.put(process.getpid(), process);
		processToDoi.put(process, doi);
		if (!dom.getInstances().contains(doi)) {
			dom.getInstances().add(doi);
		}
		dom.registerProcessToDoi(doi);
		return process.getpid();
	}

	@Override
	public int getPid() {
		return ++pidCounter;
	}

	@Override
	public void executeActivity(ProcessDiagram proc) {
		if (!proc.isRunning()) {
			logger.debug("Impossible running the process with pid "
					+ proc.getpid() + " because it is terminated.");
			remove(proc);
			return;
		}

		if (proc.getCurrentActivity().isExecuted()) {
			// logica per passare alla prossima attivita'
			if (!proc.getNextActivity().isEmpty()
					&& proc.getCurrentActivity().isExecuted()) {
				proc.setCurrentActivity(proc.getNextActivity().get(0));
			} else if (proc.getNextActivity().isEmpty()
					&& proc.getCurrentActivity().isExecuted()) {
				logger.debug("[" + proc.getpid() + "] process with pid "
						+ proc.getpid() + " terminated");
				proc.setEnded(true);
			}
		}
		if (proc.isEnded()) {
			return;
		}

		// check eventhandlers in scope
		// if (current.getScope() != null && scopeManager != null) {
		// // disabled for now
		// // scopeManager.handle(proc, current, current.getScope());
		// }

		Handler handler = handlers.get(proc.getCurrentActivity().getType());
		if (handler != null) {
			handler.handle(this, proc, proc.getCurrentActivity());
		} else {
			logger.error("Errore, handler non trovato per il tipo di attivita' "
					+ proc.getCurrentActivity().getType());
		}
		// logica per passare alla prossima attivita'
		if (!proc.getNextActivity().isEmpty()
				&& proc.getCurrentActivity().isExecuted()) {
			proc.setCurrentActivity(proc.getNextActivity().get(0));
		} else if (proc.getNextActivity().isEmpty()
				&& proc.getCurrentActivity().isExecuted()) {
			logger.debug("[" + proc.getpid() + "] process with pid "
					+ proc.getpid() + " terminated");
			proc.setEnded(true);
		}
	}

	@Override
	public void step(int pid) {
		ProcessDiagram process = find(pid);
		if (getRunningBranch(pid) != null) {
			ProcessDiagram branch = getRunningBranch(pid);
			if (branch.isRunning()) {
				executeActivity(branch);
			}
			if (branch.getEnded()) {
				// setProcessAsExecuted(branch);
				runninBranchesToRemove.add(branch.getpid());
				ProcessDiagram father = branch.getFather();
				father.setRunning(true);
				if (father.getCurrentActivity().isPick()) {
					if (father.getNextActivity().isEmpty()) {
						father.getCurrentActivity().setExecuted(true);
						father.setEnded(true);
						father.setTerminated(true);
						father.setRunning(false);
					}
				}
				if (father.getCurrentActivity().isWhile()) {
					WhileActivity act = (WhileActivity) father
							.getCurrentActivity();
					boolean ContextEval = true;
					ClauseType varCon = act.getContextCondition();
					if (varCon != null && !checkContext(father, varCon)) {
						ContextEval = false;
					}
					if (ContextEval) {
						branch.setEnded(false);
						branch.setTerminated(false);
						// reset del branch
						for (ProcessActivity a : branch.getActivities()) {
							a.setExecuted(false);
						}
						branch.setCurrentActivity(branch.getFirstActivity());
					} else {
						branch.setRunning(false);
						branch.setEnded(true);
						branch.setTerminated(true);
						// father.getCurrentActivity().setExecuted(true);
					}
					return;
				}
				if (father.getCurrentActivity().isSwitch()) {
					branch.setEnded(true);
					branch.setTerminated(true);
					// branch.setRunning(false);
					if (!father.getNextActivity().isEmpty()) {
						father.getCurrentActivity().setExecuted(true);
						father.setEnded(false);
						father.setTerminated(false);
						father.setRunning(true);
						father.setCurrentActivity(father.getNextActivity().get(
								0));
						// this.executeActivity(father);
					}
					return;
				} else {
					father.getCurrentActivity().setExecuted(true);

					if (!father.getNextActivity().isEmpty()) {
						father.setCurrentActivity(father.getNextActivity().get(
								0));
					} else {
						father.setEnded(true);
					}
				}
			}
			return;
		}
		if (rm.getRefinement(pid) != null) {
			rm.executeRefinement(pid);
			if (!process.isRunning()) {
				if (process.isEnded() && !process.isRunning()) {
					if (runningBranches.containsKey(process.getpid())) {
						runninBranchesToRemove.add(process.getpid());
					}
				}
				return;
			}
		}

		if (!process.isRunning() && !process.isEnded()
				&& rm.getRefinement(process.getpid()) == null
				&& hasRunningBranch(process.getpid())) {
			// logger.debug("[" + pid + "] Processo non partito, step fallito");
			return;
		}
		if (!process.isRunning() && process.isEnded() && process.isTerminated()) {
			return;
		}
		executeActivity(process);
	}

	private void removeRunningBranch(List<Integer> toRemove) {
		if (toRemove == null) {
			return;
		}
		for (Integer key : toRemove) {
			if (runningBranches.containsKey(key)) {
				for (ProcessDiagram p : runningBranches.get(key)) {
					// setProcessAsExecuted(p);
					runningBranches.remove(key);
				}
			}
		}
	}

	@Override
	public void applyEffectForAbstractActivity(ProcessDiagram father) {
		Handler handler = handlers.get(father.getCurrentActivity().getType());
		if (handler != null) {
			handler.handleEffect(this, father, father.getCurrentActivity());
		}
	}

	@Override
	public void end(int pid) {
		ProcessDiagram p = find(pid);
		if (p != null) {
			// setProcessAsExecuted(p);
			p.setEnded(true);
			logger.debug("Process ended");
		} else {
			logger.warn("Impossible to end a process with pid " + pid
					+ ", pid not found");
		}
	}

	// private void setProcessAsExecuted(ProcessDiagram p) {
	// for (ProcessActivity a : p.getActivities()) {
	// a.setExecuted(true);
	// }
	// } prova Martins per rimuovere attività che resta bianca

	// @Override
	// public void createMessage(String requestName, DomainObjectInstance doi) {
	// msgQueue.put(doi, requestName);
	// }

	@Override
	public void createMessage(DomainObjectInstance doi,
			Map<String, List<VariableType>> msgAndVariables) {
		msgQueue.put(doi, msgAndVariables);
	}

	@Override
	public void stepAll() {
		try {
			prm.manageProcessRequests();
		} catch (ProcessEngineRuntimeException
				| InvalidFlowInitialStateException
				| InvalidFlowActivityException | FlowDuplicateActivityException e) {
			logger.error(e.getMessage(), e);
		}
		// eseguo tutti i branch
		try {
			runningBranches.keySet().forEach(pid -> step(pid));
		} catch (ConcurrentModificationException cme) {
			// logger.error(cme.getMessage(), cme);
		}
		// rimuovo i branch eseguiti
		removeRunningBranch(runninBranchesToRemove);

		try {
			// eseguo gli step per ogni pid
			processes.keySet().forEach(pid -> step(pid));
		} catch (ConcurrentModificationException e) {
			// logger.error(e.getMessage(), e);
		}
		// rimuovere i processi eseguiti
		processes.values().stream().filter(p -> p.isEnded())
				.collect(Collectors.toCollection(ArrayList::new))
				.forEach(p -> remove(p));

	}

	private void removeAndCreateCompositionsFolder() {
		try {
			String folder = workingFolder
					+ System.getProperty("file.separator") + "Compositions";
			File dir = new File(folder);
			if (dir.exists()) {
				FileUtils.forceDelete(dir);
				logger.debug("Directory " + dir + " deleted");
			}
			FileUtils.forceMkdir(new File(folder));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public ProcessDiagram find(int pid) {
		ProcessDiagram process = processes.get(Integer.valueOf(pid));
		if (process == null) {
			if (!rm.isEmpty()) {
				List<ProcessDiagram> proc = rm.find(pid);
				if (proc != null && !proc.isEmpty()) {
					return proc.get(0);
				}
			}
			if (runningBranches != null) {
				for (List<ProcessDiagram> processes : runningBranches.values()) {
					List<ProcessDiagram> values = processes.stream()
							.filter(p -> p.getpid() == pid)
							.collect(Collectors.toCollection(ArrayList::new));
					if (values != null && !values.isEmpty()) {
						return values.get(0);
					}
				}
			}
			if (rm != null) {
				ProcessDiagram ref = rm.get(pid);
				if (ref != null) {
					return ref;
				}
				return new ProcessDiagram();

			}
			return new ProcessDiagram();
		}
		return process;
	}

	// @Override
	// public List<String> getMsg(DomainObjectInstance doi) {
	// return msgQueue.get(doi);
	// }

	@Override
	public List<Map<String, List<VariableType>>> getMsgAndVariables(
			DomainObjectInstance doi) {
		return msgQueue.get(doi);
	}

	// @Override
	// public void removeMessage(DomainObjectInstance doi, String msg) {
	// msgQueue.remove(doi, msg);
	// }

	@Override
	public void removeMessageAndVariables(DomainObjectInstance doi,
			Map<String, List<VariableType>> msgAndVariables) {
		msgQueue.remove(doi, msgAndVariables);
	}

	@Override
	public void addRunningBranch(int processId, ProcessDiagram branch) {
		if (!this.runningBranches.containsKey(processId)) {
			List<ProcessDiagram> branchList = new ArrayList<ProcessDiagram>();
			branchList.add(branch);
			this.runningBranches.put(processId, branchList);
		} else {
			this.runningBranches.get(processId).add(branch);
		}
	}

	private ProcessDiagram getRunningBranch(int processId) {
		ProcessDiagram branch = null;
		if (this.runningBranches.containsKey(processId)) {
			for (ProcessDiagram pd : this.runningBranches.get(processId)) {
				if (pd.isRunning()) {
					branch = pd;
					break;
				}
			}
		}
		return branch;
	}

	@Override
	public boolean hasRunningBranch(int fatherId) {
		Iterator<Integer> iter = runningBranches.keySet().iterator();
		while (iter.hasNext()) {
			Integer k = iter.next();
			List<ProcessDiagram> values = runningBranches.get(k);
			if (values != null) {
				for (ProcessDiagram pd : values) {
					if (pd.getFather() != null
							&& pd.getFather().getpid() == fatherId) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void addProcVar(ProcessDiagram proc, String varName, String varValue) {
		ProcVar var = new ProcVar(varName, varValue);
		if (processVarMap.containsKey(proc)) {
			processVarMap.get(proc).add(var);
		} else {
			List<ProcVar> varList = new ArrayList<ProcVar>();
			varList.add(var);
			processVarMap.put(proc, varList);
		}
	}

	@Override
	public boolean checkVarCondition(String varName, String value,
			ProcessDiagram proc) {
		boolean result = false;
		if (this.processVarMap.containsKey(proc)) {
			boolean found = false;
			for (ProcVar var : this.processVarMap.get(proc)) {
				if (var.getName().equals(varName)) {
					if (var.getValue().equals(value)) {
						result = true;
					}
					found = true;
					break;
				}
			}
			if (!found) {
				logger.warn("variable " + varName
						+ " not initialized for process " + proc.getpid());
			}
		} else {
			logger.warn("ERROR: no variable is initialized for process "
					+ proc.getpid());
		}
		return result;
	}

	/**
	 * Return number of running processes
	 */
	@Override
	public int size() {
		return processes == null ? 0 : processes.size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0 ? true : false;
	}

	@Override
	public boolean checkPrecondition(ProcessDiagram proc,
			ProcessActivity current) throws NullPointerException {
		if (proc == null || current == null) {
			throw new NullPointerException("Input must be not null");
		}
		logger.debug("Check precondition for activity: " + current.getType());
		// le attivita' che hanno precondizioni sono tutte tranne pick e
		// switch, quindi filtro quelle
		if (current.getType() == ProcessActivityType.PICK
				|| current.getType() == ProcessActivityType.SWITCH) {
			logger.debug("Precondizioni per PICK o SWITCH, non presenti, sempre vero");
			return true;
		}
		if (current.getPrecondition() == null) {
			logger.debug("No preconditions to check");
			return true;
		}
		// controllo se istanza domainObject presente
		DomainObjectInstance doi = findDoi(proc);
		if (doi == null) {
			logger.debug("Impossibile controllare le precondizioni, definizione per il processo con pid "
					+ proc.getpid() + " non trovata");
			return true;
		}
		List<ObjectDiagram> domainKnowledge = new ArrayList<ObjectDiagram>();
		if (proc.getFather() != null) {
			if (rm.get(proc.getFather().getpid()) != null
					&& proc.getFather().getCurrentActivity() != null
					&& !proc.getFather().getCurrentActivity().isScope()) {
				domainKnowledge = doi.getExternalKnowledge();
			} else if (getRunningBranch(proc.getpid()) != null) {
				domainKnowledge = doi.getInternalKnowledge();
			} else if (proc.getFather().getCurrentActivity().isScope()) {
				domainKnowledge = doi.getInternalKnowledge();
			}
		} else {
			domainKnowledge = doi.getInternalKnowledge();
		}
		// check preconditions
		boolean result = false;
		for (Point point : current.getPrecondition().getPoint()) {
			for (eu.fbk.das.process.engine.api.jaxb.ClauseType.Point.DomainProperty dp : point
					.getDomainProperty()) {
				for (ObjectDiagram domainProperty : domainKnowledge) {
					if (domainProperty.getOid().equals(dp.getDpName())) {
						if (fsmInState(dp, domainProperty)) {
							result = true;
						}
					}
				}
			}
		}
		logger.debug("Precondition " + result);
		return result;
	}

	private DomainObjectInstance findDoi(ProcessDiagram proc) {
		DomainObjectInstance doi = dom.findInstance(proc.getpid());
		if (doi != null) {
			return doi;
		} else if (proc.getFather() != null) {
			return findDoi(proc.getFather());
		}
		return null;
	}

	private boolean fsmInState(DomainProperty dp, ObjectDiagram domainProperty) {
		boolean result = false;
		for (String state : dp.getState()) {
			if (domainProperty.getCurrentState().equals(state)) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public void addRequest(ProcessRequest pr) {
		prm.addRequest(pr);
	}

	@Override
	public void applyEffect(ProcessDiagram proc, EffectType effect) {
		// TODO for storyboard first, cablato ?
		// <tns1:event dpName="HandleRoute"
		// eventName="createRoute"></tns1:event>
		if (effect != null && effect.getEvent() != null
				&& !effect.getEvent().isEmpty()) {
			Event event = effect.getEvent().get(0);
			if (event.getDpName().equals("HandleRoute")
					&& event.getEventName().equals("createRoute")) {
				DomainObjectInstance callerDoi = getDomainObjectInstance(proc);
				String callerEnsemble = callerDoi.getEnsemble();
				List<DomainObjectInstance> dois = findAllDomainObjectByTypeFilterByEnsemble(
						"FlexibusCompany", callerEnsemble);
				for (DomainObjectInstance doi : dois) {
					ProcessDiagram pp = null;
					processToDoi.forEach((p, d) -> {
						if (d.getId().equals(doi.getId())) {
							applyEffect(doi.getExternalKnowledge(), event, p);
						}
					});

				}
			}
		}
		DomainObjectInstance doi = findDoi(proc);
		if (doi == null) {
			logger.warn("Impossibile controllare gli effetti, definizione per il processo con pid "
					+ proc.getpid() + " non trovata");
			return;
		}
		List<ObjectDiagram> domainKnowledge = new ArrayList<ObjectDiagram>();
		if (proc.getCurrentActivity().isAbstract()) {
			// NOTA: solo nel caso in cui aggiungiamo un effetto su un'attivita'
			// astratta allora l'effetto andra' sulla knowledge esterna
			domainKnowledge = doi.getExternalKnowledge();
		} else {
			domainKnowledge = doi.getInternalKnowledge();
		}
		if (proc.getFather() != null) {
			if (rm.get(proc.getFather().getpid()) != null
					&& !proc.getFather().getCurrentActivity().isScope()) {
				domainKnowledge = doi.getExternalKnowledge();
			}
		}
		// cambia la knowledge sull'istanza del domain object
		for (Event e : effect.getEvent()) {
			applyEffect(domainKnowledge, e, proc);
		}

	}

	private List<DomainObjectInstance> findAllDomainObjectByTypeFilterByEnsemble(
			String type, String callerEnsemble) {
		List<DomainObjectInstance> result = new ArrayList<DomainObjectInstance>();
		List<DomainObjectInstance> dois = findAllDomainObjectByType(type);
		for (DomainObjectInstance doi : dois) {
			List<DomainObjectInstance> corrs = getCorrelated(doi);
			for (DomainObjectInstance corr : corrs) {
				if (corr.getEnsemble() != null) {
					if (corr.getEnsemble().equals(callerEnsemble)) {
						if (!result.contains(doi)) {
							result.add(doi);
						}
					}
				}
			}
		}
		return result;
	}

	private void applyEffect(List<ObjectDiagram> knowledge, Event e,
			ProcessDiagram proc) {
		for (ObjectDiagram dp : knowledge) {
			if (dp.getOid().equals(e.getDpName())) {
				try {
					logger.debug("[" + proc.getpid()
							+ "] Prima applicazione effetto "
							+ dp.getCurrentState());

					dp.publishEvent(e.getEventName());
					logger.debug("[" + proc.getpid()
							+ "] Dopo applicazione effetto "
							+ dp.getCurrentState());
				} catch (InvalidObjectEventException
						| InvalidObjectCurrentStateException e1) {
					logger.error(e1);
				}
			}
		}
	}

	@Override
	public boolean isWaiting(Integer pid, String id) {
		if (id == null || pid == null) {
			return false;
		}
		if (!aWaitingList.containsKey(pid)) {
			return false;
		}
		return aWaitingList.get(pid).contains(id);
	}

	@Override
	public void setWaiting(Integer pid, String id) {
		if (!aWaitingList.containsKey(pid)) {
			aWaitingList.put(pid, id);
		} else {
			List<String> values = aWaitingList.get(pid);
			if (!values.contains(id)) {
				aWaitingList.put(pid, id);
			}
		}
	}

	@Override
	public String submitProblem(AdaptationProblem problem) {
		return adaptationManager.submitProblem(problem, workingFolder);
	}

	@Override
	public AdaptationResult getAdaptationResult(ProcessDiagram proc, String id) {
		return adaptationManager.getResult(id);
	}

	@Override
	public void removeWaiting(Integer pid, String id) {
		if (aWaitingList.containsKey(pid)) {
			List<String> values = aWaitingList.get(pid);
			values.remove(id);
		}
	}

	@Override
	public DomainObjectInstance getDomainObjectInstance(ProcessDiagram proc) {
		if (proc == null) {
			return null;
		}
		DomainObjectInstance doi = processToDoi.get(proc);
		if (doi == null && proc.getFather() != null) {
			ProcessDiagram testProc = find(proc.getFather().getpid());
			while (testProc != null) {
				doi = getDomainObjectInstance(testProc);
				if (doi != null) {
					break;
				}
				testProc = testProc.getFather();
			}
		}
		return doi;
	}

	@Override
	public List<DomainObjectInstance> getDomainObjectInstances() {
		return dom.getInstances();
	}

	@Override
	public void addRunningRefinements(ProcessDiagram proc,
			ProcessDiagram refinement) {
		rm.put(proc.getpid(), refinement);
	}

	@Override
	public DomainObjectDefinition getDomainObjectDefinition(ProcessDiagram p) {
		return dom.findDefinition(p);
	}

	@Override
	public List<DomainObjectDefinition> getDomainObjectDefinitionWithInternalKnowledge(
			String dpName) {
		return dom.findDomainObjectDefinitionWithInternalKnowledge(dpName);
	}

	@Override
	public List<DomainObjectInstance> getDomainObjectInstance(
			DomainObjectDefinition dos) {
		return dom.findInstance(dos);
	}

	/**
	 * build relevant services list and instantiate a DomainObject if have a
	 * useful target
	 */
	@Override
	public Map<String, List<String>> buildRelevantServices(ProcessDiagram proc,
			AbstractActivity current) {
		try {
			logger.debug("Build relevant services for "
					+ proc.getCurrentActivity().getName());
			DomainObjectInstance doi = getDomainObjectInstance(proc);
			List<DomainObjectDefinition> defs = findDomainObjectDefinitionWithGoalRelation(current
					.getGoal());

			Map<String, List<String>> response = new HashMap<String, List<String>>();
			for (DomainObjectDefinition dos : defs) {
				List<DomainObjectInstance> ins = getDomainObjectInstance(dos);
				if (ins == null || ins.isEmpty()) {
					// crea sempre uno nuovo
					DomainObjectInstance in = createDoi(doi, dos);
					cm.create(doi, in);
					response.putAll(correlate(doi, in));
					/****************************
					 * INIT: CODE ADDED TO REFINE MORE TIMES SEPARATELY ON THE
					 * SAME EXTERNAL PROPERTY
					 ***************************/
					List<ObjectDiagram> external = doi.getExternalKnowledge();
					for (int i = 0; i < external.size(); i++) {
						if (external
								.get(i)
								.getOid()
								.equals(in.getInternalKnowledge().get(0)
										.getOid())) {
							external.get(i).setCurrentState("INITIAL");
							break;
						}
					}
					/******************************
					 * END: CODE ADDED TO REFINE MORE TIMES SEPARATELY ON THE
					 * SAME EXTERNAL PROPERTY
					 *******************************/
				} else {
					if (dos.getDomainObject().isSingleton()) {
						List<String> correlations = getCorrelationsForType(doi,
								"Employee");
						if (correlations.isEmpty()) {
							correlations = getCorrelationsForType(doi, "User");
						}
						if (correlations != null && !correlations.isEmpty()) {
							for (String c : correlations) {
								if (c.contains(dos.getDomainObject().getName())) {
									DomainObjectInstance correlatedDoi = dom
											.findInstanceById(c);
									cm.create(correlatedDoi, doi);
									response.putAll(correlate(doi,
											correlatedDoi));
								}
							}

						} else if (!cm.get(doi).isEmpty()
								&& cm.get(doi).get(0).getCorrelations() != null
								&& !cm.get(doi).get(0).getCorrelations()
										.isEmpty()) {
							List<String> cr = Arrays.asList(cm.get(doi).get(0)
									.getCorrelations().split(","));
							if (cr != null && !cr.isEmpty()) {
								for (String c : cr) {
									if (c.contains(dos.getDomainObject()
											.getName())) {
										DomainObjectInstance correlatedDoi = dom
												.findInstanceById(c);
										cm.create(correlatedDoi, doi);
										response.putAll(correlate(doi,
												correlatedDoi));
									}
								}

							}
						} else {
							/****************************
							 * INIT: CODE ADDED TO MAKE THE "DATA VIEWER"
							 * SINGLETON FOR THE USER
							 ***************************/
							List<ObjectDiagram> external = doi
									.getExternalKnowledge();
							for (int i = 0; i < external.size(); i++) {
								if (external.get(i).getOid()
										.equals("DataViewer")) {
									external.get(i).setCurrentState("INITIAL");
									break;
								}
							}
							/****************************
							 * INIT: CODE ADDED TO MAKE THE "DATA VIEWER"
							 * SINGLETON FOR THE USER
							 ***************************/
							// select singleton
							cm.create(ins.get(0), doi);
							response.putAll(correlate(doi, ins.get(0)));
						}
					} else {
						boolean recycled = false;
						for (DomainObjectInstance doiCandidate : ins) {
							// se possibile usa uno esistente, se e' correlato
							// con me
							int cpid = doiCandidate.getProcess().getpid();
							if (cm.isCorrelated(doi, doiCandidate)) {
								response.putAll(correlate(doi, doiCandidate));
								recycled = true;
							}
						}
						if (!recycled) {
							// se no vai a creare
							// TODO: duplicato di sopra attenzione!
							// logica creazione doi
							DomainObjectInstance in = createDoi(doi, dos);
							cm.create(doi, in);
							response.putAll(correlate(doi, in));
						}
					}
				}
			}
			return response;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new HashMap<String, List<String>>();
		}
	}

	private List<String> getCorrelationsForType(DomainObjectInstance doi,
			String type) {
		try {
			Optional<DomainObjectInstance> found = cm.get(doi).stream()
					.filter(d -> d.getId().contains(type)).findFirst();
			if (found.isPresent()) {
				return Arrays.asList(found.get().getCorrelations().split(","));
			}
		} catch (Exception e) {
		}
		return new ArrayList<String>();
	}

	/**
	 * @param goal
	 * @return a list of possible {@link DomainObjectDefinition} with internal
	 *         knowledge of given goal
	 */
	private List<DomainObjectDefinition> findDomainObjectDefinitionWithGoalRelation(
			GoalType goal) {
		List<DomainObjectDefinition> defs = new ArrayList<DomainObjectDefinition>();
		for (Point p : goal.getPoint()) {
			for (DomainProperty dp : p.getDomainProperty()) {
				defs.addAll(getDomainObjectDefinitionWithInternalKnowledge(dp
						.getDpName()));
			}
		}
		return defs;
	}

	private DomainObjectInstance createDoi(DomainObjectInstance doi,
			DomainObjectDefinition dos) {
		// logica creazione doi
		try {
			DomainObjectInstance in;
			// avoid new instance creation of singleton
			// DomainObject
			// for example RouteManagement for Allow
			// Ensembles
			// first
			// storyboard
			if (dos.getDomainObject().isSingleton()) {
				in = dom.findInstanceByType(dos.getDomainObject().getName());
				if (in == null) {
					// for first time, create instance
					in = dom.buildInstance(dos);
					start(in);
				}
			} else {
				// for non singleton domain objects, always
				// create
				// one
				in = dom.buildInstance(dos);
				start(in);
			}
			return in;
		} catch (ProcessEngineRuntimeException
				| InvalidFlowInitialStateException
				| InvalidFlowActivityException | FlowDuplicateActivityException
				| NullPointerException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	private Map<String, List<String>> correlate(DomainObjectInstance doi,
			DomainObjectInstance in) {
		Map<String, List<String>> response = new HashMap<String, List<String>>();
		for (ServiceDiagram fragment : in.getFragments()) {
			List<String> correlator = new ArrayList<String>();
			correlator.add(doi.getId());
			correlator.add(in.getId());
			response.put(fragment.getSid(), correlator);
		}
		return response;
	}

	@Override
	public void registerProcess(ProcessDiagram refinement, ProcessDiagram proc) {
		DomainObjectInstance doi = getDomainObjectInstance(proc);
		if (doi == null) {
			logger.error("domainObjectInstance not found for process with id "
					+ proc.getpid());
		}
		processToDoi.put(refinement, doi);
	}

	@Override
	public void extendKwnoledge(Map<String, List<String>> relevantServices,
			ProcessDiagram proc) {
		if (relevantServices == null
				|| (relevantServices != null && relevantServices.isEmpty())) {
			logger.error("Impossible to extend knowledge with a null or empy list");
			return;
		}
		// DO instance whose knowledge must be extended
		DomainObjectInstance doi = findDoi(proc);
		if (doi == null) {
			logger.warn("Impossible find a domainObjectInstance for process with id"
					+ proc.getpid());
			return;
		}
		Iterator<String> iter = relevantServices.keySet().iterator();
		while (iter.hasNext()) {
			// key corresponds to the fragment's name
			String key = iter.next();
			// values are the names of the DOs connected by the fragment (Key)
			List<String> values = relevantServices.get(key);
			for (String value : values) {
				if (!value.equals(doi.getId())) {
					DomainObjectInstance otherDoi = dom.findInstanceById(value);
					if (otherDoi == null) {
						logger.warn("Not found domainObjectInstance with id "
								+ value);
					} else {
						dom.copyDomainPropertyInstanceToDoi(doi, otherDoi);
					}
				}
			}
		}
	}

	@Override
	public void extendState(String scopeId,
			Map<String, List<String>> relevantServices, ProcessDiagram proc) {
		if (relevantServices == null
				|| (relevantServices != null && relevantServices.isEmpty())) {
			logger.error("Impossible to extend the state with a null or empty list");
			return;
		}
		// DO instance whose knowledge must be extended
		DomainObjectInstance doi = findDoi(proc);
		if (doi == null) {
			logger.warn("Impossible find a domainObjectInstance for the process with id "
					+ proc.getpid());
			return;
		}
		Iterator<String> iter = relevantServices.keySet().iterator();
		while (iter.hasNext()) {
			// key corresponds to the fragment's name
			String key = iter.next();
			// values are the names of the DOs connected by the fragment (Key)
			List<String> values = relevantServices.get(key);
			for (String value : values) {
				if (!value.equals(doi.getId())) {
					DomainObjectInstance otherDoi = dom.findInstanceById(value);
					if (otherDoi == null) {
						logger.warn("Not found domainObjectInstance with id "
								+ value);
					} else {
						dom.extendDoiState(scopeId, doi, otherDoi, key);
					}
				}
			}
		}
	}

	public CorrelationManager getCorrelationManager() {
		return cm;
	}

	@Override
	public DomainObjectInstance getRefinementOrigin(Integer pid) {
		Integer key = rm.getRefinementOrigin(pid);
		if (key != null) {
			ProcessDiagram p = find(key);
			if (p != null) {
				return getDomainObjectInstance(p);
			}
		}
		return null;
	}

	@Override
	public void changeProcVar(ProcessDiagram proc, String name, String value) {
		if (processVarMap.containsKey(proc)) {
			List<ProcVar> procs = processVarMap.get(proc);
			if (procs != null) {
				for (ProcVar procVar : procs) {
					if (procVar.getName().equals(name)) {
						procVar.setValue(value);
					}
				}
			}
		}
	}

	@Override
	public boolean checkContext(ProcessDiagram current, ClauseType varCon) {
		if (varCon == null) {
			return false;
		}
		DomainObjectInstance doi = getDomainObjectInstance(current);
		if (doi == null) {
			return false;
		}
		boolean result = false;
		for (Point p : varCon.getPoint()) {
			if (p.getDomainProperty() != null) {
				for (DomainProperty dp : p.getDomainProperty()) {
					result = dom.isKnowledgeIn(doi, dp);
				}
			}
		}
		return result;
	}

	@Override
	public void createCorrelation(DomainObjectInstance first,
			DomainObjectInstance second) {
		cm.create(first, second);
	}

	@Override
	public void addExecutableHandler(String activityName,
			AbstractExecutableActivityInterface handler) {
		ehm.register(activityName, handler);
	}

	@Override
	public ExecutableActivityInterface getExecutableHandler(String name, int pid) {
		return ehm.getInstance(name, pid);
	}

	@Override
	public Map<String, List<ObjectDiagram>> getExternaKnowledge(
			Map<String, List<String>> relevantServices) {
		if (relevantServices == null) {
			return null;
		}
		HashMap<String, List<ObjectDiagram>> response = new HashMap<String, List<ObjectDiagram>>();
		for (String key : relevantServices.keySet()) {
			List<String> services = relevantServices.get(key);
			for (DomainObjectInstance doi : dom.getInstances()) {
				if (services.contains(doi.getId())) {
					for (ServiceDiagram f : doi.getFragments()) {
						if (response.get(key) == null) {
							response.put(key, doi.getExternalKnowledge());
						} else {
							for (ObjectDiagram ek : doi.getExternalKnowledge()) {
								if (!contained(response.get(key), ek)) {
									response.get(key).add(ek);
								}
							}
						}
					}
				}
			}
		}
		return response;
	}

	private boolean contained(List<ObjectDiagram> list, ObjectDiagram ek) {
		for (ObjectDiagram od : list) {
			if (od.getOid().equals(ek.getOid())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<DomainObjectInstance> getCorrelated(DomainObjectInstance doi) {
		return cm.get(doi);
	}

	@Override
	public ProcVar getVariablesFor(ProcessDiagram proc, String name) {
		ProcVar response = null;
		List<ProcVar> variables = processVarMap.get(proc);
		if (variables == null) {
			return null;
		}
		for (ProcVar procVar : variables) {
			if (procVar.getName().equals(name)) {
				response = procVar;
			}
		}
		return response;
	}

	@Override
	public boolean isRefinement(Integer pid) {
		return rm.isRefinement(pid);
	}

	public ProcessDiagram getRefinement(Integer pid) {
		return rm.get(pid);
	}

	@Override
	public boolean hasRefinements(int pid) {
		return rm.getRefinement(pid) != null ? true : false;
	}

	@Override
	public ProcessDiagram getRefinement(int pid) {
		return rm.getRefinement(pid);
	}

	@Override
	public void setScopeManager(ScopeManager scopeManager) {
		this.scopeManager = scopeManager;

	}

	@Override
	public DomainObjectInstance findDomainObjectByType(String type) {
		return this.dom.findInstanceByType(type);
	}

	@Override
	public List<DomainObjectInstance> findAllDomainObjectByType(String type) {
		return this.dom.findAllInstanceByType(type);
	}

	@Override
	public ProcessDiagram getRunningBranch(ProcessDiagram pd) {
		return getRunningBranch(pd.getpid());
	}

	@Override
	public void removeRunningBranch(ProcessDiagram branch) {
		// setProcessAsExecuted(branch);
		runninBranchesToRemove.add(branch.getpid());
	}

	@Override
	public void addExternalKnowledge(DomainObjectInstance doi, String dpName,
			String initialState) {
		dom.addExternalKnowledge(doi, dpName, initialState, workingFolder);
	}

	@Override
	public void assignVariableFromIstance(
			eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d,
			int pid) {
		if (d.getHoaaVar() != null && !d.getHoaaVar().isEmpty()
				&& d.getHoaa() != null && !d.getHoaa().isEmpty()) {
			ProcessDiagram process = find(pid);
			addProcVar(process, d.getHoaaVar(), d.getHoaa());
		}
	}

	@Override
	public void removeRefinement(ProcessDiagram ref) {
		// setProcessAsExecuted(ref);
		rm.remove(ref);
	}

	@Override
	public DomainObjectDefinition getDefinitionByFragment(String serviceType) {
		return dom.findDefinitionByFragment(serviceType);
	}

	public Map<Integer, ProcessDiagram> getProcesses() {
		return processes;
	}

	@Override
	public File getServicesTest() {
		return testServices.getServicesTestFile();
	}

	public TestServicesSingleton getTestServices() {
		return testServices;
	}

	@Override
	public void setTestServicesLog(String userId, String activityName,
			long refinementTime, int numAlternatives, long avgSegments) {
		this.testServices.setToLog(userId, activityName, refinementTime,
				numAlternatives, avgSegments);
	}

	@Override
	public void addR2rAlternatives(String user, ArrayList<?> r2rAlternatives) {
		r2rAlternativesMap.put(user, r2rAlternatives);
	}

	@Override
	public ArrayList<?> getR2rAlternativesForUser(String user) {
		if (this.r2rAlternativesMap.containsKey(user)) {
			return this.r2rAlternativesMap.get(user);
		} else {
			return null;
		}
	}

	@Override
	public void addViaggiaTrentoAlternatives(String user,
			ArrayList<?> viaggiaAlternatives) {
		viaggiaAlternativesMap.put(user, viaggiaAlternatives);
	}

	@Override
	public ArrayList<?> getViaggiaTrentoForUser(String user) {
		if (this.viaggiaAlternativesMap.containsKey(user)) {
			return this.viaggiaAlternativesMap.get(user);
		} else {
			return null;
		}
	}

	@Override
	public DomainObjectInstance getReferringUser(DomainObjectInstance doi) {
		DomainObjectInstance user = new DomainObjectInstance();
		List<DomainObjectInstance> cr = new ArrayList<DomainObjectInstance>();
		if (cm.hasCorrelation(doi)) {
			cr = Arrays.asList(cm.get(doi).get(0));
		}
		for (DomainObjectInstance d : cr) {
			if (d.getType().equalsIgnoreCase("user")) {
				user = d;
				return user;
			} else {
				return getReferringUser(d);
			}
		}
		return user;
	}

	public Map<String, ArrayList<?>> getR2rAlternativesMap() {
		return r2rAlternativesMap;
	}

	public Map<String, ArrayList<?>> getViaggiaAlternativesMap() {
		return viaggiaAlternativesMap;
	}

}
