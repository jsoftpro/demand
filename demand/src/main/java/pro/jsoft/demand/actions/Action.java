package pro.jsoft.demand.actions;

import java.util.Arrays;

import lombok.val;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.spring.security.DetailedUser;

public enum Action {
	DO_UPLOAD() {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage created = demand.getCreated();
			Stage changed = demand.getChanged();
			return user.getUsername().equals(created.getActorUid())
					&& !any(changed, 
							Action.STEP_CANCEL,
							Action.STEP_FINISH,
							Action.STEP_REFUSE);
		}
	},
	DO_DOWNLOAD() {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage created = demand.getCreated();
			return user.getUsername().equals(created.getActorUid())
					|| user.getUsername().equals(created.getRecipientUid())
					|| serviced(demand, user);
		}		
	},
	DO_PRINT() {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage created = demand.getCreated();
			Stage changed = demand.getChanged();
			return (user.getUsername().equals(created.getActorUid())
					|| user.getUsername().equals(created.getRecipientUid())
					|| serviced(demand, user))
					&& !any(changed, 
							Action.STEP_CANCEL,
							Action.STEP_REFUSE);
		}		
	},
	DO_RATE() {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage created = demand.getCreated();
			Stage changed = demand.getChanged();
			return user.getUsername().equals(created.getActorUid())
					&& Action.STEP_FINISH.equals(changed.getAction())
					&& demand.getRate() == null;
		}		
	},
	STEP_CREATE(10) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			return false;
		}		
	},
	STEP_CONFIRM(20) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage created = demand.getCreated();
			Stage changed = demand.getChanged();
			return user.getUsername().equals(created.getRecipientUid())
					&& Action.STEP_CREATE.equals(changed.getAction());
		}		
	},
	STEP_ACCEPT(30) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage changed = demand.getChanged();
			return serviced(demand, user)
					&& Action.STEP_CONFIRM.equals(changed.getAction());
		}		
	},
	STEP_REACCEPT(40) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage changed = demand.getChanged();
			return serviced(demand, user)
					&& !user.getUsername().equals(changed.getActorUid())
					&& !user.getUsername().equals(changed.getRecipientUid())
					&& any(changed, 
							Action.STEP_ACCEPT,
							Action.STEP_REACCEPT,
							Action.STEP_PROCESS,
							Action.STEP_APPROVE);
		}		
	},
	STEP_PROCESS(50) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			if (serviced(demand, user)) {
				Stage changed = demand.getChanged();
				if ((user.getUsername().equals(changed.getActorUid())
						&& any(changed, 
									Action.STEP_ACCEPT,
									Action.STEP_REACCEPT,
									Action.STEP_ASKREFUSE))
					|| (user.getUsername().equals(changed.getRecipientUid())
						&& Action.STEP_APPROVE.equals(changed.getAction()))) {
					return true;
				}
			}
			return false;
		}		
	},
	STEP_ASKAPPROVE(-70) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage changed = demand.getChanged();
			return serviced(demand, user)
					&& user.getUsername().equals(changed.getActorUid())
					&& any(changed, 
							Action.STEP_ACCEPT,
							Action.STEP_REACCEPT);
		}		
	},
	STEP_APPROVE(70) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage changed = demand.getChanged();
			return user.getUsername().equals(changed.getRecipientUid())
					&& Action.STEP_ASKAPPROVE.equals(changed.getAction());
		}		
	},
	STEP_FINISH(101) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage changed = demand.getChanged();
			return (user.getUsername().equals(changed.getActorUid())
					&& Action.STEP_PROCESS.equals(changed.getAction()));
		}		
	},
	STEP_CANCEL(102) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage created = demand.getCreated();
			Stage changed = demand.getChanged();
			return user.getUsername().equals(created.getActorUid())
					&& any(changed, 
							Action.STEP_CREATE, 
							Action.STEP_CONFIRM, 
							Action.STEP_ACCEPT, 
							Action.STEP_REACCEPT);
		}		
	},
	STEP_ASKREFUSE(-103) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage created = demand.getCreated();
			Stage changed = demand.getChanged();
			return user.getUsername().equals(created.getActorUid())
					&& any(changed, 
							Action.STEP_ASKAPPROVE, 
							Action.STEP_PROCESS);
		}		
	},
	STEP_REFUSE(103) {
		public boolean isAllowed(Demand demand, DetailedUser user) {
			Stage changed = demand.getChanged();
			return (user.getUsername().equals(changed.getRecipientUid())
					&& any(changed,
						Action.STEP_CREATE,
						Action.STEP_ASKAPPROVE,
						Action.STEP_APPROVE,
						Action.STEP_ASKREFUSE))
			|| (serviced(demand, user)
					&& (Action.STEP_CONFIRM.equals(changed.getAction())
							|| (user.getUsername().equals(changed.getActorUid())
									&& any(changed, 
											Action.STEP_ACCEPT,
											Action.STEP_REACCEPT,
											Action.STEP_ASKAPPROVE,
											Action.STEP_PROCESS))));
		}		
	};
	
	private static final int NO_STEP = 0;
	private int code;
	
	private Action(int code) {
		this.code = code;
	}

	private Action() {
		this.code = NO_STEP;
	}
	
	public abstract boolean isAllowed(Demand demand, DetailedUser user);
	
	public int getCode() {
		return code;
	}
	
	public boolean isStep() {
		return this.code != NO_STEP;
	}
	
	public static Action getByCode(int code) {
		if (code == NO_STEP) return null;
		for (val action : values()) {
			if (action.getCode() == code) {
				return action;
			}
		}
		return null;
	}
	
	
	private static boolean any(Stage stage, Action ... actions) {
		val action = stage.getAction();
		return Arrays.asList(actions).contains(action);
	}
	
	private static boolean serviced(Demand demand, DetailedUser user) {
		if (user.getServicedBranch() == null || user.getServicedBranch().isEmpty()) {
			return false;
		}
		
		val demandBranch = demand.getBranch();
		for (val branch : user.getServicedBranch()) {
			if (demandBranch.startsWith(branch)) {
				return true;
			}
		}
		
		return false;
	}
}