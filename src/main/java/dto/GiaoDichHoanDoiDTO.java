package dto;

import entity.type.LoaiGiaoDich;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GiaoDichHoanDoiDTO implements Serializable {
    private String id;
    private String nhanVienID;
    private String hoaDonID;
    private String veGocID;
    private String veMoiID;
    private LoaiGiaoDich loaiGiaoDich;
    private String lyDo;
    private LocalDateTime thoiDiemGiaoDich;
    private double phiHoanDoi;
    private double soTienChenhLech;
}
