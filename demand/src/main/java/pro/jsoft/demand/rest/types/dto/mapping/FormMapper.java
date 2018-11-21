package pro.jsoft.demand.rest.types.dto.mapping;

import org.mapstruct.Mapper;

import pro.jsoft.demand.persistence.model.Field;
import pro.jsoft.demand.persistence.model.Form;
import pro.jsoft.demand.rest.types.dto.FieldDto;
import pro.jsoft.demand.rest.types.dto.FormDto;

@Mapper(componentModel = "spring")
public interface FormMapper {
	FormDto convertDomainToDto(Form form);
	FieldDto convertDomainToDto(Field form);
	
	Form convertDtoToDomain(FormDto form);
	Field convertDtoToDomain(FieldDto form);
}
