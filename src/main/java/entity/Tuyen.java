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
    private String id;

    @Column(name = "moTa", nullable = false)
    private String moTa;

    @Column(name = "trangThai", nullable = false)
    private boolean trangThai;

    @OneToMany(mappedBy = "tuyen", fetch = FetchType.LAZY)
    private Set<TuyenChiTiet> tuyenChiTiets;

    @OneToMany(mappedBy = "tuyen", fetch = FetchType.LAZY)
    private Set<Chuyen> chuyens;
}