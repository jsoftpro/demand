package pro.jsoft.spring.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class CORSFilter extends OncePerRequestFilter {
	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST");
		response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Access-Control-Allow-Headers, Origin, Accept, Authorization, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
		response.setHeader("Access-Control-Allow-Credentials", "true");

		if (request.getMethod().equals("OPTIONS")) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			filterChain.doFilter(request, response);
		}
	}
}