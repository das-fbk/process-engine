package eu.fbk.das.process.engine.impl.handlers;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.AdaptationProblem;
import eu.fbk.das.process.engine.api.AdaptationResult;
import eu.fbk.das.process.engine.api.AdaptationTrigger;
import eu.fbk.das.process.engine.api.ExecutableActivityInterface;
import eu.fbk.das.process.engine.api.ProcVar;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point.DomainProperty;
import eu.fbk.das.process.engine.api.jaxb.GoalType;

public class AbstractActivityHandler extends AbstractHandler {

	private static final String HOAA = "HOAA";
	private static final Logger logger = LogManager
			.getLogger(AbstractActivityHandler.class);

	@Override
	public void handle(ProcessEngine pe, ProcessDiagram proc,
			ProcessActivity current) {

		AbstractActivity currentAbstract = (AbstractActivity) current;
		boolean prec = handlePrecondition(pe, proc, currentAbstract);
		if (!prec) {
			logger.debug("[" + proc.getpid()
					+ "] Precondizioni non soddisfatte, invoke non eseguita");
			return;
		}
		logger.debug("[" + proc.getpid() + "] Abstract Activity - "
				+ currentAbstract.getName());

		if (currentAbstract.getAbstractType() != null
				&& currentAbstract.getAbstractType().equals(HOAA)) {
			// if (currentAbstract.isAbstract()) {
			logger.debug("Abstract Activity - HOAA with receiveVar "
					+ currentAbstract.getReceiveVar());
			ExecutableActivityInterface handler = pe.getExecutableHandler(
					currentAbstract.getName(), proc.getpid());
			if (handler != null) {
				handler.execute(proc, currentAbstract);
			} else {
				logger.warn("Hoaa without proper handler "
						+ currentAbstract.getName());
			}
			currentAbstract.setExecuted(true);
		} else {

			String id = null;
			if (currentAbstract.getProblem() != null
					&& currentAbstract.getProblem().getProblemId() != null) {
				id = currentAbstract.getProblem().getProblemId();
			}
			if (!pe.isWaiting(proc.getpid(), id)) {
				currentAbstract.setRunning(true);

				if (currentAbstract.getGoal() != null
						&& !currentAbstract.getGoal().getPoint().isEmpty()) {
					logger.debug("Abstract activity with goal");
				} else {
					logger.debug("Abstract activity with no goal, check for receiveGoal");
					GoalType goal = buildGoal(pe, proc, currentAbstract);
					if (goal != null) {
						currentAbstract.setGoal(goal);
					}
				}

				Map<String, List<String>> relevantServices = pe
						.buildRelevantServices(proc, currentAbstract);

				if (relevantServices == null || relevantServices.isEmpty()) {
					logger.warn("No relevant services found for process with pid "
							+ proc.getpid());
					return;
				}

				Map<String, List<ObjectDiagram>> relevantProperties = pe
						.getExternaKnowledge(relevantServices);
				logger.debug("Relevant Services = "
						+ relevantServices.toString());
				pe.extendKwnoledge(relevantServices, proc);
				// DO's state extension with the variables of the fragments
				// received for the abstract activity refinement
				pe.extendState(relevantServices, proc);
				AdaptationProblem problem = new AdaptationProblem(
						currentAbstract, pe.getDomainObjectInstance(proc),
						pe.getDomainObjectInstances(),
						AdaptationTrigger.UNREFINED_ABSTRACT, proc, null, null,
						pe.getDomainObjectInstances(), relevantServices,
						relevantProperties);
				//
				id = pe.submitProblem(problem);
				problem.setProblemId(id);
				//
				pe.setWaiting(proc.getpid(), id);
				// set in currentAbstractActivity adaptationProblem for later
				// use
				currentAbstract.setAdaptationProblem(problem);

			}
			AdaptationResult rst = pe.getAdaptationResult(proc, currentAbstract
					.getProblem().getProblemId());
			//
			// /* if the problem has not been processed */
			if (rst == null) {
				return;
			}

			ProcessDiagram refinement = rst.getRefinementProcess();
			if (refinement == null) {
				logger.error("Refinement process not found");
				return;
			}
			refinement.setPid(pe.getPid());
			pe.registerProcess(refinement, proc);
			// we can remove the refinement from the awaiting list
			pe.removeWaiting(proc.getpid(), currentAbstract.getProblem()
					.getProblemId());
			refinement.setFather(proc);
			if (refinement.getStates() != null
					&& refinement.getStates().size() > 0) {
				logger.debug("[" + proc.getpid()
						+ "] Process in pause, waiting for refinement "
						+ refinement.getpid());
				currentAbstract.setRunning(false);
				// proc.setEnded(true);
				proc.setRunning(false);
				refinement.setCurrentActivity(refinement.getFirstActivity());
				refinement.setRunning(true);
				refinement.setFather(proc);
				pe.addRunningRefinements(proc, refinement);
			} else {
				logger.warn("[" + proc.getpid() + "] Refinement is empty");
				currentAbstract.setRunning(false);
				currentAbstract.setExecuted(true);
			}
		}
	}

	private GoalType buildGoal(ProcessEngine pe, ProcessDiagram proc,
			AbstractActivity current) {
		if (current == null) {
			logger.error("Not possible to build goal with null activity");
			return null;
		}
		if (current.getReceiveGoal() == null) {
			logger.warn("Receive goal is null");
			return null;
		}
		// build goal from receive goal, for now in format DP=value
		String dpName = current.getReceiveGoal();
		// TODO: fix for variables
		ProcVar procVar = pe.getVariablesFor(proc, current.getReceiveGoal());
		// if (procVar == null) {
		// List<Integer> correlated = pe.getCorrelated(proc.getpid());
		// for (Integer cpid : correlated) {
		// procVar = pe.getVariablesFor(pe.find(cpid),
		// current.getReceiveGoal());
		// if (procVar != null) {
		// break;
		// }
		// }
		// }
		String dpState = procVar.getValue();

		GoalType response = new GoalType();
		List<Point> points = response.getPoint();
		Point p = new Point();
		List<DomainProperty> dps = p.getDomainProperty();
		DomainProperty dp = new DomainProperty();
		dp.setDpName(dpName);
		dp.getState().add(dpState);
		dps.add(dp);
		points.add(p);
		return response;
	}

}
