package eu.fbk.das.process.engine.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import eu.fbk.das.process.engine.api.DomainObjectInstance;

public class CorrelationManagerTest {

    private CorrelationManager cm;
    private DomainObjectInstance first;
    private DomainObjectInstance second;
    private DomainObjectInstance third;
    private DomainObjectInstance fourth;

    @Before
    public void setup() {
	cm = new CorrelationManager(null);
	first = build("first");
	second = build("second");
	third = build("third");
	fourth = build("fourth");
    }

    @Test
    public void testCorrelationManagerNull() {
	List<DomainObjectInstance> result = cm.get(null);
	assertTrue(result.isEmpty());
    }

    @Test
    public void testAddSimpleCorrelation() {
	cm.create(first, second);
	assertTrue(cm.isCorrelated(first, second));
    }

    @Test
    public void testAddMultipleCorrelation() {
	cm.create(first, second);
	cm.create(first, third);
	cm.create(first, fourth);
	assertTrue(cm.get(first).containsAll(
		Arrays.asList(second, third, fourth)));
    }

    @Test
    public void testAddCrossedCorrelation() {
	cm.create(first, second);
	cm.create(second, third);
	cm.create(first, fourth);
	assertTrue(cm.get(first).containsAll(Arrays.asList(second, fourth)));
    }

    @Test
    public void testRemoveCorrelation() {
	cm.create(first, second);
	cm.remove(first, second);
	assertTrue(cm.get(first).isEmpty());
    }

    @Test
    public void testAddCorrelationWithItselfNotValid() {
	cm.create(first, first);

	assertTrue(cm.get(first).isEmpty());
    }

    private DomainObjectInstance build(String id) {
	DomainObjectInstance doi = new DomainObjectInstance();
	doi.setId(id);
	return doi;
    }

}
