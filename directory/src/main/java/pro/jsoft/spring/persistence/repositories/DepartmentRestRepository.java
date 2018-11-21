package pro.jsoft.spring.persistence.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import pro.jsoft.spring.persistence.model.Department;

@RepositoryRestResource(collectionResourceRel = "departments", path = "departments")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.HEAD})
public interface DepartmentRestRepository extends PagingAndSortingRepository<Department, Long> {
	public List<Department> findByNameContaining(@Param("name") String name, Pageable page);

	public List<Department> findByNameContainingAndParentIdIsNull(@Param("name") String name, Pageable page);

	@Query(value = "SELECT d FROM Department d WHERE d.branch LIKE CONCAT((SELECT p.branch FROM Department p WHERE p.id = :ancestorId), '%') AND d.name LIKE %:name%")  
	public List<Department> findByNameContainingAndAncestor(@Param("name") String name, @Param("ancestorId") Long ancestorId, Pageable page);

	@Query(value = "SELECT d FROM Department d WHERE d.parentId IS NULL AND (SELECT c.branch FROM Department c WHERE c.id = :departmentId) LIKE CONCAT(d.branch, '%')")  
	public Department findByChildAndParentIdIsNull(@Param("departmentId") Long departmentId);
}
