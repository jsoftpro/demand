package pro.jsoft.spring.config;

import javax.servlet.ServletContext;

import org.springframework.data.rest.webmvc.RepositoryRestDispatcherServlet;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.filter.CharacterEncodingFilter;

import lombok.val;

public class WebAppInitializer extends AbstractSecurityWebApplicationInitializer {
	public WebAppInitializer() {
		super(ApplicationConfig.class, SecurityConfig.class);
	}

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {				
		val restRegistration = servletContext.addServlet("rest", RepositoryRestDispatcherServlet.class);
		restRegistration.setLoadOnStartup(2);
		restRegistration.addMapping("/api/*");

		val encodingFilter = servletContext.addFilter("encoding-filter",
				new CharacterEncodingFilter());
		encodingFilter.setInitParameter("encoding", "UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(null, true, "/*");
	}
}
