package entity;

/*
 * @(#) SuDungKhuyenMai.java  1.0  [3:39:39 PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 22, 2025
 * @version: 1.0
 */

import entity.type.TrangThaiSDKM;
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
@Table(name = "SuDungKhuyenMai")
public class SuDungKhuyenMai implements Serializable {
    @Id
    @Column(name = "suDungKhuyenMaiID", length = 50)
    private String suDungKhuyenMaiID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khuyenMaiID", nullable = false)
    private KhuyenMai khuyenMai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoaDonChiTietID", nullable = false)
    private HoaDonChiTiet hoaDonChiTiet;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 50)
    private TrangThaiSDKM trangThai;
}