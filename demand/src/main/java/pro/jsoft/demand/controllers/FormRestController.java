package pro.jsoft.demand.controllers;

import java.util.List;
import java.util.stream.Collectors;

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
import pro.jsoft.demand.persistence.model.Form;
import pro.jsoft.demand.persistence.repositories.FormRepository;
import pro.jsoft.demand.rest.types.dto.FormDto;
import pro.jsoft.demand.rest.types.dto.mapping.FormMapper;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/forms")
public class FormRestController {
	private FormRepository formRepository;
	private FormMapper formMapper;

	@Autowired
	public FormRestController(FormRepository formRepository, FormMapper formMapper) {
		this.formRepository = formRepository;
		this.formMapper = formMapper;
	}
	
	@GetMapping(value = "")
	public HttpEntity<List<FormDto>> getAll(@RequestParam(required = true) String profile) {
		val formList = formRepository.findByProfileOrderByName(profile);
		
		List<FormDto> dtoList = formList.stream()
				.map(e -> formMapper.convertDomainToDto(e))
				.collect(Collectors.toList());
		
		return new ResponseEntity<>(dtoList, HttpStatus.OK);
	}

	@PostMapping(value = "")
	public HttpEntity<FormDto> save(@RequestBody(required = true) FormDto form) {
		Form formEntity = formMapper.convertDtoToDomain(form);
		formEntity = formRepository.save(formEntity);
		form = formMapper.convertDomainToDto(formEntity);
		return new ResponseEntity<>(form, HttpStatus.OK);
	}

	@DeleteMapping(path = "/{formId}")
	@ResponseStatus(value = HttpStatus.OK)
	public void remove(@PathVariable(name = "formId", required = true) String formId) {
		formRepository.deleteById(formId);
	}
}
