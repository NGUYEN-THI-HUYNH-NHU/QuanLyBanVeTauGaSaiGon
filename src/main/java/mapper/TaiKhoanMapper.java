package mapper;

import dto.TaiKhoanDTO;
import entity.TaiKhoan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaiKhoanMapper {
    TaiKhoanMapper INSTANCE = Mappers.getMapper(TaiKhoanMapper.class);

    // Entity -> DTO
    @Mapping(source = "taiKhoanID", target = "id")
    @Mapping(source = "vaiTroTaiKhoan.vaiTroTaiKhoanID", target = "vaiTroTaiKhoanID")
    @Mapping(source = "nhanVien.nhanVienID", target = "nhanVienID")
    TaiKhoanDTO toDTO(TaiKhoan entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "taiKhoanID")
    @Mapping(source = "vaiTroTaiKhoanID", target = "vaiTroTaiKhoan.vaiTroTaiKhoanID")
    @Mapping(source = "nhanVienID", target = "nhanVien.nhanVienID")
    TaiKhoan toEntity(TaiKhoanDTO dto);
}
