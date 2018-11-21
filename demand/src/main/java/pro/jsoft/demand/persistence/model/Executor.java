package pro.jsoft.demand.persistence.model;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

@Entity
@Table(name = "DEM_EXECUTOR")
public class Executor {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;	

    @Column(length = 40, nullable = false, updatable = false)
	private String profile;
    
    @Column(name = "EX_NAME", length = 120, nullable = false, updatable = false)
    private String name;

    @Column(name = "EX_UID", length = 80, nullable = false, updatable = false)
    private String uid;

    @Column(name = "EX_EMAIL", length = 80, updatable = false)
    private String email;

    @Column(name = "EX_PHONE", length = 120, updatable = false)
    private String phone;

    @Column(name = "ORG_ID", nullable = false, updatable = false)
    private Long organizationId;

    @Column(name = "ORG_NAME", length = 255, nullable = false, updatable = false)
    private String organizationName;

    @Column(name = "DEP_ID", nullable = false, updatable = false)
    private Long departmentId;

    @Column(name = "DEP_NAME", length = 255, nullable = false, updatable = false)
    private String departmentName;


    @Column(name = "SVC_ORG_ID", nullable = false, updatable = false)
    private Long servicedOrganizationId;

    @Column(name = "SVC_ORG_NAME", length = 255, nullable = false, updatable = false)
    private String servicedOrganizationName;

    @Column(name = "SVC_DEP_ID", nullable = false, updatable = false)
    private Long servicedDepartmentId;

    @Column(name = "SVC_DEP_NAME", length = 255, nullable = false, updatable = false)
    private String servicedDepartmentName;

    
    @Column(length = 400, nullable = false, updatable = false)
    private String branch;
}
