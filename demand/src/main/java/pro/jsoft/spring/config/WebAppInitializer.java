package pro.jsoft.spring.config;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import lombok.val;
import pro.jsoft.spring.security.RequestAndResponseLoggingFilter;

public class WebAppInitializer extends AbstractSecurityWebApplicationInitializer {
	public WebAppInitializer() {
		super(ApplicationConfig.class, SecurityConfig.class);
		Locale.setDefault(Locale.forLanguageTag("ru-RU"));
	}

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		val dispatcherServletContext = new AnnotationConfigWebApplicationContext();
		val dispatcherServlet = new DispatcherServlet(dispatcherServletContext);
		val dispatcherRegistration = servletContext.addServlet("dispatcher", dispatcherServlet);
		dispatcherRegistration.setLoadOnStartup(1);
		dispatcherRegistration.addMapping("/");

		val loggingFilter = servletContext.addFilter("loggingFilter", RequestAndResponseLoggingFilter.class);
		loggingFilter.addMappingForServletNames(null, false, "dispatcher");

		val encodingFilter = servletContext.addFilter("encoding-filter", new CharacterEncodingFilter());
		encodingFilter.setInitParameter("encoding", "UTF-8");
		encodingFilter.setInitParameter("forceEncoding", "true");
		encodingFilter.addMappingForUrlPatterns(null, true, "/*");
	}
}
