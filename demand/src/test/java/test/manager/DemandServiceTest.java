package test.manager;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.services.DemandService;
import test.DemandServiceTestPrototype;
import test.MockRepositoriesConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MockUserServiceConfig.class, MockRepositoriesConfig.class })
@ActiveProfiles({"test"})
public class DemandServiceTest extends DemandServiceTestPrototype {
	// Allowed

	@Test
	public void testConfirm() {
		assertNotNull(addStage(Action.STEP_CREATE, Action.STEP_CONFIRM));
	}
	
	@Test
	public void testRefuse() {
		assertNotNull(addStage(Action.STEP_CREATE, Action.STEP_REFUSE));
	}

	// Not allowed
	
	@Test(expected = AccessDeniedException.class)
	public void testAccept() {
		addStage(Action.STEP_CONFIRM, Action.STEP_ACCEPT);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testReAccept() {
		addStage(Action.STEP_ACCEPT, Action.STEP_REACCEPT);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testCancel() {
		assertNotNull(addStage(Action.STEP_ACCEPT, Action.STEP_CANCEL));
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testAskApprove() {
		addStage(Action.STEP_ACCEPT, Action.STEP_ASKAPPROVE);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testApprove() {
		addStage(Action.STEP_ASKAPPROVE, Action.STEP_APPROVE);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testAskRefuse() {
		addStage(Action.STEP_PROCESS, Action.STEP_ASKREFUSE);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testRefuseAccepted() {
		addStage(Action.STEP_ACCEPT, Action.STEP_REFUSE);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testProcess() {
		assertNotNull(addStage(Action.STEP_APPROVE, Action.STEP_PROCESS));
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testFinish() {
		assertNotNull(addStage(Action.STEP_PROCESS, Action.STEP_FINISH));
	}
}
