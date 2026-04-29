package entity;
/*
 * @(#) ChuyenGa.java  1.0  [3:17:46PM] Sep 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@IdClass(ChuyenGa.ChuyenGaID.class)
@Table(name = "ChuyenGa")
public class ChuyenGa implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chuyenID")
    private Chuyen chuyen;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaID")
    private Ga ga;

    @Column(name = "thuTu", nullable = false)
    private int thuTu;

    @Column(name = "ngayDen")
    private LocalDate ngayDen;

    @Column(name = "gioDen")
    private LocalTime gioDen;

    @Column(name = "ngayDi")
    private LocalDate ngayDi;

    @Column(name = "gioDi")
    private LocalTime gioDi;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class ChuyenGaID implements Serializable {
        private String chuyen;
        private String ga;
    }
}