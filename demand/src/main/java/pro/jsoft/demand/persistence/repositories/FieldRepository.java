package pro.jsoft.demand.persistence.repositories;

import org.springframework.data.repository.CrudRepository;

import pro.jsoft.demand.persistence.model.Field;

public interface FieldRepository extends CrudRepository<Field, Long> {
}
