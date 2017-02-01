package eu.fbk.das.process.engine.impl;

import java.util.List;

import eu.fbk.das.composer.api.exceptions.InvalidServiceInitialStateException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceTransitionException;
import eu.fbk.das.composer.api.exceptions.ServiceDuplicateActionException;
import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.impl.util.Parser;

/**
 * Utility class for conversion between classes use {@link Parser}
 */
public final class Converter {

    private static Parser parser = new Parser();

    private Converter() {
    }

    /**
     * Convert an entity Definition into a runnable ProcessDiagram by
     * {@link ProcessEngine}
     * 
     * @param ed
     *            - {@link DomainObjectDefinition}
     * @param dirs
     * @return {@link ProcessDiagram}
     * 
     * @throws InvalidFlowInitialStateException
     * @throws InvalidFlowActivityException
     * @throws FlowDuplicateActivityException
     * @throws ServiceDuplicateActionException
     * @throws InvalidServiceTransitionException
     * @throws InvalidServiceInitialStateException
     */
    public static DomainObjectInstance convertToDomainObjectInstance(
	    DomainObjectDefinition ed, List<String> dirs)
	    throws InvalidFlowInitialStateException,
	    InvalidFlowActivityException, FlowDuplicateActivityException,
	    InvalidServiceInitialStateException,
	    InvalidServiceTransitionException, ServiceDuplicateActionException {
	DomainObjectInstance doi = new DomainObjectInstance();
	doi.setType(ed.getDomainObject().getName());
	if (ed.getDomainObject().getDomainKnowledge() != null) {
	    doi.setInternalKnowledge(parser.convertInternalToObjectDiagrams(ed
		    .getDomainObject().getDomainKnowledge()
		    .getInternalDomainProperty(), dirs));
	    doi.setExternalKnowledge(parser.convertExternalToObjectDiagrams(ed
		    .getDomainObject().getDomainKnowledge()
		    .getExternalDomainProperty(), dirs));
	}
	if (ed.getFragments() != null) {
	    doi.setFragments(parser.convertToServiceDiagram(ed.getFragments()));
	}
	ProcessDiagram process = parser.convertToProcessDiagram(ed.getProcess()
		.getActivity(), ed.getProcess().getName());
	doi.setProcess(process);
	doi.setRole(ed.isRole());
	doi.setSingleton(ed.getDomainObject().isSingleton());
	return doi;
    }

    // private static ProcessDiagram convertScope(ScopeType scope,
    // ProcessDiagram process) throws InvalidFlowInitialStateException,
    // InvalidFlowActivityException, FlowDuplicateActivityException {
    // if (process == null) {
    // return process;
    // }
    // if (scope == null) {
    // return process;
    // }
    // List<ProcessActivity> activities = parser.parseActivities(
    // scope.getActivity(), scope);
    // if (activities != null && !activities.isEmpty()) {
    // ProcessActivity last = activities.get(activities.size() - 1);
    // int source = last.getTarget();
    // for (ProcessActivity pa : activities) {
    // pa.setSource(source);
    // pa.setTarget(source + 1);
    // process.addActivity(pa);
    // source++;
    // }
    // }
    // if (scope.getScope() != null) {
    // for (ScopeType s : scope.getScope()) {
    // process = convertScope(s, process);
    // }
    // }
    // return process;
    // }
}
