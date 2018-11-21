package pro.jsoft.demand.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import pro.jsoft.spring.security.DomainUserDetailsService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user")
public class UserRestController {
	private DomainUserDetailsService userService;
	
	@Autowired
	public UserRestController(DomainUserDetailsService userService) {
		this.userService = userService;
	}
	
	
	@GetMapping(value = "/identify")
	public HttpEntity<String> identify() {
		val user = userService.loadCurrentUser();
		if (user == null) {
			throw new ResourceNotFoundException("anonymous");
		}

		return new ResponseEntity<>(user.getUsername(), HttpStatus.OK);
	}
}
