package pro.jsoft.spring.config;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.val;
import lombok.var;
import pro.jsoft.utils.rest.StringWithoutSpaceDeserializer;

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/views/", ".jsp");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("index");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/html/**").addResourceLocations("/WEB-INF/html/").setCachePeriod(31556926);
		registry.addResourceHandler("/fonts/**").addResourceLocations("/WEB-INF/fonts/").setCachePeriod(31556926);
        registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/").setCachePeriod(31556926);
        registry.addResourceHandler("/images/**").addResourceLocations("/WEB-INF/images/").setCachePeriod(31556926);
        registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/").setCachePeriod(31556926);
	}
	
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        	.allowedOrigins("*")
        	.allowedHeaders("*")
        	.allowedMethods("GET", "POST", "PUT", "OPTIONS");
    }
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(jacksonHttpMessageConverter());
		converters.add(stringHttpMessageConverter());
		converters.add(resourceHttpMessageConverter());
	}
	
	@Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        // Turn off suffix-based content negotiation
        configurer.favorPathExtension(false);
    }

	@Bean
	public MappingJackson2HttpMessageConverter jacksonHttpMessageConverter() {
		val converter = new MappingJackson2HttpMessageConverter();
		converter.setPrettyPrint(true);

        val mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        val dateFormat = new SimpleDateFormat(Constants.DATETIME_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mapper.setDateFormat(dateFormat);
        
        val stringModule = new SimpleModule();
        stringModule.addDeserializer(String.class, new StringWithoutSpaceDeserializer(String.class));
        mapper.registerModule(stringModule);

        converter.setObjectMapper(mapper);

        return converter;
	}
	
	@Bean
	public StringHttpMessageConverter stringHttpMessageConverter() {
		return new StringHttpMessageConverter();
	}
	
	@Bean
	public ResourceHttpMessageConverter resourceHttpMessageConverter() {
		return new ResourceHttpMessageConverter();
	}
	
	@Value("${spring.servlet.multipart.max-file-size}")
	private String hrFileSize;
	@Value("${spring.servlet.multipart.max-request-size}")
	private String hrSize;
	
	@Bean
	public MultipartResolver multipartResolver() {
	    val multipartResolver = new CommonsMultipartResolver();
	    val maxFileSize = parseHumanReadableSize(hrFileSize, 5000000);
	    multipartResolver.setMaxUploadSizePerFile(maxFileSize);
	    return multipartResolver;
	}
	
	private static long parseHumanReadableSize(String hrFileSize, long defaultValue) {
		var fileSize = defaultValue;
		if (StringUtils.hasText(hrFileSize)) {
	    	hrFileSize = hrFileSize.trim().toUpperCase();
	    	if (hrFileSize.length() > 2) {
	    		if (hrFileSize.endsWith("MB")) {
		    		fileSize = Long.parseLong(hrFileSize.substring(0, hrFileSize.length() - 2));
					fileSize *= FileUtils.ONE_MB;
	    		} else if (hrFileSize.endsWith("KB")) {
		    		fileSize = Long.parseLong(hrFileSize.substring(0, hrFileSize.length() - 2));
					fileSize *= FileUtils.ONE_KB;
				} else {
		    		fileSize = Long.parseLong(hrFileSize);
				}
	    	} else {
	    		fileSize = Long.parseLong(hrFileSize);
	    	}
	    }
		return fileSize;
	}
}
