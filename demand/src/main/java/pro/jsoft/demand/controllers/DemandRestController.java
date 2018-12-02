package pro.jsoft.demand.controllers;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.controllers.convertors.DemandListToCsvConverter;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.rest.types.DateRange;
import pro.jsoft.demand.rest.types.DemandFilter;
import pro.jsoft.demand.rest.types.Mode;
import pro.jsoft.demand.rest.types.PageableResultList;
import pro.jsoft.demand.rest.types.Pagination;
import pro.jsoft.demand.rest.types.dto.DemandDto;
import pro.jsoft.demand.rest.types.dto.StageDto;
import pro.jsoft.demand.rest.types.dto.mapping.DemandMapper;
import pro.jsoft.demand.rest.types.dto.mapping.StageMapper;
import pro.jsoft.demand.services.DemandService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/demands")
@Transactional
@Slf4j
public class DemandRestController {
	private DemandService demandService;
	private DemandMapper demandMapper;
	private StageMapper stageMapper;
	private DemandListToCsvConverter demandConverter;
	private MappingJackson2HttpMessageConverter jsonConverter;	

	@Autowired
	public DemandRestController(DemandService demandService, 
			DemandMapper demandMapper, 
			StageMapper stageMapper, 
			MappingJackson2HttpMessageConverter jsonConverter,
			DemandListToCsvConverter demandConverter) {
		this.demandService = demandService;
		this.demandMapper = demandMapper;
		this.stageMapper = stageMapper; 
		this.jsonConverter = jsonConverter;
		this.demandConverter = demandConverter;
	}
	
	@GetMapping(value = "/{id}")
	public HttpEntity<Demand> get(@PathVariable(name = "id", required = true) Long id, @RequestParam(required = true) String profile) {
		log.debug("Entry method DemandRestController.get");
		return new ResponseEntity<>(demandService.get(id, profile), HttpStatus.OK);
	}
	
	@PostMapping(value = "/")
	public HttpEntity<DemandDto> save(@Valid @RequestBody(required = true) DemandDto demand) {
		log.debug("Entry method DemandRestController.post");
		Demand demandEntity = demandMapper.convertDtoToDomain(demand);
		demandEntity = demandService.save(demandEntity);
		demand = demandMapper.convertDomainToDto(demandEntity);
		return new ResponseEntity<>(demand, HttpStatus.OK);
	}

	
	@PostMapping(value = "/{id}/stages")
	public HttpEntity<StageDto> addStage(@PathVariable(name = "id", required = true) Long id, 
										@RequestBody(required = true) StageDto stage) {
		log.debug("Entry method DemandRestController.addStage {}", stage.getAction());
		Stage stageEntity = stageMapper.convertDtoToDomain(stage);
		stageEntity.setDemand(new Demand(id));
		stageEntity = demandService.addStage(stageEntity);
		stage = stageMapper.convertDomainToDto(stageEntity);
		return new ResponseEntity<>(stage, HttpStatus.OK);
	}
	
	
	@PutMapping(value = "/{id}")
	public HttpEntity<DemandDto> update(@PathVariable(name = "id", required = true) Long id, 
			@RequestBody(required = true) DemandDto demand) {
		log.debug("Entry method DemandRestController.update {}", id);
		demand.setId(id);
		Demand demandEntity = demandMapper.convertDtoToDomain(demand);
		demandEntity = demandService.update(demandEntity);
		demand = demandMapper.convertDomainToDto(demandEntity);
		return new ResponseEntity<>(demand, HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/")
	public HttpEntity<PageableResultList<DemandDto>> getAll(@RequestParam DemandFilter filter, @RequestParam(required = true) String profile) {
		log.debug("Entry method DemandRestController.getAll {}", filter.getMode());
		PageableResultList<Demand> resultList = demandService.getAll(filter, profile);
		PageableResultList<DemandDto> dtoList = new PageableResultList<>();
		dtoList.setPagination(resultList.getPagination());
		List<DemandDto> demands = resultList.getResultList()
				.stream()
				.map(e -> demandMapper.convertDomainToDto(e))
				.collect(Collectors.toList());
		dtoList.setResultList(demands);
		return new ResponseEntity<>(dtoList, HttpStatus.OK);
	}
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

	@GetMapping(path = "/report", produces = { MediaType.TEXT_PLAIN_VALUE })
	@ResponseBody
	public HttpEntity<Resource> getReport(@RequestParam String from, @RequestParam String to, @RequestParam(required = true) String profile) {
		log.debug("Entry method DemandRestController.getReport {} {} {}", profile, from, to);
		
		val fromCalendar = Calendar.getInstance();
		val toCalendar = Calendar.getInstance();
		try {
			fromCalendar.setTimeInMillis(dateFormatter.parse(from).getTime());
			toCalendar.setTimeInMillis(dateFormatter.parse(to).getTime());
			toCalendar.add(Calendar.DAY_OF_YEAR, 1);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
		val createdAt = new DateRange(fromCalendar, toCalendar);

		val pagination = new Pagination(1, 500, "id", false, null);
		
		val filter = new DemandFilter();
		filter.setMode(Mode.ADMINISTRATOR);
		filter.setCreatedAt(createdAt);
		filter.setPagination(pagination);
		PageableResultList<Demand> resultList = demandService.getAll(filter, profile);
		
		String csv = demandConverter.convert(resultList.getResultList());
		if (csv == null) {
			throw new ResourceNotFoundException();
		}
		
		Resource resource = new ByteArrayResource(csv.getBytes());
		val headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "report.csv");
        headers.setContentLength(csv.length());
        return ResponseEntity.ok()
				.headers(headers)
				.body(resource);
	}

	
	@InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(
        		DemandFilter.class, 
        		new PropertyEditorSupport() {
        			Object value;
        			@Override
        			public Object getValue() {
        				return value;
        			}
        			
        			@Override
        			public void setAsText(String text) {
        				try {
							value = jsonConverter.getObjectMapper().readValue(text, DemandFilter.class);
						} catch (Exception e) {
							throw new IllegalArgumentException(e);
						}
        			}
        		});
    }
}
