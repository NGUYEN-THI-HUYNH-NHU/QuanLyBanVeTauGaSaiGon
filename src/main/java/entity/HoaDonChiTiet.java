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
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "HoaDonChiTiet")
public class HoaDonChiTiet implements Serializable {
    @Id
    @Column(name = "hoaDonChiTietID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoaDonID", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veID")
    private Ve ve;

    // Giữ ID dưới dạng String nếu bạn chưa ánh xạ bảng PhieuDungPhongVIP
    @Column(name = "phieuDungPhongVIPID", length = 50)
    private String phieuDungPhongVIPID;

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
}