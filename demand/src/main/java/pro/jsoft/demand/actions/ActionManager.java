package pro.jsoft.demand.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.User;

import lombok.val;
import lombok.var;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Rules;
import pro.jsoft.spring.security.DetailedUser;

public class ActionManager {
	private final DetailedUser user;
	private final Optional<Rules> rules;
	
	public ActionManager(final DetailedUser user, final Optional<Rules> rules) {
		this.user = user;
		this.rules = rules;
	}
	
	public List<String> defineActions(final Demand demand) {
		var actions = demand.getActions();
		if (actions == null) {
			actions = new ArrayList<>();
		}
		
		if (user != null) {
			for (val action : Action.values()) {
				if ((!rules.isPresent() || rules.get().isEnabled(action)) && action.isAllowed(demand, user)) {
					actions.add(action.name());
				}
			}
		}
		
		return actions;
	}

	public User getUser() {
		return user;
	}
}
