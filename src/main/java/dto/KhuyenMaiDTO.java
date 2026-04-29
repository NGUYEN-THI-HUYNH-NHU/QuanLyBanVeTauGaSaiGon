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
public class KhuyenMaiDTO implements Serializable {
    private String id;
    private String maKhuyenMai;
    private String moTa;
    private double tyLeGiamGia;
    private double tienGiamGia;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private int soLuong;
    private int gioiHanMoiKhachHang;
    private boolean trangThai;
}
