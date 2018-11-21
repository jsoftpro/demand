package pro.jsoft.demand.rest.types;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PageableResultList<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("result")
	private List<T> resultList;
	private Pagination pagination;
}
