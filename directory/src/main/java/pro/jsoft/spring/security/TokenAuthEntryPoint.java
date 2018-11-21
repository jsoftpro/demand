package pro.jsoft.spring.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import lombok.val;


/**
 * Returns a 401 error code (Unauthorized) to the client.
 */
public class TokenAuthEntryPoint implements AuthenticationEntryPoint {
	@Value("${jwt.client}")
	private String client;

	/**
     * Always returns a 401 error code to the client.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException)
        throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("WWW-Authenticate", "Bearer realm=\"" + client + "\", error=\"invalid_token\"");
        response.addHeader("Content-Type", "text/plain");
        val writer = response.getWriter();
        writer.print("Http 401 - " + authenticationException.getMessage().trim());
    }
}
