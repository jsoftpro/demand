package pro.jsoft.spring.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode(callSuper = false)
public class DetailedUser extends User {
	private static final long serialVersionUID = 1L;

	private final String firstName;
	private final String lastName;
	private final String email;
	private final String language;
	private final String scope;
	@Setter
	private Set<String> servicedBranch;

	@Builder(builderMethodName = "createBuilder") 
	private DetailedUser(
			String username, 
			Collection<? extends GrantedAuthority> authorities,
			String firstName,
			String lastName,
			String email,
			String language,
			String scope) {
		super(username, "", authorities);
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.language = language;
		this.scope = scope;
	}
}
