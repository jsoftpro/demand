package pro.jsoft.demand.persistence.repositories;

import org.springframework.data.repository.CrudRepository;

import pro.jsoft.demand.persistence.model.FileComplete;

public interface FileRepository extends CrudRepository<FileComplete, Long> {
}
