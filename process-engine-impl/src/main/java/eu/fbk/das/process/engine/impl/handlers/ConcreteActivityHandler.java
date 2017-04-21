package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.ConcreteActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

public class ConcreteActivityHandler extends AbstractHandler {

	private static final Logger logger = LogManager
			.getLogger(ConcreteActivityHandler.class);

	@Override
	public void handle(ProcessEngine pe, ProcessDiagram proc,
			ProcessActivity current) {
		if (current.getName().equals("TA_CheckLegSet")) {
			System.out.println();
		}
		int x = 0;
		boolean prec = handlePrecondition(pe, proc, current);
		if (!prec) {
			logger.debug("[" + proc.getpid()
					+ "] Precondizioni non soddisfatte, Concrete non eseguita");
			return;
		}

		logger.debug("[" + proc.getpid() + "] Esecuzione Concrete");
		ConcreteActivity concrete = (ConcreteActivity) current;
		// try first with registered executable activity handlers
		if (pe.getExecutableHandler(concrete.getName(), proc.getpid()) != null) {
			logger.debug("Using executable handler for concrete activity: "
					+ concrete.getName());
			// for current user retrieval: name =
			// controller.getTypeForProcess(proc, "User");
			pe.getExecutableHandler(concrete.getName(), proc.getpid()).execute(
					proc, concrete);
			if (concrete.isExecuted()) {
				handleEffect(pe, proc, current);
			}
		} else {
			logger.debug("Executable handler not found for activity with name "
					+ concrete.getName());
			// TODO pesante cablatura
			if (concrete.getName().equals("testConcrete")) {
				pe.addProcVar(proc, "AssignedDGate", "G1");
			}
			if (concrete.getName().equals("testConcrete2")) {
				if (proc.getFather() != null) {
					pe.changeProcVar(proc.getFather(), "AssignedDGate", "G2");
				}
			}
			if (concrete.getName().equals("testConcrete3")) {
				pe.changeProcVar(proc, "AssignedDGate", "G2");
			}
			if (concrete.getName().equals("writeENDED")) {
				pe.addProcVar(proc, "testDp", "ENDED");
			}
			if (concrete.getName().equals("defineTransportation")) {
				pe.addProcVar(proc, concrete.getReturnsTo(), "walk");
			}
			current.setExecuted(true);
			handleEffect(pe, proc, current);

		}

	}
}
