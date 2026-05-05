package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DieuKienKhuyenMaiDTO implements Serializable {
    private String id;
    private String khuyenMaiID;
    private String tuyenID;
    private String loaiTauID;
    private String hangToaID;
    private String loaiDoiTuongID;
    private Integer ngayTrongTuan;
    private boolean ngayLe;
    private Double minGiaTriDonHang;
}
