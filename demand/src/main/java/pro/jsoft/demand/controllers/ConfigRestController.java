package pro.jsoft.demand.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/config")
public class ConfigRestController {
	@Value("${service.auth}")
	private String serviceAuth;
	@Value("${service.directory}")
	private String serviceDirectory;
	
	@GetMapping(value = "")
	public HttpEntity<Map<String, String>> getLinks() {
		Map<String, String> config = new HashMap<>();
		config.put("auth", serviceAuth);
		config.put("directory", serviceDirectory);
		return new ResponseEntity<>(config, HttpStatus.OK);
	}
}
