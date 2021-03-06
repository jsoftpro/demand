package test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import pro.jsoft.spring.security.DomainUserDetailsService;
import test.MockUserServiceConfigPrototype;
import test.Users;

@Configuration
@ComponentScan(basePackages = { "pro.jsoft.spring.security", "pro.jsoft.demand" })
@Profile("test")
public class MockUserServiceConfig extends MockUserServiceConfigPrototype {
	protected String getUid() {
		return Users.USER;
	}
	
	@Bean
	public DomainUserDetailsService userDetailsService() {
		return super.userDetailsService();
	}
}
