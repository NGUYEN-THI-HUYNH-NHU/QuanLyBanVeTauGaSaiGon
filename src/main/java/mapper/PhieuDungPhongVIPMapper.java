package mapper;

import dto.PhieuDungPhongVIPDTO;
import entity.PhieuDungPhongVIP;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhieuDungPhongVIPMapper {
    PhieuDungPhongVIPMapper INSTANCE = Mappers.getMapper(PhieuDungPhongVIPMapper.class);

    // Entity -> DTO
    @Mapping(source = "phieuDungPhongVIPID", target = "id")
    @Mapping(source = "dichVuPhongChoVIP.dichVuPhongChoVIPID", target = "dichVuPhongChoVIPID")
    @Mapping(source = "ve.veID", target = "veID")
    PhieuDungPhongVIPDTO toDTO(PhieuDungPhongVIP entity);

    // DTO -> Entity
    @Mapping(source = "id", target = "phieuDungPhongVIPID")
    @Mapping(source = "dichVuPhongChoVIPID", target = "dichVuPhongChoVIP.dichVuPhongChoVIPID")
    @Mapping(source = "veID", target = "ve.veID")
    PhieuDungPhongVIP toEntity(PhieuDungPhongVIPDTO dto);
}