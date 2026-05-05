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
@Table(name = "LoaiKhachHang")
public class LoaiKhachHang implements Serializable {
    @Id
    @Column(name = "loaiKhachHangID", length = 50)
    private String loaiKhachHangID;

    @Column(name = "moTa", nullable = false)
    private String moTa;

    public String getDescription() {
        return this.moTa;
    }
}
