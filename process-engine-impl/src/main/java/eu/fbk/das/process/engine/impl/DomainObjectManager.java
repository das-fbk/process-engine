package eu.fbk.das.process.engine.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.composer.api.exceptions.InvalidServiceInitialStateException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceTransitionException;
import eu.fbk.das.composer.api.exceptions.ServiceDuplicateActionException;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.ActionTypeValues;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point;
import eu.fbk.das.process.engine.api.jaxb.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.DomainObject.DomainKnowledge.ExternalDomainProperty;
import eu.fbk.das.process.engine.api.jaxb.DomainObject.DomainKnowledge.InternalDomainProperty;
import eu.fbk.das.process.engine.api.jaxb.DomainObject.State;
import eu.fbk.das.process.engine.api.jaxb.DomainProperty;
import eu.fbk.das.process.engine.api.jaxb.Fragment;
import eu.fbk.das.process.engine.api.jaxb.Fragment.Action;
import eu.fbk.das.process.engine.api.jaxb.Process;
import eu.fbk.das.process.engine.api.jaxb.VariableType;
import eu.fbk.das.process.engine.impl.util.FileUtil;
import eu.fbk.das.process.engine.impl.util.Parser;

/**
 * Manage Domain Object definition loaded from file and instances
 */
public class DomainObjectManager implements DomainObjectManagerInterface {

	private static final Logger logger = LogManager
			.getLogger(DomainObjectManager.class);

	// handle all domain object definitions
	private List<DomainObjectDefinition> definitions = new ArrayList<DomainObjectDefinition>();
	// handle all domain object instances
	private List<DomainObjectInstance> instances = new ArrayList<DomainObjectInstance>();

	private Parser parser = new Parser();

	private Map<ProcessDiagram, DomainObjectDefinition> processToDefinition = new HashMap<ProcessDiagram, DomainObjectDefinition>();

	private Map<Integer, DomainObjectInstance> processToInstance = new HashMap<Integer, DomainObjectInstance>();

	private List<String> dirs;

	private int idGenerator = 1;

	private Map<DomainObjectDefinition, ArrayList<DomainObjectInstance>> definitionToInstance = new HashMap<DomainObjectDefinition, ArrayList<DomainObjectInstance>>();

	/**
	 * Create EntityManager
	 * 
	 * @param dirs
	 *            - a set of dirs where search for process and fragments
	 *            definitionss
	 */
	public DomainObjectManager(List<String> dirs) {
		this.dirs = dirs;
	}

	/**
	 * Load {@link Entity} from file
	 * 
	 * @param ref
	 *            - name of the file
	 * @param dirs
	 *            - list of directories to check for the file
	 * @return Entity
	 * @throws NullPointerException
	 *             - if ref or dirs is null
	 * @throws ProcessEngineRuntimeException
	 */
	@Override
	public DomainObjectDefinition load(String ref) throws NullPointerException,
			ProcessEngineRuntimeException {
		if ((ref == null || ref.isEmpty()) || (dirs == null || dirs.isEmpty())) {
			throw new NullPointerException();
		}
		File f = FileUtil.findFile(ref, dirs);
		if (f == null) {
			return null;
		}
		DomainObject e = parser.parseDomainObject(f);

		Process process = loadProcess(e, dirs);

		// load services
		List<Fragment> fragments = loadFragments(e, dirs);

		// load domain properties
		List<DomainProperty> properties = loadProperties(e, dirs);

		// load state
		State state = e.getState();

		// assembly response
		DomainObjectDefinition ed = new DomainObjectDefinition();
		ed.setDomainObject(e);
		ed.setRole(process.isRole());
		ed.setProcess(process);
		ed.setFragments(fragments);
		ed.setProperties(properties);
		ed.setState(state);
		return ed;

	}

	private List<DomainProperty> loadProperties(DomainObject e,
			List<String> dirs) {
		if (e == null || e.getDomainKnowledge() == null) {
			return null;
		}
		List<DomainProperty> result = new ArrayList<DomainProperty>();
		// internal
		if (e.getDomainKnowledge().getInternalDomainProperty() != null) {
			List<InternalDomainProperty> internal = e.getDomainKnowledge()
					.getInternalDomainProperty();
			for (InternalDomainProperty dp : internal) {
				File f = FileUtil.findFile(dp.getName(), dirs);
				if (f != null) {
					DomainProperty d = parser.parseDomainProperties(f);
					result.add(d);
				}
			}
		}
		// external
		if (e.getDomainKnowledge().getExternalDomainProperty() != null) {
			List<ExternalDomainProperty> external = e.getDomainKnowledge()
					.getExternalDomainProperty();
			for (ExternalDomainProperty dp : external) {
				File f = FileUtil.findFile(dp.getName(), dirs);
				if (f != null) {
					DomainProperty d = parser.parseDomainProperties(f);
					result.add(d);
				}
			}
		}
		return result;
	}

	private List<Fragment> loadFragments(DomainObject e, List<String> dirs) {
		if (e == null || e.getProcess() == null || e.getFragment() == null) {
			return null;
		}
		List<Fragment> response = new ArrayList<Fragment>();
		for (eu.fbk.das.process.engine.api.jaxb.DomainObject.Fragment fragment : e
				.getFragment()) {
			File f = FileUtil.findFile(fragment.getName(), dirs);
			if (f != null) {
				response.add(parser.parseFragment(f));
			}
		}
		return response;
	}

	private Process loadProcess(DomainObject e, List<String> dirs)
			throws ProcessEngineRuntimeException {
		if (e == null) {
			return null;
		}
		if (e.getProcess() == null) {
			logger.error("Process must be not null");
			throw new ProcessEngineRuntimeException("Process must be not null");
		}
		File f = FileUtil.findFile(e.getProcess().getName(), dirs);
		if (f != null) {
			Process p = parser.parseProcess(f);
			return p;
		}
		return null;
	}

	@Override
	public DomainObjectDefinition add(DomainObjectDefinition ed)
			throws ProcessEngineRuntimeException {
		// validate process
		if (ed == null) {
			logger.warn("Process must be not null");
			throw new ProcessEngineRuntimeException("Process must be not null");
		}
		// add entity definition
		definitions.add(ed);
		logger.debug("Process definition added correctly ");
		return ed;
	}

	@Override
	public List<DomainObjectDefinition> getDefinitions() {
		return definitions;
	}

	/**
	 * From a Domain Object Definition create a default instance and register it
	 * into manager
	 * 
	 * @param dod
	 * @return created instance
	 */
	@Override
	public DomainObjectInstance buildInstance(DomainObjectDefinition dod) {
		try {
			DomainObjectInstance doi = Converter.convertToDomainObjectInstance(
					dod, dirs);
			doi.setId(doi.getType() + ID_SEPARATOR + +idGenerator);
			idGenerator++;
			register(dod, doi);
			return doi;
		} catch (InvalidFlowInitialStateException
				| InvalidFlowActivityException | FlowDuplicateActivityException
				| InvalidServiceInitialStateException
				| InvalidServiceTransitionException
				| ServiceDuplicateActionException e) {
			logger.error(e);
			return null;
		}
	}

	/**
	 * Convert from definition to DomainObjectInstance, don't register into
	 * DomainObjectManager
	 */
	@Override
	public DomainObjectInstance convertToDomainObjectInstance(
			DomainObjectDefinition dod) {
		try {
			return Converter.convertToDomainObjectInstance(dod, dirs);
		} catch (InvalidFlowInitialStateException
				| InvalidFlowActivityException | FlowDuplicateActivityException
				| InvalidServiceInitialStateException
				| InvalidServiceTransitionException
				| ServiceDuplicateActionException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public void clearProcessDefinition() {
		processToDefinition.clear();
	}

	@Override
	public void remove(ProcessDiagram process) {
		if (processToDefinition.containsKey(process)) {
			definitionToInstance.remove(processToDefinition.get(process));
			processToDefinition.remove(process);
			processToInstance.remove(process.getpid());
		}
	}

	/**
	 * @return entity definition for given {@link ProcessDiagram}
	 */
	@Override
	public DomainObjectDefinition findDefinition(ProcessDiagram p) {
		if (p != null) {
			return processToDefinition.get(p);
		}
		return null;
	}

	@Override
	public List<DomainObjectInstance> getInstances() {
		return instances;
	}

	@Override
	public DomainObjectInstance findInstance(int pid) {
		return processToInstance.get(pid);
	}

	// useful for testing, for real code use other buildInstance
	@Override
	public DomainObjectInstance buildInstance(
			DomainObjectDefinition ed,
			eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d,
			List<String> dirs) {
		DomainObjectInstance doi = buildInstance(ed);
		doi.setId(d.getId());
		doi.setLat(d.getLat());
		doi.setLon(d.getLong());
		doi.setOnTurn(d.getOnTurn());
		if (d.getProcess() != null
				&& d.getProcess().getCurentActivity() != null) {
			if (doi.getProcess() != null) {
				ProcessDiagram process = doi.getProcess();
				ProcessActivity found = process.findProcessActivity(d);
				if (found == null) {
					logger.error("Impossible to found activity with name "
							+ d.getProcess().getCurentActivity());
					return null;
				}
				process.setCurrentActivity(found);
			} else {
				logger.warn("Process " + doi.getProcess().getName()
						+ " is null");
				return null;
			}
		}
		doi.setRole(ed.isRole());
		doi.setSingleton(ed.getDomainObject().isSingleton());
		// if (d.getPickupPoint() != null) {
		// doi.setPickupPoint(d.getPickupPoint());
		// }
		register(ed, doi);
		return doi;
	}

	public void register(DomainObjectDefinition ed, DomainObjectInstance doi) {
		if (!instances.contains(doi)) {
			instances.add(doi);
		}
		if (!processToInstance.containsKey(doi.getProcess())) {
			processToInstance.put(doi.getProcess().getpid(), doi);
		}
		if (!processToDefinition.containsKey(doi.getProcess())) {
			processToDefinition.put(doi.getProcess(), ed);
		}
		if (!definitionToInstance.containsKey(ed)) {
			if (definitionToInstance.get(ed) == null) {
				definitionToInstance.put(ed,
						new ArrayList<DomainObjectInstance>());
			}
		}
		if (!definitionToInstance.get(ed).contains(doi)) {
			definitionToInstance.get(ed).add(doi);
		}
	}

	@Override
	public void registerProcessToDoi(DomainObjectInstance doi) {
		if (!processToInstance.containsKey(doi.getProcess())) {
			processToInstance.put(doi.getProcess().getpid(), doi);
		}
	}

	@Override
	public List<DomainObjectDefinition> findDomainObjectDefinitionWithInternalKnowledge(
			String dpName) {
		List<DomainObjectDefinition> response = new ArrayList<DomainObjectDefinition>();
		for (DomainObjectDefinition dod : definitions) {
			if (dod.getDomainObject().getDomainKnowledge() != null) {
				for (InternalDomainProperty idp : dod.getDomainObject()
						.getDomainKnowledge().getInternalDomainProperty()) {
					if (idp.getName().endsWith(dpName)) {
						if (!response.contains(dod)) {
							response.add(dod);
						}
					}
				}
			}
		}
		return response;
	}

	@Override
	public List<DomainObjectInstance> findInstance(DomainObjectDefinition dos) {
		return definitionToInstance.get(dos);
	}

	@Override
	public DomainObjectInstance findInstanceById(String value) {
		for (DomainObjectInstance doi : instances) {
			if (doi.getId().equals(value)) {
				return doi;
			}
		}
		return null;
	}

	/**
	 * return true if no problem
	 */
	@Override
	public boolean copyDomainPropertyInstanceToDoi(DomainObjectInstance doi,
			DomainObjectInstance otherDoi) {
		// partendo da otherDoi, vado a cercare la definizione e se la
		// definition ha un frammento con attivita' astratta, copio l'istanza
		// della domainProperty in doi
		// cerco la definizione di other doi
		if (otherDoi.getId().equals("TripOrganization_12")) {
			System.out.println();
		}
		DomainObjectDefinition dod = findDefinition(otherDoi);
		if (dod == null) {
			logger.warn("DomainObjectDefinition not found");
			return false;
		}
		// per ogni frammento con attivita' astratta della definizione di
		// otherDoi, copio l'istanza di domainProperty (serviceDiagram) in doi
		for (Fragment f : dod.getFragments()) {
			for (Action a : f.getAction()) {
				if (a.getGoal() != null
						&& a.getActionType() == ActionTypeValues.ABSTRACT) {
					// abstract found
					for (Point p : a.getGoal().getPoint()) {
						for (eu.fbk.das.process.engine.api.jaxb.ClauseType.Point.DomainProperty dp : p
								.getDomainProperty()) {
							ObjectDiagram sd = findServiceDiagram(otherDoi,
									dp.getDpName());
							if (sd != null
									&& !contains(doi.getExternalKnowledge(), sd)) {
								doi.getExternalKnowledge().add(sd);
							}
						}
					}
				}
			}
		}
		return true;

	}

	private boolean contains(List<ObjectDiagram> externalKnowledge,
			ObjectDiagram sd) {
		for (ObjectDiagram od : externalKnowledge) {
			if (od.getOid().equals(sd.getOid())) {
				return true;
			}
		}

		return false;
	}

	private ObjectDiagram findServiceDiagram(DomainObjectInstance doi,
			String name) {
		for (ObjectDiagram sd : doi.getExternalKnowledge()) {
			if (sd.getType().equals(name)) {
				return sd;
			}
		}
		return null;
	}

	private DomainObjectDefinition findDefinition(DomainObjectInstance doi) {
		Set<DomainObjectDefinition> set = definitionToInstance.keySet();
		for (DomainObjectDefinition dod : set) {
			if (definitionToInstance.get(dod) != null) {
				List<DomainObjectInstance> ds = definitionToInstance.get(dod);
				for (DomainObjectInstance d : ds) {
					if (d.getId().equals(doi.getId())) {
						return dod;
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean isKnowledgeIn(
			DomainObjectInstance doi,
			eu.fbk.das.process.engine.api.jaxb.ClauseType.Point.DomainProperty dp) {
		for (ObjectDiagram internal : doi.getInternalKnowledge()) {
			if (internal.getOid().equals(dp.getDpName())) {
				return internal.getCurrentState().equals(dp.getState().get(0));
			}
		}
		return false;
	}

	@Override
	public DomainObjectDefinition findDefinition(String name) {
		for (DomainObjectDefinition dod : definitions) {
			if (dod.getDomainObject().getName().equals(name)) {
				return dod;
			}
		}
		return null;
	}

	@Override
	public List<Integer> getPids(List<DomainObjectInstance> ins) {
		if (ins == null) {
			return new ArrayList<Integer>();
		}
		List<Integer> response = new ArrayList<Integer>();
		for (DomainObjectInstance doi : ins) {
			if (doi.getProcess() != null) {
				response.add(doi.getProcess().getpid());
			}
		}
		return response;
	}

	@Override
	public DomainObjectInstance findInstanceByType(String type) {
		for (DomainObjectInstance doi : instances) {
			if (doi.getType().equals(type)) {
				return doi;
			}
		}
		return null;
	}

	@Override
	public List<DomainObjectInstance> findAllInstanceByType(String type) {
		return instances.stream().filter(doi -> doi.getType().equals(type))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public DomainObjectInstance findInstanceRecursive(ProcessDiagram current) {
		if (current == null) {
			return null;
		}
		DomainObjectInstance doi = findInstance(current.getpid());
		if (doi != null) {
			return doi;
		}
		return findInstanceRecursive(current.getFather());
	}

	@Override
	public void addExternalKnowledge(DomainObjectInstance doi, String dpName,
			String initialState, String workingFolder) {
		try {
			eu.fbk.das.composer.api.Parser parser = new eu.fbk.das.composer.api.Parser(
					workingFolder, workingFolder);
			ObjectDiagram objectDiagram = parser.parseObjectDiagram(dpName,
					dpName);
			objectDiagram.setCurrentState(initialState);
			boolean found = false;
			for (ObjectDiagram ek : doi.getExternalKnowledge()) {
				if (ek.getOid().equals(dpName)) {
					found = true;
				}
			}
			if (!found) {
				doi.getExternalKnowledge().add(objectDiagram);
			}
		} catch (InvalidObjectCurrentStateException e) {
			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public DomainObjectDefinition findDefinitionByFragment(String type) {
		DomainObjectDefinition result = new DomainObjectDefinition();

		for (DomainObjectDefinition dod : this.definitions) {
			if (dod.getFragments() != null) {
				for (Fragment fragment : dod.getFragments()) {
					if (fragment.getId().equals(type)) {
						result = dod;
					}
				}
			}
		}
		return result;
	}

	@Override
	public void extendDoiState(DomainObjectInstance doi,
			DomainObjectInstance otherDoi, String key) {
		// partendo da otherDoi, vado a cercare il frammento con nome key,
		// scorro il frammento e cerco le variabili sulle sue attività.
		List<VariableType> extendedState = new ArrayList<VariableType>();
		DomainObjectDefinition dod = findDefinition(otherDoi);
		if (dod == null) {
			logger.warn("DomainObjectDefinition not found");
		}
		// look for fragments involved in the refinement
		for (Fragment f : dod.getFragments()) {
			if (f.getId().equals(key)) {
				// for each actions we take action variables, if any
				for (Action a : f.getAction()) {
					if (a.getActionVariable() != null
							&& !a.getActionVariable().isEmpty()) {
						// VariableType newVar = new VariableType();
						// for (VariableType v : a.getActionVariable()) {
						// newVar.setName(v.getName());
						// // Element content = (Element) v.getContent();
						// newVar.setContent(v.getContent());
						// extendedState.add(newVar);
						// }
						extendedState.addAll(a.getActionVariable());
					}
				}
			}
		}
		// for each collected variables, we check if it must be added to the DO
		// state or not
		doi.extendInternalState(extendedState);
	}
}
