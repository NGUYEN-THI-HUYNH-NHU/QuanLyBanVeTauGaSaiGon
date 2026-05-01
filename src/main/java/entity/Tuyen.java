package entity;
/*
 * @(#) Tuyen.java  1.0  [10:06:00PM] Sep 17, 2025
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
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"tuyenChiTiets", "chuyens"})
@Entity
@Table(name = "Tuyen")
public class Tuyen implements Serializable {
    @Id
    @Column(name = "tuyenID", length = 50)
    private String tuyenID;

    @Column(name = "moTa", nullable = false)
    private String moTa;

    @Column(name = "trangThai", nullable = false)
    private Boolean trangThai;

    @OneToMany(mappedBy = "tuyen", fetch = FetchType.LAZY)
    private Set<TuyenChiTiet> tuyenChiTiets;

    @OneToMany(mappedBy = "tuyen", fetch = FetchType.LAZY)
    private Set<Chuyen> chuyens;

    public Tuyen(String tuyenID, String moTa) {
        this.tuyenID = tuyenID;
        this.moTa = moTa;
    }

    public Tuyen(String tuyenID, String moTa, boolean trangThai) {
        this.tuyenID = tuyenID;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public Tuyen(String tuyenID) {
        super();
        this.tuyenID = tuyenID;
    }
}