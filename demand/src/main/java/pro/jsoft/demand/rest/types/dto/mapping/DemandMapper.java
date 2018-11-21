package pro.jsoft.demand.rest.types.dto.mapping;

import org.mapstruct.Mapper;

import pro.jsoft.demand.persistence.model.Demand;
import pro.jsoft.demand.persistence.model.Value;
import pro.jsoft.demand.rest.types.dto.DemandDto;
import pro.jsoft.demand.rest.types.dto.ValueDto;

@Mapper(componentModel = "spring", uses = { StageMapper.class, FileMapper.class })
public interface DemandMapper {
	DemandDto convertDomainToDto(Demand demand);
	ValueDto convertDomainToDto(Value value);

	Demand convertDtoToDomain(DemandDto demand);
	Value convertDtoToDomain(ValueDto value);
}
