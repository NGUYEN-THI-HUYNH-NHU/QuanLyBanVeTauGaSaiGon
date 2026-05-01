package mapper;

import dto.LoaiTauDTO;
import entity.LoaiTau;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoaiTauMapper {
    LoaiTauMapper INSTANCE = Mappers.getMapper(LoaiTauMapper.class);

    // Entity -> DTO
    @Mapping(source = "loaiTauID", target = "id")
    LoaiTauDTO toDTO(LoaiTau entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "loaiTauID")
    LoaiTau toEntity(LoaiTauDTO dto);
}
