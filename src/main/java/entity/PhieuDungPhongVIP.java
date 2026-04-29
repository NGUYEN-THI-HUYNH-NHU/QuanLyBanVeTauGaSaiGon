package entity;

import entity.type.TrangThaiPDPVIP;
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
@Table(name = "PhieuDungPhongVIP")
public class PhieuDungPhongVIP implements Serializable {
    @Id
    @Column(name = "phieuDungPhongVIPID", length = 50)
    private String phieuDungPhongVIPID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dichVuPhongChoVIPID", nullable = false)
    private DichVuPhongChoVIP dichVuPhongChoVIP;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veID", nullable = false, unique = true)
    private Ve ve;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai", nullable = false, length = 100)
    private TrangThaiPDPVIP trangThai;

    public PhieuDungPhongVIP(String phieuDungPhongVIPID) {
        super();
        this.phieuDungPhongVIPID = phieuDungPhongVIPID;
    }
}