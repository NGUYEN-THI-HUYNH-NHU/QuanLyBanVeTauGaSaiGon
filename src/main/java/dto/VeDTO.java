package dto;

import entity.type.TrangThaiVe;
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
    private String id;
    private KhachHangDTO khachHang;
    private DonDatChoDTO donDatCho;
    private ChuyenDTO chuyen;
    private GaDTO gaDi;
    private GaDTO gaDen;
    private GheDTO ghe;
    private LocalDateTime ngayGioDi;
    private double gia;
    private TrangThaiVe trangThai;
}
