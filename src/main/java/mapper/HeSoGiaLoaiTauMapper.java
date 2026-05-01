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
    @Mapping(source = "hsgloaiTauID", target = "id")
    @Mapping(source = "loaiTau.loaiTauID", target = "loaiTauID")
    HeSoGiaLoaiTauDTO toDTO(HeSoGiaLoaiTau entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "hsgloaiTauID")
    @Mapping(source = "loaiTauID", target = "loaiTau.loaiTauID")
    HeSoGiaLoaiTau toEntity(HeSoGiaLoaiTauDTO dto);
}
