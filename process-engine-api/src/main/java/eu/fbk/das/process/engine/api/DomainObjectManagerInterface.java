package eu.fbk.das.process.engine.api;

import java.util.List;

import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.ClauseType.Point.DomainProperty;

public interface DomainObjectManagerInterface {

	public static final String ID_SEPARATOR = "_";

	public DomainObjectInstance buildInstance(DomainObjectDefinition dod);

	public void remove(ProcessDiagram process);

	public void clearProcessDefinition();

	public void registerProcessToDoi(DomainObjectInstance doi);

	public DomainObjectInstance findInstance(int pid);

	public List<DomainObjectDefinition> getDefinitions();

	public List<DomainObjectInstance> getInstances();

	public DomainObjectDefinition findDefinition(ProcessDiagram p);

	public List<DomainObjectDefinition> findDomainObjectDefinitionWithInternalKnowledge(
			String dpName);

	public List<DomainObjectInstance> findInstance(DomainObjectDefinition dos);

	public DomainObjectInstance findInstanceById(String value);

	public boolean copyDomainPropertyInstanceToDoi(DomainObjectInstance doi,
			DomainObjectInstance otherDoi);

	public boolean isKnowledgeIn(DomainObjectInstance doi, DomainProperty dp);

	public DomainObjectDefinition load(String file)
			throws NullPointerException, ProcessEngineRuntimeException;

	public DomainObjectDefinition add(DomainObjectDefinition load)
			throws ProcessEngineRuntimeException;

	public DomainObjectInstance buildInstance(
			DomainObjectDefinition ed,
			eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d,
			List<String> asList);

	public DomainObjectDefinition findDefinition(String name);

	DomainObjectInstance convertToDomainObjectInstance(
			DomainObjectDefinition dod);

	public List<Integer> getPids(List<DomainObjectInstance> ins);

	public DomainObjectInstance findInstanceByType(String type);

	public DomainObjectDefinition findDefinitionByFragment(String type);

	public List<DomainObjectInstance> findAllInstanceByType(String type);

	public DomainObjectInstance findInstanceRecursive(ProcessDiagram current);

	public void addExternalKnowledge(DomainObjectInstance doi, String dpName,
			String initialState, String workingFolder);

	public void extendDoiState(String scopeId, DomainObjectInstance doi,
			DomainObjectInstance otherDoi, String key);

}
