package pro.jsoft.spring.security;

import lombok.val;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class BasicAuthEntryPoint extends BasicAuthenticationEntryPoint {
    @Override
    public void commence
      (HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) 
      throws IOException {
    	boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    	
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String auth = "Basic realm=\"" + getRealmName() + "\"";
        if (isAjax) {
        	auth = "x-" + auth;
        }
        response.addHeader("WWW-Authenticate", auth);
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Content-Type", "text/plain");
        val writer = response.getWriter();
        writer.print("Http 401 - " + authenticationException.getMessage().trim());
    }
 
    @Override
    public void afterPropertiesSet() throws Exception {
        setRealmName("default");
        super.afterPropertiesSet();
    }
}
