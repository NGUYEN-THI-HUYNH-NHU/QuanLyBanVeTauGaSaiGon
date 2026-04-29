package entity;

/*
 * @(#) DichVuPhongChoVIP.java  1.0  [1:51:58 PM] Sep 19, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 19, 2025
 * @version: 1.0
 */

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "DichVuPhongChoVIP")
public class DichVuPhongChoVIP implements Serializable {
    @Id
    @Column(name = "dichVuPhongChoVIPID", length = 50)
    private String id;

    @Column(name = "gia", nullable = false, precision = 8, scale = 2)
    private BigDecimal gia;

    @Column(name = "moTa", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "ngayCoHieuLuc", nullable = false)
    private LocalDate ngayCoHieuLuc;

    @Column(name = "ngayHetHieuLuc", nullable = false)
    private LocalDate ngayHetHieuLuc;

    @Column(name = "trangThai", nullable = false)
    private boolean trangThai;
}