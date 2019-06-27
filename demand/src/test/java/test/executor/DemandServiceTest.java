package test.executor;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pro.jsoft.demand.actions.Action;
import test.DemandServiceTestPrototype;
import test.MockMailConfig;
import test.MockRepositoriesConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MockUserServiceConfig.class, MockRepositoriesConfig.class, MockMailConfig.class })
@ActiveProfiles({"test"})
public class DemandServiceTest extends DemandServiceTestPrototype {
	// Allowed

	@Test
	public void testRefuseConfirmed() {
		assertNotNull(addStage(Action.STEP_CONFIRM, Action.STEP_REFUSE));
	}

	@Test
	public void testAccept() {
		assertNotNull(addStage(Action.STEP_CONFIRM, Action.STEP_ACCEPT));
	}

	@Test
	public void testRefuseAccepted() {
		addStage(Action.STEP_ACCEPT, Action.STEP_REFUSE);
	}
	
	// Not allowed

	@Test(expected = AccessDeniedException.class)
	public void testConfirm() {
		assertNotNull(addStage(Action.STEP_CREATE, Action.STEP_CONFIRM));
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testCancel() {
		assertNotNull(addStage(Action.STEP_ACCEPT, Action.STEP_CANCEL));
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testApprove() {
		addStage(Action.STEP_ASKAPPROVE, Action.STEP_APPROVE);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testAskRefuse() {
		addStage(Action.STEP_PROCESS, Action.STEP_ASKREFUSE);
	}
}
