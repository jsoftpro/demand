package test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import pro.jsoft.spring.security.DetailedUser;
import pro.jsoft.spring.security.DomainUserDetailsService;

public abstract class MockUserServiceConfigPrototype {
	protected String getUid() {
		return Users.USER;
	}

	public DomainUserDetailsService userDetailsService() {
		DomainUserDetailsService userDetailsService = mock(DomainUserDetailsService.class);
		when(userDetailsService.loadCurrentUser()).thenReturn(newMockUser(getUid()));
		return userDetailsService;
	}
	
	protected DetailedUser newMockUser(String uid) {
		DetailedUser user = DetailedUser.createBuilder()
				.username(uid) 
				.authorities(Collections.emptySet()) 
				.firstName("test")
				.lastName("test") 
				.email("test") 
				.language("ru") 
				.scope("TEST")
				.build();
		
		if (Users.EXECUTOR.equals(uid) || Users.EXECUTOR2.equals(uid)) {
			user.setServicedBranch(Collections.singleton(Users.BRANCH));
		}
		return user;
	}
}
