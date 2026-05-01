package mapper;

import dto.GheDTO;
import entity.Ghe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GheMapper {
    GheMapper INSTANCE = Mappers.getMapper(GheMapper.class);

    // Entity -> DTO
    @Mapping(source = "gheID", target = "id")
    @Mapping(source = "toa.toaID", target = "toaID")
    GheDTO toDTO(Ghe entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "gheID")
    @Mapping(source = "toaID", target = "toa.toaID")
    Ghe toEntity(GheDTO dto);
}
