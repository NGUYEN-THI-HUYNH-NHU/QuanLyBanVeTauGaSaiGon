package entity;/*
 * @ (#) KhoangCachChuan.java   1.0     28/10/2025
package entity;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 28/10/2025
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
@IdClass(KhoangCachChuan.KhoangCachChuanID.class)
@Table(name = "KhoangCachChuan")
public class KhoangCachChuan implements Serializable {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaID_Dau", nullable = false)
    private Ga gaDau;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gaID_Cuoi", nullable = false)
    private Ga gaCuoi;

    @Column(name = "khoangCachKm", nullable = false)
    private int khoangCachKm;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class KhoangCachChuanID implements Serializable {
        private String gaDau;
        private String gaCuoi;
    }
}
