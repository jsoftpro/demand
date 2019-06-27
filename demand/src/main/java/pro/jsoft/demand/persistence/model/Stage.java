package pro.jsoft.demand.persistence.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pro.jsoft.demand.actions.Action;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

//@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)

@Entity
@Table(name = "DEM_STAGE")
public class Stage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Basic
	private int code;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SET_AT", updatable = false)
	private Calendar date;

	@Column(name = "ACTOR_NAME", length = 120, updatable = false)
	private String actorName;

	@Column(name = "ACTOR_UID", length = 80, updatable = false)
	private String actorUid;

	@Column(name = "ACTOR_POS", length = 255, updatable = false)
	private String actorPosition;

	@Column(name = "REC_NAME", length = 120, updatable = false)
	private String recipientName;

	@Column(name = "REC_UID", length = 80, updatable = false)
	private String recipientUid;

	@Column(name = "REC_POS", length = 255, updatable = false)
	private String recipientPosition;
	
	@Column(name = "NOTE", length = 255, updatable = false)
	private String comment;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "DEMAND_ID", nullable = false, updatable = false)
	private Demand demand;

	
	public Action getAction() {
		return Action.getByCode(code);
	}
	
	public void setAction(Action action) {
		if (action != null && action.getCode() != 0) {
			this.code = action.getCode();
		}
	}
}
