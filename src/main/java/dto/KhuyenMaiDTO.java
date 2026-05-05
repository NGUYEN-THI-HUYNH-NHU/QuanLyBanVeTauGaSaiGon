package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMaiDTO implements Serializable {
    private String id;
    private String maKhuyenMai;
    private String moTa;
    private Double tyLeGiamGia;
    private Double tienGiamGia;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer soLuong;
    private Integer gioiHanMoiKhachHang;
    private Boolean trangThai;
}
