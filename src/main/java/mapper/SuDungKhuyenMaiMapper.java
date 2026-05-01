package mapper;

import dto.SuDungKhuyenMaiDTO;
import entity.SuDungKhuyenMai;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SuDungKhuyenMaiMapper {
    SuDungKhuyenMaiMapper INSTANCE = Mappers.getMapper(SuDungKhuyenMaiMapper.class);

    // Entity -> DTO
    @Mapping(source = "suDungKhuyenMaiID", target = "id")
    @Mapping(source = "khuyenMai.khuyenMaiID", target = "khuyenMaiID")
    @Mapping(source = "hoaDonChiTiet.hoaDonChiTietID", target = "hoaDonChiTietID")
    SuDungKhuyenMaiDTO toDTO(SuDungKhuyenMai entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "suDungKhuyenMaiID")
    @Mapping(source = "khuyenMaiID", target = "khuyenMai.khuyenMaiID")
    @Mapping(source = "hoaDonChiTietID", target = "hoaDonChiTiet.hoaDonChiTietID")
    SuDungKhuyenMai toEntity(SuDungKhuyenMaiDTO dto);
}
