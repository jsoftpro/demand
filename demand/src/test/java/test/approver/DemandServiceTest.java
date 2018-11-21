package test.approver;

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
	public void testApprove() {
		addStage(Action.STEP_ASKAPPROVE, Action.STEP_APPROVE);
	}
	
	@Test
	public void testRefuseApproving() {
		addStage(Action.STEP_ASKAPPROVE, Action.STEP_REFUSE);
	}
}
