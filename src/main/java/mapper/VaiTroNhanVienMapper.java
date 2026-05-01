package mapper;

import dto.VaiTroNhanVienDTO;
import entity.VaiTroNhanVien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VaiTroNhanVienMapper {
    VaiTroNhanVienMapper INSTANCE = Mappers.getMapper(VaiTroNhanVienMapper.class);

    // Entity -> DTO
    @Mapping(source = "vaiTroNhanVienID", target = "id")
    VaiTroNhanVienDTO toDTO(VaiTroNhanVien entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "vaiTroNhanVienID")
    VaiTroNhanVien toEntity(VaiTroNhanVienDTO dto);
}
