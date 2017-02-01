package eu.fbk.das.process.engine.api;

import java.util.List;
import java.util.Map;

import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * Represents an adaptation problem passed to the AdaptationManagerInterface
 * 
 * @see AdaptationManagerInterface
 */

public class AdaptationProblem {
    private ProcessDiagram mainProcess;
    private ProcessDiagram refinementProcess;
    private ProcessDiagram localProcess;
    /**
     * domainObject that owns the process to be adapted
     */
    private DomainObjectInstance domainObjectInstance;
    private AdaptationTrigger trigger;
    private List<DomainObjectInstance> domainObjectInstances;
    private List<DomainObjectInstance> relevantEntities;
    private Map<String, List<String>> relevantServices;
    private Map<String, List<ObjectDiagram>> relevantProperties;
    private String apid;
    private AbstractActivity currentAbstract;

    /**
     * Build a new AdaptationProblem
     * 
     * @param currentAbstract
     * 
     * @param domainObject
     *            , domainObjectInstance origin of this problem
     * @param domainObjects
     *            , instances that could be used to solve the problem
     * @param trigger
     *            , adaptationTrigger
     * @param mainProcess
     *            , main process
     * @param refinementProcess
     *            , refinement process
     * @param localProcess
     *            , local process
     * @param relevantEntities
     *            , list of relevant entities
     * @param rs
     *            , list of relevant services
     * @param relevantProperties
     */
    public AdaptationProblem(AbstractActivity currentAbstract,
	    DomainObjectInstance domainObject,
	    List<DomainObjectInstance> domainObjects,
	    AdaptationTrigger trigger, ProcessDiagram mainProcess,
	    ProcessDiagram refinementProcess, ProcessDiagram localProcess,
	    List<DomainObjectInstance> relevantEntities,
	    Map<String, List<String>> rs, Map<String, List<ObjectDiagram>> rp) {
	this.currentAbstract = currentAbstract;
	this.domainObjectInstance = domainObject;
	this.domainObjectInstances = domainObjects;
	this.trigger = trigger;
	this.mainProcess = mainProcess;
	this.refinementProcess = refinementProcess;
	this.localProcess = localProcess;
	this.relevantEntities = relevantEntities;
	this.relevantServices = rs;
	this.relevantProperties = rp;
    }

    public Map<String, List<ObjectDiagram>> getRelevantProperties() {
	return relevantProperties;
    }

    public void setRelevantProperties(
	    Map<String, List<ObjectDiagram>> relevantProperties) {
	this.relevantProperties = relevantProperties;
    }

    public ProcessDiagram getMainProcess() {
	return mainProcess;
    }

    public ProcessDiagram getRefinementProcess() {
	return refinementProcess;
    }

    public ProcessDiagram getLocalProcess() {
	return localProcess;
    }

    public DomainObjectInstance getDomainObjectInstance() {
	return domainObjectInstance;
    }

    public AdaptationTrigger getTrigger() {
	return trigger;
    }

    public List<DomainObjectInstance> getDomainObjectInstances() {
	return domainObjectInstances;
    }

    public List<DomainObjectInstance> getRelevantEntities() {
	return relevantEntities;
    }

    public Map<String, List<String>> getRelevantServices() {
	return relevantServices;
    }

    public void setRelevantServices(Map<String, List<String>> rs) {
	this.relevantServices = rs;
    }

    public void setProblemId(String id) {
	this.apid = id;
    }

    public String getProblemId() {
	return apid;
    }

    public AbstractActivity getCurrentAbstract() {
	return currentAbstract;
    }

    public void setCurrentAbstract(AbstractActivity currentAbstract) {
	this.currentAbstract = currentAbstract;
    }

}
