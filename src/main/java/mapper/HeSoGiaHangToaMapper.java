package mapper;

import dto.HeSoGiaHangToaDTO;
import entity.HeSoGiaHangToa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HeSoGiaHangToaMapper {
    HeSoGiaHangToaMapper INSTANCE = Mappers.getMapper(HeSoGiaHangToaMapper.class);

    // Entity -> DTO
    @Mapping(source = "hsgHangToaID", target = "id")
    @Mapping(source = "hangToa.hangToaID", target = "hangToaID")
    HeSoGiaHangToaDTO toDTO(HeSoGiaHangToa entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "hsgHangToaID")
    @Mapping(source = "hangToaID", target = "hangToa.hangToaID")
    HeSoGiaHangToa toEntity(HeSoGiaHangToaDTO dto);
}
