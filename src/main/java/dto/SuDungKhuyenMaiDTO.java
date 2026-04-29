package dto;

import entity.type.TrangThaiSDKM;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SuDungKhuyenMaiDTO implements Serializable {
    private String id;
    private KhuyenMaiDTO khuyenMai;
    private HoaDonChiTietDTO hoaDonChiTiet;
    private TrangThaiSDKM trangThai;
}
