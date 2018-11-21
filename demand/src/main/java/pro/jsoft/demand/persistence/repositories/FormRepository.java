package pro.jsoft.demand.persistence.repositories;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;

import pro.jsoft.demand.persistence.model.Form;

@Profile("production")
public interface FormRepository extends CrudRepository<Form, String> {
	List<Form> findByProfileOrderByName(String profile);
}
