package entity;
/*
 * @(#) CaLam.java  1.0  [10:55:00 PM] Dec 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "CaLam")
public class CaLam implements Serializable {
    @Id
    @Column(name = "caLamID", length = 50)
    private String caLamID;

    @Column(name = "gioVaoCa", nullable = false)
    private LocalTime gioVaoCa;

    @Column(name = "gioKetCa", nullable = false)
    private LocalTime gioKetCa;

    public CaLam(String caLamID) {
        this.caLamID = caLamID;
    }
}
