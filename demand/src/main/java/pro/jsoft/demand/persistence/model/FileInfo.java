package pro.jsoft.demand.persistence.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

@MappedSuperclass
public class FileInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "CTYPE")
	private String contentType = "application/octet-stream";
	
	@Column(name = "FNAME")
	private String name;
	
	@Column(name = "FSIZE")
	private Long size;
	
	@Column(name = "DESCRIPT", length = 255)
	private String description;
	
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "DEMAND_ID", nullable = false, updatable = false)
	private Demand demand;
}
