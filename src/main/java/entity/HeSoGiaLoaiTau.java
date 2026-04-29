package entity;
/*
 * @(#) HeSoGiaLoaiTau.java  1.0  [10:20:47 AM] Sep 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 21, 2025
 * @version: 1.0
 */

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "HeSoGiaLoaiTau")
public class HeSoGiaLoaiTau implements Serializable {
    @Id
    @Column(name = "hsgloaiTauID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loaiTauID", nullable = false)
    private LoaiTau loaiTau;

    @Column(name = "hsg", nullable = false, precision = 5, scale = 2)
    private BigDecimal hsg;

    @Column(name = "isCoHieuLuc")
    private Boolean isCoHieuLuc;
}