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
public class TaiKhoanDTO implements Serializable {
    private String id;
    private VaiTroTaiKhoanDTO vaiTroTaiKhoan;
    private NhanVienDTO nhanVien;
    private String tenDangNhap;
    private String matKhauHash;
    private LocalDateTime thoiDiemTao;
    private boolean trangThai;
}
