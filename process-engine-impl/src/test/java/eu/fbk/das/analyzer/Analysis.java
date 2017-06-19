package eu.fbk.das.analyzer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import eu.fbk.das.composer.api.CompositionProblem;
import eu.fbk.das.composer.api.Parser;
import eu.fbk.das.composer.api.exceptions.CompositionDuplicateOidException;
import eu.fbk.das.composer.api.exceptions.InvalidCompositionEffectException;
import eu.fbk.das.composer.api.exceptions.InvalidServiceObjectAssignmentException;
import eu.fbk.das.composer.impl.Composer;
import eu.fbk.das.process.engine.api.domain.ObjectDiagram;
import eu.fbk.das.process.engine.api.domain.ServiceDiagram;

public class Analysis {

	private static String csv = ",";

	// private static String directoryName = System.getenv("CAPTEVO_HOME") +
	// System.getProperty("file.separator") +
	// projectName + System.getProperty("file.separator") + "Compositions";

	// TODO ANNAPAOLA: esempio di cartella che contiene i risultati di
	// composizioni
	// C:\Users\Martina\git\process-engine\process-engine-impl\src\test\resources
	private static String workingDir = "C:/Users/Martina/git/process-engine/process-engine-impl/src/test/resources/ICSOC2017";
	private static int numAP = 153;
	private static int numRun = 10;

	private static String directoryName = workingDir + "/Compositions";

	private static final String analysisDet = workingDir
			+ "/analysis_details.csv";
	private static final String analysis = workingDir + "/analysis.csv";

	public static void main(String[] args)
			throws IOException,
			eu.fbk.das.composer.api.exceptions.CompositionDuplicateSidException,
			eu.fbk.das.composer.api.exceptions.InvalidCompositionPreconditionException,
			eu.fbk.das.process.engine.api.domain.exceptions.InvalidObjectCurrentStateException,
			eu.fbk.das.composer.api.exceptions.InvalidServiceCurrentStateException,
			eu.fbk.das.composer.api.exceptions.ServiceGroundingTypeMismatchException,
			CompositionDuplicateOidException,
			InvalidCompositionEffectException,
			InvalidServiceObjectAssignmentException {
		// /INIZIO ANNAP
		System.out.println(directoryName);

		Parser parser = new Parser(workingDir, workingDir);
		Composer composer = new Composer(workingDir);
		String prob = "";

		String toLog = "";
		String toGlobalLog = "";

		toLog += "Problem_Id" + csv + "DP_Num" + csv + "DP_Trans" + csv
				+ "Fragments_Num" + csv + "Fragments_Trans" + csv + "Tot_Trans"
				+ csv + "Comp_Time\n";
		toGlobalLog += "Problem_Id" + csv + "DP_Num" + csv + "DP_Trans" + csv
				+ "Fragments_Num" + csv + "Fragments_Trans" + csv + "Tot_Trans"
				+ csv + "AVG_Comp_Time\n";

		for (int i = 0; i < numAP; i++) {
			long sumComposingTime = 0;
			prob = "AP_" + i;
			System.out.println("ADAPTATION PROBLEM: " + prob);

			long startParsingTime = System.currentTimeMillis();

			CompositionProblem cp = parser.parseCompositionProblem(prob);
			long now = System.currentTimeMillis();
			long parsingTime = (long) (now - startParsingTime);
			// System.out.println("STart time: "+startParsingTime );
			// System.out.println("Now time: "+now );
			// System.out.println("Parsing pre: "+parsingTime );
			System.out.println("Parsing time: " + parsingTime / 1000f);

			int Srv = getSrvNum(cp);
			int Obj = getObjNum(cp);
			int SrvT = getSrvTrans(cp);
			int ObjT = getObjTrans(cp);
			int totT = SrvT + ObjT;
			System.out.println("Fragments number: " + Srv);
			System.out.println("Fragments transitions number: " + SrvT);
			System.out.println("Domain properties number: " + Obj);
			System.out.println("Domain Properties transitions number: " + ObjT);

			for (int j = 0; j < numRun; j++) {
				long startComposeTime = System.currentTimeMillis();
				composer.compose(cp, prob, false, false, prob);
				now = System.currentTimeMillis();
				long compTime = (long) (now - startComposeTime);
				System.out.println(" Refinement time: " + compTime / 1000f);

				sumComposingTime += compTime;

				toLog += cp.getCompId() + csv + Obj + csv + ObjT + csv + Srv
						+ csv + SrvT + csv + totT + csv + compTime / 1000f
						+ "\n";

			}

			toGlobalLog += cp.getCompId() + csv + Obj + csv + ObjT + csv + Srv
					+ csv + SrvT + csv + totT + csv + sumComposingTime / numRun
					/ 1000f + "\n";

			System.out.println("AVERAGE refinement time: " + sumComposingTime
					/ numRun / 1000f + "\n=================\n");

		}

		// System.out.println(toLog);

		try {
			File f = new File(analysisDet);
			f.delete();
			f.createNewFile();
			FileWriter fw = new FileWriter(f, true);
			fw.write(toLog);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			File f = new File(analysis);
			f.delete();
			f.createNewFile();
			FileWriter fw = new FileWriter(f, true);
			fw.write(toGlobalLog);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// /FINE ANNAP

	}

	private static int getSrvTrans(CompositionProblem cp) {

		int ret = 0;

		for (ServiceDiagram service : cp.getServiceDiagrams()) {
			int numOfServiceTransitions = service.getTransitions().size();
			ret += numOfServiceTransitions;
		}
		return ret;
	}

	private static int getSrvNum(CompositionProblem cp) {

		int ret = 0;

		for (ServiceDiagram service : cp.getServiceDiagrams())
			ret++;

		return ret;
	}

	private static int getObjNum(CompositionProblem cp) {

		int ret = 0;

		for (ObjectDiagram object : cp.getObjectDiagrams()) {
			ret++;
		}

		return ret;
	}

	private static int getObjTrans(CompositionProblem cp) {

		int ret = 0;

		for (ObjectDiagram object : cp.getObjectDiagrams()) {
			int numOfObjectTransitions = object.getTransitions().size();
			ret += numOfObjectTransitions;
		}

		return ret;
	}

}
