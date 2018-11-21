package pro.jsoft.spring.security;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.val;
import pro.jsoft.auth.rest.types.AuthResponse;

@Service
public class AuthService {
	private Logger logger = LoggerFactory.getLogger(AuthService.class);
	private static final int LIFETIME_SECONDS = 10 * 60;
	@Value("${jwt.secret}")
	private String secretKey;

	private Map<String, Principal> userMap = new HashMap<>();
	
	public AuthService() {
		//TODO: TEST!!!
		userMap.put("korchagin-vu@co.rosenergoatom.ru", 
				new Principal(
						"korchagin-vu@co.rosenergoatom.ru", 
						"123", 
						Arrays.asList(new SimpleGrantedAuthority("DEMAND_TEST_USER")),
						"Вячеслав Юрьевич", "Корчагин", 
						"korchagin-vu@rosenergoatom.ru", 
						"RU"));
		
		userMap.put("pereslegin-va@co.rosenergoatom.ru", 
				new Principal(
						"pereslegin-va@co.rosenergoatom.ru", 
						"123", 
						Arrays.asList(new SimpleGrantedAuthority("DEMAND_TEST_USER"),
								new SimpleGrantedAuthority("DEMAND_TEST_ADMIN")),
						"Владимир Александрович", "Переслегин", 
						"pereslegin-va@rosenergoatom.ru", 
						"RU"));
		
		userMap.put("poznyakova-nn@co.rosenergoatom.ru", 
				new Principal(
						"poznyakova-nn@co.rosenergoatom.ru", 
						"123", 
						Arrays.asList(new SimpleGrantedAuthority("DEMAND_TEST_USER"),
								new SimpleGrantedAuthority("DEMAND_TEST_ADMIN")),
						"Нина Николаевна", "Познякова", 
						"poznyakova-nn@rosenergoatom.ru", 
						"RU"));
		
		userMap.put("orlova-da@co.rosenergoatom.ru", 
				new Principal(
						"orlova-da@co.rosenergoatom.ru", 
						"123", 
						Arrays.asList(new SimpleGrantedAuthority("DEMAND_TEST_USER")),
						"Диана Андреевна", "Орлова", 
						"orlova-da@rosenergoatom.ru", 
						"RU"));
		
		userMap.put("rozenberg-bi@co.rosenergoatom.ru", 
				new Principal(
						"rozenberg-bi@co.rosenergoatom.ru", 
						"123", 
						Arrays.asList(new SimpleGrantedAuthority("DEMAND_TEST_USER")),
						"Борис Израилевич", "Розенберг", 
						"rozenberg-bi@rosenergoatom.ru", 
						"RU"));
		
		logger.debug(">>> FAKE USER DB WAS CREATED");
	}
	
	public AuthResponse createToken(String scope) {
		if (isAuthenticated()) {
			val auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				val userName = auth.getName();
				val user = findUserByUniqueName(userName);
				if (user != null) {
					val response = new AuthResponse();
					val claims = new HashMap<String, Object>();
					val authorities = user.getAuthorities().stream()
							.map(GrantedAuthority::getAuthority)
							.collect(Collectors.joining(","));
					claims.put(JwtFields.AUTHORITIES_KEY, authorities);
					claims.put(JwtFields.EMAIL_KEY, user.getEmail());
					claims.put(JwtFields.LASTNAME_KEY, user.getLastName());
					claims.put(JwtFields.FIRSTNAME_KEY, user.getFirstName());
					claims.put(JwtFields.LOCALE_KEY, (user.getLanguage() != null ? user.getLanguage() : "RU"));
					claims.put(JwtFields.SCOPE_KEY, scope);
					
					val cal = Calendar.getInstance();
					cal.add(Calendar.SECOND, LIFETIME_SECONDS);
					val expirationDate = cal.getTime();
					
					val jti = UUID.randomUUID().toString();
					
					val jwt = Jwts.builder()
							.setClaims(claims)
							.setId(jti)
							.setExpiration(expirationDate)
							.setSubject(userName)
							.setIssuedAt(Calendar.getInstance().getTime())
							.signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
							.compact();
							
					response.setJti(jti);
					response.setAccessToken(jwt);
					response.setExpiresIn(expirationDate.getTime() / 1000);
					response.setEmail(user.getEmail());
					return response;
				}
			}
		}

		return null;
	}
	
	private Principal findUserByUniqueName(String userName) {
		//TODO: TEST!!! Replace with UME
		return userMap.get(userName);
	}
	
	private boolean isAuthenticated() {
    	return SecurityContextHolder.getContext().getAuthentication() != null &&
    				SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
    				//when Anonymous Authentication is enabled
    				!(SecurityContextHolder.getContext().getAuthentication() 
    			          instanceof AnonymousAuthenticationToken); 
    }
}
