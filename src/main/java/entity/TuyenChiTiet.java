package entity;/*
 * @ (#) TuyenChiTiet.java   1.0     02/10/2025
package entity;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 02/10/2025
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
@Entity
@IdClass(TuyenChiTiet.TuyenChiTietID.class)
@Table(name = "TuyenChiTiet")
public class TuyenChiTiet implements Serializable {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tuyenID")
    private Tuyen tuyen;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaID")
    private Ga ga;

    @Column(name = "thuTu", nullable = false)
    private int thuTu;

    @Column(name = "khoangCachTuGaXuatPhatKm", nullable = false)
    private int khoangCachTuGaXuatPhatKm;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class TuyenChiTietID implements Serializable {
        private String tuyen;
        private String ga;
    }
}
