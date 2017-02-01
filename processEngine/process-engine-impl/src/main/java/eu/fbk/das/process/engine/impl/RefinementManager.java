package eu.fbk.das.process.engine.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * Manage process refinements
 * 
 * @see ProcessEngineImpl
 *
 */
public class RefinementManager {

    private static final Logger logger = LogManager
	    .getLogger(RefinementManager.class);

    private Map<Integer, ProcessDiagram> runningRefinements = new HashMap<Integer, ProcessDiagram>();

    private ProcessEngine pe;

    public RefinementManager(ProcessEngine processEngine) {
	this.pe = processEngine;
    }

    public ProcessDiagram getRefinement(int pid) {
	return runningRefinements.get(pid);
    }

    public void executeRefinement(int pid) {
	if (getRefinement(pid) != null) {
	    ProcessDiagram refinement = getRefinement(pid);
	    if (refinement.isRunning()) {
		executeRefinementProcess(refinement);
	    } else if (getRefinement(refinement.getpid()) != null) {
		executeRefinement(refinement.getpid());
	    }
	    if (refinement.isEnded() && !refinement.isRunning()) {
		runningRefinements.remove(refinement.getFather().getpid());
		ProcessDiagram father = refinement.getFather();
		father.setRunning(true);
		father.getCurrentActivity().setExecuted(true);
		// handle effect of current activity (abstract one)
		pe.applyEffectForAbstractActivity(father);
		logger.debug(father.getCurrentActivity().getName()
			+ " FINISHED");
	    }
	}
    }

    private void executeRefinementProcess(ProcessDiagram refinement) {
	pe.executeActivity(refinement);
	if (refinement.getEnded()) {
	    refinement.getCurrentActivity().setExecuted(true);
	    runningRefinements.remove(refinement.getFather().getpid());
	    ProcessDiagram father = refinement.getFather();
	    father.setRunning(true);
	    father.getCurrentActivity().setExecuted(true);
	    pe.applyEffectForAbstractActivity(father);
	    logger.debug(father.getCurrentActivity().getName() + " FINISHED");
	    if (!father.getNextActivity().isEmpty()) {
		father.setCurrentActivity(father.getNextActivity().get(0));
	    } else {
		father.setRunning(false);
		father.setEnded(true);
	    }

	    if (father.getCurrentActivity().getName().equals("FD_scope1")) {
		System.out.println();
	    }
	}
    }

    public boolean isEmpty() {
	return runningRefinements == null || runningRefinements.isEmpty();
    }

    public List<ProcessDiagram> find(int pid) {
	return runningRefinements.values().stream()
		.filter(p -> p.getpid() == pid)
		.collect(Collectors.toCollection(ArrayList::new));
    }

    public ProcessDiagram get(int pid) {
	return runningRefinements.get(pid);
    }

    public void put(int pid, ProcessDiagram refinement) {
	runningRefinements.put(pid, refinement);
    }

    public boolean isRefinement(int pid) {
	for (ProcessDiagram v : runningRefinements.values()) {
	    if (v.getpid() == pid) {
		return true;
	    }
	}
	return false;
    }

    public Integer getRefinementOrigin(Integer pid) {
	Iterator<Integer> iter = runningRefinements.keySet().iterator();
	while (iter.hasNext()) {
	    Integer key = iter.next();
	    ProcessDiagram value = runningRefinements.get(key);
	    if (value != null && value.getpid() == pid) {
		return key;
	    }
	}
	return null;
    }

    public void remove(ProcessDiagram ref) {
	runningRefinements.remove(ref.getpid());
    }

}
