package eu.fbk.das.process.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.ProcessRequest;
import eu.fbk.das.process.engine.api.domain.DefaultActivity;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.IFActivity;
import eu.fbk.das.process.engine.api.domain.OnMessageActivity;
import eu.fbk.das.process.engine.api.domain.PickActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivityType;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ScopeActivity;
import eu.fbk.das.process.engine.api.domain.SwitchActivity;
import eu.fbk.das.process.engine.api.domain.WhileActivity;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.ActivityType;
import eu.fbk.das.process.engine.api.jaxb.PickType;
import eu.fbk.das.process.engine.api.jaxb.PickType.OnMessage;
import eu.fbk.das.process.engine.api.jaxb.ScopeType;
import eu.fbk.das.process.engine.api.jaxb.VariableType;
import eu.fbk.das.process.engine.api.jaxb.WhileType;

/**
 * Manage requests for new processes
 * 
 * @see ProcessEngineImpl
 */
public class ProcessRequestManager {

	private static final Logger logger = LogManager
			.getLogger(ProcessRequestManager.class);

	private List<ProcessRequest> processRequests = new ArrayList<ProcessRequest>();

	private ProcessEngine pe;

	private DomainObjectManagerInterface dom;

	public ProcessRequestManager(ProcessEngine processEngine,
			DomainObjectManagerInterface dom) {
		this.pe = processEngine;
		this.dom = dom;
	}

	public void addRequest(ProcessRequest pr) {
		processRequests.add(pr);
		logger.debug("Added request for " + pr.toString());
	}

	public void manageProcessRequests() throws ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		List<ProcessRequest> toRemove = new ArrayList<ProcessRequest>();
		DomainObjectInstance result = null;
		for (ProcessRequest processRequest : processRequests) {
			if (processRequest.getName().equals("FC_BookingResult")) {
				System.out.println();
			}
			// get domainObjectInstance of request
			DomainObjectInstance doi = processRequest.getDomainObjectInstance();
			// get correlated to doi
			List<DomainObjectInstance> correlated = pe.getCorrelated(doi);
			// check if correlated to doi can handle this request
			if (!correlated.isEmpty()) {
				for (DomainObjectInstance candidate : correlated) {
					result = canHandle(candidate, processRequest);
					if (result != null
							&& (!result.getProcess().isTerminated() || !result
									.getProcess().isEnded())) {
						toRemove.add(processRequest);

						// extract the request and its variables
						Map<String, List<VariableType>> varForRequest = new HashMap<String, List<VariableType>>();
						varForRequest.put(processRequest.getName(),
								processRequest.getVariables());
						// and send message by also adding variables
						pe.createMessage(candidate, varForRequest);
						// pe.createMessage(processRequest.getName(),candidate);
						break;
					}
				}
			}

			// if so add message
			// if not create new one
			if (result == null) {
				// second: if not found, create new one from definitions
				for (DomainObjectDefinition dod : dom.getDefinitions()) {
					if (definitionCanHandle(dod, processRequest)) {
						if (dod.getDomainObject().isSingleton()) {
							// TODO singleton
							List<DomainObjectInstance> in = pe
									.getDomainObjectInstance(dod);
							if (in != null) {
								toRemove.add(processRequest);
								// extract the request and its variables
								Map<String, List<VariableType>> varForRequest = new HashMap<String, List<VariableType>>();
								varForRequest.put(processRequest.getName(),
										processRequest.getVariables());
								// and send message by also adding variables
								pe.createMessage(in.get(0), varForRequest);
								// // and send message
								// pe.createMessage(processRequest.getName(),
								// in.get(0));
								break;
							}
						} else {
							DomainObjectInstance createdDoi = dom
									.buildInstance(dod);
							pe.start(createdDoi);
							toRemove.add(processRequest);
							pe.createCorrelation(createdDoi, doi);
							// extract the request and its variables
							Map<String, List<VariableType>> varForRequest = new HashMap<String, List<VariableType>>();
							varForRequest.put(processRequest.getName(),
									processRequest.getVariables());
							// and send message by also adding variables
							pe.createMessage(createdDoi, varForRequest);
							// // and send message
							// pe.createMessage(processRequest.getName(),
							// createdDoi);
						}
					}
				}
			}
			// if is singleton, use singleton doi
		}
		processRequests.removeAll(toRemove);
	}

	private boolean definitionCanHandle(DomainObjectDefinition dod,
			ProcessRequest processRequest) {
		eu.fbk.das.process.engine.api.jaxb.Process proc = dod.getProcess();
		for (ActivityType act : proc.getActivity()) {
			// CHECK PICK
			if (isInsidePick(act, processRequest)) {
				return true;
			}
			// CHECK WHILE
			else if (isInsideWhile(act, processRequest)) {
				return true;
			}
			// CHECK SCOPE
			else if (isInsideScope(act, processRequest)) {
				return true;
			}
			// CHECK OTHER ACTIVITIES
			else {
				if (act.getName().equalsIgnoreCase(processRequest.getName())
						&& processRequest.sameTypeOf(act)) {
					return true;
				}
			}
		}
		return false;
	}

	private DomainObjectInstance canHandle(DomainObjectInstance candidate,
			ProcessRequest processRequest) {
		// check inside process
		if (checkProcessForRequest(processRequest, candidate.getProcess())) {
			return candidate;
		}
		// try into current refinement
		if (checkRecursiveOnRefinement(processRequest, candidate.getProcess())) {
			return candidate;
		}
		// try into branch
		ProcessDiagram branch = pe.getRunningBranch(candidate.getProcess());
		if (branch != null) {
			if (checkProcessForRequest(processRequest, branch)) {
				return candidate;
			}
		}
		return null;
	}

	private boolean checkRecursiveOnRefinement(ProcessRequest processRequest,
			ProcessDiagram process) {
		if (processRequest == null || process == null) {
			return false;
		}
		ProcessDiagram ref = pe.getRefinement(process.getpid());
		if (ref != null) {
			if (checkProcessForRequest(processRequest, ref)) {
				return true;
			}
			return checkRecursiveOnRefinement(processRequest, ref);
		} else {
			// non e' detto che il refinement sia direttamente del processo, ma
			// anche dei rami della pick
			// TODO: limite.. becca solo la prima pick nel processo
			ProcessActivity act = processWithPick(process);
			if (act != null) {
				PickActivity pa = (PickActivity) act;
				for (OnMessageActivity on : pa.getOnMessages()) {
					// ref = pe.getRefinement(on.getBranch().getpid());
					if (checkRecursiveOnRefinement(processRequest,
							on.getBranch())) {
						return true;
					}
				}
			} else {
				// non e' detto che il refinement sia direttamente del processo,
				// ma
				// anche nei rami dello switch
				ProcessActivity actSwitch = processWithSwitch(process);
				if (actSwitch != null) {
					SwitchActivity sa = (SwitchActivity) actSwitch;
					for (IFActivity sact : sa.getIFs()) {
						// ref = pe.getRefinement(on.getBranch().getpid());
						if (checkRecursiveOnRefinement(processRequest,
								sact.getBranch())) {
							return true;
						}
					}
					if (sa.getDEF() != null) {
						DefaultActivity da = sa.getDEF();
						if (checkRecursiveOnRefinement(processRequest,
								da.getDefaultBranch())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private ProcessActivity processWithPick(ProcessDiagram process) {
		for (ProcessActivity act : process.getActivities()) {
			if (act.isPick()) {
				return act;
			}
			// TODO: limite, non valuta all'interno della pick while, ecc..
		}
		return null;
	}

	private ProcessActivity processWithSwitch(ProcessDiagram process) {
		for (ProcessActivity act : process.getActivities()) {
			if (act.isSwitch()) {
				return act;
			}
			// TODO: limite, non valuta all'interno dello switch se c'è per
			// esempio un while
		}
		return null;
	}

	private boolean checkProcessForRequest(ProcessRequest processRequest,
			ProcessDiagram pd) {
		if (pd == null) {
			return false;
		}
		for (ProcessActivity act : pd.getActivities()) {
			if (act.getName().equalsIgnoreCase(processRequest.getName())
					&& act.getType().equals(processRequest.getType())) {
				return true;
			} else if (checkInsidePick(act, processRequest) != null) {
				return true;
			} else if (checkInsideWhile(act, processRequest) != null) {
				return true;
			} else if (checkInsideScope(act, processRequest) != null) {
				return true;
			} else if (checkInsideSwitch(act, processRequest)) {
				return true;
			}
		}
		return false;
	}

	private ProcessDiagram checkInsidePick(ProcessActivity act,
			ProcessRequest processRequest) {
		if (act.getType().equals(ProcessActivityType.PICK)) {
			PickActivity pick = ((PickActivity) act);
			ProcessDiagram p;
			for (OnMessageActivity on : pick.getOnMessages()) {
				if (on.getName().equals(processRequest.getName())) {
					return on.getBranch();
				}
				p = on.getBranch();
				for (ProcessActivity msg : p.getActivities()) {
					if (msg.getName().equals(processRequest.getName())
							&& msg.getType().equals(processRequest.getType())) {
						if (msg.getName().equals("RM_PickupPointRequest")) {
							System.out.println();
						}
						return p;
					} else {
						ProcessDiagram sp = checkInsideScope(act,
								processRequest);
						if (sp != null) {
							return sp;
						}
						ProcessDiagram wp = checkInsideWhile(msg,
								processRequest);
						if (wp != null) {
							return wp;
						}
					}
				}
			}

		}
		return null;
	}

	private boolean checkInsideSwitch(ProcessActivity act,
			ProcessRequest processRequest) {
		if (act.getType().equals(ProcessActivityType.SWITCH)) {
			SwitchActivity sact = ((SwitchActivity) act);
			ProcessDiagram p;
			// check if is activated
			if (sact.getIFs() != null) {
				for (IFActivity ifact : sact.getIFs()) {
					if (ifact.getBranch() != null
							&& ifact.getBranch().getpid() != ProcessEngine.DEFAULT_PID) {
						boolean result = checkProcessForRequest(processRequest,
								ifact.getBranch());
						if (result) {
							return result;
						}
					}
				}
			} else {
				DefaultActivity defaultAct = sact.getDEF();
				if (defaultAct != null
						&& defaultAct.getDefaultBranch() != null
						&& defaultAct.getDefaultBranch().getpid() != ProcessEngine.DEFAULT_PID) {
					// then check default
					if (defaultAct.getDefaultBranch() != null
							&& defaultAct.getDefaultBranch().getpid() != ProcessEngine.DEFAULT_PID) {
						boolean result = checkProcessForRequest(processRequest,
								defaultAct.getDefaultBranch());
						if (result) {
							return result;
						}
					}

				}
			}
		}
		return false;
	}

	private ProcessDiagram checkInsideScope(ProcessActivity act,
			ProcessRequest processRequest) {
		if (act.getType().equals(ProcessActivityType.SCOPE)) {
			ProcessDiagram p = ((ScopeActivity) act).getBranch();
			for (ProcessActivity msg : p.getActivities()) {
				if (msg.getName().equals(processRequest.getName())
						&& msg.getType().equals(processRequest.getType())) {
					return p;
				} else {
					ProcessDiagram wp = checkInsideWhile(msg, processRequest);
					if (wp != null) {
						return wp;
					}
					ProcessDiagram sp = checkInsideScope(msg, processRequest);
					if (sp != null) {
						return sp;
					}

				}
			}
		}
		return null;
	}

	private ProcessDiagram checkInsideWhile(ProcessActivity act,
			ProcessRequest processRequest) {
		if (act.getType().equals(ProcessActivityType.WHILE)) {
			if (act.getName().equals("RM_PickupPointRequest")) {
				System.out.println();
			}
			ProcessDiagram p = ((WhileActivity) act).getDefaultBranch();
			for (ProcessActivity msg : p.getActivities()) {
				if (msg.getName().equals(processRequest.getName())
						&& msg.getType().equals(processRequest.getType())) {
					if (msg.getName().equals("RM_PickupPointRequest")) {
						System.out.println();
					}
					return p;
				} else {
					ProcessDiagram sp = checkInsideScope(act, processRequest);
					if (sp != null) {
						return sp;
					}
					ProcessDiagram wp = checkInsideWhile(msg, processRequest);
					if (wp != null) {
						return wp;
					}
				}
			}
		}
		return null;
	}

	private boolean isInsidePick(ActivityType act, ProcessRequest processRequest) {
		if (act instanceof PickType) {
			PickType pick = (PickType) act;
			for (OnMessage msg : pick.getOnMessage()) {
				if (msg.getName().equals(processRequest.getName())) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isInsideWhile(ActivityType act,
			ProcessRequest processRequest) {
		if (act instanceof WhileType) {
			WhileType wact = (WhileType) act;
			for (ActivityType a : wact.getActivity()) {
				if (a.getName().equalsIgnoreCase(processRequest.getName())
						&& processRequest.sameTypeOf(a)) {
					if (processRequest.getName()
							.equals("RM_PickupPointRequest")) {
						System.out.println();
					}
					return true;
				} else if (isInsideWhile(a, processRequest)) {
					return true;
				} else if (isInsideScope(a, processRequest)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isInsideScope(ActivityType act,
			ProcessRequest processRequest) {
		if (act instanceof ScopeType) {
			ScopeType sact = (ScopeType) act;
			for (ActivityType a : sact.getActivity()) {
				// CHECK WHILE INSIDE A SCOPE
				if (isInsideWhile(a, processRequest)) {
					return true;
				} else if (isInsideScope(a, processRequest)) {
					return true;
				} else if (a.getName().equalsIgnoreCase(
						processRequest.getName())
						&& processRequest.sameTypeOf(a)) {
					return true;
				}
			}
		}
		return false;
	}

}
