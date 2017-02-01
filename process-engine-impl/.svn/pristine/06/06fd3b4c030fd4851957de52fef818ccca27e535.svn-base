package eu.fbk.das.process.engine.impl.util;

import java.io.File;

import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario;

public class ScenarioLoader {

    private String mainDir;

    public ScenarioLoader(String dir) {
	this.mainDir = dir;
    }

    public Scenario load(String ref) throws NullPointerException {
	if (ref == null) {
	    throw new NullPointerException("Scenario file is null");
	}
	File dir = new File(mainDir);
	if (!dir.isDirectory()) {
	    throw new NullPointerException(
		    "Impossibile to load scenario, mainDir not found " + dir);
	}
	File f = new File(mainDir + File.separator + ref);
	Parser parser = new Parser();
	Scenario s = parser.parse(f);
	return s;
    }
}
