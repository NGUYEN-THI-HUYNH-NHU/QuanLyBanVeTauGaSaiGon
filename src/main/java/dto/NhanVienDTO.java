package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@ToString(exclude = "avatar")
@NoArgsConstructor
@AllArgsConstructor

public class NhanVienDTO implements Serializable {
    private String id;
    private String vaiTroNhanVienID;
    private String hoTen;
    private boolean nu;
    private LocalDate ngaySinh;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private LocalDate ngayThamGia;
    private boolean isHoatDong;
    private String caLamID;
    private LocalTime gioVaoCa;
    private LocalTime gioKetCa;
    private byte[] avatar;
}
