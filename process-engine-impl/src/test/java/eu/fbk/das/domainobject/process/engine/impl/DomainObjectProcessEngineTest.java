package eu.fbk.das.domainobject.process.engine.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import eu.fbk.das.adaptation.AdaptationManager;
import eu.fbk.das.composer.api.ComposerInterface;
import eu.fbk.das.composer.impl.Composer;
import eu.fbk.das.domainobject.executable.test.AskToUseCurrentLocationExecutable;
import eu.fbk.das.domainobject.executable.test.BBCServiceCallExecutable;
import eu.fbk.das.domainobject.executable.test.ChooseAlternativeExecutable;
import eu.fbk.das.domainobject.executable.test.DVMDefineDataPatternExecutable;
import eu.fbk.das.domainobject.executable.test.InsertDestinationExecutable;
import eu.fbk.das.domainobject.executable.test.InsertOptionalDataExecutable;
import eu.fbk.das.domainobject.executable.test.Rome2RioCallExecutable;
import eu.fbk.das.domainobject.executable.test.SelectPlanningModeExecutable;
import eu.fbk.das.domainobject.executable.test.ShowResultsExecutable;
import eu.fbk.das.domainobject.executable.test.StartChatbotExecutable;
import eu.fbk.das.domainobject.executable.test.TAHandleLegResultsExecutable;
import eu.fbk.das.domainobject.executable.test.TAShowLegResultsExecutable;
import eu.fbk.das.domainobject.executable.test.TAcheckLegSetExecutable;
import eu.fbk.das.domainobject.executable.test.TAdefineJourneyLegsExecutable;
import eu.fbk.das.domainobject.executable.test.TAidentifyLegExecutable;
import eu.fbk.das.domainobject.executable.test.TestJourney2Executable;
import eu.fbk.das.domainobject.executable.test.TestJourney3Executable;
import eu.fbk.das.domainobject.executable.test.TestJourneyExecutable;
import eu.fbk.das.domainobject.executable.test.TestServicesSingleton;
import eu.fbk.das.domainobject.executable.test.UserInsertSourceLocationExecutable;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance.DomainObjectInstanceKnowledge.Dp;
import eu.fbk.das.process.engine.api.util.DomainObjectInstanceWithVariable;
import eu.fbk.das.process.engine.impl.DomainObjectManager;
import eu.fbk.das.process.engine.impl.ProcessEngineImpl;
import eu.fbk.das.process.engine.impl.util.ScenarioLoader;

public class DomainObjectProcessEngineTest {

	private DomainObjectManager manager;
	private List<String> dirs;
	// C:\Users\Martina\git\process-engine\process-engine-impl\src\test\resources\DOdemoTests\DOdataModelTest\Storyboard1.xml
	private final String BASE_DIR = "C:/Users/Martina/git/process-engine/process-engine-impl/src/test/resources/";
	private AdaptationManager adaptationManager;
	private ProcessEngine processEngine;
	private List<DomainObjectInstanceWithVariable> laterExecutionDoiList = new ArrayList<DomainObjectInstanceWithVariable>();

	// private Map<String, ArrayList<TripAlternativeRome2Rio>>
	// r2rAlternativesMap = new HashMap<String,
	// ArrayList<TripAlternativeRome2Rio>>();
	// private Map<String, ArrayList<TravelViaggiaTrento>>
	// viaggiaAlternativesMap = new HashMap<String,
	// ArrayList<TravelViaggiaTrento>>();

	@Before
	public void setup() throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			JAXBException {
		dirs = Arrays.asList(BASE_DIR, BASE_DIR + "DOdataModelTest/", BASE_DIR
				+ "concrete/");
		manager = new DomainObjectManager(dirs);
	}

	// metodi accessori per i tests
	private void execute(ProcessEngine processEngine, int max) {
		int t = 0;
		while (true) {
			t++;
			if (t > max) {
				break;
			}
			if (laterExecutionDoiList != null
					&& !laterExecutionDoiList.isEmpty()) {
				updateLaterExecutionDoiList();
			}
			processEngine.stepAll();
		}
	}

	private void updateLaterExecutionDoiList() {
		Iterator<DomainObjectInstanceWithVariable> iter = laterExecutionDoiList
				.iterator();
		while (iter.hasNext()) {
			DomainObjectInstanceWithVariable doi = iter.next();
			doi.getDoi().setOnTurn(doi.getDoi().getOnTurn() - 1);
			if (doi.getDoi().getOnTurn() <= 0) {
				try {
					int pid = processEngine.start(doi.getDoi());
					if (doi.getDoiFromScenario() != null) {
						processEngine.assignVariableFromIstance(
								doi.getDoiFromScenario(), pid);
					}
					iter.remove();
				} catch (ProcessEngineRuntimeException
						| InvalidFlowInitialStateException
						| InvalidFlowActivityException
						| FlowDuplicateActivityException e) {
				}
			}
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
				if (d.getOnTurn() != null && d.getOnTurn() > 0) {
					DomainObjectInstanceWithVariable doiwv = new DomainObjectInstanceWithVariable();
					doiwv.setDoi(doi);
					doiwv.setDoiFromScenario(d);
					laterExecutionDoiList.add(doiwv);
				} else {
					int pid = processEngine.start(doi);
					processEngine.assignVariableFromIstance(d, pid);

				}

			}
		}
	}

	/***************************** TESTS IN THE FOLLOWING ****************************************************/

	@Test(expected = ProcessEngineRuntimeException.class)
	public void processAddNullTest() throws ProcessEngineRuntimeException {
		manager.add(null);
	}

	@Test
	public void executeConcreteProcess() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		// init
		String dir = BASE_DIR + "concrete";
		processEngine = new ProcessEngineImpl(manager, new AdaptationManager(
				null), dir);
		// load
		DomainObjectDefinition ed = manager.add(manager
				.load("concrete/E_Concrete_1.apfl"));
		DomainObjectInstance doi = manager.buildInstance(ed);
		// execute
		processEngine.start(doi);
		processEngine.stepAll();
		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeloadScenario() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "DOdataModelTest";
		executeTest(dir, "Storyboard1.xml");
		execute(processEngine, 7);

		assertTrue(processEngine != null);
	}

	@Test
	public void executeWritingOfDomainObjectState()
			throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "DOdataModelTest";
		executeTest(dir, "Storyboard1.xml");
		execute(processEngine, 100000);

		if (processEngine.getDomainObjectInstances() != null) {
			for (DomainObjectInstance doi : processEngine
					.getDomainObjectInstances()) {
				if (doi.getProcess().getName().equals("PROC_Rome2Rio")) {
					Element from = (Element) doi.getState().getStateVariable()
							.get(0).getContent();
					assertTrue(from.getFirstChild().getNodeValue()
							.equals("Trento"));
					Element to = (Element) doi.getState().getStateVariable()
							.get(1).getContent();
					assertTrue(to.getFirstChild().getNodeValue()
							.equals("Bolzano"));
				}
			}
		}
		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void checkExceptionsOnDOStateManagement()
			throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "DOnullStateTest";
		executeTest(dir, "Storyboard1.xml");
		execute(processEngine, 1000000);

		if (processEngine.getDomainObjectInstances() != null) {
			for (DomainObjectInstance doi : processEngine
					.getDomainObjectInstances()) {
				if (doi.getProcess().getName().equals("PROC_Rome2Rio")) {
					assertTrue(doi.getState() == null);
				}
				if (doi.getProcess().getName().equals("PROC_TravelAssistant")) {
					assertTrue(doi.getState() == null);
				}
			}
		}
		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void checkDOStateManagement() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "DOStateTest";
		executeTest(dir, "Storyboard1.xml");
		execute(processEngine, 100000);

		if (processEngine.getDomainObjectInstances() != null) {
			for (DomainObjectInstance doi : processEngine
					.getDomainObjectInstances()) {
				if (doi.getId().equals("BlaBlaCar_1")) {
					Element source = doi
							.getStateVariableContentByName("Source");
					source.setTextContent("Trento");
				}
			}
			for (DomainObjectInstance doi : processEngine
					.getDomainObjectInstances()) {
				if (doi.getId().equals("BlaBlaCar_2")) {
					Element source = doi
							.getStateVariableContentByName("Source");
					assertTrue(source.getFirstChild().getNodeValue()
							.equals("Trento"));
				}
			}
		}
	}

	@Test
	public void executeBasicTravelAssScenario() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "DObasicTAScenario";
		executeTest(dir, "Storyboard1.xml");
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeRobotScenario() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "RobotScenario";
		executeTest(dir, "Storyboard1.xml");
		execute(processEngine, 100000);
		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeICSOC2017Test() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "ICSOC2017";
		executeTest(dir, "Storyboard1.xml");

		TestServicesSingleton testServices = TestServicesSingleton
				.getInstance();

		// long startParsingTime = System.nanoTime();

		// ArrayList<TripAlternativeRome2Rio> alternatives = new
		// ArrayList<TripAlternativeRome2Rio>();
		// ArrayList<TravelViaggiaTrento> viaggiaAlternatives = new
		// ArrayList<TravelViaggiaTrento>();

		// test version done
		processEngine.addExecutableHandler("TA_StartChatbot",
				new StartChatbotExecutable(processEngine, null, null));

		// test version done
		processEngine.addExecutableHandler("R2R_ServiceCall",
				new Rome2RioCallExecutable(processEngine, null));

		// test version done
		processEngine.addExecutableHandler("TA_UseCurrentLocation",
				new AskToUseCurrentLocationExecutable(processEngine, null));

		// test version done MANCA GENERAZIONE RANDOM
		processEngine.addExecutableHandler("TA_InsertSource",
				new UserInsertSourceLocationExecutable(processEngine, null));

		// test version done MANCA GENERAZIONE RANDOM
		processEngine.addExecutableHandler("TA_InsertDestination",
				new InsertDestinationExecutable(processEngine, null));

		// test version done
		processEngine.addExecutableHandler("TA_InsertOptionalData",
				new InsertOptionalDataExecutable(processEngine, null));

		// as usual
		processEngine.addExecutableHandler("TA_SelectPlanningMode",
				new SelectPlanningModeExecutable(processEngine, null));

		// test version done; just set activity executed
		processEngine.addExecutableHandler("TA_ShowResults",
				new ShowResultsExecutable(processEngine, null));

		// test version done: MANCA SCELTA RANDOM
		processEngine.addExecutableHandler("TA_ChooseAlternative",
				new ChooseAlternativeExecutable(processEngine, null));

		// as usual
		processEngine.addExecutableHandler("TA_DefineJourneyLegs",
				new TAdefineJourneyLegsExecutable(processEngine));

		// as usual
		processEngine.addExecutableHandler("TA_IdentifyLeg",
				new TAidentifyLegExecutable(processEngine));

		// as usual
		processEngine.addExecutableHandler("TA_CheckLegSet",
				new TAcheckLegSetExecutable(processEngine));

		// test version done: ???THE ALTERNATIVES MUST BE SAVED IN A VARIABLE
		// TEST
		processEngine.addExecutableHandler("DVM_DefineDataPattern",
				new DVMDefineDataPatternExecutable(processEngine, null));

		// test version done;
		processEngine.addExecutableHandler("BBC_ServiceCall",
				new BBCServiceCallExecutable(processEngine, null));

		// test version done;
		processEngine.addExecutableHandler("TA_ShowLegResults",
				new TAShowLegResultsExecutable(processEngine, null));

		// as usual
		processEngine.addExecutableHandler("TA_HandleLegResults",
				new TAHandleLegResultsExecutable(processEngine));

		// test version done
		// processEngine.addExecutableHandler("VT_ServiceCall",
		// new VTServiceCallExecutable(processEngine, null));

		processEngine.addExecutableHandler("USER_testJourney",
				new TestJourneyExecutable(processEngine));

		processEngine.addExecutableHandler("USER_testJourney2",
				new TestJourney2Executable(processEngine));

		processEngine.addExecutableHandler("USER_testJourney3",
				new TestJourney3Executable(processEngine));

		// processEngine.addExecutableHandler("TA_RefineSourcePoint",
		// new TARefineSourcePointExecutable(processEngine, null));

		// processEngine.addExecutableHandler("TA_RefineDestinationPoint",
		// new TARefineDestinationPointExecutable(processEngine, null));

		// processEngine.addExecutableHandler("TA_ProvideSelectedSource",
		// new TAProvideSelectedSourceExecutable(processEngine, null));

		// processEngine
		// .addExecutableHandler("TA_ProvideSelectedDestination",
		// new TAProvideSelectedDestinationExecutable(
		// processEngine, null));

		execute(processEngine, 100000);
		processEngine.getServicesTest();
		assertTrue(processEngine.size() >= 1);

		// long executionTime = System.nanoTime() - startParsingTime;
		// testServices.setToLog("User", "total execution time", executionTime);
		// testServices.getServicesTestFile();
	}

}
