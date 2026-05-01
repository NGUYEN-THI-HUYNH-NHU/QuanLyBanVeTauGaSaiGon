package mapper;

import dto.HoaDonChiTietDTO;
import entity.HoaDonChiTiet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HoaDonChiTietMapper {
    HoaDonChiTietMapper INSTANCE = Mappers.getMapper(HoaDonChiTietMapper.class);

    // Entity -> DTO
    @Mapping(source = "hoaDonChiTietID", target = "id")
    @Mapping(source = "hoaDon.hoaDonID", target = "hoaDonID")
    @Mapping(source = "ve.veID", target = "veID")
    @Mapping(source = "phieuDungPhongVIP.phieuDungPhongVIPID", target = "phieuDungPhongVIPID")
    HoaDonChiTietDTO toDTO(HoaDonChiTiet entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "hoaDonChiTietID")
    @Mapping(source = "hoaDonID", target = "hoaDon.hoaDonID")
    @Mapping(source = "veID", target = "ve.veID")
    @Mapping(source = "phieuDungPhongVIPID", target = "phieuDungPhongVIP.phieuDungPhongVIPID")
    HoaDonChiTiet toEntity(HoaDonChiTietDTO dto);
}