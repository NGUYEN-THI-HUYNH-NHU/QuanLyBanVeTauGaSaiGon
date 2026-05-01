package mapper;

import dto.NhanVienDTO;
import entity.NhanVien;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NhanVienMapper {
    NhanVienMapper INSTANCE = Mappers.getMapper(NhanVienMapper.class);

    // Entity -> DTO
    @Mapping(source = "nhanVienID", target = "id")
    @Mapping(source = "vaiTroNhanVien.vaiTroNhanVienID", target = "vaiTroNhanVienID")
    @Mapping(source = "caLam.caLamID", target = "caLamID")
    @Mapping(source = "caLam.gioVaoCa", target = "gioVaoCa")
    @Mapping(source = "caLam.gioKetCa", target = "gioKetCa")
    NhanVienDTO toDTO(NhanVien entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "nhanVienID")
    @Mapping(source = "vaiTroNhanVienID", target = "vaiTroNhanVien.vaiTroNhanVienID")
    @Mapping(source = "caLamID", target = "caLam.caLamID")
    @Mapping(source = "gioVaoCa", target = "caLam.gioVaoCa")
    @Mapping(source = "gioKetCa", target = "caLam.gioKetCa")
    NhanVien toEntity(NhanVienDTO dto);
}
