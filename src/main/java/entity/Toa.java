package entity;

/*
 * @(#) Toa.java  1.0  [10:14:42 PM] Sep 17, 2025
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
@ToString(exclude = "ghes")
@EqualsAndHashCode(exclude = "ghes")
@Entity
@Table(name = "Toa")
public class Toa implements Serializable {
    @Id
    @Column(name = "toaID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tauID", nullable = false)
    private Tau tau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hangToaID", nullable = false)
    private HangToa hangToa;

    @Column(name = "sucChua", nullable = false)
    private int sucChua;

    @Column(name = "soToa", nullable = false)
    private int soToa;

    @OneToMany(mappedBy = "toa", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Ghe> ghes;
}