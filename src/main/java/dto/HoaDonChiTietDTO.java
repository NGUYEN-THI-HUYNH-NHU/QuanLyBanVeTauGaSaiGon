package dto;

import lombok.*;

import java.io.Serializable;

@Builder
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietDTO implements Serializable {
    private String id;
    private String hoaDonID;
    private String veID;
    private String phieuDungPhongVIPID;
    private String tenDichVu;
    private String loaiDichVu;
    private String donViTinh;
    private Integer soLuong;
    private Double donGia;
    private Double thanhTien;
}
