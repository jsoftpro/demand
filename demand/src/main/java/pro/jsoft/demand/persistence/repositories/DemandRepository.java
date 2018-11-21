package pro.jsoft.demand.persistence.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import pro.jsoft.demand.persistence.model.Demand;


public interface DemandRepository extends CrudRepository<Demand, Long>, DemandCustomRepository {
	Optional<Demand> findByIdAndProfile(Long id, String profile);
}
