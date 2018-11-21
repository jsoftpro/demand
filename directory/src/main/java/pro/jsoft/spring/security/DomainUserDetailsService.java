package pro.jsoft.spring.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Authenticate a user from the session.
 */
@Component("userDetailsService")
@Slf4j
public class DomainUserDetailsService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
		if (StringUtils.isEmpty(login)) {
			return null;
		}
		
		log.debug("DomainUserDetailsService.loadUserByUsername: {}", login);
		
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (login.equals(userDetails.getUsername()) && userDetails instanceof DetailedUser) {
			return userDetails;
		}
		
		throw new UsernameNotFoundException("Unknown user " + login);
	}

}
