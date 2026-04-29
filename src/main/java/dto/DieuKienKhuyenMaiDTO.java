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
public class DieuKienKhuyenMaiDTO implements Serializable {
    private String id;
    private KhuyenMaiDTO khuyenMai;
    private TuyenDTO tuyen;
    private LoaiTauDTO loaiTau;
    private HangToaDTO hangToa;
    private LoaiDoiTuongDTO loaiDoiTuong;
    private Integer ngayTrongTuan;
    private Boolean ngayLe;
    private double minGiaTriDonHang;
}
