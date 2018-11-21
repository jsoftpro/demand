package pro.jsoft.demand.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.FileComplete;
import pro.jsoft.demand.persistence.model.FileInfo;
import pro.jsoft.demand.persistence.repositories.DemandRepository;
import pro.jsoft.demand.persistence.repositories.FileRepository;
import pro.jsoft.spring.security.DomainUserDetailsService;

@Service
@Slf4j
public class FileService {
	private DomainUserDetailsService userService;
	private FileRepository fileRepository;
	private DemandRepository demandRepository;

	@Autowired
	public FileService(
			DomainUserDetailsService userService,
			FileRepository fileRepository,
			DemandRepository demandRepository) {
		this.userService = userService;
		this.fileRepository = fileRepository;
		this.demandRepository = demandRepository;
	}

    public FileInfo save(MultipartFile file, Long demandId) {
		if (file.isEmpty()){
			throw new IllegalArgumentException("EMPTY_CONTENT");
		}		

		log.debug("Post File: {}, {}, {}", file.getOriginalFilename(), file.getSize(), file.getContentType());

		return demandRepository.findById(demandId).map(demand -> {
			val user = userService.loadCurrentUser();
			FileInfo savedFile = null;
			if (Action.DO_UPLOAD.isAllowed(demand, user)) {
				val newFile = new FileComplete(file);
				newFile.setDemand(new Demand(demandId));
				savedFile = fileRepository.save(newFile);
			}
			
			return Optional.ofNullable(savedFile);
		})
		.orElseThrow(ResourceNotFoundException::new)
		.orElseThrow(() -> new AccessDeniedException("UPLOAD"));
	}

	public FileComplete get(Long fileId) {		
		val fileResult = fileRepository.findById(fileId);
		if (!fileResult.isPresent()) {
			throw new ResourceNotFoundException();
		}
		
		val file = fileResult.get();
		val demand = file.getDemand();
		val user = userService.loadCurrentUser();

		if (!Action.DO_DOWNLOAD.isAllowed(demand, user)) { 
			throw new AccessDeniedException("DOWNLOAD");
		}
		
		return file;
	}
	
	public void remove(Long fileId) {
		val fileResult = fileRepository.findById(fileId);
		if (!fileResult.isPresent()) {
			throw new ResourceNotFoundException();
		}
		
		val file = fileResult.get();
		val demand = file.getDemand();
		val user = userService.loadCurrentUser();

		if (!Action.DO_UPLOAD.isAllowed(demand, user)) { 
			throw new AccessDeniedException("DELETE");
		}
		
		fileRepository.delete(file);
	}

}
