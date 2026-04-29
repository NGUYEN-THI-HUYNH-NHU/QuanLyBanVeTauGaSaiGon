package entity;

/*
 * @(#) Ve.java  1.0  [11:27:32 AM] Sep 18, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 18, 2025
 * @version: 1.0
 */

import entity.type.TrangThaiVe;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"chuyen", "gaDi", "gaDen", "ghe"})
@EqualsAndHashCode(exclude = {"chuyen", "gaDi", "gaDen", "ghe"})
@Entity
@Table(name = "Ve")
public class Ve implements Serializable {
    @Id
    @Column(name = "veID", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khachHangID", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donDatChoID", nullable = false)
    private DonDatCho donDatCho;

    // Ánh xạ tách biệt để tránh lỗi lặp cột chuyenID của JPA
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

    @Column(name = "ngayGioDi", nullable = false)
    private LocalDateTime ngayGioDi;

    @Column(name = "gia", nullable = false, precision = 12, scale = 2)
    private BigDecimal gia;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", length = 50)
    private TrangThaiVe trangThai;

    /**
     * @return
     */
    public String thongTinVeHoan() {
        return String.format("<html>%s %s<br/>Toa: %s; Chỗ: %s<br/>Mã vé: %s</html>", ghe.getToa().getTau().getTauID(),
                ngayGioDi, ghe.getToa().getSoToa(), ghe.getSoGhe(), veID);
    }

    /**
     * @return
     */
    public String thongTinVeDoi(PhieuDungPhongVIP phieuDungPhongChoVIP) {
        if (phieuDungPhongChoVIP == null) {
            return String.format("<html>%s %s<br/>Toa: %s; Chỗ: %s<br/>Mã vé: %s</html>",
                    ghe.getToa().getTau().getTauID(), ngayGioDi, ghe.getToa().getSoToa(), ghe.getSoGhe(), veID);
        }
        return String.format("<html>%s %s<br/>Toa: %s; Chỗ: %s<br/>Vé: %s<br/>Phiếu: %s</html>",
                ghe.getToa().getTau().getTauID(), ngayGioDi, ghe.getToa().getSoToa(), ghe.getSoGhe(), veID,
                phieuDungPhongChoVIP.getPhieuDungPhongChoVIPID());
    }

    /**
     * @return
     */
    public String stringThongTinChuyen() {
        return String.format("<html>%s [%s - %s]<br/>%s</html>", ghe.getToa().getTau().getTauID(), gaDi.getGaID(),
                gaDen.getGaID(), ngayGioDi.format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")));
    }

    /**
     * @return
     */
    public String stringThongTinGhe() {
        return String.format("<html>Toa: %s - Chỗ: %s</html>", ghe.getToa().getSoToa(), ghe.getSoGhe());
    }
}