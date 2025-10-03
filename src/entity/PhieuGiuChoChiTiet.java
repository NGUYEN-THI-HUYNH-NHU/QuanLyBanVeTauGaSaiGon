package entity;/*
 * @ (#) PhieuGiuChoChiTiet.java   1.0     02/10/2025
package entity;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 02/10/2025
 */

import java.time.LocalDateTime;
import java.util.Objects;

public class PhieuGiuChoChiTiet {
    private String phieuGiuChoChiTietID;
    private PhieuGiuCho phieuGiuCho;
    private Chuyen chuyen;
    private Ghe ghe;
    private int thuTuGaDi;
    private int thuTuGaDen;
    private LocalDateTime thoiDiemHetGiuCho;
    private String trangThai;

    public PhieuGiuChoChiTiet(String phieuGiuChoChiTietID, PhieuGiuCho phieuGiuCho, Chuyen chuyen, Ghe ghe, int thuTuGaDi, int thuTuGaDen, LocalDateTime thoiDiemHetGiuCho, String trangThai) {
        this.phieuGiuChoChiTietID = phieuGiuChoChiTietID;
        this.phieuGiuCho = phieuGiuCho;
        this.chuyen = chuyen;
        this.ghe = ghe;
        this.thuTuGaDi = thuTuGaDi;
        this.thuTuGaDen = thuTuGaDen;
        this.thoiDiemHetGiuCho = thoiDiemHetGiuCho;
        this.trangThai = trangThai;
    }

    public String getPhieuGiuChoChiTietID() {
        return phieuGiuChoChiTietID;
    }

    public void setPhieuGiuChoChiTietID(String phieuGiuChoChiTietID) {
        this.phieuGiuChoChiTietID = phieuGiuChoChiTietID;
    }

    public PhieuGiuCho getPhieuGiuCho() {
        return phieuGiuCho;
    }

    public void setPhieuGiuCho(PhieuGiuCho phieuGiuCho) {
        if(phieuGiuCho == null) {
            throw new IllegalArgumentException("PhieuGiuCho không được để trống!");
        }
        this.phieuGiuCho = phieuGiuCho;
    }

    public Chuyen getChuyen() {
        return chuyen;
    }

    public void setChuyen(Chuyen chuyen) {
        if(chuyen == null) {
            throw new IllegalArgumentException("Chuyen không được để trống!");
        }
        this.chuyen = chuyen;
    }

    public Ghe getGhe() {
        return ghe;
    }

    public void setGhe(Ghe ghe) {
        if(ghe == null) {
            throw new IllegalArgumentException("Ghe không được để trống!");
        }
        this.ghe = ghe;
    }

    public int getThuTuGaDi() {
        return thuTuGaDi;
    }

    public void setThuTuGaDi(int thuTuGaDi) {
        if(thuTuGaDi < 0) {
            throw new IllegalArgumentException("Thứ tự ga đi không được âm!");
        }
        this.thuTuGaDi = thuTuGaDi;
    }

    public int getThuTuGaDen() {
        return thuTuGaDen;
    }

    public void setThuTuGaDen(int thuTuGaDen) {
        if(thuTuGaDen < 0) {
            throw new IllegalArgumentException("Thứ tự ga đến không được âm!");
        }
        this.thuTuGaDen = thuTuGaDen;
    }

    public LocalDateTime getThoiDiemHetGiuCho() {
        return thoiDiemHetGiuCho;
    }

    public void setThoiDiemHetGiuCho(LocalDateTime thoiDiemHetGiuCho) {
        if(thoiDiemHetGiuCho == null) {
            throw new IllegalArgumentException("ThoiDiemHetGiuCho không được để trống!");
        }
        this.thoiDiemHetGiuCho = thoiDiemHetGiuCho;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return phieuGiuChoChiTietID + ";"
                + phieuGiuCho + ";"
                + chuyen + ";"
                + ghe + ";"
                + thuTuGaDi + ";"
                + thuTuGaDen + ";"
                + thoiDiemHetGiuCho + ";"
                + trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuGiuChoChiTiet that = (PhieuGiuChoChiTiet) o;
        return Objects.equals(getPhieuGiuChoChiTietID(), that.getPhieuGiuChoChiTietID());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPhieuGiuChoChiTietID());
    }
}
