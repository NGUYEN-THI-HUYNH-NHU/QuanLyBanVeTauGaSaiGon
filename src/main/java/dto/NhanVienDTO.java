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
public class NhanVienDTO implements Serializable {
    private String id;
    private VaiTroNhanVienDTO vaiTroNhanVien;
    private String hoTen;
    private boolean isNu;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private LocalDate ngayThamGia;
    private boolean isHoatDong;
    private CaLamDTO caLam;
}
