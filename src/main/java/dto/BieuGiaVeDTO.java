package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BieuGiaVeDTO implements Serializable {
    private String id;
    private TuyenDTO tuyenApDung;
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
