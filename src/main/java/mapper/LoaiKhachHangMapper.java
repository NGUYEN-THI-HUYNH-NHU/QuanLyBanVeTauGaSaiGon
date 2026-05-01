package mapper;

import dto.LoaiKhachHangDTO;
import entity.LoaiKhachHang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoaiKhachHangMapper {
    LoaiKhachHangMapper INSTANCE = Mappers.getMapper(LoaiKhachHangMapper.class);

    // Entity -> DTO
    @Mapping(source = "loaiKhachHangID", target = "id")
    LoaiKhachHangDTO toDTO(LoaiKhachHang entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "loaiKhachHangID")
    LoaiKhachHang toEntity(LoaiKhachHangDTO dto);
}
