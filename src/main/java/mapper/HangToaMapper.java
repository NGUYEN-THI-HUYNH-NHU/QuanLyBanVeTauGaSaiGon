package mapper;

import dto.HangToaDTO;
import entity.HangToa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HangToaMapper {
    HangToaMapper INSTANCE = Mappers.getMapper(HangToaMapper.class);

    // Entity -> DTO
    @Mapping(source = "hangToaID", target = "id")
    HangToaDTO toDTO(HangToa entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "hangToaID")
    HangToa toEntity(HangToaDTO dto);
}
