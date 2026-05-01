package mapper;

import dto.DieuKienKhuyenMaiDTO;
import entity.DieuKienKhuyenMai;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DieuKienKhuyenMaiMapper {
    DieuKienKhuyenMaiMapper INSTANCE = Mappers.getMapper(DieuKienKhuyenMaiMapper.class);

    // Entity -> DTO
    @Mapping(source = "dieuKienID", target = "id")
    @Mapping(source = "khuyenMai.khuyenMaiID", target = "khuyenMaiID")
    @Mapping(source = "tuyen.tuyenID", target = "tuyenID")
    @Mapping(source = "loaiTau.loaiTauID", target = "loaiTauID")
    @Mapping(source = "hangToa.hangToaID", target = "hangToaID")
    @Mapping(source = "loaiDoiTuong.loaiDoiTuongID", target = "loaiDoiTuongID")
    DieuKienKhuyenMaiDTO toDTO(DieuKienKhuyenMai entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "dieuKienID")
    @Mapping(source = "khuyenMaiID", target = "khuyenMai.khuyenMaiID")
    @Mapping(source = "tuyenID", target = "tuyen.tuyenID")
    @Mapping(source = "loaiTauID", target = "loaiTau.loaiTauID")
    @Mapping(source = "hangToaID", target = "hangToa.hangToaID")
    @Mapping(source = "loaiDoiTuongID", target = "loaiDoiTuong.loaiDoiTuongID")
    DieuKienKhuyenMai toEntity(DieuKienKhuyenMaiDTO dto);
}
