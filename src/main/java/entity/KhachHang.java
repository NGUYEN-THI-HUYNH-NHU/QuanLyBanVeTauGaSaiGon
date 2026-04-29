package entity;
/*
 * @(#) KhachHang.java  1.0  [9:35:13 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

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
@Table(name = "KhachHang")
public class KhachHang implements Serializable {
    @Id
    @Column(name = "khachHangID", length = 50)
    private String id;

    @Column(name = "hoTen", nullable = false)
    private String hoTen;

    @Column(name = "soDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "soGiayTo", length = 100)
    private String soGiayTo;

    @Column(name = "diaChi")
    private String diaChi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiDoiTuongID")
    private LoaiDoiTuong loaiDoiTuong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiKhachHangID", nullable = false)
    private LoaiKhachHang loaiKhachHang;
}