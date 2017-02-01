package eu.fbk.das.process.engine.api;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectEventException;
import eu.fbk.das.process.engine.api.jaxb.DpChangeType;
import eu.fbk.das.process.engine.api.jaxb.EventHandlerType;
import eu.fbk.das.process.engine.api.jaxb.OnDPchangeType.DomainProperty;
import eu.fbk.das.process.engine.api.jaxb.Process;
import eu.fbk.das.process.engine.api.jaxb.ScopeType;

/**
 * Handle all {@link ScopeType} for a {@link Process}, including
 * {@link EventHandlerType}
 */
public class ScopeManager {

    private static final Logger logger = LogManager
	    .getLogger(ScopeManager.class);

    private CommunicationManager communicationManager;

    private DomainObjectManagerInterface dom;

    public ScopeManager(CommunicationManager communicationManager,
	    DomainObjectManagerInterface dom) {
	this.communicationManager = communicationManager;
	this.dom = dom;
    }

    public void handle(ProcessDiagram proc, ProcessActivity current,
	    ScopeType scope) {
	if (current == null || proc == null || scope == null) {
	    logger.warn("null input");
	    return;
	}
	DomainObjectInstance doi = dom.findInstance(proc.getpid());
	if (doi == null) {
	    logger.warn("Impossible to find domain object instance with process with pid "
		    + proc.getpid());
	    return;
	}
	logger.debug("ScopeManager - start");
	for (EventHandlerType eh : scope.getEventHandler()) {
	    // check guards
	    if (eh.getOnExternalEvent() != null) {
		String eventName = eh.getOnExternalEvent().getOnEventName();
		if (thereIsExternalEvent(eventName)) {
		    logger.debug("ScopeManager - external event with name "
			    + eventName);
		    // execute action
		    execute(proc, eh.getDpChange(), doi);
		    // remove msg
		    removeMsg(eventName);
		}
	    } else if (eh.getOnDPchange() != null) {
		for (DomainProperty dp : eh.getOnDPchange().getDomainProperty()) {
		    if (dpIsInState(proc, dp, doi)) {
			// executeAction
			execute(proc, eh.getDpChange(), doi);
		    }
		}
	    }

	}
	logger.debug("ScopeManager - end");
    }

    private boolean dpIsInState(ProcessDiagram proc, DomainProperty dp,
	    DomainObjectInstance doi) {
	if (doi.getInternalKnowledge() == null) {
	    logger.warn("No internal knowledge for doi " + doi.getId());
	    return false;
	}
	for (ObjectDiagram know : doi.getInternalKnowledge()) {
	    if (know.getOid().equals(dp.getDpName())) {
		if (know.getCurrentState().equals(dp.getState().get(0))) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean thereIsExternalEvent(String eventName) {
	for (Message msg : communicationManager.getMessages()) {
	    if (msg instanceof ExternalIssue) {
		if (((ExternalIssue) msg).getIssue().equals(eventName)) {
		    return true;
		}
	    }
	}
	return false;
    }

    private void removeMsg(String eventName) {
	Iterator<Message> iter = communicationManager.getMessages().iterator();
	while (iter.hasNext()) {
	    Message msg = iter.next();
	    if (msg instanceof ExternalIssue) {
		if (((ExternalIssue) msg).getIssue().equals(eventName)) {
		    iter.remove();
		    break;
		}
	    }
	}
    }

    private void execute(ProcessDiagram proc, DpChangeType dpChange,
	    DomainObjectInstance doi) {
	try {
	    for (ObjectDiagram dp : doi.getInternalKnowledge()) {
		if (dp.getOid().equals(dpChange.getDpName())) {
		    logger.debug("[" + proc.getpid()
			    + "] Prima applicazione evento "
			    + dp.getCurrentState());
		    dp.publishEvent(dpChange.getEventName());
		    logger.debug("[" + proc.getpid()
			    + "] Dopo applicazione evento "
			    + dp.getCurrentState());

		}
	    }
	} catch (InvalidObjectEventException
		| InvalidObjectCurrentStateException e) {
	    logger.error(e.getMessage(), e);
	}

    }
}
