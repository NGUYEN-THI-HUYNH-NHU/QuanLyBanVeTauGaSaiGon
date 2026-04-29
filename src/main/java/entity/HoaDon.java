package entity;
/*
 * @(#) HoaDon.java  1.0  [12:36:28 PM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "chiTiets")
@EqualsAndHashCode(exclude = "chiTiets")
@Entity
@Table(name = "HoaDon")
public class HoaDon implements Serializable {
    @Id
    @Column(name = "hoaDonID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khachHangID", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhanVienID", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "thoiDiemTao", nullable = false)
    private LocalDateTime thoiDiemTao;

    @Column(name = "tongTien", nullable = false, precision = 12, scale = 2)
    private double tongTien;

    @Column(name = "tienNhan", nullable = false, precision = 12, scale = 2)
    private double tienNhan;

    @Column(name = "tienHoan", precision = 12, scale = 2)
    private double tienHoan;

    @Column(name = "isThanhToanTienMat", nullable = false)
    private boolean isThanhToanTienMat;

    @Column(name = "maGD", length = 50)
    private String maGD;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<HoaDonChiTiet> chiTiets;
}