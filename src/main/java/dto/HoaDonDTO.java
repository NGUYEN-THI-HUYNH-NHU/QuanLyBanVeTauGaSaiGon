package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HoaDonDTO implements Serializable {
    private String id;
    private KhachHangDTO khachHangDTO;
    private LocalDateTime thoiDiemTao;
    private double tongTien;
    private double tienNhan;
    private double tienHoan;
    private boolean thanhToanTienMat;
    private String maGD;
}
