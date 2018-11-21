package pro.jsoft.spring.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_BEARER = "Bearer";

	@Value("${jwt.secret}")
	private String secretKey;
    private JwtParser parser;
    
    public TokenProvider() {
    }

    @PostConstruct
    public void init() {
    	parser = Jwts.parser();
    	parser.setSigningKey(secretKey.getBytes());
    }

    public Authentication getAuthentication(String authToken) {

    	log.info("TokenProvider.getAuthentication({})", authToken);

    	val claims = parser.parseClaimsJws(authToken).getBody();

    	Collection<? extends GrantedAuthority> authorities;
    	val sAuthorities = claims.get(JwtFields.AUTHORITIES_KEY, String.class);
    	if (StringUtils.hasText(sAuthorities)) {
    		authorities =
    			Arrays.stream(sAuthorities.split(","))
    				.map(SimpleGrantedAuthority::new)
    				.collect(Collectors.toList());
    	} else {
    		authorities = Collections.emptyList();
    	}
    	
        val principal = new DetailedUser(claims.getSubject(), "", authorities,
        									claims.get(JwtFields.FIRSTNAME_KEY, String.class),
        									claims.get(JwtFields.LASTNAME_KEY, String.class),
        									claims.get(JwtFields.EMAIL_KEY, String.class),
        									claims.get(JwtFields.LOCALE_KEY, String.class),
        									claims.get(JwtFields.SCOPE_KEY, String.class)
        								);
        
        return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
    }

    
    public Optional<String> resolveToken(HttpServletRequest request){
    	log.debug("TokenProvider.resolveToken");

        val bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTHORIZATION_BEARER + " ")) {
        	log.debug("Get token from header");
        	String token = bearerToken.substring(AUTHORIZATION_BEARER.length() + 1, bearerToken.length());
        	return Optional.of(token);
        }
        
        return Optional.empty();
    }

    
    public boolean validateToken(String authToken) {
        try {
        	log.info("TokenProvider.validateToken({})", authToken);
            parser.parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        
        return false;
    }
}
