package pro.jsoft.demand.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import lombok.var;
import pro.jsoft.demand.rest.types.dto.RulesDto;
import pro.jsoft.demand.rest.types.dto.mapping.RulesMapper;
import pro.jsoft.demand.services.RulesService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/rules")
public class RulesRestController {
	private RulesService rulesService;
	private RulesMapper rulesMapper;
	
	
	@Autowired
	public RulesRestController(RulesService rulesRepository, RulesMapper rulesMapper) {
		this.rulesService = rulesRepository;
		this.rulesMapper = rulesMapper;
	}

	
	@GetMapping(value = "/actions")
	public HttpEntity<List<String>> getActions(@RequestParam(required = true) String profile) {
		return new ResponseEntity<>(rulesService.getActions(profile), HttpStatus.OK);
	}

	
	@GetMapping(value = "")
	public HttpEntity<RulesDto> get(@RequestParam(required = true) String profile) {
		RulesDto rules = rulesMapper.convertDomainToDto(rulesService.get(profile));
		return new ResponseEntity<>(rules, HttpStatus.OK);
	}
	

	@PostMapping(value = "")
	public HttpEntity<RulesDto> save(@RequestBody(required = true) RulesDto rules) {
		var rulesEntity = rulesMapper.convertDtoToDomain(rules);
		rulesEntity = rulesService.save(rulesEntity);
		val savedRules = rulesMapper.convertDomainToDto(rulesEntity);
		return new ResponseEntity<>(savedRules, HttpStatus.OK);
	}
	

	@DeleteMapping(path = "/{profile}")
	@ResponseStatus(value = HttpStatus.OK)
	public void remove(@PathVariable(name = "profile", required = true) String profile) {
		rulesService.remove(profile);
	}
}
