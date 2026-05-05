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
    private Integer tongSoVe;
    private Integer soVeHoan;
    private Integer soVeDoi;

    public DonDatChoDTO(String donDatChoID, Integer tongSoVe, Integer soVeHoan, Integer soVeDoi) {
        super();
        this.id = donDatChoID;
        this.tongSoVe = tongSoVe;
        this.soVeHoan = soVeHoan;
        this.soVeDoi = soVeDoi;
    }
}
