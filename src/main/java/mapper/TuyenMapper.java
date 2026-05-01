package mapper;

import dto.TuyenDTO;
import entity.Tuyen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TuyenMapper {
    TuyenMapper INSTANCE = Mappers.getMapper(TuyenMapper.class);

    // Entity -> DTO
    @Mapping(source = "tuyenID", target = "id")
    TuyenDTO toDTO(Tuyen entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "tuyenID")
    @Mapping(target = "tuyenChiTiets", ignore = true)
    @Mapping(target = "chuyens", ignore = true)
    Tuyen toEntity(TuyenDTO dto);
}
