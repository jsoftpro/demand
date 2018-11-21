package test.approver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import pro.jsoft.spring.security.DomainUserDetailsService;
import test.MockUserServiceConfigPrototype;
import test.Users;

@Configuration
@ComponentScan(basePackages = {"pro.jsoft.demand.services", "pro.jsoft.demand.security"})
@Profile("test")
public class MockUserServiceConfig extends MockUserServiceConfigPrototype {
	@Override
	protected String getUid() {
		return Users.APPROVER;
	}
	
	@Bean
	public DomainUserDetailsService userDetailsService() {
		return super.userDetailsService();
	}
}
