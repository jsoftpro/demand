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

@Entity(name = "Department")
@Table(name = "PHB_DEPARTMENT")
public class Department extends JsonEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(unique = true, nullable = false)
	private long id;

	@Basic
	private Long parentId;

	@Basic
	@Column(name = "DNAME")
	private String name;

	@Basic
	@Column(length = 400)
	private String branch;

	@Basic
	private Long priority;
}