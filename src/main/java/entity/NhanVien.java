package entity;
/*
 * @(#) NhanVien.java  1.0  [9:52:26 PM] Sep 17, 2025
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
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "NhanVien")
public class NhanVien implements Serializable {
    @Id
    @Column(name = "nhanVienID", length = 50)
    private String nhanVienID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaiTroNhanVienID", nullable = false)
    private VaiTroNhanVien vaiTroNhanVien;

    @Column(name = "hoTen", nullable = false)
    private String hoTen;

    @Column(name = "isNu", nullable = false)
    private boolean isNu;

    @Column(name = "ngaySinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "soDienThoai", length = 50)
    private String soDienThoai;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "diaChi")
    private String diaChi;

    @Column(name = "ngayThamGia", nullable = false)
    private LocalDate ngayThamGia;

    @Column(name = "isHoatDong", nullable = false)
    private boolean isHoatDong;

    @Lob
    @Column(name = "avatar")
    private byte[] avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caLamID", nullable = false)
    private CaLam caLam;

    public NhanVien(String nhanVienID, VaiTroNhanVien vaiTroNhanVien, String hoTen, boolean isNu, LocalDate ngaySinh,
                    String soDienThoai, String email, String diaChi, LocalDate ngayThamGia, boolean isHoatDong, byte[] avatar) {
        super();
        this.nhanVienID = nhanVienID;
        this.vaiTroNhanVien = vaiTroNhanVien;
        this.hoTen = hoTen;
        this.isNu = isNu;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.ngayThamGia = ngayThamGia;
        this.isHoatDong = isHoatDong;
        this.avatar = avatar;
    }

    public NhanVien(String nhanVienID, VaiTroNhanVien vaiTroNhanVien, String hoTen, boolean isNu, LocalDate ngaySinh,
                    String soDienThoai, String email, String diaChi, LocalDate ngayThamGia, boolean isHoatDong, CaLam caLam) {
        super();
        this.nhanVienID = nhanVienID;
        this.vaiTroNhanVien = vaiTroNhanVien;
        this.hoTen = hoTen;
        this.isNu = isNu;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.ngayThamGia = ngayThamGia;
        this.isHoatDong = isHoatDong;
        this.caLam = caLam;
    }

    public NhanVien(String nhanVienID, VaiTroNhanVien vaiTroNhanVien, String hoTen, boolean isNu, LocalDate ngaySinh,
                    String soDienThoai, String email, String diaChi, LocalDate ngayThamGia, boolean isHoatDong, byte[] avatar,
                    CaLam caLam) {
        super();
        this.nhanVienID = nhanVienID;
        this.vaiTroNhanVien = vaiTroNhanVien;
        this.hoTen = hoTen;
        this.isNu = isNu;
        this.ngaySinh = ngaySinh;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.ngayThamGia = ngayThamGia;
        this.isHoatDong = isHoatDong;
        this.avatar = avatar;
        this.caLam = caLam;
    }

    public NhanVien(String nhanVienID, String hoTen) {
        super();
        this.nhanVienID = nhanVienID;
        this.hoTen = hoTen;
    }

    public NhanVien(String nhanVienID) {
        super();
        this.nhanVienID = nhanVienID;
    }

}