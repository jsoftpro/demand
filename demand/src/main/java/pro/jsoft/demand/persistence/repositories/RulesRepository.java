package pro.jsoft.demand.persistence.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

import pro.jsoft.demand.persistence.model.Rules;

@Profile("production")
public interface RulesRepository extends CrudRepository<Rules, String> {
}
