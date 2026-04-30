package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DonDatChoDTO implements Serializable {
    private String id;
    private String nhanVienID;
    private KhachHangDTO khachHangDTO;
    private LocalDateTime thoiDiemDatCho;
    private int tongSoVe;
    private int soVeHoan;
    private int soVeDoi;

    public DonDatChoDTO(String donDatChoID, int tongSoVe, int soVeHoan, int soVeDoi) {
        super();
        this.id = donDatChoID;
        this.tongSoVe = tongSoVe;
        this.soVeHoan = soVeHoan;
        this.soVeDoi = soVeDoi;
    }
}
