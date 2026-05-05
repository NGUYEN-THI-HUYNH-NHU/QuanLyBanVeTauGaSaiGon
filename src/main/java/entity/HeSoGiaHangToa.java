package entity;
/*
 * @(#) HeSoGiaHangToa.java  1.0  [6:22:46 PM] Sep 21, 2025
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "HeSoGiaHangToa")
public class HeSoGiaHangToa implements Serializable {
    @Id
    @Column(name = "hsgHangToaID", length = 50)
    private String hsgHangToaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hangToaID", nullable = false)
    private HangToa hangToa;

    @Column(name = "hsg", nullable = false, precision = 5, scale = 2)
    private Double hsg;

    @Column(name = "isCoHieuLuc")
    private boolean coHieuLuc;
}