package entity;
/*
 * @(#) Chuyen.java  1.0  [10:09:55 PM] Sep 17, 2025
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "chuyenGas")
@Entity
@Table(name = "Chuyen")
public class Chuyen implements Serializable {
    @Id
    @Column(name = "chuyenID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuyenID", nullable = false)
    private Tuyen tuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tauID", nullable = false)
    private Tau tau;

    @Column(name = "ngayDi", nullable = false)
    private LocalDate ngayDi;

    @Column(name = "gioDi")
    private LocalTime gioDi;

    @OneToMany(mappedBy = "chuyen", fetch = FetchType.LAZY)
    private Set<ChuyenGa> chuyenGas;

    @Transient
    private LocalTime gioDen;
    @Transient
    private LocalDate ngayDen;
    @Transient
    private String tenChuyenHienThi;
    @Transient
    private String tenGaDiHienThi;
    @Transient
    private String tenGaDenHienThi;

    public Chuyen(String id) {
        this.id = id;
    }
}