package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;
import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.jsoft.demand.actions.Action;
import pro.jsoft.spring.config.Constants;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor

public class StageDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private int code;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATETIME_PATTERN)
	private Calendar date;
	private String actorName;
	private String actorUid;
	private String actorPosition;
	private String recipientName;
	private String recipientUid;
	private String recipientPosition;
	private String comment;
	
	public Action getAction() {
		return Action.getByCode(code);
	}
	
	public void setAction(Action action) {
		if (action != null && action.getCode() != 0) {
			this.code = action.getCode();
		}
	}
}
