package mapper;

import dto.KhuyenMaiDTO;
import entity.KhuyenMai;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface KhuyenMaiMapper {
    KhuyenMaiMapper INSTANCE = Mappers.getMapper(KhuyenMaiMapper.class);

    // Entity -> DTO
    @Mapping(source = "khuyenMaiID", target = "id")
    KhuyenMaiDTO toDTO(KhuyenMai entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "khuyenMaiID")
    KhuyenMai toEntity(KhuyenMaiDTO dto);
}
