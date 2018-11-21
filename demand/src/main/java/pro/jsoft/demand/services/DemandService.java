package pro.jsoft.demand.services;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.val;
import lombok.var;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.actions.ActionManager;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.persistence.repositories.DemandRepository;
import pro.jsoft.demand.persistence.repositories.RulesRepository;
import pro.jsoft.demand.persistence.repositories.StageRepository;
import pro.jsoft.demand.rest.types.DemandFilter;
import pro.jsoft.demand.rest.types.PageableResultList;
import pro.jsoft.spring.security.DetailedUser;
import pro.jsoft.spring.security.DomainUserDetailsService;
import pro.jsoft.utils.Exceptional;

@Service("demandService")
@Transactional
@Slf4j
public class DemandService {
	private DomainUserDetailsService userService;
	private DemandRepository demandRepository;
	private StageRepository stageRepository;
	private RulesRepository rulesRepository;

	@Autowired
	public DemandService(DomainUserDetailsService userService, DemandRepository demandRepository,
			StageRepository stageRepository, RulesRepository rulesRepository) {
		this.userService = userService;
		this.demandRepository = demandRepository;
		this.stageRepository = stageRepository;
		this.rulesRepository = rulesRepository;
	}

	
	public Demand get(Long id, String profile) {
		return demandRepository.findByIdAndProfile(id, profile).orElseThrow(ResourceNotFoundException::new);
	}

	
	public Demand save(Demand demand) {
		if (demand.getId() != null) {
			throw new IllegalArgumentException("The Demand entity had already persisted and cannot be updated. Use method DemandService.update instead.");
		}
		
		Stage stage;
		if (demand.getStages() == null || demand.getStages().isEmpty()) {
			if (demand.getStages() == null) {
				demand.setStages(new LinkedHashSet<>());
			}
			stage = new Stage();
			demand.getStages().add(stage);
		} else {
			stage = demand.getStages().iterator().next();
		}

		val profile = demand.getProfile();
		val rules = rulesRepository.findById(profile);
		var action = (rules.isPresent() && !rules.get().isEnabled(Action.STEP_CONFIRM)) 
			? Action.STEP_CONFIRM
			: Action.STEP_CREATE;

		val user = userService.loadCurrentUser();

		stage.setCode(action.getCode());
		stage.setDemand(demand);
		completeStage(stage, user);

		demand = demandRepository.save(demand);
		val actionManager = new ActionManager(user, rules);
		actionManager.defineActions(demand);

		return demand;
	}
	

	public Stage addStage(Stage stage) {
		if (!stage.getAction().isStep()) {
			return stage;
		}

		if (stage.getDemand() == null || stage.getDemand().getId() == null) {
			throw new IllegalArgumentException("Demand is undefined");
		}
		
		return demandRepository.findById(stage.getDemand().getId()).map(demand -> {
			AccessDeniedException exception = null;
			Stage savedStage = null;

			val profile = demand.getProfile();
			val rules = rulesRepository.findById(profile);
			if (rules.isPresent() && !rules.get().isEnabled(stage.getAction())) {
				exception = new AccessDeniedException("RULE");
				log.error("Access denied: RULE");
				return Exceptional.of(savedStage, exception);
			}

			val user = userService.loadCurrentUser();
			if (!stage.getAction().isAllowed(demand, user)) {
				exception = new AccessDeniedException("PERMISSION");
				log.error("Access denied: PERMISSION");
				return Exceptional.of(savedStage, exception);
			}

			val prevStage = demand.getChanged();
			if (prevStage.getAction().name().startsWith("STEP_ASK")
					&& StringUtils.hasText(prevStage.getRecipientUid())) {
				if (user.getUsername().equals(prevStage.getRecipientUid())) {
					stage.setRecipientUid(prevStage.getActorUid());
					stage.setRecipientName(prevStage.getActorName());
					stage.setRecipientPosition(prevStage.getActorPosition());
				} else {
					exception = new AccessDeniedException("USER");
					log.error("Access denied: USER");
					return Exceptional.of(savedStage, exception);
				}
			}

			stage.setCode(stage.getAction().getCode());
			stage.setDemand(demand);
			completeStage(stage, user);

			savedStage = stageRepository.save(stage);
			return Exceptional.of(savedStage, exception);
		}).orElseThrow(ResourceNotFoundException::new).orElseThrow();
	}

	
	public Demand update(Demand demand) {
		return demandRepository.findById(demand.getId()).map(savedDemand -> {
			val user = userService.loadCurrentUser();
			if (user.getUsername().equals(savedDemand.getCreated().getActorUid())) {
				savedDemand.setRate(demand.getRate());
				savedDemand.setOpinion(demand.getOpinion());
				
				savedDemand = demandRepository.save(savedDemand);
		
				val profile = savedDemand.getProfile();
				val rules = rulesRepository.findById(profile);
				val actionManager = new ActionManager(user, rules);
				actionManager.defineActions(savedDemand);
			} else {
				log.error("Access denied: USER");
				savedDemand = null;
			}
			
			return Optional.ofNullable(savedDemand);
		})
		.orElseThrow(ResourceNotFoundException::new)
		.orElseThrow(() -> new AccessDeniedException("USER"));
	}
	
	
	public PageableResultList<Demand> getAll(DemandFilter filter, String profile) {
		val user = userService.loadCurrentUser();
		val demands = demandRepository.findByMetadata(profile, filter, user);

		if (demands.getPagination().getRowsNumber() > 0) {
			val rules = rulesRepository.findById(profile);
			val actionManager = new ActionManager(user, rules);
			demands.getResultList().stream().forEach(actionManager::defineActions);
		}
		
		return demands;
	}

	
	private static final String ANONYMOUS = "anonymous";

	private Stage completeStage(Stage stage, DetailedUser user) {
		stage.setDate(Calendar.getInstance(TimeZone.getTimeZone("UTC")));
		var userId = ANONYMOUS;
		var fullName = ANONYMOUS;
		if (user != null) {
			userId = user.getUsername();
			fullName = String.join(" ", user.getLastName(), user.getFirstName());
		}
		stage.setActorUid(userId);
		stage.setActorName(fullName);
		return stage;
	}
}
