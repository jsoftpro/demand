package pro.jsoft.spring.config;

import org.springframework.context.MessageSource;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.data.rest.webmvc.support.ExceptionMessage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@EnableWebMvc
@ControllerAdvice(basePackageClasses = RepositoryRestExceptionHandler.class)
@Slf4j
public class DataRestExceptionHandler extends RepositoryRestExceptionHandler {
	
	public DataRestExceptionHandler(MessageSource messageSource) {
		super(messageSource);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionMessage> defaultExceptionHandler(Exception exception) {
		if (exception != null) {
			val message = exception.getMessage();
			log.error(message, exception);
			if (StringUtils.hasText(message)) {
				return new ResponseEntity<>(new ExceptionMessage(exception), new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}
		}
		
		return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}
}