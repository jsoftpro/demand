package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.rest.types.dto.DemandDto;
import pro.jsoft.demand.rest.types.dto.StageDto;
import pro.jsoft.demand.rest.types.dto.mapping.DemandMapper;
import pro.jsoft.demand.rest.types.dto.mapping.DemandMapperImpl;

public class DemandMapperTest {
	// @Test
	public void testStagesOrder() {
		Demand demand = new Demand();
		Stage stage1 = new Stage();
		stage1.setId(1L);
		stage1.setAction(Action.STEP_CREATE);

		Stage stage2 = new Stage();
		stage2.setId(2L);
		stage2.setAction(Action.STEP_CONFIRM);

		demand.getStages().add(stage1);
		demand.getStages().add(stage2);
		
		DemandMapper demandMapper = new DemandMapperImpl();
		DemandDto dtoDemand = demandMapper.convertDomainToDto(demand);
		StageDto dtoStage1 = dtoDemand.getCreated();
		StageDto dtoStage2 = dtoDemand.getChanged();
		
		assertEquals(Long.valueOf(1L), dtoStage1.getId());
		assertEquals(Long.valueOf(2L), dtoStage2.getId());
	}
}
