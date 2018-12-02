package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.jsoft.demand.rest.types.Option;

/**
 * @author Vyacheslav Korchagin java@0n.ru
 *
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
public class FieldDto implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String label;
	private String type;
	@JsonIgnore
	private String optionString;
	
	public List<Option> getOptions() {
		return Option.splitOptions(optionString);
	}
	
	public void setOptions(List<Option> options) {
		this.optionString = Option.joinOptions(options);		
	}
}
