package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TauDTO implements Serializable {
    private String tauID;
    private String tenTau;
    private String loaiTau;
    private int soLuongToa;
    private String trangThai;
    private int vanTocTB;
}
