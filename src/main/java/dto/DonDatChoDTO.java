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
public class DonDatChoDTO implements Serializable {
    private String id;
    private NhanVienDTO nhanVien;
    private KhachHangDTO khachHang;
    private LocalDateTime thoiDiemDatCho;
}
