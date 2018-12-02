package pro.jsoft.spring.config;

import javax.servlet.ServletContext;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import lombok.val;
import pro.jsoft.spring.security.CORSFilter;

public class WebAppInitializer extends AbstractSecurityWebApplicationInitializer {

	public WebAppInitializer() {
		super(ApplicationConfig.class);
	}
	
	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		val servletAppContext = new AnnotationConfigWebApplicationContext();
		val dispatcherServlet = new DispatcherServlet(servletAppContext);
		dispatcherServlet.setThrowExceptionIfNoHandlerFound(true);

		val dispatcher = servletContext.addServlet("dispatcher", dispatcherServlet);
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");

		val corsFilter = servletContext.addFilter("cors-filter", new CORSFilter());
		corsFilter.addMappingForUrlPatterns(null, true, "/*");
	}
}
