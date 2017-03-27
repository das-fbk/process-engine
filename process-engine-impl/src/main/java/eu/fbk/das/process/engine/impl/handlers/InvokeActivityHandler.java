package eu.fbk.das.process.engine.impl.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.ProcessRequest;
import eu.fbk.das.process.engine.api.domain.InvokeActivty;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivityType;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.jaxb.VariableType;

/**
 * Handle an Invoke Activity
 * 
 * @see InvokeActivty
 *
 */
public class InvokeActivityHandler extends AbstractHandler {

	private static final Logger logger = LogManager
			.getLogger(InvokeActivityHandler.class);
	List<VariableType> currentActivityVariables;

	@Override
	public void handle(ProcessEngine pe, ProcessDiagram proc,
			ProcessActivity current) {

		logger.debug("[" + proc.getpid()
				+ "] invoke for a process with name : " + current.getName());
		boolean prec = handlePrecondition(pe, proc, current);
		if (!prec) {
			logger.debug("[" + proc.getpid()
					+ "] Precondizioni non soddisfatte, invoke non eseguita");
			return;
		}

		this.currentActivityVariables = new ArrayList<VariableType>();

		// management of the variables in the activities of type Invoke
		manageVariablesForInvokeActivity(pe, proc, current);

		ProcessRequest pr = new ProcessRequest();
		pr.setPid(proc.getpid());
		pr.setDomainObjectInstance(pe.getDomainObjectInstance(proc));
		pr.setName(current.getName());
		pr.setType(ProcessActivityType.REPLY);
		// passing variables from the Invoke to the corresponding Reply
		pr.setVariables(currentActivityVariables);
		pe.addRequest(pr);

		current.setExecuted(true);
		// add the completed activity to the execution history
		proc.addExecuteActivity(current);

		// handle effects
		handleEffect(pe, proc, current);
	}

	/**
	 * @param pe
	 * @param proc
	 * @param current
	 *            This method distinguish between an Invoke Activity of a
	 *            fragment and an Invoke Activity of a process, in order to
	 *            invoke different behaviors. Fragment's Invoke Activity must
	 *            only pass their variables to the same variables in the
	 *            corresponding Reply Activity. Process' Invoke Activity instead
	 *            must look in the domain object state for the variables that it
	 *            hold and copy their values in its variables, before passing
	 *            them to the corresponding Reply Activity.
	 */
	private void manageVariablesForInvokeActivity(ProcessEngine pe,
			ProcessDiagram proc, ProcessActivity current) {
		boolean isServiceActivity = false;
		DomainObjectInstance currentDoi = pe.getDomainObjectInstance(proc);

		// copy service action variable from the DO's state
		checkVariablesOfAnInvokeActivity(pe, current, currentDoi);

		if (current.getServiceType() != null) {
			isServiceActivity = true;
		}
		if (isServiceActivity) {
			// the invoke activity belongs to a fragment.
			this.currentActivityVariables = current.getServiceActionVariables();
		} else {
			// the invoke activity belongs to a core process.
			this.currentActivityVariables = current.getActionVariables();
		}
	}

	// private void checkVariablesOfAnInvokeServiceActivity(ProcessEngine pe,
	// ProcessActivity current, DomainObjectInstance doi) {
	//
	// // the invoke activity updates its variables with the values of the
	// // corresponding
	// // variables in the domain object state (Some
	// // previous executed activity has written these variables probably.)
	// if (doi.getState() != null) {
	// if (!doi.getState().getStateVariable().isEmpty()) {
	// if (doi.hasSameVariablesOfCurrentActivity(current)) {
	// copyVariablesFromAdomainObjectState(current, doi);
	// } else {
	// // find the domain object father of the fragment under
	// // execution
	// DomainObjectInstance fatherDoi = new DomainObjectInstance();
	// DomainObjectDefinition fatherDod = pe
	// .getDefinitionByFragment(current.getServiceType());
	// List<DomainObjectInstance> listDoi = pe.getCorrelated(doi);
	// for (DomainObjectInstance tempDoi : pe
	// .getDomainObjectInstance(fatherDod)) {
	// if (listDoi.contains(tempDoi)) {
	// fatherDoi = tempDoi;
	// }
	// }
	// if (fatherDoi != null) {
	// copyVariablesFromAdomainObjectState(current, fatherDoi);
	// }
	//
	// }
	// }
	// }
	// }

	private void checkVariablesOfAnInvokeActivity(ProcessEngine pe,
			ProcessActivity current, DomainObjectInstance doi) {

		// the invoke activity updates its variables with the values of the
		// corresponding
		// variables in the domain object state that is executing it.
		if (doi.getState() != null) {
			if (!doi.getState().getStateVariable().isEmpty()) {
				copyVariablesFromTheDomainObjectState(current, doi);
			}
		}
	}

	private void copyVariablesFromTheDomainObjectState(ProcessActivity current,
			DomainObjectInstance doi) {
		List<VariableType> var;
		if (current.getServiceType() != null) {
			var = current.getServiceActionVariables();
		} else {
			var = current.getActionVariables();
		}

		for (VariableType actionVar : var) {
			if (doi.hasVariableWithName(actionVar.getName())) {
				Element eleNsImplObject = doi
						.getStateVariableContentByName(actionVar.getName());

				// To get the string content of the Element use the
				// following:
				// eleNsImplObject.getFirstChild().getNodeValue();
				actionVar.setContent(eleNsImplObject);
				// Element e = (Element) (actionVar.getContent());
				// logger.debug(e.getFirstChild().getNodeValue());
			}
		}
	}
}
