package test.user;

import org.springframework.context.annotation.Bean;

import pro.jsoft.spring.security.DomainUserDetailsService;
import test.MockUserServiceConfigPrototype;
import test.Users;

public class MockUserServiceConfig extends MockUserServiceConfigPrototype {
	protected String getUid() {
		return Users.USER;
	}
	
	@Bean
	public DomainUserDetailsService userDetailsService() {
		return super.userDetailsService();
	}
}
