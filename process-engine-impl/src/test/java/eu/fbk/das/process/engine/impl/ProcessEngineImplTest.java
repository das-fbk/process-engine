package eu.fbk.das.process.engine.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

import eu.fbk.das.adaptation.AdaptationManager;
import eu.fbk.das.composer.api.ComposerInterface;
import eu.fbk.das.composer.impl.Composer;
import eu.fbk.das.process.engine.api.CommunicationManager;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
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
import eu.fbk.das.process.engine.api.util.DomainObjectInstanceWithVariable;
import eu.fbk.das.process.engine.impl.executable.FcAssignPassengerToRouteExecutable;
import eu.fbk.das.process.engine.impl.executable.FcRouteStarted;
import eu.fbk.das.process.engine.impl.executable.FcWaitRouteCreatedExecutable;
import eu.fbk.das.process.engine.impl.executable.FdAllPassengersOnBoardExecutable;
import eu.fbk.das.process.engine.impl.executable.FdAllPickupPointReachedExecutable;
import eu.fbk.das.process.engine.impl.executable.FdGoToNextPickupPointExecutable;
import eu.fbk.das.process.engine.impl.executable.FdRouteClosedNotice;
import eu.fbk.das.process.engine.impl.executable.FdWaitPassengersCheckOutExecutable;
import eu.fbk.das.process.engine.impl.executable.PassengerEnterInRouteEnsembleExecutable;
import eu.fbk.das.process.engine.impl.executable.RmAssignDriverExecutable;
import eu.fbk.das.process.engine.impl.executable.RmAssignPickupPointExecutable;
import eu.fbk.das.process.engine.impl.executable.RmCheckInEndedExecutable;
import eu.fbk.das.process.engine.impl.executable.RmCheckInExecuted;
import eu.fbk.das.process.engine.impl.executable.RmRouteSoldOutExecutable;
import eu.fbk.das.process.engine.impl.executable.RmUnlockWaitingPassengerExecutable;
import eu.fbk.das.process.engine.impl.executable.RouteManagerCreateRouteEnsembleExecutable;
import eu.fbk.das.process.engine.impl.executable.RpPassengerCheckedInExecutable;
import eu.fbk.das.process.engine.impl.executable.RpPassengerChekedOutExecutable;
import eu.fbk.das.process.engine.impl.executable.RpWaitFlexibusExecutable;
import eu.fbk.das.process.engine.impl.executable.TestHoaaHandler;
import eu.fbk.das.process.engine.impl.executable.ToHoaaOrganizeTripExecutable;
import eu.fbk.das.process.engine.impl.executable.UserExecuteTripHoaa;
import eu.fbk.das.process.engine.impl.executable.UserSyncExecutable;
import eu.fbk.das.process.engine.impl.util.ScenarioLoader;

public class ProcessEngineImplTest {

	private DomainObjectManager manager;
	private List<String> dirs;
	private final String BASE_DIR = "C:/Users/Martina/DomainObjectDEMO/workspace/process-engine-impl/src/test/resources/testEntities/";
	private AdaptationManager adaptationManager;
	private ProcessEngine processEngine;

	private List<DomainObjectInstanceWithVariable> laterExecutionDoiList = new ArrayList<DomainObjectInstanceWithVariable>();

	@Before
	public void setup() throws InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			JAXBException {
		dirs = Arrays.asList(BASE_DIR, BASE_DIR + "concrete/", BASE_DIR
				+ "invoke/", BASE_DIR + "invokereceive/", BASE_DIR
				+ "swichtactivity/", BASE_DIR + "pick/", BASE_DIR + "abstrac/",
				BASE_DIR + "testBasicDomainObject/", BASE_DIR + "user/",
				BASE_DIR + "loadpreconditionTest", BASE_DIR
						+ "invokereceiveDoppio/", BASE_DIR + "whileExample/",
				BASE_DIR + "whileExampleContext/", BASE_DIR
						+ "abstractWithGoalAndEffect/", BASE_DIR
						+ "loadOrpreconditionTest", BASE_DIR
						+ "fragmentWithConcrete");
		manager = new DomainObjectManager(dirs);
	}

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
	public void executeInvokeProcess() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		// init
		String dir = BASE_DIR + "invoke";
		processEngine = new ProcessEngineImpl(manager, new AdaptationManager(
				null), dir);
		// load
		DomainObjectDefinition ed = manager.add(manager
				.load("invoke/E_Invoke_1.apfl"));
		DomainObjectInstance doi = manager.buildInstance(ed);
		// execute
		processEngine.start(doi);
		processEngine.stepAll();
		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeInvokeReceive() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		// init
		String dir = BASE_DIR + "invokereceive";
		processEngine = new ProcessEngineImpl(manager, new AdaptationManager(
				null), dir);
		// load
		DomainObjectDefinition ed = manager.add(manager
				.load("invokereceive/E_LandingManager_ir.apfl"));
		ed = manager.add(manager.load("invokereceive/E_Ship_ir.apfl"));
		DomainObjectInstance doi = manager.buildInstance(ed);
		// execute
		processEngine.start(doi);
		execute(processEngine, 10);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeInvokeReceiveDoppio() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		// init
		String dir = BASE_DIR + "invokereceiveDoppio";
		processEngine = new ProcessEngineImpl(manager, new AdaptationManager(
				null), dir);
		// load
		DomainObjectDefinition ed = manager.add(manager
				.load("invokereceiveDoppio/E_Supporter"));
		ed = manager.add(manager.load("E_Helper"));
		ed = manager.add(manager.load("E_Starter"));
		DomainObjectInstance doi = manager.buildInstance(ed);
		// execute
		processEngine.start(doi);
		execute(processEngine, 6);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeSwitch() throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException {
		// init
		String dir = BASE_DIR + "switchactivity";
		processEngine = new ProcessEngineImpl(manager, new AdaptationManager(
				null), dir);
		// load
		DomainObjectDefinition ed = manager.add(manager
				.load("swichtactivity/E_LandingManager_sw.xml"));
		ed = manager.add(manager.load("swichtactivity/E_Ship_sw.xml"));
		DomainObjectInstance doi = manager.buildInstance(ed);
		// execute
		processEngine.start(doi);
		execute(processEngine, 8);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executePick() throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "pick";
		executeTest(dir, "storyboard-pick.xml");
		// execute
		execute(processEngine, 10);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeTestBasicDomainObject() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		// init
		String dir = BASE_DIR + "testBasicDomainObject";
		processEngine = new ProcessEngineImpl(manager, new AdaptationManager(
				null), dir);
		// load
		DomainObjectDefinition ed = manager.add(manager.load("DO_test1.apfl"));
		DomainObjectInstance doi = manager.buildInstance(ed);
		// execute
		processEngine.start(doi);
		execute(processEngine, 1);
	}

	@Test
	public void executeloadPreconditionTest()
			throws ProcessEngineRuntimeException, NullPointerException {
		DomainObjectDefinition ed = manager.add(manager
				.load("DO_prec_test1.apfl"));
		DomainObjectInstance doi = manager.buildInstance(ed);

		assertTrue("Preconditions must be loaded in this test", doi
				.getProcess().getActivities().get(0).getPrecondition() != null);
	}

	@Test
	public void executeloadScenario() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "collectiveUrbanMobility";
		executeTest(dir, "scenario-test-2user.xml");
		execute(processEngine, 7);

		assertTrue(processEngine != null);
	}

	@Test
	public void loadFragmentTest() throws ProcessEngineRuntimeException,
			NullPointerException {
		String dir = BASE_DIR + "loadFragmentTest/";
		manager = new DomainObjectManager(Arrays.asList(dir));
		DomainObjectDefinition ed = manager.add(manager
				.load("UrbanMobilitySystem"));
		DomainObjectInstance doi = manager.buildInstance(ed);

		assertNotNull(doi.getFragments());
	}

	@Test
	public void executeAbstractScenario() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "abstractScenario/collectiveUrbanMobility";
		executeTest(dir, "scenario-test-2user.xml");
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeStoryboardOneScenario() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "storyboard1";
		executeTest(dir, "storyboard1.xml");
		// execute
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeStoryboardOneExtended() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "storyboardOneExtended";
		executeTest(dir, "storyboard1.xml");
		// execute
		execute(processEngine, 200000);

		assertTrue(processEngine.size() == 2);
	}

	@Test
	public void executeFirstStoryboard() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "firstStoryboard";
		executeTest(dir, "storyboard1.xml");
		processEngine.setScopeManager(new ScopeManager(
				new CommunicationManager(), manager));

		// aggiunta degli handlers
		processEngine.addExecutableHandler("RP_WaitFlexibus",
				new RpWaitFlexibusExecutable(processEngine));

		processEngine.addExecutableHandler("RM_CreateRouteEnsemble",
				new RouteManagerCreateRouteEnsembleExecutable(processEngine));
		processEngine.addExecutableHandler("RP_EnterInRouteEnsemble",
				new PassengerEnterInRouteEnsembleExecutable(processEngine));
		// TODO: commentata per review
		// processEngine.addExecutableHandler("FD_EnterInRouteEnsemble",
		// new DriverEnterInRouteEnsembleExecutable(processEngine));
		processEngine.addExecutableHandler("FC_AssignPassengerToRoute",
				new FcAssignPassengerToRouteExecutable(processEngine));
		processEngine.addExecutableHandler("RM_AssignDriver",
				new RmAssignDriverExecutable(processEngine));
		processEngine.addExecutableHandler("RM_AssignPickupPoint",
				new RmAssignPickupPointExecutable(processEngine));
		processEngine.addExecutableHandler("RM_RouteSoldOut",
				new RmRouteSoldOutExecutable(processEngine));
		processEngine.addExecutableHandler("RM_CheckinEnded",
				new RmCheckInEndedExecutable(processEngine));
		processEngine.addExecutableHandler("FD_GoToNextPickupPoint",
				new FdGoToNextPickupPointExecutable(processEngine));
		processEngine.addExecutableHandler("RP_PassengerCheckedIn",
				new RpPassengerCheckedInExecutable(processEngine));
		processEngine.addExecutableHandler("FD_AllPassengersOnBoard",
				new FdAllPassengersOnBoardExecutable(processEngine));
		processEngine.addExecutableHandler("FD_AllPickupPointReached",
				new FdAllPickupPointReachedExecutable(processEngine));
		processEngine.addExecutableHandler("RP_PassengerCheckedOut",
				new RpPassengerChekedOutExecutable(processEngine));
		processEngine.addExecutableHandler("FD_WaitForPassengersCheckOut",
				new FdWaitPassengersCheckOutExecutable(processEngine));

		processEngine.addExecutableHandler("FC_WaitRouteCreated",
				new FcWaitRouteCreatedExecutable(processEngine));
		processEngine.addExecutableHandler("RM_UnlockWaitingPassenger",
				new RmUnlockWaitingPassengerExecutable(processEngine));

		processEngine.addExecutableHandler("RM_CheckInExecuted",
				new RmCheckInExecuted(processEngine));

		processEngine.addExecutableHandler("FD_RouteClosedNotice",
				new FdRouteClosedNotice());
		processEngine.addExecutableHandler("FC_RouteStarted",
				new FcRouteStarted());

		// execute
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeICWS16() throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "ICWS16";
		executeTest(dir, "storyboard1.xml");
		processEngine.setScopeManager(new ScopeManager(
				new CommunicationManager(), manager));

		// aggiunta degli handlers
		processEngine.addExecutableHandler("RP_WaitFlexibus",
				new RpWaitFlexibusExecutable(processEngine));

		processEngine.addExecutableHandler("RM_CreateRouteEnsemble",
				new RouteManagerCreateRouteEnsembleExecutable(processEngine));
		processEngine.addExecutableHandler("RP_EnterInRouteEnsemble",
				new PassengerEnterInRouteEnsembleExecutable(processEngine));
		// TODO: commentata per review
		// processEngine.addExecutableHandler("FD_EnterInRouteEnsemble",
		// new DriverEnterInRouteEnsembleExecutable(processEngine));
		processEngine.addExecutableHandler("FC_AssignPassengerToRoute",
				new FcAssignPassengerToRouteExecutable(processEngine));
		processEngine.addExecutableHandler("RM_AssignDriver",
				new RmAssignDriverExecutable(processEngine));
		processEngine.addExecutableHandler("RM_AssignPickupPoint",
				new RmAssignPickupPointExecutable(processEngine));
		processEngine.addExecutableHandler("RM_RouteSoldOut",
				new RmRouteSoldOutExecutable(processEngine));
		processEngine.addExecutableHandler("RM_CheckinEnded",
				new RmCheckInEndedExecutable(processEngine));
		processEngine.addExecutableHandler("FD_GoToNextPickupPoint",
				new FdGoToNextPickupPointExecutable(processEngine));
		processEngine.addExecutableHandler("RP_PassengerCheckedIn",
				new RpPassengerCheckedInExecutable(processEngine));
		processEngine.addExecutableHandler("FD_AllPassengersOnBoard",
				new FdAllPassengersOnBoardExecutable(processEngine));
		processEngine.addExecutableHandler("FD_AllPickupPointReached",
				new FdAllPickupPointReachedExecutable(processEngine));
		processEngine.addExecutableHandler("RP_PassengerCheckedOut",
				new RpPassengerChekedOutExecutable(processEngine));
		processEngine.addExecutableHandler("FD_WaitForPassengersCheckOut",
				new FdWaitPassengersCheckOutExecutable(processEngine));

		processEngine.addExecutableHandler("FC_WaitRouteCreated",
				new FcWaitRouteCreatedExecutable(processEngine));
		processEngine.addExecutableHandler("RM_UnlockWaitingPassenger",
				new RmUnlockWaitingPassengerExecutable(processEngine));

		processEngine.addExecutableHandler("RM_CheckInExecuted",
				new RmCheckInExecuted(processEngine));

		processEngine.addExecutableHandler("FD_RouteClosedNotice",
				new FdRouteClosedNotice());
		processEngine.addExecutableHandler("FC_RouteStarted",
				new FcRouteStarted());

		// handler for hoaa for pre-phase
		processEngine.addExecutableHandler("TO_HOAAorganizeTrip",
				new ToHoaaOrganizeTripExecutable(processEngine));
		// handler for hoaa for execute phase
		processEngine.addExecutableHandler("USER_ExecuteTrip",
				new UserExecuteTripHoaa(processEngine));

		processEngine.addExecutableHandler("USER_sync", new UserSyncExecutable(
				processEngine));

		// execute
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
		// assertTrue(processEngine.size() == 2);
	}

	/*
	 * @Test public void executeCarPoolTest() throws NullPointerException,
	 * JAXBException, ProcessEngineRuntimeException,
	 * InvalidFlowInitialStateException, InvalidFlowActivityException,
	 * FlowDuplicateActivityException, InvalidObjectCurrentStateException {
	 * String dir = BASE_DIR + "carPoolingTest"; executeTest(dir,
	 * "storyboard1.xml"); processEngine.setScopeManager(new ScopeManager( new
	 * CommunicationManager(), manager));
	 * 
	 * // aggiunta degli handlers
	 * processEngine.addExecutableHandler("RP_WaitFlexibus", new
	 * RpWaitFlexibusExecutable(processEngine));
	 * 
	 * processEngine.addExecutableHandler("RM_CreateRouteEnsemble", new
	 * RouteManagerCreateRouteEnsembleExecutable(processEngine));
	 * processEngine.addExecutableHandler("RP_EnterInRouteEnsemble", new
	 * PassengerEnterInRouteEnsembleExecutable(processEngine));
	 * processEngine.addExecutableHandler("FD_EnterInRouteEnsemble", new
	 * DriverEnterInRouteEnsembleExecutable(processEngine));
	 * processEngine.addExecutableHandler("FC_AssignPassengerToRoute", new
	 * FcAssignPassengerToRouteExecutable(processEngine));
	 * processEngine.addExecutableHandler("RM_AssignDriver", new
	 * RmAssignDriverExecutable(processEngine));
	 * processEngine.addExecutableHandler("RM_AssignPickupPoint", new
	 * RmAssignPickupPointExecutable(processEngine));
	 * processEngine.addExecutableHandler("RM_RouteSoldOut", new
	 * RmRouteSoldOutExecutable(processEngine));
	 * processEngine.addExecutableHandler("RM_CheckinEnded", new
	 * RmCheckInEndedExecutable(processEngine));
	 * processEngine.addExecutableHandler("FD_GoToNextPickupPoint", new
	 * FdGoToNextPickupPointExecutable(processEngine));
	 * processEngine.addExecutableHandler("RP_PassengerCheckedIn", new
	 * RpPassengerCheckedInExecutable(processEngine));
	 * processEngine.addExecutableHandler("FD_AllPassengersOnBoard", new
	 * FdAllPassengersOnBoardExecutable(processEngine));
	 * processEngine.addExecutableHandler("FD_AllPickupPointReached", new
	 * FdAllPickupPointReachedExecutable(processEngine));
	 * processEngine.addExecutableHandler("RP_PassengerCheckedOut", new
	 * RpPassengerChekedOutExecutable(processEngine));
	 * processEngine.addExecutableHandler("FD_WaitForPassengersCheckOut", new
	 * FdWaitPassengersCheckOutExecutable(processEngine));
	 * 
	 * processEngine.addExecutableHandler("FC_WaitRouteCreated", new
	 * FcWaitRouteCreatedExecutable());
	 * processEngine.addExecutableHandler("RM_UnlockWaitingPassenger", new
	 * RmUnlockWaitingPassengerExecutable());
	 * 
	 * processEngine.addExecutableHandler("RM_CheckInExecuted", new
	 * RmCheckInExecuted(processEngine));
	 * 
	 * processEngine.addExecutableHandler("FD_RouteClosedNotice", new
	 * FdRouteClosedNotice());
	 * processEngine.addExecutableHandler("FC_RouteStarted", new
	 * FcRouteStarted());
	 * 
	 * // execute execute(processEngine, 100000);
	 * 
	 * assertTrue(processEngine.isEmpty()); // assertTrue(processEngine.size()
	 * == 2); }
	 */

	@Test
	public void executeabsActivityGoalEffectTest() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "abstractWithGoalAndEffect";
		executeTest(dir, "storyboard1.xml");
		// execute
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeWhileTest() throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "whileExample";
		executeTest(dir, "storyboardWhile.xml");
		// execute
		execute(processEngine, 100);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeWhileExampleWithInvokeTest()
			throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "whileExampleWithInvoke";
		executeTest(dir, "storyboardWhile.xml");
		// execute
		execute(processEngine, 10);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void whileRunningExampleWithInvoke() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "whileRunningExampleWithInvoke";
		executeTest(dir, "storyboardWhile.xml");
		// execute
		execute(processEngine, 10);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void scopeExampleWithInvoke() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "scopeExampleWithInvoke";
		executeTest(dir, "storyboardScope.xml");
		// execute
		execute(processEngine, 10);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void scopeExampleWithRunningInvoke() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "scopeExampleWithRunningInvoke";
		executeTest(dir, "storyboardScope.xml");
		// execute
		execute(processEngine, 10);

		assertTrue(processEngine.isEmpty());
	}

	// TODO: superato? check con Martina
	// @Test
	// public void executeStoryboardOneExtendedWhile()
	// throws NullPointerException, JAXBException,
	// ProcessEngineRuntimeException, InvalidFlowInitialStateException,
	// InvalidFlowActivityException, FlowDuplicateActivityException,
	// InvalidObjectCurrentStateException {
	// String dir = BASE_DIR + "storyboardOneExtendedWhileWorking";
	// executeTest(dir, "storyboard1.xml");
	// // execute
	// execute(processEngine, 100000);
	//
	// assertTrue(processEngine.isEmpty());
	// }

	@Test
	public void executeWhileContextTest() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "whileExampleContext";
		executeTest(dir, "storyboardWhile.xml");
		execute(processEngine, 10);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeDoubleEffectTest() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "DoubleEffectTest";
		executeTest(dir, "storyboardDoubleEffect.xml");
		execute(processEngine, 1);

		assertTrue(processEngine.isEmpty()
				&& processEngine.getDomainObjectInstances().get(0)
						.getInternalKnowledge().get(0).getCurrentState()
						.equals("SERVICEADDED"));
	}

	// @Test
	// public void executeabstractWithReceiveGoalTest()
	// throws NullPointerException, JAXBException,
	// ProcessEngineRuntimeException, InvalidFlowInitialStateException,
	// InvalidFlowActivityException, FlowDuplicateActivityException,
	// InvalidObjectCurrentStateException {
	// String dir = BASE_DIR + "abstractWithReceiveGoal";
	// executeTest(dir, "storyboard1.xml");
	// // execute
	// execute(processEngine, 100000);
	//
	// assertTrue(processEngine.size() == 0);
	// }

	@Test
	public void executeInvokeTheSame() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "invokeTheSame";
		executeTest(dir, "storyboard1.xml");
		execute(processEngine, 20);

		assertTrue(processEngine.isEmpty());
	}

	// @Test
	// public void executeCorrelationFragment() throws NullPointerException,
	// JAXBException, ProcessEngineRuntimeException,
	// InvalidFlowInitialStateException, InvalidFlowActivityException,
	// FlowDuplicateActivityException, InvalidObjectCurrentStateException {
	// String dir = BASE_DIR + "correlationFragment";
	// executeTest(dir, "storyboard1.xml");
	// execute(processEngine, 1000000);
	//
	// assertTrue(processEngine.isEmpty());
	// }

	// Disattivato, per funzionare � necessario che siano attivate i veri
	// executables del dimostratore
	// @Test
	// public void executeStoryboardOneDemonstrator() throws
	// NullPointerException,
	// JAXBException, ProcessEngineRuntimeException,
	// InvalidFlowInitialStateException, InvalidFlowActivityException,
	// FlowDuplicateActivityException, InvalidObjectCurrentStateException {
	// String dir =
	// "C:\\Lavoro\\workspace\\soa\\demonstrator\\src\\main\\resources\\storyboard1\\";
	// executeTest(dir, "storyboard1-scenario.xml");
	//
	// processEngine.addExecutableHandler("UMS_SecurityAndPrivacyFiltering",
	// new DummyExecutable());
	// processEngine.addExecutableHandler("UMS_UtilityRanking",
	// new DummyExecutable());
	// processEngine.addExecutableHandler("USER_ChooseAlternative",
	// new DummyExecutable());
	// processEngine.addExecutableHandler("USER_executeTrip",
	// new DummyExecutable());
	//
	// // execute
	// execute(processEngine, 2000000);
	//
	// assertTrue(processEngine.isEmpty());
	// }

	@Test
	public void executeloadOrPreconditionTest()
			throws ProcessEngineRuntimeException, NullPointerException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException {
		DomainObjectDefinition ed = manager.add(manager
				.load("DO_prec_or_test1.xml"));
		DomainObjectInstance doi = manager.buildInstance(ed);

		assertTrue("Preconditions must be loaded in this test", doi
				.getProcess().getActivities().get(0).getPrecondition()
				.getPoint().size() == 2);

		String dir = BASE_DIR + "loadOrPreconditionTest";
		processEngine = new ProcessEngineImpl(manager, new AdaptationManager(
				null), dir);
		// execute
		processEngine.start(doi);
		processEngine.stepAll();
		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeFragmentWithConcrete() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "fragmentWithConcrete";
		executeTest(dir, "storyboard.xml");
		// execute
		execute(processEngine, 10000);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeHOAA() throws NullPointerException, JAXBException,
			ProcessEngineRuntimeException, InvalidFlowInitialStateException,
			InvalidFlowActivityException, FlowDuplicateActivityException,
			InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "abstractHighOrderAbstractActivity";
		executeTest(dir, "storyboard.xml");
		processEngine.setScopeManager(new ScopeManager(
				new CommunicationManager(), manager));

		// set handlers
		processEngine.addExecutableHandler("testHOAA", new TestHoaaHandler(
				processEngine));

		// execute
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
	}

	@Test
	public void executeScenarioReview() throws NullPointerException,
			JAXBException, ProcessEngineRuntimeException,
			InvalidFlowInitialStateException, InvalidFlowActivityException,
			FlowDuplicateActivityException, InvalidObjectCurrentStateException {
		String dir = BASE_DIR + "ScenarioReview";
		executeTest(dir, "storyboard1.xml");
		processEngine.setScopeManager(new ScopeManager(
				new CommunicationManager(), manager));

		// aggiunta degli handlers
		processEngine.addExecutableHandler("RP_WaitFlexibus",
				new RpWaitFlexibusExecutable(processEngine));

		processEngine.addExecutableHandler("RM_CreateRouteEnsemble",
				new RouteManagerCreateRouteEnsembleExecutable(processEngine));
		processEngine.addExecutableHandler("RP_EnterInRouteEnsemble",
				new PassengerEnterInRouteEnsembleExecutable(processEngine));
		// TODO: commentata per review
		// processEngine.addExecutableHandler("FD_EnterInRouteEnsemble",
		// new DriverEnterInRouteEnsembleExecutable(processEngine));
		processEngine.addExecutableHandler("FC_AssignPassengerToRoute",
				new FcAssignPassengerToRouteExecutable(processEngine));
		processEngine.addExecutableHandler("RM_AssignDriver",
				new RmAssignDriverExecutable(processEngine));
		processEngine.addExecutableHandler("RM_AssignPickupPoint",
				new RmAssignPickupPointExecutable(processEngine));
		processEngine.addExecutableHandler("RM_RouteSoldOut",
				new RmRouteSoldOutExecutable(processEngine));
		processEngine.addExecutableHandler("RM_CheckinEnded",
				new RmCheckInEndedExecutable(processEngine));
		processEngine.addExecutableHandler("FD_GoToNextPickupPoint",
				new FdGoToNextPickupPointExecutable(processEngine));
		processEngine.addExecutableHandler("RP_PassengerCheckedIn",
				new RpPassengerCheckedInExecutable(processEngine));
		processEngine.addExecutableHandler("FD_AllPassengersOnBoard",
				new FdAllPassengersOnBoardExecutable(processEngine));
		processEngine.addExecutableHandler("FD_AllPickupPointReached",
				new FdAllPickupPointReachedExecutable(processEngine));
		processEngine.addExecutableHandler("RP_PassengerCheckedOut",
				new RpPassengerChekedOutExecutable(processEngine));
		processEngine.addExecutableHandler("FD_WaitForPassengersCheckOut",
				new FdWaitPassengersCheckOutExecutable(processEngine));

		processEngine.addExecutableHandler("FC_WaitRouteCreated",
				new FcWaitRouteCreatedExecutable(processEngine));
		processEngine.addExecutableHandler("RM_UnlockWaitingPassenger",
				new RmUnlockWaitingPassengerExecutable(processEngine));

		processEngine.addExecutableHandler("RM_CheckInExecuted",
				new RmCheckInExecuted(processEngine));

		processEngine.addExecutableHandler("FD_RouteClosedNotice",
				new FdRouteClosedNotice());
		processEngine.addExecutableHandler("FC_RouteStarted",
				new FcRouteStarted());

		// handler for hoaa for pre-phase
		processEngine.addExecutableHandler("TO_HOAAorganizeTrip",
				new ToHoaaOrganizeTripExecutable(processEngine));
		// handler for hoaa for execute phase
		processEngine.addExecutableHandler("USER_ExecuteTrip",
				new UserExecuteTripHoaa(processEngine));

		processEngine.addExecutableHandler("USER_sync", new UserSyncExecutable(
				processEngine));

		// execute
		execute(processEngine, 100000);

		assertTrue(processEngine.isEmpty());
		// assertTrue(processEngine.size() == 2);
	}

}
