package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BieuGiaVeDTO implements Serializable {
    private String id;
    private String tuyenApDungID;
    private String loaiTauApDungID;
    private String moTaLoaiTau;
    private String hangToaApDungID;
    private String moTaHangToa;
    private Integer minKm;
    private Integer maxKm;
    private Double donGiaTrenKm;
    private Double giaCoBan;
    private Double phuPhiCaoDiem;
    private Integer doUuTien;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
}
