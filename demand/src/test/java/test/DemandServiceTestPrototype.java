package test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.services.DemandService;
import test.approver.MockUserServiceConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MockUserServiceConfig.class, MockRepositoriesConfig.class, MockMailConfig.class })
@ActiveProfiles({"test"})
public abstract class DemandServiceTestPrototype {
	@Autowired
	protected DemandService demandService;

	protected Stage addStage(Action previousAction, Action action) {
		Stage stage = new Stage();
		stage.setDemand(new Demand(Long.valueOf(previousAction.getCode())));
		stage.setAction(action);
		return demandService.addStage(stage);
	}
}
