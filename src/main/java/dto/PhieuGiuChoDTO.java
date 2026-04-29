package dto;

import entity.type.TrangThaiPhieuGiuCho;
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
    private String id;
    private NhanVienDTO nhanVien;
    private LocalDateTime thoiDiemTao;
    private TrangThaiPhieuGiuCho trangThai;
    private Set<PhieuGiuChoChiTietDTO> chiTiets;
}
