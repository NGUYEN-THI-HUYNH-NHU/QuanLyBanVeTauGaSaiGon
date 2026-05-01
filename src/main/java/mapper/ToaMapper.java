package mapper;

import dto.ToaDTO;
import entity.Toa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ToaMapper {
    ToaMapper INSTANCE = Mappers.getMapper(ToaMapper.class);

    // Entity -> DTO
    @Mapping(source = "toaID", target = "id")
    @Mapping(source = "tau.tauID", target = "tauID")
    @Mapping(source = "hangToa.hangToaID", target = "hangToaID")
    @Mapping(source = "tau.loaiTau.loaiTauID", target = "loaiTauID")
    @Mapping(source = "hangToa.moTa", target = "moTa")
    ToaDTO toDTO(Toa entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "toaID")
    @Mapping(source = "tauID", target = "tau.tauID")
    @Mapping(source = "hangToaID", target = "hangToa.hangToaID")
    @Mapping(source = "loaiTauID", target = "tau.loaiTau.loaiTauID")
    @Mapping(source = "moTa", target = "hangToa.moTa")
    Toa toEntity(ToaDTO dto);
}
