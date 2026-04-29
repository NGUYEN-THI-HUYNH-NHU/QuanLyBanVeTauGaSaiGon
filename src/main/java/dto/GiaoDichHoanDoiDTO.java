package dto;
import lombok.*;
import java.io.Serializable;
import java.util.*;
import java.time.*;
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GiaoDichHoanDoiDTO implements Serializable {
    private String id;
    private NhanVienDTO nhanVien;
    private HoaDonDTO hoaDon;
    private VeDTO veGoc;
    private VeDTO veMoi;
    private LoaiGiaoDich loaiGiaoDich;
    private String lyDo;
    private LocalDateTime thoiDiemGiaoDich;
    private double phiHoanDoi;
    private double soTienChenhLech;
}
