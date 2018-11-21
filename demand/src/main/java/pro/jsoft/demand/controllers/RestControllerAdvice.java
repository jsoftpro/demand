package pro.jsoft.demand.controllers;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.rest.types.LocalizedExceptionMessage;

@ControllerAdvice
@CrossOrigin("*")
@RequestMapping(produces = "application/vnd.error+json")
@Slf4j
public class RestControllerAdvice {
	private static ResponseEntity<LocalizedExceptionMessage> createMessage(Throwable e, HttpStatus httpStatus) {
		log.error("Handled error: " + e.getMessage(), e);
		return new ResponseEntity<>(new LocalizedExceptionMessage(e), httpStatus);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<LocalizedExceptionMessage> notFoundException(final ResourceNotFoundException e) {
		return createMessage(e, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<LocalizedExceptionMessage> permissionException(final AccessDeniedException e) {
		return createMessage(e, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<LocalizedExceptionMessage> methodException(final HttpRequestMethodNotSupportedException e) {
		return createMessage(e, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<LocalizedExceptionMessage> parameterException(final IllegalArgumentException e) {
		return createMessage(e, HttpStatus.PRECONDITION_FAILED);
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<LocalizedExceptionMessage> uploadException(final MaxUploadSizeExceededException e) {
		return createMessage(new Exception(e), HttpStatus.PAYLOAD_TOO_LARGE);
	}

	@ExceptionHandler(Throwable.class)
	public ResponseEntity<LocalizedExceptionMessage> defaultHandler(final Throwable e) {
		return createMessage(e, HttpStatus.BAD_REQUEST);
	}
}
