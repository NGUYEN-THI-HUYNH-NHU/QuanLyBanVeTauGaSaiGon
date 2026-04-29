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

import java.util.Objects;
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
    private int soLuongToa;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 50)
    private TrangThaiTau trangThai;

    @Column(name = "vanTocTB", nullable = false)
    private int vanTocTB;

    @OneToMany(mappedBy = "tau", fetch = FetchType.LAZY)
    private Set<Toa> toas;
}