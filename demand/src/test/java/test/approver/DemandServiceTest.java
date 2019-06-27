package test.approver;

import org.junit.Test;

import pro.jsoft.demand.actions.Action;
import test.DemandServiceTestPrototype;

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
