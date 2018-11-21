package pro.jsoft.demand.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Executor;
import pro.jsoft.demand.persistence.repositories.ExecutorRepository;

@Service
@Slf4j
public class ExecutorService {
	private ExecutorRepository executorRepository;
	
	@Autowired
	public ExecutorService(ExecutorRepository executorRepository) {
		this.executorRepository = executorRepository;
	}

	public List<Executor> getAll(String profile) {
		List<Executor> executors = executorRepository.findByProfileOrderByBranch(profile);
		log.debug("Found {} executors", executors.size());
		return executors;
	}
	
	public Executor save(Executor executor) {
		return executorRepository.save(executor);
	}
	
	public void remove(Long id) {
		Optional<Executor> executorResult = executorRepository.findById(id);
		if (!executorResult.isPresent()) {
			throw new ResourceNotFoundException();
		}
		
		executorRepository.delete(executorResult.get());
	}
}
