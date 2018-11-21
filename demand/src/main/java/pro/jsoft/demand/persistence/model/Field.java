package pro.jsoft.demand.persistence.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "DEM_FIELD")
public class Field implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 40, updatable = false, unique = true)
	private String name;
	
	@Column(length = 80, updatable = false)
	private String label;

	@Column(name="DATATYPE", length = 40, updatable = false)
	private String type;

	@Column(name="OPTIONS", length = 400, updatable = false)
	private String optionString;
}
