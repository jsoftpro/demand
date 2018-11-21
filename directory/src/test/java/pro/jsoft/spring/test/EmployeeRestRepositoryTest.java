package pro.jsoft.spring.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pro.jsoft.spring.persistence.model.Employee;
import pro.jsoft.spring.persistence.repositories.EmployeeRestRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@EnableSpringDataWebSupport
public class EmployeeRestRepositoryTest {

    @Autowired
    private EmployeeRestRepository employeeRestRepository;

	@Test
	public void testFindByLastNameStartingWithAndDepartmentNested() {
		List<Employee> result = employeeRestRepository.findByLastNameStartingWithAndDepartmentNested(
				"позняков",
				5785L,
				null);
		assertNotNull(result);
		assertEquals(result.size(), 1);
	}

	@Test
	public void testFindByLastNameAndFirstNameStartingWithAndDepartmentNested() {
		List<Employee> result = employeeRestRepository.findByLastNameAndFirstNameStartingWithAndDepartmentNested(
				"познякова",
				"нин",
				5785L,
				null);
		assertNotNull(result);
		assertEquals(result.size(), 1);
	}
}
