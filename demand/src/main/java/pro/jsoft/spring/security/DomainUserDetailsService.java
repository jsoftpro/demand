package pro.jsoft.spring.security;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.val;
import lombok.var;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Executor;
import pro.jsoft.demand.persistence.repositories.ExecutorRepository;

/**
 * Authenticate a user from the session.
 */
@Service("userDetailsService")
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {
	@Autowired
	private ExecutorRepository executorRepository;

	@Override
	public UserDetails loadUserByUsername(final String login) {
		if (!StringUtils.isEmpty(login)) {
			log.debug("DomainUserDetailsService.loadUserByUsername: {}", login);
		
			val user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (login.equals(user.getUsername()) && user instanceof DetailedUser) {
				return user;
			}
		}
		
		throw new UsernameNotFoundException("Unknown user " + login);
	}
	
	public DetailedUser loadCurrentUser() {
		val auth = SecurityContextHolder.getContext().getAuthentication();
		val user = (auth == null || !(auth.getPrincipal() instanceof DetailedUser)) 
								? null 
								: (DetailedUser) auth.getPrincipal();

		if (user != null) {
			val executors = executorRepository.findByProfileAndUid(user.getScope(), user.getUsername());
			log.debug("Executor of {} branches", executors.size());
			var servicedBranch = executors.stream().map(Executor::getBranch).collect(Collectors.toSet());
			user.setServicedBranch(servicedBranch);
		}
		
		return user;
	}
}
