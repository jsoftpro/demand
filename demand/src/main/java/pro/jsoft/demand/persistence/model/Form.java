package pro.jsoft.demand.persistence.model;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

@Entity
@Table(name = "DEM_FORM")
public class Form implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 40, updatable = false, unique = true)
	private String name;
	
    @Column(length = 40, nullable = false, updatable = false)
	private String profile;
    
	@Column(length = 80, updatable = false)
	private String label;

	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinTable(name = "DEM_FORM_FIELDS", joinColumns =
		@JoinColumn(name = "FIELD_NAME", referencedColumnName="NAME", nullable = false, updatable = false))
	@OrderBy("name")
	private Set<Field> fields = new LinkedHashSet<>();
}
