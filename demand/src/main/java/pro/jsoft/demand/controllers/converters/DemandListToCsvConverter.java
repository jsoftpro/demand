package pro.jsoft.demand.controllers.converters;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.val;
import lombok.var;
import pro.jsoft.demand.actions.Action;
import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Field;
import pro.jsoft.demand.persistence.model.Form;
import pro.jsoft.demand.persistence.repositories.FormRepository;
import pro.jsoft.demand.rest.types.Option;

@Component
public class DemandListToCsvConverter {
	private FormRepository formRepository;
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	@Autowired
	public DemandListToCsvConverter(FormRepository formRepository) {
		this.formRepository = formRepository;
	}
	
	private String formatDateTime(Calendar c) {
		return dateFormatter.format(c.getTime());
	}
	
	public String convert(List<Demand> demandList) {
		if (!demandList.isEmpty()) {
			val profile = demandList.get(0).getProfile();
			val forms = formRepository.findByProfileOrderByName(profile);
			Map<String, String> formNameMap = forms.stream().collect(Collectors.toMap(Form::getName, Form::getLabel));
			Map<String, Map<String, Field>> formFieldMap = forms.stream().collect(Collectors.toMap(Form::getName, 
					form -> form.getFields().stream()
						.collect(Collectors.toMap(Field::getName, field -> field))));
			val csv = new StringBuilder();
			val optionsMap = new HashMap<String, Map<String, String>>();
			for (val demand : demandList) {
				val formName = demand.getFormName();
				val formLabel = formNameMap.getOrDefault(formName, formName);
				val fieldMap = formFieldMap.get(demand.getFormName());
				
				csv.append(demand.getId())
					.append("\t")
					.append(formLabel)
					.append("\t")
					.append(formatDateTime(demand.getCreated().getDate()))
					.append("\t")
					.append(demand.getUid())
					.append("\t")
					.append(demand.getCode())
					.append("\t")
					.append(demand.getName())
					.append("\t")
					.append(demand.getPosition())
					.append("\t")
					.append(demand.getOrganizationId())
					.append(". ")
					.append(demand.getOrganizationName())
					.append("\t")
					.append(demand.getDepartmentId())
					.append(". ")
					.append(demand.getDepartmentName())
					.append("\t")
					.append(demand.getEmail())
					.append("\t")
					.append(demand.getPhone())
					.append("\t");

				if (Arrays.asList(Action.STEP_FINISH, Action.STEP_CANCEL).contains(demand.getChanged().getAction())) {
					csv.append(formatDateTime(demand.getChanged().getDate()));
				}
				
				csv.append("\t");
				
				if (demand.getRate() != null) {
					csv.append(demand.getRate());
					if (demand.getOpinion() != null) {
						csv.append(" ").append(demand.getOpinion());
					}
				}
				
				val values = demand.getValues().stream().map(value -> {
						var label = value.getName();
						var text = value.getText();
						Field field = fieldMap.get(label);
						if (field != null) {
							label = field.getLabel();
							if ("select".equals(field.getType())) {
								var options = optionsMap.get(field.getName());
								if (options == null) {
									options = Option.mapOptions(Option.splitOptions(field.getOptionString()));
									optionsMap.put(field.getName(), options);
								}
								text = options.getOrDefault(text, text);
							}
						}
						return label + ":" + text;
					}).collect(Collectors.joining("|"));
				
				csv.append("\t")
					.append(values);
				csv.append("\n");
			}
			
			return csv.toString();
		}
		
		return null;
	}
}
