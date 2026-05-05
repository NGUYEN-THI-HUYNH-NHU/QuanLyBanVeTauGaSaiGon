package entity;
/*
 * @(#) Tau.java  1.0  [10:01:01 PM] Sep 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */


/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 17, 2025
 * @version: 1.0
 */

import entity.type.TrangThaiTau;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "toas")
@EqualsAndHashCode(exclude = "toas")
@Entity
@Table(name = "Tau")
public class Tau implements Serializable {
    @Id
    @Column(name = "tauID", length = 50)
    private String tauID;

    @Column(name = "tenTau", nullable = false)
    private String tenTau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiTauID", nullable = false)
    private LoaiTau loaiTau;

    @Column(name = "soLuongToa", nullable = false)
    private Integer soLuongToa;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 50)
    private TrangThaiTau trangThai;

    @Column(name = "vanTocTB", nullable = false)
    private Integer vanTocTB;

    @OneToMany(mappedBy = "tau", fetch = FetchType.LAZY)
    private Set<Toa> toas;

    public Tau(String tauID, String tenTau, LoaiTau loaiTau, Integer soLuongToa, TrangThaiTau trangThai) {
        super();
        this.tauID = tauID;
        this.tenTau = tenTau;
        this.loaiTau = loaiTau;
        this.soLuongToa = soLuongToa;
        this.trangThai = trangThai;
    }

    public Tau(String tauID, String tenTau, LoaiTau loaiTau, Integer soLuongToa, TrangThaiTau trangThai, Integer vanTocTB) {
        this.tauID = tauID;
        this.tenTau = tenTau;
        this.loaiTau = loaiTau;
        this.soLuongToa = soLuongToa;
        this.trangThai = trangThai;
        this.vanTocTB = vanTocTB;
    }

    public Tau(String tauID) {
        super();
        this.tauID = tauID;
    }

    public Tau(String tauID, LoaiTau loaiTau) {
        super();
        this.tauID = tauID;
        this.loaiTau = loaiTau;
    }

    public Tau(String tauID, String tenTau) {
        super();
        this.tauID = tauID;
        this.tenTau = tenTau;
    }
}