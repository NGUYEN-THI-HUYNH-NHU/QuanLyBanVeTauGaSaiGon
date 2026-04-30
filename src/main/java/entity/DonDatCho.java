package entity;
/*
 * @(#) LanDatCho.java  1.0  [12:44:56 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "DonDatCho")
public class DonDatCho implements Serializable {
    @Id
    @Column(name = "donDatChoID", length = 50)
    private String donDatChoID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhanVienID", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khachHangID", nullable = false)
    private KhachHang khachHang;

    @Column(name = "thoiDiemDatCho", nullable = false)
    private LocalDateTime thoiDiemDatCho;

    @Transient
    private int tongSoVe;
    @Transient
    private int soVeHoan;
    @Transient
    private int soVeDoi;

    public DonDatCho(String donDatChoID) {
        super();
        this.donDatChoID = donDatChoID;
    }
}