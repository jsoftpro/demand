package pro.jsoft.spring.persistence.model;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "idAndFullname", types = {Employee.class})
public interface EmployeeIdAndFullname {
	Long getId();
	String getFullName();
}
