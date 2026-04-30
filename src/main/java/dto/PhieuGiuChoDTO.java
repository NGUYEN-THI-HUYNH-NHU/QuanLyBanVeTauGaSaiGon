package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

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
    private Set<PhieuGiuChoChiTietDTO> chiTiets;
}
