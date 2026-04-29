package entity;
/*
 * @(#) HoaDon_ChiTiet.java  1.0  [3:23:13 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

import entity.type.LoaiDichVu;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// CỰC KỲ QUAN TRỌNG: Exclude tất cả các object liên kết để tránh đệ quy vô hạn
@ToString(exclude = {"hoaDon", "ve", "phieuDungPhongVIP"})
@EqualsAndHashCode(exclude = {"hoaDon", "ve", "phieuDungPhongVIP"})
@Entity
@Table(name = "HoaDonChiTiet")
public class HoaDonChiTiet implements Serializable {
    @Id
    @Column(name = "hoaDonChiTietID", length = 50)
    private String hoaDonChiTietID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoaDonID", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veID")
    private Ve ve;

    // ĐÃ SỬA: Chuyển từ String sang cấu trúc Object @ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieuDungPhongVIPID")
    private PhieuDungPhongVIP phieuDungPhongVIP;

    @Column(name = "tenDichVu", nullable = false, length = 100)
    private String tenDichVu;

    @Enumerated(EnumType.STRING)
    @Column(name = "loaiDichVu", nullable = false, length = 30)
    private LoaiDichVu loaiDichVu;

    @Column(name = "donViTinh", length = 20)
    private String donViTinh;

    @Column(name = "soLuong", nullable = false)
    private int soLuong;

    @Column(name = "donGia", nullable = false, precision = 12, scale = 2)
    private double donGia;

    @Column(name = "thanhTien", nullable = false, precision = 12, scale = 2)
    private double thanhTien;
    
    /*
     * HoaDonChiTiet vé
     */
    public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, Ve ve, String tenDichVu, LoaiDichVu loaiDichVu,
                         String donViTinh, int soLuong, double donGia, double thanhTien) {
        super();
        this.hoaDonChiTietID = hoaDonChiTietID;
        this.hoaDon = hoaDon;
        this.ve = ve;
        this.tenDichVu = tenDichVu;
        this.loaiDichVu = loaiDichVu;
        this.donViTinh = donViTinh;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    /*
     * HoaDonChiTiet Phiếu dùng phòng chờ VIP
     */
    public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, PhieuDungPhongVIP phieuDungPhongVIP, String tenDichVu,
                         LoaiDichVu loaiDichVu, String donViTinh, int soLuong, double donGia, double thanhTien) {
        super();
        this.hoaDonChiTietID = hoaDonChiTietID;
        this.hoaDon = hoaDon;
        this.phieuDungPhongVIP = phieuDungPhongVIP;
        this.tenDichVu = tenDichVu;
        this.loaiDichVu = loaiDichVu;
        this.donViTinh = donViTinh;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public HoaDonChiTiet(String hoaDonChiTietID, HoaDon hoaDon, Ve ve, String tenDichVu, LoaiDichVu loaiDichVu,
                         int soLuong, double donGia, double thanhTien) {
        super();
        this.hoaDonChiTietID = hoaDonChiTietID;
        this.hoaDon = hoaDon;
        this.ve = ve;
        this.tenDichVu = tenDichVu;
        this.loaiDichVu = loaiDichVu;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }
}