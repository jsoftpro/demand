package pro.jsoft.demand.rest.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@AllArgsConstructor
@Setter
@Getter
public class Option implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String OPTIONS_DELIMETER = "="; 
	private static final String PAIRS_DELIMETER = ";"; 
	private String value;
	private String label;

	public static List<Option> splitOptions(String optionString) {
		if (optionString == null || optionString.trim().length() == 0) {
			return Collections.emptyList();
		}
		
		val options = new ArrayList<Option>();
		val pairs = optionString.split(PAIRS_DELIMETER);
		for (val pair : pairs) {
			val entry = pair.split(OPTIONS_DELIMETER, 2);
			options.add(new Option(entry[0], entry[1]));
		}
		
		return options;
	}
	
	public static String joinOptions(List<Option> options) {
		return options.stream()
				.map(option -> (option.getValue() + OPTIONS_DELIMETER + option.getLabel()))
				.collect(Collectors.joining(PAIRS_DELIMETER));		
	}
	
	public static Map<String, String> mapOptions(List<Option> options) {
		return options.stream()
				.collect(Collectors.toMap(Option::getValue, Option::getLabel));		
	}
}
