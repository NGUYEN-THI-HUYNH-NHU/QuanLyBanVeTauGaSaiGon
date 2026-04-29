package entity;
/*
 * @(#) Account.java  1.0  [9:33:42 PM] Sep 17, 2025
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
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"nhanVien"})
@EqualsAndHashCode(exclude = {"nhanVien"})
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan implements Serializable {
    @Id
    @Column(name = "taiKhoanID", length = 50)
    private String taiKhoanID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaiTroTaiKhoanID", nullable = false)
    private VaiTroTaiKhoan vaiTroTaiKhoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhanVienID", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "tenDangNhap", length = 50, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "matKhauHash", length = 500, nullable = false)
    private String matKhauHash;

    @Column(name = "thoiDiemTao", nullable = false)
    private LocalDateTime thoiDiemTao;

    @Column(name = "trangThai", nullable = false)
    private boolean trangThai;
    
    public TaiKhoan(VaiTroTaiKhoan vaiTroTaiKhoan, NhanVien nhanVien, String tenDangNhap,
                    String matKhauHash, LocalDateTime thoiDiemTao, boolean isHoatDong) {
        super();
        this.vaiTroTaiKhoan = vaiTroTaiKhoan;
        this.nhanVien = nhanVien;
        this.tenDangNhap = tenDangNhap;
        this.matKhauHash = matKhauHash;
        this.thoiDiemTao = thoiDiemTao;
        this.trangThai = isHoatDong;
    }
}