package mapper;

import dto.KhachHangDTO;
import entity.KhachHang;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface KhachHangMapper {

    KhachHangMapper INSTANCE = Mappers.getMapper(KhachHangMapper.class);

    @Mapping(source = "khachHangID", target = "id")
    @Mapping(source = "loaiKhachHang.loaiKhachHangID", target = "loaiKhachHangID")
    @Mapping(source = "loaiDoiTuong.loaiDoiTuongID", target = "loaiDoiTuongID")
    KhachHangDTO toDTO(KhachHang entity);

    @Mapping(source = "id", target = "khachHangID")
    @Mapping(source = "loaiKhachHangID", target = "loaiKhachHang.loaiKhachHangID")
    @Mapping(source = "loaiDoiTuongID", target = "loaiDoiTuong.loaiDoiTuongID")
    KhachHang toEntity(KhachHangDTO dto);
}

