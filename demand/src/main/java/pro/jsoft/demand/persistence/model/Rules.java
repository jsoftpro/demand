package pro.jsoft.demand.persistence.model;

import java.io.Serializable;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.jsoft.demand.actions.Action;

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
@Table(name = "DEM_RULES")
public class Rules implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @Column(length = 40, nullable = false, updatable = false, unique = true)
	private String profile;

    @Basic
    private boolean upload;

    @Basic
    private boolean confirm;

    @Basic
    private boolean accept;

    @Basic
    private boolean reaccept;

    @Basic
    private boolean approve;
    
    public boolean isEnabled(Action action) {
    	switch (action) {
    		case DO_UPLOAD:
    			return upload;
    		case DO_DOWNLOAD:
    			return upload;
			case STEP_CONFIRM:
				return confirm;
			case STEP_ACCEPT:
				return accept;
			case STEP_REACCEPT:
				return reaccept;
			case STEP_ASKAPPROVE:
				return approve;
			case STEP_APPROVE:
				return approve;
			default:
				return true;
    	}
    }
}
