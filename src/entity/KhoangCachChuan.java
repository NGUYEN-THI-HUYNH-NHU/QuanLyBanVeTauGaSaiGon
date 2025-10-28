package entity;/*
 * @ (#) KhoangCachChuan.java   1.0     28/10/2025
package entity;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 28/10/2025
 */

import java.util.Objects;

public class KhoangCachChuan {
    private String gaID_Dau;
    private String gaID_Cuoi;
    private int khoangCachKm;

    public KhoangCachChuan(String gaID_Dau, String gaID_Cuoi, int khoangCachKm) {
        this.gaID_Dau = gaID_Dau;
        this.gaID_Cuoi = gaID_Cuoi;
        this.khoangCachKm = khoangCachKm;
    }

    public String getGaID_Dau() {
        return gaID_Dau;
    }

    public void setGaID_Dau(String gaID_Dau) {
        this.gaID_Dau = gaID_Dau;
    }

    public String getGaID_Cuoi() {
        return gaID_Cuoi;
    }

    public void setGaID_Cuoi(String gaID_Cuoi) {
        this.gaID_Cuoi = gaID_Cuoi;
    }

    public int getKhoangCachKm() {
        return khoangCachKm;
    }

    public void setKhoangCachKm(int khoangCachKm) {
        this.khoangCachKm = khoangCachKm;
    }

    @Override
    public String toString() {
        return gaID_Dau + ";" + gaID_Cuoi + ";" + khoangCachKm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhoangCachChuan that = (KhoangCachChuan) o;
        return Objects.equals(getGaID_Dau(), that.getGaID_Dau()) && Objects.equals(getGaID_Cuoi(), that.getGaID_Cuoi());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGaID_Dau(), getGaID_Cuoi());
    }
}
