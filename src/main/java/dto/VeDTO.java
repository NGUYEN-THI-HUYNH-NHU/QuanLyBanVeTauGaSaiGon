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
public class VeDTO implements Serializable {
    private String veID;

    private KhachHangDTO khachHangDTO;

    private String donDatChoID;
    private String tuyenID;
    private String chuyenID;

    private String gaDiID;
    private String tenGaDi;

    private String gaDenID;
    private String tenGaDen;

    private String tauID;
    private String loaiTauID;
    private String toaID;
    private String hangToaID;
    private Integer soToa;
    private String gheID;
    private Integer soGhe;

    private LocalDateTime ngayGioDi;

    private Double gia;

    private String trangThai;

    private boolean isVeDoi;
}
