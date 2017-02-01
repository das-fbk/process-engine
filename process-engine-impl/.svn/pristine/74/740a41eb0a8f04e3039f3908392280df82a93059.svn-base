package eu.fbk.das.process.engine.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import eu.fbk.das.adaptation.AdaptationManager;
import eu.fbk.das.composer.api.ComposerInterface;
import eu.fbk.das.composer.impl.Composer;
import eu.fbk.das.process.engine.api.CommunicationManager;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.ExternalIssue;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.ScopeManager;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance.DomainObjectInstanceKnowledge.Dp;
import eu.fbk.das.process.engine.impl.util.ScenarioLoader;

public class ScopeTest {

    private DomainObjectManager manager;
    private List<String> dirs;
    private final String BASE_DIR = "C:/Lavoro/workspace/soa/process-engine-impl/src/test/resources/testEntities/";
    private AdaptationManager adaptationManager;
    private ProcessEngine processEngine;
    private CommunicationManager communicationManager = new CommunicationManager();
    private ScopeManager scopeManager;

    private void execute(ProcessEngine processEngine, int max) {
	int t = 0;
	while (true) {
	    t++;
	    if (t > max) {
		break;
	    }
	    processEngine.stepAll();
	}
    }

    private void executeTest(String dir, String storyboardName)
	    throws ProcessEngineRuntimeException,
	    InvalidObjectCurrentStateException,
	    InvalidFlowInitialStateException, InvalidFlowActivityException,
	    FlowDuplicateActivityException {
	manager = new DomainObjectManager(Arrays.asList(dir));
	ComposerInterface composer = new Composer(dir);
	adaptationManager = new AdaptationManager(composer);
	processEngine = new ProcessEngineImpl(manager, adaptationManager, dir);
	adaptationManager.setProcessEngine(processEngine);
	scopeManager = new ScopeManager(communicationManager, manager);
	processEngine.setScopeManager(scopeManager);

	// load
	ScenarioLoader loader = new ScenarioLoader(dir);
	Scenario scenario = loader.load(storyboardName);

	assertNotNull(scenario);
	assertNotNull(scenario.getDomainObject());
	assertTrue(!scenario.getDomainObject().isEmpty());

	for (DomainObject domainObject : scenario.getDomainObject()) {
	    DomainObjectDefinition ed = manager.add(manager.load(domainObject
		    .getFile()));
	    for (eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d : domainObject
		    .getDomainObjectInstance()) {
		DomainObjectInstance doi = manager.buildInstance(ed, d,
			Arrays.asList(dir));
		if (d.getDomainObjectInstanceKnowledge() != null) {
		    for (Dp knownledge : d.getDomainObjectInstanceKnowledge()
			    .getDp()) {
			doi.updateKnowledge(knownledge);
		    }
		}
		processEngine.start(doi);
	    }
	}
    }

    @Test
    public void nullScopeTest() {
	// eu.fbk.das.process.engine.api.jaxb.Process process = new Process();
	// process.setScope(null);
	//
	// assertNull(process.getScope());
    }

    @Test
    public void loadScopeTest() throws ProcessEngineRuntimeException,
	    NullPointerException, InvalidFlowInitialStateException,
	    InvalidFlowActivityException, FlowDuplicateActivityException {
	// init
	String dir = BASE_DIR + "concreteScope";
	DomainObjectManagerInterface manager = new DomainObjectManager(
		Arrays.asList(BASE_DIR, dir));
	ProcessEngineImpl processEngine = new ProcessEngineImpl(manager,
		new AdaptationManager(null), dir);
	scopeManager = new ScopeManager(communicationManager, manager);
	processEngine.setScopeManager(scopeManager);

	// load
	DomainObjectDefinition ed = manager.add(manager
		.load("concreteScope/E_Concrete_1.apfl"));
	DomainObjectInstance doi = manager.buildInstance(ed);

	// execute
	processEngine.start(doi);
	processEngine.stepAll();

	// asserts
	assertTrue(processEngine.isEmpty());

	// for (ProcessActivity act : doi.getProcess().getActivities()) {
	// assertNotNull(act.getScope());
	// assertNotNull(act.getScope().getEventHandler());
	// for (EventHandlerType eh : act.getScope().getEventHandler()) {
	// assertTrue(eh.getOnDPchange() != null
	// || eh.getOnExternalEvent() != null);
	// }
	// }
    }

    @Test
    public void externalIssueHandlingTest()
	    throws ProcessEngineRuntimeException,
	    InvalidObjectCurrentStateException,
	    InvalidFlowInitialStateException, InvalidFlowActivityException,
	    FlowDuplicateActivityException {
	String dir = BASE_DIR + "scopeExternaIssueTest";

	// add externalIssue
	communicationManager.addMessage(new ExternalIssue("externaEventName"));

	executeTest(dir, "scopeExternaIssueTest-scenario.xml");
	execute(processEngine, 7);

	assertTrue(communicationManager.getMessages().isEmpty());
    }

    @Test
    public void dpChangeHandlingTest() throws ProcessEngineRuntimeException,
	    InvalidObjectCurrentStateException,
	    InvalidFlowInitialStateException, InvalidFlowActivityException,
	    FlowDuplicateActivityException {
	String dir = BASE_DIR + "dpChangeScopeTest";

	executeTest(dir, "dpchangeTest-scenario.xml");
	execute(processEngine, 2);

	assertTrue(processEngine.isEmpty());
    }

}
