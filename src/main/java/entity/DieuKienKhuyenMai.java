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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khuyenMaiID", nullable = false, unique = true)
    private KhuyenMai khuyenMai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tuyenID")
    private Tuyen tuyen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loaiTauID")
    private LoaiTau loaiTau;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hangToaID")
    private HangToa hangToa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loaiDoiTuongID")
    private LoaiDoiTuong loaiDoiTuong;

    @Column(name = "ngayTrongTuan", nullable = true)
    private Integer ngayTrongTuan;

    @Column(name = "ngayLe")
    private boolean ngayLe;

    @Column(name = "minGiaTriDonHang", precision = 12, scale = 2)
    private Double minGiaTriDonHang;
}