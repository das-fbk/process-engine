package eu.fbk.das.process.engine.api;

import eu.fbk.das.process.engine.api.domain.ProcessActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

/**
 * Activities executables runnable by demonstrator and processEngine
 *
 */
public interface ExecutableActivityInterface {

    public void execute(ProcessDiagram proc, ProcessActivity pa);

}
