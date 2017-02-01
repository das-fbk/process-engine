package eu.fbk.das.process.engine.impl.util;

import java.util.Arrays;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.ProcessEngine;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.domain.exceptions.FlowDuplicateActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowActivityException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidFlowInitialStateException;
import eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject;
import eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance.DomainObjectInstanceKnowledge.Dp;
import eu.fbk.das.process.engine.impl.DomainObjectManager;
import eu.fbk.das.process.engine.impl.ProcessEngineImpl;

/**
 * A command line tool to load scenario and test them
 *
 */
public class ScenarioLoaderCmdLine {

    public static void main(String[] args) {
	// create the command line parser
	CommandLineParser parser = new BasicParser();
	// create the Options
	Options options = new Options();
	options.addOption("d", "scenarioDirectory", true, "scenario directory");
	options.addOption("f", "scenarioFile", true, "scenario file");
	options.addOption("m", "maxStep", true,
		"max simulation step (default 50) ");

	printHelp(options);

	// default values
	int maxStep = 50;

	try {
	    // parse the command line arguments
	    CommandLine line = parser.parse(options, args);
	    if (line.hasOption("m")) {
		String max = line.getOptionValue("m");
		try {
		    maxStep = Integer.valueOf(max);
		} catch (NumberFormatException e) {
		    System.out.println("M must be a valid number");
		}
	    }
	    if (line.hasOption("d") && line.hasOption("f")) {
		String dir = line.getOptionValue("d");
		DomainObjectManagerInterface entityManager = new DomainObjectManager(
			Arrays.asList(dir));
		ProcessEngine processEngine = new ProcessEngineImpl(
			entityManager, null, dir);

		ScenarioLoader sl = new ScenarioLoader(dir);
		Scenario scenario = sl.load(line.getOptionValue("f"));

		DomainObjectDefinition ed = null;
		for (DomainObject domainObject : scenario.getDomainObject()) {
		    ed = entityManager.add(entityManager.load(domainObject
			    .getFile()));
		    for (eu.fbk.das.process.engine.api.jaxb.scenario.Scenario.DomainObject.DomainObjectInstance d : domainObject
			    .getDomainObjectInstance()) {
			DomainObjectInstance doi = entityManager.buildInstance(
				ed, d, Arrays.asList(dir));
			if (d.getDomainObjectInstanceKnowledge() != null) {
			    for (Dp knownledge : d
				    .getDomainObjectInstanceKnowledge().getDp()) {
				doi.updateKnowledge(knownledge);
			    }
			}

			processEngine.start(doi);
		    }
		}

		int t = 0;
		while (true) {
		    t++;
		    if (t > maxStep) {
			break;
		    }
		    processEngine.stepAll();
		}

	    } else {
		System.out.println("Please specify all mandatory options");
	    }
	} catch (ParseException | ProcessEngineRuntimeException
		| NullPointerException | InvalidObjectCurrentStateException
		| InvalidFlowInitialStateException
		| InvalidFlowActivityException | FlowDuplicateActivityException exp) {
	    System.out.println("Unexpected exception:" + exp.getMessage());
	}
    }

    private static void printHelp(Options options) {
	HelpFormatter formatter = new HelpFormatter();
	formatter.printHelp(" [SCENARIO DIRECTORY] [SCENARIO FILE]",
		"FBK Das Unit - 2015 - Process Engine Scenario loader ",
		options, null);
    }

}
