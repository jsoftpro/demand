package test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Rules;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.persistence.repositories.DemandRepository;
import pro.jsoft.demand.persistence.repositories.ExecutorRepository;
import pro.jsoft.demand.persistence.repositories.FieldRepository;
import pro.jsoft.demand.persistence.repositories.FileRepository;
import pro.jsoft.demand.persistence.repositories.FormRepository;
import pro.jsoft.demand.persistence.repositories.RulesRepository;
import pro.jsoft.demand.persistence.repositories.StageRepository;

@Configuration
@ComponentScan(basePackages = {"pro.jsoft.demand.services"})
@Profile("test")
public class MockRepositoriesConfig {
	@Bean
	public DemandRepository demandRepository() {
		DemandRepository demandRepository = mock(DemandRepository.class);
		
		when(demandRepository.findByIdAndProfile(anyLong(), any())).thenAnswer(invocation -> {
			Long id = (Long) invocation.getArgument(0);
			return Optional.ofNullable(savedMockDemand(id));
		});
		
		when(demandRepository.findById(anyLong())).thenAnswer(invocation -> {
						Long id = (Long) invocation.getArgument(0);
						return Optional.ofNullable(savedMockDemand(id));
					});
		
		when(demandRepository.save(any(Demand.class))).thenAnswer(invocation -> {
						Demand demand =(Demand) invocation.getArgument(0);
						if (demand.getId() == null || demand.getId() == 0) {
							demand.setId(System.currentTimeMillis());
						}
						return demand;
					});
		
		return demandRepository;
	}
	
	
	@Bean
	public ExecutorRepository executorRepository() {
		ExecutorRepository executorRepository = mock(ExecutorRepository.class);
		return executorRepository;
	}
	
	
	@Bean
	public StageRepository stageRepository() {
		StageRepository stageRepository = mock(StageRepository.class);
		when(stageRepository.save(any(Stage.class))).thenAnswer(invocation -> {
			Stage stage = (Stage) invocation.getArgument(0);
			if (stage.getId() == null || stage.getId() == 0) {
				stage.setId(System.currentTimeMillis());
			}
			return stage;
		});

		return stageRepository;
	}
	
	
	@Bean
	public RulesRepository rulesRepository() {
		RulesRepository rulesRepository = mock(RulesRepository.class);
		when(rulesRepository.findById("TEST2")).then(invocation -> Optional.of(new Rules("TEST2", false, false, true, true, false)));
		return rulesRepository;
	}
	
	
	@Bean
	public FormRepository formRepository() {
		return mock(FormRepository.class);
	}
	
	
	@Bean
	public FileRepository fileRepository() {
		return mock(FileRepository.class);
	}
	
	
	@Bean
	public FieldRepository fieldRepository() {
		return mock(FieldRepository.class);
	}
	
	
	protected Demand newMockDemand() {
		Demand demand = new Demand();
		demand.setProfile("TEST");
		demand.setName("test");
		demand.setCode("test");
		demand.setOrganizationId(1L);
		demand.setOrganizationName("test org");
		demand.setDepartmentId(1L);
		demand.setDepartmentName("test dep");
		demand.setBranch(Users.BRANCH);
		demand.setFormName("testForm");
		demand.setUid(Users.USER);

		return demand;
	}
		
	
	protected Demand savedMockDemand(long id) {
		Demand demand = newMockDemand();
		demand.setId(1L);
		demand.setProfile("TEST");
		demand.setBranch(Users.BRANCH);
		Stage created = new Stage();
		created.setAction(Action.STEP_CREATE);
		created.setActorUid(Users.USER);
		created.setRecipientUid(Users.MANAGER);
		created.setDate(Calendar.getInstance());
		demand.getStages().add(created);
		
		if (id != 1) {
			Action action = Action.getByCode((int) id);
			if (Action.STEP_CONFIRM.equals(action) 
					|| Action.STEP_ACCEPT.equals(action) 
					|| Action.STEP_REACCEPT.equals(action) 
					|| Action.STEP_PROCESS.equals(action) 
					|| Action.STEP_ASKAPPROVE.equals(action) 
					|| Action.STEP_APPROVE.equals(action) 
					|| Action.STEP_REFUSE.equals(action) 
					|| Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_CONFIRM);
				stage.setActorUid(Users.MANAGER);
				stage.setRecipientUid(null);
				demand.getStages().add(stage);
			}

			if (Action.STEP_ACCEPT.equals(action) 
					|| Action.STEP_REACCEPT.equals(action) 
					|| Action.STEP_PROCESS.equals(action) 
					|| Action.STEP_ASKAPPROVE.equals(action) 
					|| Action.STEP_APPROVE.equals(action) 
					|| Action.STEP_REFUSE.equals(action) 
					|| Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_ACCEPT);
				stage.setActorUid(Users.EXECUTOR);
				stage.setRecipientUid(null);
				demand.getStages().add(stage);
			}

			if (Action.STEP_REACCEPT.equals(action) 
					|| Action.STEP_PROCESS.equals(action) 
					|| Action.STEP_ASKAPPROVE.equals(action) 
					|| Action.STEP_APPROVE.equals(action) 
					|| Action.STEP_REFUSE.equals(action) 
					|| Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_REACCEPT);
				stage.setActorUid(Users.EXECUTOR2);
				stage.setRecipientUid(null);
				demand.getStages().add(stage);
			}

			if (Action.STEP_ASKAPPROVE.equals(action) 
					|| Action.STEP_APPROVE.equals(action) 
					|| Action.STEP_REFUSE.equals(action) 
					|| Action.STEP_PROCESS.equals(action) 
					|| Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_ASKAPPROVE);
				stage.setActorUid(Users.EXECUTOR2);
				stage.setRecipientUid(Users.APPROVER);
				demand.getStages().add(stage);
			}

			if (Action.STEP_APPROVE.equals(action) 
					|| Action.STEP_PROCESS.equals(action) 
					|| Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_APPROVE);
				stage.setActorUid(Users.APPROVER);
				stage.setRecipientUid(Users.EXECUTOR2);
				demand.getStages().add(stage);
			}

			if (Action.STEP_REFUSE.equals(action) 
					|| Action.STEP_PROCESS.equals(action) 
					|| Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_REFUSE);
				stage.setActorUid(Users.APPROVER);
				stage.setRecipientUid(null);
				demand.getStages().add(stage);
			}


			if (Action.STEP_PROCESS.equals(action) 
					|| Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_PROCESS);
				stage.setActorUid(Users.EXECUTOR2);
				stage.setRecipientUid(null);
				demand.getStages().add(stage);
			}

			if (Action.STEP_FINISH.equals(action)) {
				Stage stage = new Stage();
				stage.setAction(Action.STEP_FINISH);
				stage.setActorUid(Users.EXECUTOR2);
				stage.setRecipientUid(null);
				demand.getStages().add(stage);
			}
		}
		
		return demand;
	}
}
