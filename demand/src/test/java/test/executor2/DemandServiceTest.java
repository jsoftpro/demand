package test.executor2;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pro.jsoft.demand.actions.Action;
import test.DemandServiceTestPrototype;
import test.MockRepositoriesConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MockUserServiceConfig.class, MockRepositoriesConfig.class })
@ActiveProfiles({"test"})
public class DemandServiceTest extends DemandServiceTestPrototype {
	// Allowed
	
	@Test
	public void testReAccept() {
		assertNotNull(addStage(Action.STEP_ACCEPT, Action.STEP_REACCEPT));
	}
	
	@Test
	public void testAskApprove() {
		addStage(Action.STEP_REACCEPT, Action.STEP_ASKAPPROVE);
	}
	
	@Test
	public void testRefuseReaccepted() {
		addStage(Action.STEP_REACCEPT, Action.STEP_REFUSE);
	}
	
	@Test
	public void testProcess() {
		assertNotNull(addStage(Action.STEP_APPROVE, Action.STEP_PROCESS));
	}
	
	@Test
	public void testFinish() {
		assertNotNull(addStage(Action.STEP_PROCESS, Action.STEP_FINISH));
	}

}
