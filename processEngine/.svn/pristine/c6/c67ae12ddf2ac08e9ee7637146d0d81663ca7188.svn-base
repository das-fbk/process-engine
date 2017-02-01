package eu.fbk.das.adaptation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

import eu.fbk.das.composer.api.ComposerInterface;
import eu.fbk.das.composer.api.CompositionStatus;
import eu.fbk.das.composer.api.exceptions.CompositionDuplicateOidException;
import eu.fbk.das.composer.api.exceptions.CompositionDuplicateSidException;
import eu.fbk.das.composer.api.exceptions.InvalidCompositionEffectException;
import eu.fbk.das.composer.api.exceptions.InvalidCompositionPreconditionException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceCurrentStateException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceObjectAssignmentException;
import eu.fbk.das.composer.api.exceptions.ServiceGroundingTypeMismatchException;
import eu.fbk.das.composer.impl.Composer;
import eu.fbk.das.process.engine.api.AdaptationProblem;
import eu.fbk.das.process.engine.api.AdaptationTrigger;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.domain.AbstractActivity;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance.DomainObjectInstanceKnowledge.Dp;
import eu.fbk.das.process.engine.impl.DomainObjectManager;
import eu.fbk.das.process.engine.impl.util.ScenarioLoader;

public class AdaptationTest {

    private DomainObjectManagerInterface manager;
    private List<String> dirs;
    private List<DomainObjectInstance> relevant;
    private static final String BASE_DIR = "C:/Lavoro/workspace/soa/processEngine/process-engine-impl/src/test/resources/testEntities/";
    private static final String LINE_SEPARATOR = System
	    .getProperty("file.separator");

    @Before
    public void setup() {
	dirs = Arrays.asList(BASE_DIR, BASE_DIR + "adaptationBasicTest");
	manager = new DomainObjectManager(dirs);
	relevant = new ArrayList<DomainObjectInstance>();
    }

    @Test
    public void testFromAdaptationProblemToComposition() throws IOException,
	    CompositionDuplicateOidException, CompositionDuplicateSidException,
	    InvalidCompositionPreconditionException,
	    InvalidCompositionEffectException,
	    InvalidObjectCurrentStateException,
	    InvalidServiceCurrentStateException,
	    InvalidServiceObjectAssignmentException,
	    ServiceGroundingTypeMismatchException,
	    InvalidFlowInitialStateException, InvalidFlowActivityException,
	    JAXBException, ProcessEngineRuntimeException, NullPointerException {
	// init stuff
	String dir = BASE_DIR + "adaptationBasicTest" + LINE_SEPARATOR
		+ "collectiveUrbanMobility";
	manager = new DomainObjectManager(Arrays.asList(dir));
	// load scenario
	List<DomainObjectInstance> dois = new ArrayList<DomainObjectInstance>();
	ScenarioLoader loader = new ScenarioLoader(dir);
	Scenario scenario = loader.load("scenario-test-2user.xml");
	DomainObjectInstance doi = null;
	for (DomainObject domainObject : scenario.getDomainObject()) {
	    DomainObjectDefinition ed = manager.add(manager.load(domainObject
		    .getFile()));
	    for (eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d : domainObject
		    .getDomainObjectInstance()) {
		doi = manager.buildInstance(ed, d, Arrays.asList(dir));
		if (d.getDomainObjectInstanceKnowledge() != null) {
		    for (Dp knownledge : d.getDomainObjectInstanceKnowledge()
			    .getDp()) {
			doi.updateKnowledge(knownledge);
		    }
		}
		// add as relevant
		relevant.add(doi);
		dois.add(doi);
	    }
	}
	ComposerInterface composer = new Composer(BASE_DIR
		+ "adaptationBasicTest" + LINE_SEPARATOR
		+ "collectiveUrbanMobility" + LINE_SEPARATOR);

	// load relevant services
	AdaptationThread at = new AdaptationThread(null, null, "C1", composer,
		dir);
	// Define Adaptation problem
	ProcessDiagram process = dois.get(1).getProcess();
	process.setCurrentActivity(process.getFirstActivity());
	Map<String, List<String>> relevantServices = new HashMap<String, List<String>>();
	relevantServices.put("UMS_PlanTrip",
		Arrays.asList("First_User, UrbanMobilitySystem2"));

	AdaptationProblem problem = new AdaptationProblem(
		(AbstractActivity) process.getCurrentActivity(), dois.get(1),
		dois, AdaptationTrigger.UNREFINED_ABSTRACT, process, null,
		null, dois, relevantServices, null);

	// define goal
	AdaptationGoal goal = at.verticalAdaptationGoal(
		problem.getCurrentAbstract(), problem.getMainProcess(), dois);
	at = new AdaptationThread(problem, null, "C1", composer, dir);
	String id = "C1";
	GeneralAdaptationProblem gap = new GeneralAdaptationProblem("1",
		relevant, relevantServices, goal, doi);

	GeneralAdaptationResult adaptationResult = at
		.generalAdaptation(gap, id);

	assertTrue(adaptationResult != null
		&& adaptationResult.getCompositionStatus() == CompositionStatus.OK
		& adaptationResult.getAdaptationProcess() != null
		&& adaptationResult.getAdaptationProcess().getActivities() != null);

    }
}
