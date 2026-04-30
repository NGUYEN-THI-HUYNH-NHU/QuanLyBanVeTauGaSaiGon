package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO implements Serializable {
    private String id;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String soGiayTo;
    private String diaChi;
    private String loaiDoiTuongID;
    private String loaiKhachHangID;
}
