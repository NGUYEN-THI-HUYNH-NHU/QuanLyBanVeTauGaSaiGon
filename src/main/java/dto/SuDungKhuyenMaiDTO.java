package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SuDungKhuyenMaiDTO implements Serializable {
    private String id;
    private String khuyenMaiID;
    private String hoaDonChiTietID;
    private String trangThai;
}
