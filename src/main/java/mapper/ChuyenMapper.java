package mapper;

import dto.ChuyenDTO;
import entity.Chuyen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChuyenMapper {
    ChuyenMapper INSTANCE = Mappers.getMapper(ChuyenMapper.class);

    // Entity -> DTO
    @Mapping(source = "chuyenID", target = "id")
    @Mapping(source = "tuyen.tuyenID", target = "tuyenID")
    @Mapping(source = "tau.tauID", target = "tauID")
    @Mapping(source = "tau.loaiTau.loaiTauID", target = "loaiTauID")
    ChuyenDTO toDTO(Chuyen entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "chuyenID")
    @Mapping(source = "tuyenID", target = "tuyen.tuyenID")
    @Mapping(source = "tauID", target = "tau.tauID")
    @Mapping(source = "loaiTauID", target = "tau.loaiTau.loaiTauID")
    Chuyen toEntity(ChuyenDTO dto);
}
