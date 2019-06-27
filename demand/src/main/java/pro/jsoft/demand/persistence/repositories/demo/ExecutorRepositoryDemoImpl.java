package pro.jsoft.demand.persistence.repositories.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Executor;
import pro.jsoft.demand.persistence.repositories.ExecutorRepository;

@Repository
@Profile("demo~")
@Slf4j
public class ExecutorRepositoryDemoImpl implements ExecutorRepository {
	private Map<String, List<Executor>> executors = new HashMap<>(); 
	private String[] uids = {
			"poznyakova-nn@co.rosenergoatom.ru", 
			"orlova-da@co.rosenergoatom.ru"
	};
	
	public ExecutorRepositoryDemoImpl() {
		for (String uid : uids) {
			Executor executor = new Executor();
			executor.setUid(uid);
			executor.setBranch("34 f 12c9 2147 2197 211a");
			executors.put(uid, Collections.singletonList(executor));
		}

		log.debug("Using demo ExecutorRepository");
	}

	@Override
	public List<Executor> findByProfileAndUid(String profile, String userId) {
		if (executors.containsKey(userId)) {
			return executors.get(userId);
		}
		
		return Collections.emptyList();
	}

	@Override
	public List<Executor> findByProfileOrderByBranch(String profile) {
		return executors.values().stream().flatMap(List::stream).collect(Collectors.toList());
	}

	@Override
	public Optional<Executor> findById(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Executor> S save(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Executor> Iterable<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsById(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Executor> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Executor> findAllById(Iterable<Long> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteById(Long id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Executor entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Executor> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}
}
