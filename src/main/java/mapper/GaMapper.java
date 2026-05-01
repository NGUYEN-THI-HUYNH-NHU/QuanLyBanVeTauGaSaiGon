package mapper;

import dto.GaDTO;
import entity.Ga;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GaMapper {
    GaMapper INSTANCE = Mappers.getMapper(GaMapper.class);

    // Entity -> DTO
    @Mapping(source = "gaID", target = "id")
    GaDTO toDTO(Ga entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "gaID")
    Ga toEntity(GaDTO dto);
}
