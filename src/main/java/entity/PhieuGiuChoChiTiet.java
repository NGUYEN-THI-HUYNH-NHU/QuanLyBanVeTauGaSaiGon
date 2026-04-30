package entity;/*
				* @ (#) PhieuGiuChoChiTiet.java   1.0     02/10/2025
				package entity;
				
				
				/**
				* @description :
				* @author : Vy, Pham Kha Vy
				* @version 1.0
				* @created : 02/10/2025
				*/

import entity.type.TrangThaiPhieuGiuCho;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "PhieuGiuChoChiTiet")
public class PhieuGiuChoChiTiet implements Serializable {
    @Id
    @Column(name = "phieuGiuChoChiTietID", length = 50)
    private String phieuGiuChoChiTietID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieuGiuChoID", nullable = false)
    private PhieuGiuCho phieuGiuCho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chuyenID", nullable = false)
    private Chuyen chuyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaDiID", nullable = false)
    private Ga gaDi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaDenID", nullable = false)
    private Ga gaDen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gheID", nullable = false)
    private Ghe ghe;

    @Column(name = "thoiDiemGiuCho", nullable = false)
    private LocalDateTime thoiDiemGiuCho;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 50)
    private TrangThaiPhieuGiuCho trangThai;

    public PhieuGiuChoChiTiet(String pgcctID, PhieuGiuCho pgc, Chuyen chuyen, Ghe ghe, Ga gaDi, Ga gaDen, LocalDateTime thoiDiemGiuCho, String trangThai) {
        this.phieuGiuChoChiTietID = pgcctID;
        this.phieuGiuCho = pgc;
        this.chuyen = chuyen;
        this.ghe = ghe;
        this.gaDi = gaDi;
        this.gaDen = gaDen;
        this.thoiDiemGiuCho = thoiDiemGiuCho;
        this.trangThai = TrangThaiPhieuGiuCho.valueOf(trangThai);
    }
}