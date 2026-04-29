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
    private String gaID;

    @Column(name = "tenGa", nullable = false)
    private String tenGa;

    @Column(name = "isGaLon", nullable = false)
    private boolean isGaLon;

    @Column(name = "tinhThanh")
    private String tinhThanh;

    public Ga(String gaID, String tenGa, boolean isGaLon, String tinhThanh) {
        super();
        this.gaID = gaID;
        this.tenGa = tenGa;
        this.tinhThanh = tinhThanh;
    }

    public Ga(String gaID, String tenGa) {
        super();
        this.gaID = gaID;
        this.tenGa = tenGa;
    }

    public Ga(String gaID) {
        super();
        this.gaID = gaID;
    }
}
