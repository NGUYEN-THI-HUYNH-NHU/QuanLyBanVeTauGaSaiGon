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
public class PhieuGiuChoDTO implements Serializable {
    private String phieuGiuChoID;
    private String nhanVienID;
    private String hoTenNhanVien;
    private LocalDateTime thoiDiemTao;
    private String trangThai;
}
