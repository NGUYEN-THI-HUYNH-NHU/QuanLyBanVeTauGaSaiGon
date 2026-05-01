package mapper;

import dto.CaLamDTO;
import entity.CaLam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CaLamMapper {
    CaLamMapper INSTANCE = Mappers.getMapper(CaLamMapper.class);

    // Entity -> DTO
    @Mapping(source = "caLamID", target = "id")
    CaLamDTO toDTO(CaLam entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "caLamID")
    CaLam toEntity(CaLamDTO dto);
}
