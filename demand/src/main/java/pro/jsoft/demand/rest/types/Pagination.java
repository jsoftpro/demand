package pro.jsoft.demand.rest.types;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Pagination implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer page;
    private Integer rowsPerPage;
    private String sortBy;
    private Boolean descending;
    private Integer rowsNumber;
}
