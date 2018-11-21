package test;

import org.springframework.beans.factory.annotation.Autowired;

import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.services.DemandService;

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
