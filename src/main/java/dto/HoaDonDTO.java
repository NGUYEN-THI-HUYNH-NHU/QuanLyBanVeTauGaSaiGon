package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HoaDonDTO implements Serializable {
    private String id;
    private String khachHangID;
    private String hoTenKhachHang;
    private String nhanVienID;
    private String hoTenNhanVien;
    private LocalDateTime thoiDiemTao;
    private double tongTien;
    private double tienNhan;
    private double tienHoan;
    private boolean isThanhToanTienMat;
    private String maGD;
    private List<HoaDonChiTietDTO> chiTiets;
}
