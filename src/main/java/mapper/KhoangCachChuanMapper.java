package mapper;

import dto.KhoangCachChuanDTO;
import entity.KhoangCachChuan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface KhoangCachChuanMapper {
    KhoangCachChuanMapper INSTANCE = Mappers.getMapper(KhoangCachChuanMapper.class);

    @Mapping(source = "gaDau.id", target = "gaDauID")
    @Mapping(source = "gaDau.tenGa", target = "tenGaDau")
    @Mapping(source = "gaCuoi.id", target = "gaCuoiID")
    @Mapping(source = "gaCuoi.tenGa", target = "tenGaCuoi")
    KhoangCachChuanDTO toDTO(KhoangCachChuan entity);


    @Mapping(source = "gaDauID", target = "gaDau.id")
    @Mapping(source = "tenGaDau", target = "gaDau.tenGa")
    @Mapping(source = "gaCuoiID", target = "gaCuoi.id")
    @Mapping(source = "tenGaCuoi", target = "gaCuoi.tenGa")
    KhoangCachChuan toEntity(KhoangCachChuanDTO dto);
}