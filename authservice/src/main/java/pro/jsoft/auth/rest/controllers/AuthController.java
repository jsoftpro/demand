package pro.jsoft.auth.rest.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import pro.jsoft.auth.rest.types.AuthResponse;
import pro.jsoft.spring.security.AuthService;

@RestController
@RequestMapping("/api/oauth")
public class AuthController {
	@Autowired
	AuthService authService;
	
	@CrossOrigin(origins = "*")
	@PostMapping(value = "token")
	public HttpEntity<AuthResponse> createToken(@RequestParam("client") String client) {
		val response = authService.createToken(client);
		return new ResponseEntity<>(response, response == null ? HttpStatus.UNAUTHORIZED : HttpStatus.OK);
	}
}
