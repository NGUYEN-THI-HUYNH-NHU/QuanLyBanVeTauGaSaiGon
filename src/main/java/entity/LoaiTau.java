/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/29/2026
 */

package entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = "taus")
@Entity
@Table(name = "LoaiTau")
public class LoaiTau implements Serializable {
    @Id
    @Column(name = "loaiTauID", length = 50)
    private String loaiTauID;

    @Column(name = "moTa", nullable = false)
    private String moTa;

    @OneToMany(mappedBy = "loaiTau", fetch = FetchType.LAZY)
    private Set<Tau> taus;

    public LoaiTau(String loaiTauStr) {
        this.loaiTauID = loaiTauStr;
    }

    public String getDescription() {
        return this.moTa;
    }

    @Override
    public String toString() {
        return this.loaiTauID;
    }
}