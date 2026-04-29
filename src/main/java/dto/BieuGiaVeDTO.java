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
    private String tuyenApDung;
    private LoaiTauDTO loaiTauApDung;
    private HangToaDTO hangToaApDung;
    private int minKm;
    private int maxKm;
    private double donGiaTrenKm;
    private double giaCoBan;
    private double phuPhiCaoDiem;
    private int doUuTien;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
}
