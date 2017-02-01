package eu.fbk.das.process.engine.impl.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.IFActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;
import eu.fbk.das.process.engine.api.domain.SwitchActivity;
import eu.fbk.das.process.engine.api.jaxb.SwitchType;

public class SwitchActivityHandler extends AbstractHandler {

    private static final Logger logger = LogManager
	    .getLogger(SwitchActivityHandler.class);

    // private Map<ProcessDiagram, List<ProcVar>> processVarMap = new
    // HashMap<ProcessDiagram, List<ProcVar>>();

    @Override
    public void handle(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current) {
	logger.debug("[" + proc.getpid() + "] Esecuzione Switch");
	boolean foundIf = false;
	ProcessDiagram father = proc;
	for (IFActivity ifact : ((SwitchActivity) current).getIFs()) {

	    boolean VarConditionEval = true;
	    boolean ContextConditionEval = true;

	    if (ifact.getVarConditions().size() > 0) {
		for (SwitchType.If.VarCondition varCond : ifact
			.getVarConditions()) {

		    if (!pe.checkVarCondition(varCond.getName(),
			    varCond.getValue(), father)) {
			VarConditionEval = false;
		    }

		}
	    }

	    // if (ifact.getConditions().size() > 0) {
	    // // create the list of relevant object instances for the
	    // // process
	    // List<ObjectDiagram> relatedObjects = new
	    // ArrayList<ObjectDiagram>();
	    // if (Orchestrator.processesToEntity.containsKey(father)) {
	    // if (Orchestrator.processesToEntity.get(father).getObjects() !=
	    // null) {
	    // for (ObjectDiagram enObj : Orchestrator.processesToEntity
	    // .get(father).getObjects()) {
	    // relatedObjects.add(enObj);
	    // }
	    // }
	    // }
	    // for (ProcessDiagram partner : simulator.correlation.get(father))
	    // {
	    // if (Orchestrator.processesToEntity.containsKey(partner)) {
	    // if (Orchestrator.processesToEntity.get(partner)
	    // .getObjects() != null) {
	    // for (ObjectDiagram partnerObj : Orchestrator.processesToEntity
	    // .get(partner).getObjects()) {
	    // relatedObjects.add(partnerObj);
	    // }
	    // }
	    // }
	    // }
	    //
	    // for (PreconditionType contextCond : ifact.getConditions()) {
	    // for (PreconditionType.Point pt : contextCond.getPoint()) {
	    // for (Object obj : pt.getObject()) {
	    // for (ObjectDiagram CurrentObj : relatedObjects) {
	    // if (CurrentObj.getOid()
	    // .contains(obj.getOName())) {
	    // ContextConditionEval = ContextConditionEval
	    // && CurrentObj.getCurrentState()
	    // .equals(obj.getState().get(
	    // 0));
	    //
	    // }
	    // }
	    //
	    // }
	    // }
	    // }
	    //
	    // }

	    if (ContextConditionEval && VarConditionEval) {
		ProcessDiagram branch = ifact.getBranch();
		int pid = pe.getPid();
		branch.setPid(pid);
		branch.setCurrentActivity(branch.getFirstActivity());
		pe.addRunningBranch(pid, branch);

		branch.setFather(proc);
		proc.setRunning(false);
		branch.setRunning(true);

		current.setRunning(false);
		foundIf = true;
		// break;
	    }

	}
	// if not executed if, execute def
	if (!foundIf) {
	    ProcessDiagram branch = ((SwitchActivity) current).getDEF()
		    .getDefaultBranch();
	    branch.setCurrentActivity(branch.getFirstActivity());
	    pe.addRunningBranch(proc.getpid(), branch);
	    branch.setFather(proc);
	    proc.setRunning(false);
	    branch.setRunning(true);
	    current.setRunning(false);
	}
    }

}
