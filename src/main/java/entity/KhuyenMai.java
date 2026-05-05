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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "dieuKienKhuyenMai")
@EqualsAndHashCode(exclude = "dieuKienKhuyenMai")
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
    private Double tyLeGiamGia;

    @Column(name = "tienGiamGia", precision = 12, scale = 2)
    private Double tienGiamGia;

    @Column(name = "ngayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "soLuong", nullable = false)
    private Integer soLuong;

    @Column(name = "gioiHanMoiKhachHang", nullable = false)
    private Integer gioiHanMoiKhachHang;

    @Column(name = "trangThai", nullable = false)
    private boolean trangThai;

    @OneToOne(mappedBy = "khuyenMai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DieuKienKhuyenMai dieuKienKhuyenMai;

    public KhuyenMai(String khuyenMaiID, String maKhuyenMai, String moTa, Double tyLeGiamGia, Double tienGiamGia, LocalDate ngayBatDau, LocalDate ngayKetThuc, Integer soLuong, Integer gioiHanMoiKhachHang, boolean trangThai) {
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