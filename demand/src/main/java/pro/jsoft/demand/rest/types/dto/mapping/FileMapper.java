package pro.jsoft.demand.rest.types.dto.mapping;

import org.mapstruct.Mapper;

import pro.jsoft.demand.persistence.model.File;
import pro.jsoft.demand.persistence.model.FileInfo;
import pro.jsoft.demand.rest.types.dto.FileDto;

@Mapper(componentModel = "spring")
public interface FileMapper {
	FileDto convertDomainToDto(File file);
	FileDto convertDomainToDto(FileInfo file);

	File convertDtoToDomain(FileDto file);
}
