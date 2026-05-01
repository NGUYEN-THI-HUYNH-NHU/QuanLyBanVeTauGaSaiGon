package mapper;

import dto.VaiTroTaiKhoanDTO;
import entity.VaiTroTaiKhoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface VaiTroTaiKhoanMapper {
    VaiTroTaiKhoanMapper INSTANCE = Mappers.getMapper(VaiTroTaiKhoanMapper.class);

    // Entity -> DTO
    @Mapping(source = "vaiTroTaiKhoanID", target = "id")
    VaiTroTaiKhoanDTO toDTO(VaiTroTaiKhoan entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "vaiTroTaiKhoanID")
    VaiTroTaiKhoan toEntity(VaiTroTaiKhoanDTO dto);
}
