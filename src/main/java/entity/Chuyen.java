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
    private String chuyenID;

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
    private LocalDate ngayDen;
    @Transient
    private LocalTime gioDen;
    @Transient
    private int soChoDat;
    @Transient
    private int soChoTrong;
    @Transient
    private String tenChuyenHienThi;
    @Transient
    private String tenGaDiHienThi;
    @Transient
    private String tenGaDenHienThi;
    @Transient
    private Ga gaDi;
    @Transient
    private Ga gaDen;

    public Chuyen(String chuyenID, Tuyen tuyen, Tau tau, LocalDate ngayDi, LocalTime gioDi, LocalDate ngayDen,
                  LocalTime gioDen, int soChoDat, int soChoTrong) {
        super();
        this.chuyenID = chuyenID;
        this.tuyen = tuyen;
        this.tau = tau;
        this.ngayDi = ngayDi;
        this.gioDi = gioDi;
        this.ngayDen = ngayDen;
        this.gioDen = gioDen;
        this.soChoDat = soChoDat;
        this.soChoTrong = soChoTrong;
    }

    public Chuyen(String chuyenID, Tau tau, LocalDate ngayDi, LocalTime gioDi, LocalDate ngayDen, LocalTime gioDen) {
        super();
        this.chuyenID = chuyenID;
        this.tau = tau;
        this.ngayDi = ngayDi;
        this.gioDi = gioDi;
        this.ngayDen = ngayDen;
        this.gioDen = gioDen;
    }

    public Chuyen(String chuyenID, Tau tau, LocalDate ngayDi, LocalTime gioDi) {
        super();
        this.chuyenID = chuyenID;
        this.tau = tau;
        this.ngayDi = ngayDi;
        this.gioDi = gioDi;
    }

    public Chuyen(String chuyenID) {
        this.chuyenID = chuyenID;
    }

    public Chuyen(String chuyenID, Tau tau) {
        super();
        this.chuyenID = chuyenID;
        this.tau = tau;
    }
}