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
public class TaiKhoanDTO implements Serializable {
    private String id;
    private String vaiTroTaiKhoanID;
    private String nhanVienID;
    private String tenDangNhap;
    private LocalDateTime thoiDiemTao;
    private boolean trangThai;
}
