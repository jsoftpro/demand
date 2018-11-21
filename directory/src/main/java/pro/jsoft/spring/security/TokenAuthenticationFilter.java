package pro.jsoft.spring.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.val;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
	private final TokenProvider tokenProvider;

    public TokenAuthenticationFilter(TokenProvider tokenProvider) {
		this.tokenProvider = tokenProvider;
	}
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
    	if (!isAuthenticated()) {
	        val authToken = this.tokenProvider.resolveToken(request);
	        authToken.ifPresent(token -> {
	        	if (this.tokenProvider.validateToken(token)) {
	        		Authentication authentication = this.tokenProvider.getAuthentication(token);
	        		SecurityContextHolder.getContext().setAuthentication(authentication);
	        	}
	        });
    	}
    	
        filterChain.doFilter(request, response);
	}

    private boolean isAuthenticated() {
    	return SecurityContextHolder.getContext().getAuthentication() != null &&
    				SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
    				//when Anonymous Authentication is enabled
    				!(SecurityContextHolder.getContext().getAuthentication() 
    			          instanceof AnonymousAuthenticationToken); 
    }
}
