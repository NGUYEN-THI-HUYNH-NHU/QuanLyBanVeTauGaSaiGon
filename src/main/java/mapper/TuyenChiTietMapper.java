package mapper;

import dto.TuyenChiTietDTO;
import entity.TuyenChiTiet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TuyenChiTietMapper {
    TuyenChiTietMapper INSTANCE = Mappers.getMapper(TuyenChiTietMapper.class);

    // Entity -> DTO
    @Mapping(source = "tuyen.tuyenID", target = "tuyenID")
    @Mapping(source = "ga.gaID", target = "gaID")
    @Mapping(source = "ga.tenGa", target = "tenGa")
    TuyenChiTietDTO toDTO(TuyenChiTiet entity);

    // DTO -> Entity
    @Mapping(source = "tuyenID", target = "tuyen.tuyenID")
    @Mapping(source = "gaID", target = "ga.gaID")
    @Mapping(source = "tenGa", target = "ga.tenGa")
    TuyenChiTiet toEntity(TuyenChiTietDTO dto);
}
