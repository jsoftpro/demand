package pro.jsoft.demand.rest.types.dto.mapping;

import org.mapstruct.Mapper;

import pro.jsoft.demand.persistence.model.Rules;
import pro.jsoft.demand.rest.types.dto.RulesDto;

@Mapper(componentModel = "spring")
public interface RulesMapper {
	RulesDto convertDomainToDto(Rules rules);
	Rules convertDtoToDomain(RulesDto rules);
}
