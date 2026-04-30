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
    private String hangToaApDungID;
    private int minKm;
    private int maxKm;
    private double donGiaTrenKm;
    private double giaCoBan;
    private double phuPhiCaoDiem;
    private int doUuTien;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
}
