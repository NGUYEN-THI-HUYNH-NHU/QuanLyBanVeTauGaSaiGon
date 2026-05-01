package mapper;

import dto.ChuyenGaDTO;
import entity.ChuyenGa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ChuyenGaMapper {
    ChuyenGaMapper INSTANCE = Mappers.getMapper(ChuyenGaMapper.class);

    // Entity -> DTO
    @Mapping(source = "chuyen.chuyenID", target = "id")
    @Mapping(source = "ga.gaID", target = "gaID")
    ChuyenGaDTO toDTO(ChuyenGa entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "chuyen.chuyenID")
    @Mapping(source = "gaID", target = "ga.gaID")
    ChuyenGa toEntity(ChuyenGaDTO dto);
}
