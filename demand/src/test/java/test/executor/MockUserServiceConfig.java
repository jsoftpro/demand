package test.executor;

import org.springframework.context.annotation.Bean;

import pro.jsoft.spring.security.DomainUserDetailsService;
import test.MockUserServiceConfigPrototype;
import test.Users;

public class MockUserServiceConfig extends MockUserServiceConfigPrototype {
	@Override
	protected String getUid() {
		return Users.EXECUTOR;
	}
	
	@Bean
	public DomainUserDetailsService userDetailsService() {
		return super.userDetailsService();
	}
}
