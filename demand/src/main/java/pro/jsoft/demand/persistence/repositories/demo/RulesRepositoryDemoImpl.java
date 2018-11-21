package pro.jsoft.demand.persistence.repositories.demo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import pro.jsoft.demand.persistence.model.Rules;
import pro.jsoft.demand.persistence.repositories.RulesRepository;

@Repository
@Profile("demo")
@Slf4j
public class RulesRepositoryDemoImpl implements RulesRepository {
	private Map<String, Rules> rules = new HashMap<>();
	
	public RulesRepositoryDemoImpl() {
		Rules rules1 = new Rules("TEST", false, false, true, true, false);
		Rules rules2 = new Rules("TEST2", true, true, true, true, true);
		rules.put(rules1.getProfile(), rules1);
		rules.put(rules2.getProfile(), rules2);
		
		log.debug("Using demo RulesRepository");
	}
	
	@Override
	public Optional<Rules> findById(String id) {
		log.debug("Load rules for {}", id);
		return Optional.ofNullable(rules.get(id));
	}


	@Override
	public <S extends Rules> S save(S entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <S extends Rules> Iterable<S> saveAll(Iterable<S> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean existsById(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Rules> findAll() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterable<Rules> findAllById(Iterable<String> ids) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long count() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteById(String id) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Rules entity) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll(Iterable<? extends Rules> entities) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAll() {
		throw new UnsupportedOperationException();
	}

}
