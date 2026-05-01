package mapper;

import dto.GiaoDichHoanDoiDTO;
import entity.GiaoDichHoanDoi;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GiaoDichHoanDoiMapper {
    GiaoDichHoanDoiMapper INSTANCE = Mappers.getMapper(GiaoDichHoanDoiMapper.class);

    // Entity -> DTO
    @Mapping(source = "giaoDichHoanDoiID", target = "id")
    @Mapping(source = "nhanVien.nhanVienID", target = "nhanVienID")
    @Mapping(source = "hoaDon.hoaDonID", target = "hoaDonID")
    @Mapping(source = "veGoc.veID", target = "veGocID")
    @Mapping(source = "veMoi.veID", target = "veMoiID")
    GiaoDichHoanDoiDTO toDTO(GiaoDichHoanDoi entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "giaoDichHoanDoiID")
    @Mapping(source = "nhanVienID", target = "nhanVien.nhanVienID")
    @Mapping(source = "hoaDonID", target = "hoaDon.hoaDonID")
    @Mapping(source = "veGocID", target = "veGoc.veID")
    @Mapping(source = "veMoiID", target = "veMoi.veID")
    GiaoDichHoanDoi toEntity(GiaoDichHoanDoiDTO dto);
}
