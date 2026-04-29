package entity;
/*
 * @(#) DieuKienKhuyenMai.java  1.0  [3:36:18 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
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
@Table(name = "DieuKienKhuyenMai")
public class DieuKienKhuyenMai implements Serializable {
    @Id
    @Column(name = "dieuKienID", length = 50)
    private String dieuKienID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khuyenMaiID", nullable = false)
    private KhuyenMai khuyenMai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuyenID")
    private Tuyen tuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiTauID")
    private LoaiTau loaiTau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hangToaID")
    private HangToa hangToa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiDoiTuongID")
    private LoaiDoiTuong loaiDoiTuong;

    @Column(name = "ngayTrongTuan")
    private Integer ngayTrongTuan; // Dùng wrapper class (Integer) vì cột này cho phép NULL

    @Column(name = "ngayLe")
    private Boolean ngayLe;

    @Column(name = "minGiaTriDonHang", precision = 12, scale = 2)
    private double minGiaTriDonHang;
}