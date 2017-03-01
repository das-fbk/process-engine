package eu.fbk.das.process.engine.impl.handlers;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.ReplyActivity;
import eu.fbk.das.process.engine.api.jaxb.VariableType;

/**
 * @author Martina
 *
 */
/**
 * @author Martina
 *
 */
public class ReplyActivityHandler extends AbstractHandler {

	private static final Logger logger = LogManager
			.getLogger(ReplyActivityHandler.class);

	@Override
	public void handle(ProcessEngine pe, ProcessDiagram proc,
			ProcessActivity current) {

		logger.debug("[" + proc.getpid() + "] receive for " + current.getName());
		ReplyActivity receive = (ReplyActivity) current;
		boolean prec = handlePrecondition(pe, proc, current);
		if (!prec) {
			logger.debug("[" + proc.getpid()
					+ "] Precondizioni non soddisfatte, receive non eseguita");
			return;
		}
		String msg = receive.getName();
		// NB: I use current instead of receive to be aligned with what I did in
		// the InvokeActivityHandler
		if (thereAreMessageAndVariablesForMe(pe, proc, msg, current)) {
			receive.setExecuted(true);
			// add the completed activity to the execution history
			proc.addExecuteActivity(receive);

			logger.debug("[" + proc.getpid() + "] receive executed");
			handleEffect(pe, proc, current);
		} else {
			logger.debug("[" + proc.getpid()
					+ "] receive waiting for a message");
		}
	}

	/**
	 * @param pe
	 * @param proc
	 * @param msg
	 * @param current
	 * @return true if there is at least a message for the reply activity
	 * 
	 *         With this method he reply activity waits for being awakened
	 *         through the reception of a specific message for it. When it wakes
	 *         up, it also checks if the corresponding Invoke Activity send
	 *         variables to it. If true, it updates its variables - this is a
	 *         bit different depending on the reply's father, if it is a process
	 *         or a fragment. At the end, the reply also updates the state of
	 *         the domain object in which it is in execution.
	 */
	private boolean thereAreMessageAndVariablesForMe(ProcessEngine pe,
			ProcessDiagram proc, String msg, ProcessActivity current) {
		DomainObjectInstance doi = pe.getDomainObjectInstance(proc);
		if (!pe.getMsgAndVariables(doi).isEmpty()) {
			List<Map<String, List<VariableType>>> mapMsgAndVar = pe
					.getMsgAndVariables(doi);
			Iterator<Map<String, List<VariableType>>> it = mapMsgAndVar
					.iterator();
			while (it.hasNext()) {
				Map<String, List<VariableType>> map = it.next();
				String[] keys = (map.keySet()).toArray(new String[0]);
				if (contains(keys, msg)) {
					/************************************************************/
					// check also for variables and distinguish between a
					// process reply and a fragment reply
					if (current.getServiceType() != null) {
						// the activity is a reply in a fragment
						current.setServiceActionVariables(map.get(msg));
					} else {
						current.setActionVariables(map.get(msg));
					}
					// write the variables in the domain object state
					writeTheDomainObjectState(doi, current);
					/****************************************************************/
					pe.removeMessageAndVariables(doi, map);
					// removeMessage(doi, msg);
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * @param doi
	 * @param current
	 * 
	 *            With this method the reply activity updates the state of the
	 *            domain object that is executing it. In particular, a Reply
	 *            activity in a core process will update the state of the DO
	 *            which it belongs to. A Reply activity in a fragment will
	 *            update the state of the DO in which it is in execution as part
	 *            of the refinement of an abstract activity.
	 * 
	 */
	private void writeTheDomainObjectState(DomainObjectInstance doi,
			ProcessActivity current) {
		List<VariableType> activityVariables;
		if (doi.getState() != null) {
			if (!doi.getState().getStateVariable().isEmpty()) {
				if (current.getServiceType() != null) {
					activityVariables = current.getServiceActionVariables();
				} else {
					activityVariables = current.getActionVariables();
				}
				for (VariableType actionVar : activityVariables) {
					if (doi.hasVariableWithName(actionVar.getName())) {
						int index = doi.getIndexOfVariableWithName(actionVar
								.getName());
						Element eleNsImplObject = (Element) actionVar
								.getContent();
						doi.getState().getStateVariable().get(index)
								.setContent(eleNsImplObject);
						Element e = (Element) (doi.getState()
								.getStateVariable().get(index).getContent());
						logger.debug(e.getFirstChild().getNodeValue());
					}
					// int i = DatatypeConverter.parseInt(content);
				}
			}
		}
	}

	private boolean contains(String[] keys, String key) {
		if (keys.length != 0) {
			for (int i = 0; i < keys.length; i++) {
				if (keys[i].equals(key))
					return true;
			}
		}
		return false;
	}

}
