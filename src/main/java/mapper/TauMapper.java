package mapper;

import dto.TauDTO;
import entity.Tau;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TauMapper {
    TauMapper INSTANCE = Mappers.getMapper(TauMapper.class);

    // Entity -> DTO
    @Mapping(source = "loaiTau.loaiTauID", target = "loaiTau")
    TauDTO toDTO(Tau entity);

    // DTO -> Entity
    @Mapping(source = "loaiTau", target = "loaiTau.loaiTauID")
    Tau toEntity(TauDTO dto);
}
