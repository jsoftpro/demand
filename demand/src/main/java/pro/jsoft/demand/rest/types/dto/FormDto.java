package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;
import java.util.LinkedHashSet;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FormDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("value")
	private String name;
	private String label;
	private LinkedHashSet<FieldDto> fields = new LinkedHashSet<>();
}
