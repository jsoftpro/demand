package pro.jsoft.demand.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.FileComplete;
import pro.jsoft.demand.rest.types.ResultMessage;
import pro.jsoft.demand.rest.types.dto.FileDto;
import pro.jsoft.demand.rest.types.dto.mapping.FileMapper;
import pro.jsoft.demand.services.FileService;

@RestController
@RequestMapping("/api/demands")
@CrossOrigin("*")
@Slf4j
public class FileRestController {
	private FileService fileService;
	private FileMapper fileMapper;
	
	@Autowired
	public FileRestController(FileService fileService, FileMapper fileMapper) {
		this.fileService = fileService;
		this.fileMapper = fileMapper;
	}
	
	@PostMapping("/{demandId}/files")
    public HttpEntity<FileDto> upload(@RequestParam("file") MultipartFile file, 
    		@PathVariable(name = "demandId", required = true) Long demandId) {
		FileDto fileInfo = fileMapper.convertDomainToDto(fileService.save(file, demandId));
		return new ResponseEntity<>(fileInfo, HttpStatus.OK);
	}
	
	@Transactional
	@GetMapping(path = "/files/{fileId}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
	@ResponseBody
	public ResponseEntity<Resource> download(@PathVariable(name = "fileId", required = true) Long fileId) {
		log.debug("Get File: {}", fileId);
		
		FileComplete file = fileService.get(fileId);
		
		Resource resource = new ByteArrayResource(file.getContent());
		val headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentDispositionFormData("attachment", encodeString(file.getName()));
        headers.setContentLength(file.getSize());
        return ResponseEntity.ok()
				.headers(headers)
				.body(resource);
	}
	
	@DeleteMapping(path = "/files/{fileId}")
	public ResponseEntity<ResultMessage> remove(@PathVariable(name = "fileId", required = true) Long fileId) {
		log.debug("Remove File: {}", fileId);
		fileService.remove(fileId);
		return new ResponseEntity<>(new ResultMessage(HttpStatus.OK.value(), "SUCCESS"), HttpStatus.OK);
	}
	
	private static String encodeString(String s) {
		try {
			return URLEncoder.encode(s, "UTF8").replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			return s;
		}
	}
}
