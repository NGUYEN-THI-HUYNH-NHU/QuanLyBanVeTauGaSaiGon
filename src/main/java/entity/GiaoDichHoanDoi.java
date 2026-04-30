package entity;
/*
 * @(#) GiaoDichHoanDoi.java  1.0  [1:59:59 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

import entity.type.LoaiGiaoDich;
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
@Table(name = "GiaoDichHoanDoi")
public class GiaoDichHoanDoi implements Serializable {
    @Id
    @Column(name = "giaoDichHoanDoiID", length = 50)
    private String giaoDichHoanDoiID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhanVienID", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoaDonID", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veGocID", nullable = false)
    private Ve veGoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veMoiID")
    private Ve veMoi;

    @Enumerated(EnumType.STRING)
    @Column(name = "loaiGiaoDich", nullable = false, length = 50)
    private LoaiGiaoDich loaiGiaoDich;

    @Column(name = "lyDo", length = 500)
    private String lyDo;

    @Column(name = "thoiDiemGiaoDich", nullable = false)
    private LocalDateTime thoiDiemGiaoDich;

    @Column(name = "phiHoanDoi", nullable = false, precision = 12, scale = 2)
    private double phiHoanDoi;

    @Column(name = "soTienChenhLech", nullable = false, precision = 12, scale = 2)
    private double soTienChenhLech;

    public GiaoDichHoanDoi(String gdhdID, NhanVien nhanVien, HoaDon hoaDon, Ve ve, LoaiGiaoDich loaiGiaoDich, String lyDo, LocalDateTime thoiDiemTao, double lePhiHoanVe, double soTienChenhLech) {
        this.giaoDichHoanDoiID = gdhdID;
        this.nhanVien = nhanVien;
        this.hoaDon = hoaDon;
        this.veGoc = ve;
        this.loaiGiaoDich = loaiGiaoDich;
        this.lyDo = lyDo;
        this.thoiDiemGiaoDich = thoiDiemTao;
        this.phiHoanDoi = lePhiHoanVe;
        this.soTienChenhLech = soTienChenhLech;
    }
}