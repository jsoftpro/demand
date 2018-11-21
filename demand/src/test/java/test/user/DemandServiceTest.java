package test.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.services.DemandService;
import test.DemandServiceTestPrototype;
import test.MockRepositoriesConfig;
import test.Users;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MockUserServiceConfig.class, MockRepositoriesConfig.class })
@ActiveProfiles({"test"})
public class DemandServiceTest extends DemandServiceTestPrototype {
	// Allowed

	@Test
	public void testCreate() {
		Demand initDemand = new Demand();
		Stage stageCreated = new Stage();
		stageCreated.setRecipientUid(Users.MANAGER);
		initDemand.getStages().add(stageCreated);
		Demand savedDemand = demandService.save(initDemand);
		assertNotNull(savedDemand.getCreated());
	}
	
	@Test
	public void testLoad() {
		Demand loadedDemand = demandService.get(1L, "TEST");
		assertNotNull(loadedDemand);
	}
	
	@Test
	public void testAskRefuse() {
		assertNotNull(addStage(Action.STEP_PROCESS, Action.STEP_ASKREFUSE));
	}
	
	@Test
	public void testCancel() {
		assertNotNull(addStage(Action.STEP_ACCEPT, Action.STEP_CANCEL));
	}

	// ...
	
	@Test
	public void testOpinion() {
		Demand demand = demandService.get(Long.valueOf(Action.STEP_FINISH.getCode()), "TEST");
		demand.setOpinion("text");
		Demand updatedDemand = demandService.update(demand);
		assertEquals(demand.getId(), updatedDemand.getId());
		assertNotNull(updatedDemand.getOpinion());
	}
	
	// Not allowed
	

	@Test(expected = AccessDeniedException.class)
	public void testConfirm() {
		addStage(Action.STEP_CREATE, Action.STEP_CONFIRM);
	}

	@Test(expected = AccessDeniedException.class)
	public void testRefuse() {
		assertNotNull(addStage(Action.STEP_CREATE, Action.STEP_REFUSE));
	}

	@Test(expected = AccessDeniedException.class)
	public void testAccept() {
		addStage(Action.STEP_CONFIRM, Action.STEP_ACCEPT);
	}
	
	@Test(expected = AccessDeniedException.class)
	public void testReAccept() {
		addStage(Action.STEP_ACCEPT, Action.STEP_REACCEPT);
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
