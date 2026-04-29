package dto;

import entity.type.TrangThaiPhieuGiuCho;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhieuGiuChoChiTietDTO implements Serializable {
    private String id;
    private PhieuGiuChoDTO phieuGiuCho;
    private ChuyenDTO chuyen;
    private GaDTO gaDi;
    private GaDTO gaDen;
    private GheDTO ghe;
    private LocalDateTime thoiDiemGiuCho;
    private TrangThaiPhieuGiuCho trangThai;
}
