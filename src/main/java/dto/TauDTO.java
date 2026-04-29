package dto;

import entity.type.TrangThaiTau;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TauDTO implements Serializable {
    private String tauID;
    private String tenTau;
    private LoaiTauDTO loaiTau;
    private int soLuongToa;
    private TrangThaiTau trangThai;
    private int vanTocTB;
    private Set<ToaDTO> toas;
}
