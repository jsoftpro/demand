package pro.jsoft.spring.persistence.model;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;

@Data
@EqualsAndHashCode(callSuper = false)

@Immutable
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_ONLY)

@Entity(name = "Employee")
@Table(name = "PHB_EMPLOYEE")
public class Employee extends JsonEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private long id;

	@Column(name = "FNAME", length = 80)
	private String firstName;

	@Column(name = "LNAME", length = 80)
	private String lastName;

	@Column(name = "MNAME", length = 80)
	private String middleName;

	@Column(length = 120)
	private String email;

	@Column(length = 120)
	private String phone;

	@Column(name = "DEPORG")
	private Long departmentOrg;

	@Column(name = "DEPFUNC")
	private Long departmentFunc;

	@Column(name = "POSORG")
	private String positionOrg;

	@Column(name = "POSFUNC")
	private String positionFunc;

	@Column(name="USERID", length = 120)
	private String uid;

	@Column(name = "ASSISTANT")
	private Long assistantOf;
	
	public void setFullName(String name) {
		String[] names = name.split(" ", 3);
		lastName = names.length > 0 ? names[0] : null;
		firstName = names.length > 1 ? names[1] : null;
		middleName = names.length > 2 ? names[2] : null;
	}

	@Transient
	public String getFullName() {
		return String.join(" ", lastName, firstName, middleName);
	}

}