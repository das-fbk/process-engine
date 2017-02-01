package eu.fbk.das.process.engine.api.jaxb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import eu.fbk.das.process.engine.api.domain.ConcreteActivity;
import eu.fbk.das.process.engine.api.domain.ProcessDiagram;

public class ProcessInstanceTest {

    @Test
    public void helloProcessTest() {
	Process p = new Process();
	p.setName("Hello Process");

	assertTrue(p.getName().length() > 0);
    }

    @Test
    public void nextActivityTest() {
	ProcessDiagram p = new ProcessDiagram();
	p.addActivity(new ConcreteActivity());
	p.addActivity(new ConcreteActivity());

	assertTrue(p.getNextActivity().size() == 2);
    }

    @Test
    public void executedActivityAndThenNextActivityTest() {
	ProcessDiagram p = new ProcessDiagram();
	p.addActivity(new ConcreteActivity(0, 1, "concrete1", "concrete2"));
	p.addActivity(new ConcreteActivity(1, 2, "concrete2", "concrete3"));

	p.setCurrentActivity(p.getNextActivity().get(0));
	p.getCurrentActivity().setExecuted(true);

	assertTrue(p.getNextActivity().size() == 1);
    }

}
