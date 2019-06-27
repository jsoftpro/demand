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
import lombok.var;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.rest.types.ResultMessage;
import pro.jsoft.demand.rest.types.dto.ExecutorDto;
import pro.jsoft.demand.rest.types.dto.mapping.ExecutorMapper;
import pro.jsoft.demand.services.ExecutorService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/executors")
@Slf4j
public class ExecutorRestController {
	private ExecutorService executorService;
	private ExecutorMapper executorMapper;
	
	
	@Autowired
	public ExecutorRestController(ExecutorService executorService, ExecutorMapper executorMapper) {
		this.executorService = executorService;
		this.executorMapper = executorMapper;
	}

	
	@GetMapping(value = "")
	public HttpEntity<List<ExecutorDto>> getAll(@RequestParam(required = true) String profile) {
		val executorList = executorService.getAll(profile);
		
		List<ExecutorDto> dtoList = executorList.stream()
				.map(e -> executorMapper.convertDomainToDto(e))
				.collect(Collectors.toList());
		
		return new ResponseEntity<>(dtoList, HttpStatus.OK);
	}


	@PostMapping(value = "")
	public HttpEntity<ExecutorDto> save(@RequestBody(required = true) ExecutorDto executor) {
		var executorEntity = executorMapper.convertDtoToDomain(executor);
		executorEntity = executorService.save(executorEntity);
		val savedExecutor = executorMapper.convertDomainToDto(executorEntity);
		return new ResponseEntity<>(savedExecutor, HttpStatus.OK);
	}
	

	@DeleteMapping(path = "/{id}")
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ResultMessage> remove(@PathVariable(name = "id", required = true) Long id) {
		log.debug("Remove Executor: {}", id);
		executorService.remove(id);
		return new ResponseEntity<>(new ResultMessage(HttpStatus.OK.value(), "SUCCESS"), HttpStatus.OK);
	}
}
