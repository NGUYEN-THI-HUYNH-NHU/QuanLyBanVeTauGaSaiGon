package dto;

import entity.type.LoaiDichVu;
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
    private LoaiDichVu loaiDichVu;
    private String donViTinh;
    private int soLuong;
    private double donGia;
    private double thanhTien;
}
