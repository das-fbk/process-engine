package eu.fbk.das.process.engine.impl.handlers;

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

		this.currentActivityVariables = null;

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
		String isServiceActivity = "false";
		DomainObjectInstance currentDoi = pe.getDomainObjectInstance(proc);
		if (current.getServiceType() != null) {
			isServiceActivity = "true";
		}
		switch (isServiceActivity) {
		case ("true"):
			// the invoke activity belongs to a fragment
			this.currentActivityVariables = current.getServiceActionVariables();
			break;

		case ("false"):
			// the invoke activity belongs to a process
			checkVariablesOfAnInvokeProcessActivity(pe, current, currentDoi);
			this.currentActivityVariables = current.getActionVariables();
			break;
		}

	}

	private void checkVariablesOfAnInvokeProcessActivity(ProcessEngine pe,
			ProcessActivity current, DomainObjectInstance doi) {

		// the invoke activity updates its variables with the values of the
		// corresponding
		// variables in the domain object state (Some
		// previous executed activity has written these variables probably.)
		if (doi.getState() != null) {
			if (!doi.getState().getStateVariable().isEmpty()) {
				for (VariableType actionVar : current.getActionVariables()) {
					if (doi.hasVariableWithName(actionVar.getName())) {
						int index = doi.getIndexOfVariableWithName(actionVar
								.getName());
						Element eleNsImplObject = (Element) doi.getState()
								.getStateVariable().get(index).getContent();
						// To get the string content of the Element use the
						// following:
						// eleNsImplObject.getFirstChild().getNodeValue();
						actionVar.setContent(eleNsImplObject);
						Element e = (Element) (current.getActionVariables()
								.get(0).getContent());
						logger.debug(e.getFirstChild().getNodeValue());
					}
				}
			}
		}
	}

}
