/**
 * File: PhieuGiuChoChiTietMapper.java.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/30/2026
 */

package mapper;

import dto.PhieuGiuChoChiTietDTO;
import entity.PhieuGiuChoChiTiet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
interface PhieuGiuChoChiTietMapper {
    PhieuGiuChoChiTietMapper INSTANCE = Mappers.getMapper(PhieuGiuChoChiTietMapper.class);

    // Entity -> DTO
    // Bỏ qua map ngược phiếu cha để tránh vòng lặp đệ quy vô hạn
    @Mapping(source = "phieuGiuCho.phieuGiuChoID", target = "phieuGiuChoID")
    PhieuGiuChoChiTietDTO toDTO(PhieuGiuChoChiTiet entity);

    // DTO -> Entity
    // Khóa ngoại phieuGiuCho sẽ được set ở tầng BUS hoặc tự động nếu cấu hình thêm
    PhieuGiuChoChiTiet toEntity(PhieuGiuChoChiTietDTO dto);
}
