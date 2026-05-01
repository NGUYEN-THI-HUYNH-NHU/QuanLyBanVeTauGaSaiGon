package mapper;
/**
 * File: VeMapper.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/30/2026
 */

import dto.VeDTO;
import entity.Ve;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {KhachHangMapper.class})
public interface VeMapper {

    VeMapper INSTANCE = Mappers.getMapper(VeMapper.class);

    // --- 1. MAPPING TỪ ENTITY SANG DTO ---
    // MapStruct sẽ tự hiểu: source "khachHang" (Entity) -> target "khachHangDTO" (DTO)
    // dựa vào KhachHangMapper đã khai báo trong 'uses'
    @Mapping(source = "khachHang", target = "khachHangDTO")
    @Mapping(source = "donDatCho.donDatChoID", target = "donDatChoID")
    @Mapping(source = "chuyen.tuyen.tuyenID", target = "tuyenID")
    @Mapping(source = "chuyen.chuyenID", target = "chuyenID")
    @Mapping(source = "gaDi.gaID", target = "gaDiID")
    @Mapping(source = "gaDi.tenGa", target = "tenGaDi")
    @Mapping(source = "gaDen.gaID", target = "gaDenID")
    @Mapping(source = "gaDen.tenGa", target = "tenGaDen")
    @Mapping(source = "ghe.toa.tau.tauID", target = "tauID")
    @Mapping(source = "ghe.toa.tau.loaiTau.loaiTauID", target = "loaiTauID")
    @Mapping(source = "ghe.toa.toaID", target = "toaID")
    @Mapping(source = "ghe.toa.hangToa.hangToaID", target = "hangToaID")
    @Mapping(source = "ghe.toa.soToa", target = "soToa")
    @Mapping(source = "ghe.gheID", target = "gheID")
    @Mapping(source = "ghe.soGhe", target = "soGhe")
    VeDTO toDTO(Ve entity);

    // --- 2. MAPPING TỪ DTO SANG ENTITY ---
    @Mapping(target = "khachHang", source = "khachHangDTO")
    @Mapping(target = "donDatCho.donDatChoID", source = "donDatChoID")
    @Mapping(target = "chuyen.tuyen.tuyenID", source = "tuyenID")
    @Mapping(target = "chuyen.chuyenID", source = "chuyenID")
    @Mapping(target = "gaDi.gaID", source = "gaDiID")
    @Mapping(target = "gaDi.tenGa", source = "tenGaDi")
    @Mapping(target = "gaDen.gaID", source = "gaDenID")
    @Mapping(target = "gaDen.tenGa", source = "tenGaDen")
    @Mapping(target = "ghe.toa.tau.tauID", source = "tauID")
    @Mapping(target = "ghe.toa.tau.loaiTau.loaiTauID", source = "loaiTauID")
    @Mapping(target = "ghe.toa.toaID", source = "toaID")
    @Mapping(target = "ghe.toa.hangToa.hangToaID", source = "hangToaID")
    @Mapping(target = "ghe.toa.soToa", source = "soToa")
    @Mapping(target = "ghe.gheID", source = "gheID")
    @Mapping(target = "ghe.soGhe", source = "soGhe")
    Ve toEntity(VeDTO dto);
}