package pro.jsoft.demand.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Rules;
import pro.jsoft.demand.persistence.repositories.RulesRepository;

@Service
@Slf4j
public class RulesService {
	private RulesRepository rulesRepository;
	
	@Autowired
	public RulesService(RulesRepository rulesRepository) {
		this.rulesRepository = rulesRepository;
	}
	
	public List<String> getActions(String profile) {
		val rules = get(profile);
		val actions = new ArrayList<String>();
		for (val action : Action.values()) {
			if (Action.STEP_CREATE.equals(action) && !rules.isEnabled(Action.STEP_CONFIRM)) {
				actions.add(Action.STEP_CONFIRM.name());
			} else if (rules.isEnabled(action)) {
				actions.add(action.name());
			}
		}
		return actions;
	}
	
	public Rules get(String profile) {
		val rulesEntityResult = rulesRepository.findById(profile);
		if (!rulesEntityResult.isPresent()) {
			log.debug("Use default Rules");
			return new Rules(profile, true, true, true, true, true);
		}
		return rulesEntityResult.get();
	}
	
	public Rules save(Rules rules) {
		return rulesRepository.save(rules);
	}
	
	public void remove(String profile) {
		rulesRepository.deleteById(profile);
	}
}
