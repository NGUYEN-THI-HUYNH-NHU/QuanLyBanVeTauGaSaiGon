/**
 * File: HoaDonMapper.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 5/1/2026
 */

package mapper;

import dto.HoaDonDTO;
import entity.HoaDon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {KhachHangMapper.class})
public interface HoaDonMapper {
    HoaDonMapper INSTANCE = Mappers.getMapper(HoaDonMapper.class);

    // Entity -> DTO
    @Mapping(source = "hoaDonID", target = "id")
    @Mapping(source = "khachHang", target = "khachHangDTO")
    HoaDonDTO toDTO(HoaDon entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "hoaDonID")
    @Mapping(source = "khachHangDTO", target = "khachHang")
    @Mapping(target = "chiTiets", ignore = true)
    HoaDon toEntity(HoaDonDTO dto);
}
