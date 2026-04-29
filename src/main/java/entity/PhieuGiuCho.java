package entity;
/*
 * @(#) PhieuGiuCho_DAO.java  1.0  [2:50:00 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

import entity.type.TrangThaiPhieuGiuCho;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "chiTiets")
@EqualsAndHashCode(exclude = "chiTiets")
@Entity
@Table(name = "PhieuGiuCho")
public class PhieuGiuCho implements Serializable {
    @Id
    @Column(name = "phieuGiuChoID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhanVienID", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "thoiDiemTao", nullable = false)
    private LocalDateTime thoiDiemTao;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 50)
    private TrangThaiPhieuGiuCho trangThai;

    @OneToMany(mappedBy = "phieuGiuCho", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<PhieuGiuChoChiTiet> chiTiets;
}