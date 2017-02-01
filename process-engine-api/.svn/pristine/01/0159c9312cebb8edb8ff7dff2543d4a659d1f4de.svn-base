package eu.fbk.das.process.engine.api;

import java.util.ArrayList;
import java.util.List;

import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.jaxb.Fragment;

/**
 * Represents the result of the adaptation done by AdaptationManager
 */
public class AdaptationResult {
    private String localId;
    private String refinementId;
    private String compensationId;
    private ProcessDiagram localProcess = null;
    private ProcessDiagram refinementProcess = null;
    private ProcessDiagram compensationProcess = null;
    private ProcessActivity nextActivity = null;
    private AdaptationStrategy strategy;
    private List<Fragment> usedFragments = new ArrayList<Fragment>();

    public void setStrategy(AdaptationStrategy strategy) {
	this.strategy = strategy;
    }

    public void setLocal(ProcessDiagram local, String localId,
	    ProcessActivity nextActivity) {
	this.localProcess = local;
	this.localId = localId;
	this.nextActivity = nextActivity;
    }

    public void setRefinement(ProcessDiagram refinement, String refId,
	    ProcessActivity nextActivity) {
	this.refinementProcess = refinement;
	this.refinementId = refId;
	this.nextActivity = nextActivity;
    }

    public void setCompensation(ProcessDiagram compensation, String compId) {
	this.compensationProcess = compensation;
	this.compensationId = compId;
    }

    public String getLocalId() {
	return localId;
    }

    public String getRefinementId() {
	return refinementId;
    }

    public String getCompensationId() {
	return compensationId;
    }

    public ProcessDiagram getLocalProcess() {
	return localProcess;
    }

    public ProcessDiagram getRefinementProcess() {
	return refinementProcess;
    }

    public ProcessDiagram getCompensationProcess() {
	return compensationProcess;
    }

    public ProcessActivity getNextActivity() {
	return nextActivity;
    }

    public AdaptationStrategy getStrategy() {
	return strategy;
    }

    public void addUsedFragment(Fragment f) {
	if (!usedFragments.contains(f)) {
	    this.usedFragments.add(f);
	}
    }

    public List<Fragment> getUsedFragments() {
	return usedFragments;
    }

}
