package dto;

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
    private String loaiGiaoDich;
    private String lyDo;
    private LocalDateTime thoiDiemGiaoDich;
    private double phiHoanDoi;
    private double soTienChenhLech;
}
