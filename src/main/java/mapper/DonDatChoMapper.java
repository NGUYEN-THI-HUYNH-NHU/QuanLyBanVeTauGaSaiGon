/**
 * File: DonDatChoMapper.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/30/2026
 */

package mapper;

import dto.DonDatChoDTO;
import entity.DonDatCho;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = KhachHangMapper.class)
public interface DonDatChoMapper {
    DonDatChoMapper INSTANCE = Mappers.getMapper(DonDatChoMapper.class);

    @Mapping(source = "donDatChoID", target = "id")
    @Mapping(source = "nhanVien.nhanVienID", target = "nhanVienID")
    @Mapping(source = "khachHang", target = "khachHangDTO")
    DonDatChoDTO toDTO(DonDatCho entity);

    @Mapping(source = "id", target = "donDatChoID")
    @Mapping(source = "nhanVienID", target = "nhanVien.nhanVienID")
    @Mapping(source = "khachHangDTO", target = "khachHang")
    DonDatCho toEntity(DonDatChoDTO dto);
}
