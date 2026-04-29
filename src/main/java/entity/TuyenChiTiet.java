package entity;/*
 * @ (#) TuyenChiTiet.java   1.0     02/10/2025
package entity;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 02/10/2025
 */

import java.util.Objects;

public class TuyenChiTiet {
    private Tuyen tuyen;
    private Ga ga;
    private int thuTu;
    private int khoangCachTuGaXuatPhatKm;

    public TuyenChiTiet(Tuyen tuyen, Ga ga, int thuTu, int khoangCachTuGaXuatPhatKm) {
        this.tuyen = tuyen;
        this.ga = ga;
        this.thuTu = thuTu;
        this.khoangCachTuGaXuatPhatKm = khoangCachTuGaXuatPhatKm;
    }

    public Tuyen getTuyen() {
        return tuyen;
    }

    public void setTuyen(Tuyen tuyen) {
        if(tuyen == null) {
            throw new IllegalArgumentException("Tuyen không được để trống!");
        }
        this.tuyen = tuyen;
    }

    public Ga getGa() {
        return ga;
    }

    public void setGa(Ga ga) {
        if(ga == null) {
            throw new IllegalArgumentException("Ga không được để trống!");
        }
        this.ga = ga;
    }

    public int getThuTu() {
        return thuTu;
    }

    public void setThuTu(int thuTu) {
        this.thuTu = thuTu;
    }

    public int getKhoangCachTuGaXuatPhatKm() {
        return khoangCachTuGaXuatPhatKm;
    }

    public void setKhoangCachTuGaXuatPhatKm(int khoangCachTuGaXuatPhatKm) {
        this.khoangCachTuGaXuatPhatKm = khoangCachTuGaXuatPhatKm;
    }

    @Override
    public String toString() {
        return tuyen.getTuyenID() + ";"
                + ga.getGaID() + ";"
                + thuTu + ";"
                + khoangCachTuGaXuatPhatKm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TuyenChiTiet that = (TuyenChiTiet) o;
        return Objects.equals(getTuyen(), that.getTuyen()) && Objects.equals(getGa(), that.getGa());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTuyen(), getGa());
    }
}
