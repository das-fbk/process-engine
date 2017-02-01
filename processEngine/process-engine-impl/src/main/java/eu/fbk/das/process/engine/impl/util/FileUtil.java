package eu.fbk.das.process.engine.impl.util;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {

    private static final String APFL_EXTENSION = ".apfl";
    private static final Logger logger = LogManager.getLogger(FileUtil.class);
    private static final String XML_EXTENSION = ".xml";

    private FileUtil() {
    }

    /**
     * Search for a APFL file (extension.apfl) with given name into a list of
     * directories
     * 
     * @param name
     * @return first file found, null if not found
     */
    public static File findFile(String name, List<String> dirs) {
	if (name == null) {
	    logger.error("name must be not null");
	    return null;
	}
	if (!name.endsWith(APFL_EXTENSION)) {
	    name = name.concat(APFL_EXTENSION);
	}
	for (String dir : dirs) {
	    File f = new File(dir + File.separator + name);
	    if (f.exists()) {
		logger.info("File found " + f.getAbsolutePath());
		return f;
	    }
	}
	if (name.endsWith(APFL_EXTENSION)) {
	    name = name.replace(APFL_EXTENSION, "");
	}
	if (!name.endsWith(XML_EXTENSION)) {
	    name = name.concat(XML_EXTENSION);
	}
	for (String dir : dirs) {
	    File f = new File(dir + File.separator + name);
	    if (f.exists()) {
		logger.info("File found " + f.getAbsolutePath());
		return f;
	    }
	}

	logger.warn("File not found");
	return null;
    }

}
