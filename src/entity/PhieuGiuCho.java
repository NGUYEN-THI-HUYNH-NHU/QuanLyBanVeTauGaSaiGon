package entity;/*
 * @ (#) PhieuGiuCho.java   1.0     02/10/2025
package entity;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 02/10/2025
 */

import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuGiuCho {
    private String phieuGiuChoID;
    private NhanVien nhanVien;
    private int thoiGianGiuChoS;
    private LocalDateTime thoiDiemTao;
    private String trangThai;

    public PhieuGiuCho(String phieuGiuChoID, NhanVien nhanVien, int thoiGianGiuChoS, LocalDateTime thoiDiemTao, String trangThai) {
        this.phieuGiuChoID = phieuGiuChoID;
        this.nhanVien = nhanVien;
        this.thoiGianGiuChoS = thoiGianGiuChoS;
        this.thoiDiemTao = thoiDiemTao;
        this.trangThai = trangThai;
    }

    public String getPhieuGiuChoID() {
        return phieuGiuChoID;
    }

    public void setPhieuGiuChoID(String phieuGiuChoID) {
        if(phieuGiuChoID == null || phieuGiuChoID.isEmpty()) {
            throw new IllegalArgumentException("PhieuGiuChoID không được để trống!");
        }
        this.phieuGiuChoID = phieuGiuChoID;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        if(nhanVien == null) {
            throw new IllegalArgumentException("NhanVien không được để trống!");
        }
        this.nhanVien = nhanVien;
    }

    public int getThoiGianGiuChoS() {
        return thoiGianGiuChoS;
    }

    public void setThoiGianGiuChoS(int thoiGianGiuChoS) {
        if(thoiGianGiuChoS <= 0) {
            throw new IllegalArgumentException("ThoiGianGiuChoS phải lớn hơn 0!");
        }
        this.thoiGianGiuChoS = thoiGianGiuChoS;
    }

    public LocalDateTime getThoiDiemTao() {
        return thoiDiemTao;
    }

    public void setThoiDiemTao(LocalDateTime thoiDiemTao) {
        if(thoiDiemTao == null) {
            throw new IllegalArgumentException("ThoiDiemTao không được để trống!");
        }
        this.thoiDiemTao = thoiDiemTao;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return phieuGiuChoID + ";" + nhanVien + ";" + thoiGianGiuChoS
                + ";" + thoiDiemTao + ";" + trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuGiuCho that = (PhieuGiuCho) o;
        return Objects.equals(getPhieuGiuChoID(), that.getPhieuGiuChoID());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPhieuGiuChoID());
    }
}
