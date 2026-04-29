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
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toaID", nullable = false)
    private Toa toa;

    @Column(name = "soGhe", nullable = false)
    private int soGhe;

    @Transient
    private TrangThaiGhe trangThai;
}