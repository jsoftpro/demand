package pro.jsoft.demand.rest.types.dto.mapping;

import org.mapstruct.Mapper;

import pro.jsoft.demand.persistence.model.Stage;
import pro.jsoft.demand.rest.types.dto.StageDto;

@Mapper(componentModel = "spring")
public interface StageMapper {
	StageDto convertDomainToDto(Stage stage);
	Stage convertDtoToDomain(StageDto stage);
}
