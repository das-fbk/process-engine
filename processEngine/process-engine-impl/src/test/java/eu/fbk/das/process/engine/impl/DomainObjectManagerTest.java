package eu.fbk.das.process.engine.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;

import eu.fbk.das.process.engine.api.DomainObjectInstance;
import eu.fbk.das.process.engine.api.DomainObjectManagerInterface;
import eu.fbk.das.process.engine.api.domain.DomainObjectDefinition;
import eu.fbk.das.process.engine.api.exceptions.ProcessEngineRuntimeException;

public class DomainObjectManagerTest {

    private DomainObjectManagerInterface manager;
    private List<String> dirs;

    @Before
    public void setup() throws JAXBException {
	dirs = Arrays
		.asList("C:/Lavoro/workspace/soa/processEngine/process-engine-impl/src/test/resources/testEntities/testBasicDomainObject/");
	manager = new DomainObjectManager(dirs);
    }

    @Test
    public void loadDomainObjectDefinitionTest() throws Exception {
	DomainObjectDefinition ed = manager.load("DO_test1.apfl");
	assertTrue(ed != null && ed.getProcess() != null);
    }

    @Test
    public void getDomainObjectInstanceTest()
	    throws ProcessEngineRuntimeException {
	DomainObjectDefinition ed = manager.load("DO_test1.apfl");
	manager.add(ed);
	DomainObjectInstance doi = manager.buildInstance(ed);
	assertTrue(doi != null && doi.getProcess() != null);

    }
}
