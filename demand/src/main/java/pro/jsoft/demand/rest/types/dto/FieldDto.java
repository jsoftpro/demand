package pro.jsoft.demand.rest.types.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

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
		if (optionString == null || optionString.trim().length() == 0) {
			return Collections.emptyList();
		}
		
		val options = new ArrayList<Option>();
		val pairs = optionString.split("\\;");
		for (val pair : pairs) {
			val entry = pair.split("=", 2);
			options.add(new Option(entry[0], entry[1]));
		}
		
		return options;
	}
	
	public void setOptions(List<Option> options) {
		this.optionString = options.stream()
				.map(option -> (option.getValue() + "=" + option.getLabel()))
				.collect(Collectors.joining(","));		
	}

	@AllArgsConstructor
	@Setter
	@Getter
	public static class Option {
		private String value;
		private String label;
	}
}
