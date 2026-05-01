package mapper;

import dto.DichVuPhongChoVIPDTO;
import entity.DichVuPhongChoVIP;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DichVuPhongChoVIPMapper {
    mapper.DichVuPhongChoVIPMapper INSTANCE = Mappers.getMapper(mapper.DichVuPhongChoVIPMapper.class);

    // Entity -> DTO
    @Mapping(source = "dichVuPhongChoVIPID", target = "id")
    DichVuPhongChoVIPDTO toDTO(DichVuPhongChoVIP entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "dichVuPhongChoVIPID")
    DichVuPhongChoVIP toEntity(DichVuPhongChoVIPDTO dto);
}
