package entity;

/*
 * @(#) Cho.java  1.0  [11:21:20 AM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

import entity.type.TrangThaiGhe;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "Ghe")
public class Ghe implements Serializable {
    @Id
    @Column(name = "gheID", length = 50)
    private String gheID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toaID", nullable = false)
    private Toa toa;

    @Column(name = "soGhe", nullable = false)
    private Integer soGhe;

    @Transient
    private TrangThaiGhe trangThai;

    public Ghe(String gheID, Integer soGhe) {
        super();
        this.gheID = gheID;
        this.soGhe = soGhe;
    }

    public Ghe(String gheID) {
        super();
        this.gheID = gheID;
    }

    public Ghe(String gheID, Toa toa, Integer soGhe) {
        super();
        this.gheID = gheID;
        this.toa = toa;
        this.soGhe = soGhe;
    }
}