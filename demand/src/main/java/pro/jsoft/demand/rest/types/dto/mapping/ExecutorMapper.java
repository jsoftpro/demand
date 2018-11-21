package pro.jsoft.demand.rest.types.dto.mapping;

import org.mapstruct.Mapper;

import pro.jsoft.demand.persistence.model.Executor;
import pro.jsoft.demand.rest.types.dto.ExecutorDto;

@Mapper(componentModel = "spring")
public interface ExecutorMapper {
	ExecutorDto convertDomainToDto(Executor executor);
	Executor convertDtoToDomain(ExecutorDto executor);
}
