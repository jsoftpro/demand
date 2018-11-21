package pro.jsoft.spring.persistence.model;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "idAndName", types = {Department.class})
public interface DepartmentIdAndName {
	Long getId();
	String getName();
}
