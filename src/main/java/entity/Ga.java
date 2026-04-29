package entity;
/*
 * @(#) Ga.java  1.0  [10:00:22PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "Ga")
public class Ga implements Serializable {
    @Id
    @Column(name = "gaID", length = 50)
    @EqualsAndHashCode.Include
    private String id;

    @Column(name = "tenGa", nullable = false)
    private String tenGa;

    @Column(name = "isGaLon", nullable = false)
    private boolean isGaLon;

    @Column(name = "tinhThanh")
    private String tinhThanh;

    public Ga(String id, String tenGa, boolean isGaLon, String tinhThanh) {
        super();
        this.id = id;
        this.tenGa = tenGa;
        this.tinhThanh = tinhThanh;
    }

    public Ga(String id, String tenGa) {
        super();
        this.id = id;
        this.tenGa = tenGa;
    }

    public Ga(String id) {
        super();
        this.id = id;
    }

    public String getGaID() {
        return id;
    }

    public void setGaID(String id) {
        if (id != null && !id.isEmpty()) {
            this.id = id;
        } else {
            throw new IllegalArgumentException("Ga ID không được để trống!");
        }
    }

    public boolean isGaLon() {
        return isGaLon;
    }

    public void setGaLon(boolean gaLon) {
        isGaLon = gaLon;
    }

    public String getTenGa() {
        return tenGa;
    }

    public void setTenGa(String tenGa) {
        if (tenGa != null && !tenGa.isEmpty()) {
            this.tenGa = tenGa;
        } else {
            throw new IllegalArgumentException("Tên ga không được để trống!");
        }
    }

    public String getTinhThanh() {
        return tinhThanh;
    }

    public void setTinhThanh(String tinhThanh) {
        if (tinhThanh != null && !tinhThanh.isEmpty()) {
            this.tinhThanh = tinhThanh;
        } else {
            throw new IllegalArgumentException("Tỉnh thành không được để trống!");
        }
    }

    @Override
    public String toString() {
        return id + ";" + tenGa + ";" + tinhThanh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Ga ga = (Ga) o;
        return Objects.equals(id, ga.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
