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
    @Mapping(source = "tau.tenTau", target = "tenTau")
    @Mapping(source = "tau.loaiTau.loaiTauID", target = "loaiTauID")

    @Mapping(source = "tenChuyenHienThi", target = "tenChuyenHienThi")
    @Mapping(source = "tenGaDiHienThi", target = "tenGaDiHienThi")
    @Mapping(source = "tenGaDenHienThi", target = "tenGaDenHienThi")
    ChuyenDTO toDTO(Chuyen entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "chuyenID")
    @Mapping(source = "tuyenID", target = "tuyen.tuyenID")
    @Mapping(source = "tauID", target = "tau.tauID")
    @Mapping(source = "tenTau", target = "tau.tenTau")
    @Mapping(source = "loaiTauID", target = "tau.loaiTau.loaiTauID")
    Chuyen toEntity(ChuyenDTO dto);
}
