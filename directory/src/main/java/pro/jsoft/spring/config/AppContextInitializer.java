package pro.jsoft.spring.config;

import java.io.IOException;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String DEFAULT_SPRING_PROFILE = "default";
    
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		try {
			environment.getPropertySources()
					.addFirst(new ResourcePropertySource("classpath:application.properties"));
			if (environment.getProperty("spring.profiles.active") == null) {
				environment.setActiveProfiles(DEFAULT_SPRING_PROFILE);
			}
			log.info("Activated Spring Profile: " + environment.getProperty("spring.profiles.active"));
		} catch (IOException e) {
			log.error("Could not find properties file in classpath.");
		}
	}
}
