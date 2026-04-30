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
public class PhieuGiuChoChiTietDTO implements Serializable {
    private String id;
    private String phieuGiuChoID;
    private String chuyenID;
    private String gaDiID;
    private String gaDenID;
    private String gheID;
    private LocalDateTime thoiDiemGiuCho;
    private String trangThai;
}
