package pro.jsoft.demand.persistence.repositories;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

import pro.jsoft.demand.persistence.model.Executor;

@Profile("production")
public interface ExecutorRepository extends CrudRepository<Executor, Long> {
	List<Executor> findByProfileOrderByBranch(String profile);
	List<Executor> findByProfileAndUid(String profile, String userId);
}
