package pro.jsoft.demand.persistence.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

@Entity
@Table(name = "DEM_DEMAND")
public class Demand implements Serializable {
	private static final long serialVersionUID = 1L;

	public Demand(Long id) {
		this.id = id;
	}
	
	@Id
	@TableGenerator(name = "Demand", allocationSize = 1, pkColumnValue = "DEMAND_ID", table = "DEM_SEQUENCE")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Demand")
	private Long id;

    @Column(length = 40, nullable = false, updatable = false)
	private String profile;

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(mappedBy = "demand", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
	@OrderBy("date")
	private Set<Stage> stages = new LinkedHashSet<>();

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
	@JoinTable(name = "DEM_DEMAND_VALUES", 
		joinColumns = @JoinColumn(name = "DEMAND_ID", nullable = false, updatable = false),
		inverseJoinColumns = @JoinColumn(name = "VALUE_ID", nullable = false, updatable = false))
	@OrderBy("name")
	private Set<Value> values = new LinkedHashSet<>();

    //@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(mappedBy = "demand", fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
	@OrderBy("id")
	private Set<File> files = new LinkedHashSet<>();
	
    @Column(name = "EMP_ID", updatable = false)
    private Long staffId;

    @Column(name = "EMP_NAME", length = 120, nullable = false, updatable = false)
    private String name;

    @Column(name = "EMP_UID", length = 80, updatable = false)
    private String uid;

    @Column(name = "EMP_EMAIL", length = 80, updatable = false)
    private String email;

    @Column(name = "EMP_PHONE", length = 120, updatable = false)
    private String phone;

    @Column(name = "EMP_CODE", length = 40, nullable = false, updatable = false)
    private String code;

    @Column(name = "EMP_POS", length = 255, updatable = false)
    private String position;

    @Column(name = "EMP_LOC", length = 400, updatable = false)
    private String location;

    @Column(name = "ORG_ID", nullable = false, updatable = false)
    private Long organizationId;

    @Column(name = "ORG_NAME", length = 255, nullable = false, updatable = false)
    private String organizationName;

    @Column(name = "DEP_ID", nullable = false, updatable = false)
    private Long departmentId;

    @Column(name = "DEP_NAME", length = 255, nullable = false, updatable = false)
    private String departmentName;

    @Column(length = 400, nullable = false, updatable = false)
    private String branch;
    
	@Column(name = "FORM_NAME", length = 40, nullable = false, updatable = false)
	private String formName;
    
    @Basic
    private Integer rate;

    @Column(length = 255)
    private String opinion;
    
    @Transient
    private List<String> actions = new ArrayList<>();
    
    public Stage getCreated() {
		return stages.iterator().next();
    }
    
    public Stage getChanged() {
		val stagesIterator = stages.iterator();
		Stage changed = null;
		while (stagesIterator.hasNext()) {
			changed = stagesIterator.next();
		}
		return changed;
    }
}
