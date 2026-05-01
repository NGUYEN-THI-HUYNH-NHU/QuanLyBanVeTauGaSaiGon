/**
 * File: PhieuGiuChoMapper.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/30/2026
 */

package mapper;

import dto.PhieuGiuChoDTO;
import entity.PhieuGiuCho;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

// Báo cho MapStruct biết đây là class dùng để map
@Mapper()
public interface PhieuGiuChoMapper {

    PhieuGiuChoMapper INSTANCE = Mappers.getMapper(PhieuGiuChoMapper.class);

    // --- 1. MAPPING TỪ ENTITY SANG DTO ---
    @Mapping(source = "nhanVien.nhanVienID", target = "nhanVienID")
    @Mapping(source = "nhanVien.hoTen", target = "hoTenNhanVien")
    PhieuGiuChoDTO toDTO(PhieuGiuCho entity);

    // --- 2. MAPPING TỪ DTO SANG ENTITY ---
    // MA THUẬT DẤU CHẤM: Tự tạo NhanVien và nhét dữ liệu vào
    @Mapping(source = "nhanVienID", target = "nhanVien.nhanVienID")
    @Mapping(source = "hoTenNhanVien", target = "nhanVien.hoTen")
    PhieuGiuCho toEntity(PhieuGiuChoDTO dto);
}

