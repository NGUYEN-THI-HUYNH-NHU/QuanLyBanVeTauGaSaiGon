/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/29/2026
 */

package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "VaiTroNhanVien")
public class VaiTroNhanVien implements Serializable {
    @Id
    @Column(name = "vaiTroNhanVienID", length = 50)
    private String vaiTroNhanVienID;

    @Column(name = "moTa", nullable = false)
    private String moTa;

    public VaiTroNhanVien(String vaiTroNhanVienID) {
        this.vaiTroNhanVienID = vaiTroNhanVienID;
    }

    public String getDescription() {
        return this.moTa;
    }
}