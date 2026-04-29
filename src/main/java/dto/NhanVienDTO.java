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

public class NhanVienDTO implements Serializable {
    private String id;
    private String vaiTroNhanVien;
    private String hoTen;
    private boolean isNu;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private LocalDate ngayThamGia;
    private boolean isHoatDong;
    private CaLamDTO caLam;
}
