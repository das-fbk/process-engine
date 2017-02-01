package eu.fbk.das.process.engine.impl.handlers;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.OnMessageActivity;
import eu.fbk.das.process.engine.api.domain.PickActivity;
import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

public class PickActivityHandler extends AbstractHandler {

    private static final Logger logger = LogManager
	    .getLogger(PickActivityHandler.class);

    @Override
    public void handle(ProcessEngine pe, ProcessDiagram proc,
	    ProcessActivity current) {
	logger.debug("[" + proc.getpid() + "] Pick Execution");
	DomainObjectInstance doi = pe.getDomainObjectInstance(proc);

	boolean foundOnMsg = false;
	ProcessDiagram branch = null;
	for (OnMessageActivity OMact : ((PickActivity) current).getOnMessages()) {

	    if (!pe.getMsg(doi).isEmpty()) {

		// Iterator<String> iter = pe.getMsg(proc.getpid()).iterator();
		Iterator<String> iter = pe.getMsg(doi).iterator();
		while (iter.hasNext()) {
		    String msg = iter.next();
		    if (msg.equalsIgnoreCase(OMact.getName())) {
			foundOnMsg = true;
			logger.debug("[" + proc.getpid()
				+ "] Pick: on message: " + msg);
			branch = OMact.getBranch();
			// rimuovere il msg una volta trovato
			iter.remove();
		    }

		}
	    }
	    //
	    //
	    // if (simulator.messageQueue.containsKey(father)) {
	    // for (String msg : simulator.messageQueue.get(father)) {
	    // if (msg.equals(OMact.getName())) {
	    // if (orchestrator
	    // .checkPrecondition(iface, current, proc)) {
	    // foundOnMsg = true;
	    // Entity e = Orchestrator.getProcessesToEntity().get(
	    // proc);
	    //
	    // String haAct = "";
	    // if (runningAdaptations.containsKey(proc.getpid())) {
	    // haAct = proc.getFather().getCurrentActivity()
	    // .getName();
	    // }
	    // if (!OMact.isHighlighted())
	    // iface.writeToSocket("Activity,"
	    // + entity.getType() + "_"
	    // + proc.getpid() + "," + OMact.getName()
	    // + "," + e.getType() + "," + e.getId()
	    // + "," + haAct);
	    // OMact.setHighlighted(true);
	    // simulator.messageQueue.get(father).remove(msg);
	    //
	    // if (orchestrator.checkEffect(iface, OMact, proc)) {
	    // System.out.println("EFFECTS EXECUTION");
	    // }
	    // branch = OMact.getBranch();
	    // } else {
	    // // //
	    // //
	    // System.out.println("PRECONDITION VIOLATION for onMessage activity "+OMact.getName()+" of process "+proc.getpid());
	    // // //
	    // //
	    // iface.writeToSocket("PreconditionViolated,"+entity.getId()+","+proc.getpid()+","+current.getName());
	    // // // execute HA
	    // // boolean haRes = this.executeHA(iface, current,
	    // // entity, proc);
	    // // if (!haRes) {
	    // // // PLAN NOT FOUND for HA
	    // // System.out
	    // // .println("Plan not found for HA of process "
	    // // + entity.getType()
	    // // + " "
	    // // + proc.getpid());
	    // // proc.setRunning(false);
	    // // // end the main process
	    // // orchestrator.getProcessById(proc.getpid())
	    // // .setEnded(true);
	    // //
	    // // } else {
	    // // //
	    // // ProcessDiagram myBranch = OMact.getBranch();
	    // // myBranch.setCurrentActivity(myBranch
	    // // .getFirstActivity());
	    // // this.addRunningOMBranch(proc.getpid(), myBranch);
	    // // myBranch.setFather(proc);
	    // // proc.setRunning(false);
	    // // myBranch.setRunning(false);
	    // // runningAdaptations.get(proc.getpid())
	    // // .setFather(myBranch);
	    // //
	    // // }
	    // }
	    //
	    // break;
	    // }
	    // }
	    // if (foundOnMsg) {
	    // break;
	    // }
	    //
	    // }
	    //
	    // }
	    //
	    if (foundOnMsg) {
		if (branch != null && branch.getActivities().size() > 0) {
		    branch.setCurrentActivity(branch.getFirstActivity());
		    // this.addRunningOMBranch(proc.getpid(), branch);
		    branch.setPid(pe.getPid());
		    branch.setFather(proc);
		    branch.setRunning(true);
		    current.setRunning(false);
		    proc.setRunning(false);
		    pe.addRunningBranch(branch.getpid(), branch);

		    // add correlation between branch and father
		    // pe.createCorrelation(branch.getpid(), proc.getpid());
		    // craere la correlazione tra il branch e chi e' in
		    // correlazione col padre che crea questo branch
		    // TODO: non serve piu' ?
		    // List<Integer> fatherCorrelated = pe.getCorrelated(proc
		    // .getpid());
		    // if (fatherCorrelated != null &&
		    // !fatherCorrelated.isEmpty()) {
		    // for (Integer fc : fatherCorrelated) {
		    // pe.createCorrelation(branch.getpid(), fc);
		    // }
		    // }

		} else {
		    current.setExecuted(true);
		    if (!proc.getNextActivity().isEmpty()) {
			proc.setCurrentActivity(proc.getNextActivity().get(0));
		    } else {
			proc.setRunning(false);
			proc.setEnded(true);
		    }
		}
		foundOnMsg = false;
	    }
	}
	if (proc.isTerminated() && pe.getRunningBranch(proc) != null
		&& pe.getRunningBranch(proc).isEnded()) {
	    current.setRunning(false);
	    current.setExecuted(true);
	    proc.setEnded(true);
	    System.out.println();
	}
    }
}
