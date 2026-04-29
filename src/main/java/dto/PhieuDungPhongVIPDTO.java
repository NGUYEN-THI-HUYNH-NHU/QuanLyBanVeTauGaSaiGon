package dto;

import entity.type.TrangThaiPDPVIP;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDungPhongVIPDTO implements Serializable {
    private String id;
    private DichVuPhongChoVIPDTO dichVuPhongChoVIP;
    private VeDTO ve;
    private TrangThaiPDPVIP trangThai;
}
