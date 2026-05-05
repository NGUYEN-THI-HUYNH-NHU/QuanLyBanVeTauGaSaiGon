package mapper;

import dto.BieuGiaVeDTO;
import entity.BieuGiaVe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BieuGiaVeMapper {
    BieuGiaVeMapper INSTANCE = Mappers.getMapper(BieuGiaVeMapper.class);

    // Entity -> DTO
    @Mapping(source = "bieuGiaVeID", target = "id")
    @Mapping(source = "tuyenApDung.tuyenID", target = "tuyenApDungID")
    @Mapping(source = "loaiTauApDung.loaiTauID", target = "loaiTauApDungID")
    @Mapping(source = "loaiTauApDung.moTa", target = "moTaLoaiTau")
    @Mapping(source = "hangToaApDung.hangToaID", target = "hangToaApDungID")
    @Mapping(source = "hangToaApDung.moTa", target = "moTaHangToa")
    BieuGiaVeDTO toDTO(BieuGiaVe entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "bieuGiaVeID")
    @Mapping(source = "tuyenApDungID", target = "tuyenApDung.tuyenID")
    @Mapping(source = "loaiTauApDungID", target = "loaiTauApDung.loaiTauID")
    @Mapping(source = "moTaLoaiTau", target = "loaiTauApDung.moTa")
    @Mapping(source = "hangToaApDungID", target = "hangToaApDung.hangToaID")
    @Mapping(source = "moTaHangToa", target = "hangToaApDung.moTa")
    BieuGiaVe toEntity(BieuGiaVeDTO dto);
}
