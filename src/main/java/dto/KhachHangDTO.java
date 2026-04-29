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
public class KhachHangDTO implements Serializable {
    private String id;
    private String hoTen;
    private String soDienThoai;
    private String email;
    private String soGiayTo;
    private String diaChi;
    private LoaiDoiTuongDTO loaiDoiTuong;
    private LoaiKhachHangDTO loaiKhachHang;
}
