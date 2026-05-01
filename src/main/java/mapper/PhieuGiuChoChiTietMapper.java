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
public interface PhieuGiuChoChiTietMapper {
    PhieuGiuChoChiTietMapper INSTANCE = Mappers.getMapper(PhieuGiuChoChiTietMapper.class);

    // Entity -> DTO
    @Mapping(source = "phieuGiuChoChiTietID", target = "id")
    @Mapping(source = "phieuGiuCho.phieuGiuChoID", target = "phieuGiuChoID")
    @Mapping(source = "chuyen.chuyenID", target = "chuyenID")
    @Mapping(source = "gaDi.gaID", target = "gaDiID")
    @Mapping(source = "gaDen.gaID", target = "gaDenID")
    @Mapping(source = "ghe.gheID", target = "gheID")
    PhieuGiuChoChiTietDTO toDTO(PhieuGiuChoChiTiet entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "phieuGiuChoChiTietID")
    @Mapping(source = "phieuGiuChoID", target = "phieuGiuCho.phieuGiuChoID")
    @Mapping(source = "chuyenID", target = "chuyen.chuyenID")
    @Mapping(source = "gaDiID", target = "gaDi.gaID")
    @Mapping(source = "gaDenID", target = "gaDen.gaID")
    @Mapping(source = "gheID", target = "ghe.gheID")
    PhieuGiuChoChiTiet toEntity(PhieuGiuChoChiTietDTO dto);
}
