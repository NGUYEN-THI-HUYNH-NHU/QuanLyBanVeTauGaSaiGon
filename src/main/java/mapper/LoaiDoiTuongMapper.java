package mapper;

import dto.LoaiDoiTuongDTO;
import entity.LoaiDoiTuong;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoaiDoiTuongMapper {
    LoaiDoiTuongMapper INSTANCE = Mappers.getMapper(LoaiDoiTuongMapper.class);

    // Entity -> DTO
    @Mapping(source = "loaiDoiTuongID", target = "id")
    LoaiDoiTuongDTO toDTO(LoaiDoiTuong entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "loaiDoiTuongID")
    LoaiDoiTuong toEntity(LoaiDoiTuongDTO dto);
}
