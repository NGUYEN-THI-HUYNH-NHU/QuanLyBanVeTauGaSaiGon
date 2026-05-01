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
    @Mapping(source = "hangToaApDung.hangToaID", target = "hangToaApDungID")
    BieuGiaVeDTO toDTO(BieuGiaVe entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "bieuGiaVeID")
    @Mapping(source = "tuyenApDungID", target = "tuyenApDung.tuyenID")
    @Mapping(source = "loaiTauApDungID", target = "loaiTauApDung.loaiTauID")
    @Mapping(source = "hangToaApDungID", target = "hangToaApDung.hangToaID")
    BieuGiaVe toEntity(BieuGiaVeDTO dto);
}
