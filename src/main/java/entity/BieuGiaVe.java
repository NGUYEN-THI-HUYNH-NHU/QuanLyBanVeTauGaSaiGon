package entity;
/*
 * @(#) BangGia.java  1.0  [12:54:26 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "BieuGiaVe")
public class BieuGiaVe implements Serializable {
    @Id
    @Column(name = "bieuGiaVeID", length = 50)
    private String bieuGiaVeID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuyenApDungID")
    private Tuyen tuyenApDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiTauApDungID")
    private LoaiTau loaiTauApDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hangToaApDungID")
    private HangToa hangToaApDung;

    @Column(name = "minKm", nullable = false)
    private Integer minKm;

    @Column(name = "maxKm", nullable = false)
    private Integer maxKm;

    @Column(name = "donGiaTrenKm", precision = 10, scale = 4)
    private Double donGiaTrenKm;

    @Column(name = "giaCoBan", precision = 12, scale = 2)
    private Double giaCoBan;

    @Column(name = "phuPhiCaoDiem", precision = 12, scale = 2)
    private Double phuPhiCaoDiem;

    @Column(name = "doUuTien", nullable = false)
    private Integer doUuTien;

    @Column(name = "ngayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngayKetThuc")
    private LocalDate ngayKetThuc;

}