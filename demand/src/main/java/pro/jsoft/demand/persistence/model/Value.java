package pro.jsoft.demand.persistence.model;

import java.io.Serializable;

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
@Table(name = "DEM_VALUE")
public class Value implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(length = 40, updatable = false)
	private String name;
	
	@Column(length = 255, updatable = false)
	private String text;
}
