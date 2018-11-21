package pro.jsoft.spring.persistence.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import pro.jsoft.spring.persistence.model.Employee;

@RepositoryRestResource(collectionResourceRel = "employees", path = "employees")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.HEAD})
public interface EmployeeRestRepository extends PagingAndSortingRepository<Employee, Long> {
	public Employee findFirstByUid(@Param("uid") String uid);

	public List<Employee> findByLastNameStartingWith(
			@Param("lastName") String lastName,
			Pageable page);

	public List<Employee> findByLastNameAndFirstNameStartingWith(
			@Param("lastName") String lastName, 
			@Param("firstName") String firstName,
			Pageable page);

	public List<Employee> findByLastNameAndFirstNameAndMiddleNameStartingWith(
			@Param("lastName") String lastName, 
			@Param("firstName") String firstName,
			@Param("middleName") String middleName,
			Pageable page);

	@Query(value = "SELECT e FROM Employee e WHERE "  
			+ "((e.departmentFunc IS NULL AND e.departmentOrg IN (SELECT d.id FROM Department d WHERE d.branch LIKE CONCAT((SELECT o.branch FROM Department o WHERE o.id = :departmentId), '%'))) " 
			+ "OR (e.departmentFunc IS NOT NULL AND e.departmentFunc IN (SELECT d.id FROM Department d WHERE d.branch LIKE CONCAT((SELECT o.branch FROM Department o WHERE o.id = :departmentId), '%')))) "
			+ "AND e.lastName LIKE :lastName%")
	public List<Employee> findByLastNameStartingWithAndDepartmentNested(
			@Param("lastName") String lastName,
			@Param("departmentId") Long departmentId,
			Pageable page);

	@Query(value = "SELECT e FROM Employee e WHERE "  
			+ "((e.departmentFunc IS NULL AND e.departmentOrg IN (SELECT d.id FROM Department d WHERE d.branch LIKE CONCAT((SELECT o.branch FROM Department o WHERE o.id = :departmentId), '%'))) " 
			+ "OR (e.departmentFunc IS NOT NULL AND e.departmentFunc IN (SELECT d.id FROM Department d WHERE d.branch LIKE CONCAT((SELECT o.branch FROM Department o WHERE o.id = :departmentId), '%')))) "
			+ "AND e.lastName = :lastName AND e.firstName LIKE :firstName%")
	public List<Employee> findByLastNameAndFirstNameStartingWithAndDepartmentNested(
			@Param("lastName") String lastName,
			@Param("firstName") String firstName,
			@Param("departmentId") Long departmentId,
			Pageable page);

	@Query(value = "SELECT e FROM Employee e WHERE "  
			+ "((e.departmentFunc IS NULL AND e.departmentOrg IN (SELECT d.id FROM Department d WHERE d.branch LIKE CONCAT((SELECT o.branch FROM Department o WHERE o.id = :departmentId), '%'))) " 
			+ "OR (e.departmentFunc IS NOT NULL AND e.departmentFunc IN (SELECT d.id FROM Department d WHERE d.branch LIKE CONCAT((SELECT o.branch FROM Department o WHERE o.id = :departmentId), '%')))) "
			+ "AND e.lastName = :lastName AND e.firstName = :firstName AND e.middleName LIKE :middleName%")
	public List<Employee> findByLastNameAndFirstNameAndMiddleNameStartingWithAndDepartmentNested(
			@Param("lastName") String lastName,
			@Param("firstName") String firstName,
			@Param("departmentId") Long departmentId,
			@Param("middleName") String middleName,
			Pageable page);
}
