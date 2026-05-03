package entity;
/*
 * @(#) KhuyenMai.java  1.0  [12:36:16 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai implements Serializable {
    @Id
    @Column(name = "khuyenMaiID", length = 50)
    private String khuyenMaiID;

    @Column(name = "maKhuyenMai", length = 50, nullable = false, unique = true)
    private String maKhuyenMai;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "tyLeGiamGia", precision = 5, scale = 2)
    private double tyLeGiamGia;

    @Column(name = "tienGiamGia", precision = 12, scale = 2)
    private double tienGiamGia;

    @Column(name = "ngayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "soLuong", nullable = false)
    private int soLuong;

    @Column(name = "gioiHanMoiKhachHang", nullable = false)
    private int gioiHanMoiKhachHang;

    @Column(name = "trangThai", nullable = false)
    private boolean trangThai;

    @OneToOne(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DieuKienKhuyenMai dieuKienKhuyenMai;

    public KhuyenMai(String khuyenMaiID, String maKhuyenMai, String moTa, double tyLeGiamGia, double tienGiamGia, LocalDate ngayBatDau, LocalDate ngayKetThuc, int soLuong, int gioiHanMoiKhachHang, boolean trangThai) {
        this.khuyenMaiID = khuyenMaiID;
        this.maKhuyenMai = maKhuyenMai;
        this.moTa = moTa;
        this.tyLeGiamGia = tyLeGiamGia;
        this.tienGiamGia = tienGiamGia;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.soLuong = soLuong;
        this.gioiHanMoiKhachHang = gioiHanMoiKhachHang;
        this.trangThai = trangThai;
        this.dieuKienKhuyenMai = null;
    }
}