package mapper;

import dto.HeSoGiaLoaiTauDTO;
import entity.HeSoGiaLoaiTau;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeSoGiaLoaiTauMapper {
    HeSoGiaLoaiTauMapper INSTANCE = Mappers.getMapper(HeSoGiaLoaiTauMapper.class);

    // Entity -> DTO
    @Mapping(source = "hsgLoaiTauID", target = "id")
    @Mapping(source = "loaiTau.loaiTauID", target = "loaiTauID")
    HeSoGiaLoaiTauDTO toDTO(HeSoGiaLoaiTau entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "hsgLoaiTauID")
    @Mapping(source = "loaiTauID", target = "loaiTau.hangToaID")
    HeSoGiaLoaiTau toEntity(HeSoGiaLoaiTauDTO dto);
}
