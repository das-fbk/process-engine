package eu.fbk.das.process.engine.api;

import java.util.List;
import java.util.Map;

import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.ClauseType;
import eu.fbk.das.process.engine.api.jaxb.EffectType;

/**
 * General Interface for ProcessEngine that handle processes lifecycle (start,
 * step, end, remove) and interact with domain object instances
 */
public interface ProcessEngine {

    /**
     * Default initial pid for a new process (not running)
     */
    public static final int DEFAULT_PID = 1;

    /**
     * Remove process from {@link ProcessEngine}
     * 
     * @param pid
     *            - processId
     */
    public void remove(ProcessDiagram process);

    /**
     * Remove all processes from {@link ProcessEngine}
     */
    public void removeAll();

    /**
     * Start process
     * 
     * @param pid
     *            - processId
     * @throws ProcessEngineRuntimeException
     */
    public int start(DomainObjectInstance doi)
	    throws ProcessEngineRuntimeException,
	    InvalidFlowInitialStateException, InvalidFlowActivityException,
	    FlowDuplicateActivityException;

    /**
     * End process
     * 
     * @param pid
     *            , processId
     */
    public void end(int pid);

    /**
     * Update with one step a process with given pid
     * 
     * @param pid
     */
    public void step(int pid);

    /**
     * Update with one step all processes currenctly registered into
     * {@link ProcessEngine}
     */
    public void stepAll();

    /**
     * @param processDiagram
     * @return corresponding domainObjectDefinition (using as link
     *         DomainObjectInstance)
     */
    public DomainObjectDefinition getDomainObjectDefinition(
	    ProcessDiagram processDiagram);

    public boolean checkPrecondition(ProcessDiagram proc,
	    ProcessActivity current);

    public void applyEffect(ProcessDiagram proc, EffectType effect);

    public DomainObjectInstance getDomainObjectInstance(ProcessDiagram proc);

    public List<DomainObjectDefinition> getDomainObjectDefinitionWithInternalKnowledge(
	    String dpName);

    public List<DomainObjectInstance> getDomainObjectInstances();

    public void addRequest(ProcessRequest pr);

    public void addProcVar(ProcessDiagram proc, String varName, String varValue);

    public boolean isWaiting(Integer pid, String id);

    public void setWaiting(Integer pid, String id);

    public void removeWaiting(Integer pid, String id);

    public String submitProblem(AdaptationProblem problem);

    public AdaptationResult getAdaptationResult(ProcessDiagram proc, String id);

    public void addRunningRefinements(ProcessDiagram proc,
	    ProcessDiagram refinement);

    public boolean checkVarCondition(String name, String value,
	    ProcessDiagram father);

    public int getPid();

    public void addRunningBranch(int getpid, ProcessDiagram branch);

    public List<String> getMsg(DomainObjectInstance doi);

    public List<DomainObjectInstance> getDomainObjectInstance(
	    DomainObjectDefinition dos);

    public Map<String, List<String>> buildRelevantServices(ProcessDiagram proc,
	    AbstractActivity currentAbstract);

    public void registerProcess(ProcessDiagram refinement, ProcessDiagram proc);

    public void extendKwnoledge(Map<String, List<String>> relevantServices,
	    ProcessDiagram proc);

    public void changeProcVar(ProcessDiagram proc, String name, String value);

    public boolean checkContext(ProcessDiagram proc, ClauseType varCon);

    public void createCorrelation(DomainObjectInstance first,
	    DomainObjectInstance second);

    public void removeMessage(DomainObjectInstance doi, String msg);

    public ProcessDiagram find(int i);

    public int size();

    public boolean isEmpty();

    public void addExecutableHandler(String activityName,
	    AbstractExecutableActivityInterface handler);

    public ExecutableActivityInterface getExecutableHandler(String name, int pid);

    public Map<String, List<ObjectDiagram>> getExternaKnowledge(
	    Map<String, List<String>> relevantServices);

    public List<DomainObjectInstance> getCorrelated(DomainObjectInstance doi);

    public ProcVar getVariablesFor(ProcessDiagram proc, String name);

    public void createMessage(String name, DomainObjectInstance doi);

    public void applyEffectForAbstractActivity(ProcessDiagram father);

    public void executeActivity(ProcessDiagram refinement);

    public boolean hasRefinements(int getpid);

    public ProcessDiagram getRefinement(int pid);

    public boolean isRefinement(Integer pid);

    public DomainObjectInstance getRefinementOrigin(Integer pid);

    public void setScopeManager(ScopeManager scopeManager);

    public DomainObjectInstance findDomainObjectByType(String type);

    public List<DomainObjectInstance> findAllDomainObjectByType(String type);

    public boolean hasRunningBranch(int fatherId);

    public ProcessDiagram getRunningBranch(ProcessDiagram pd);

    public void removeRunningBranch(ProcessDiagram branch);

    public void addExternalKnowledge(DomainObjectInstance doi, String dpName,
	    String initialState);

    public void assignVariableFromIstance(
	    eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d,
	    int pid);

    public void removeRefinement(ProcessDiagram ref);

}
