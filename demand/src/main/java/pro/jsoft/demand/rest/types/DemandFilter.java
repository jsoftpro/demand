package pro.jsoft.demand.rest.types;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pro.jsoft.demand.actions.Action;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class DemandFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String formName;
	private DateRange createdAt; 
	private String createdBy;
	private DateRange changedAt; 
	private String changedBy;
	private Action stageName;
	private Pagination pagination;
	private Mode mode;
}
