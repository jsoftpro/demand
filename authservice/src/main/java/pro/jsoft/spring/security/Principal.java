package pro.jsoft.spring.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class Principal extends User {
	private static final long serialVersionUID = 1L;

	private final String firstName;
	private final String lastName;
	private final String email;
	private final String language;

	public Principal(
			String username, 
			String password, 
			Collection<? extends GrantedAuthority> authorities,
			String firstName,
			String lastName,
			String email,
			String language) {
		super(username, password, authorities);
		
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.language = language;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getLanguage() {
		return language;
	}
}
