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
@Table(name = "VaiTroTaiKhoan")
public class VaiTroTaiKhoan implements Serializable {
    @Id
    @Column(name = "vaiTroTaiKhoanID", length = 50)
    private String id;

    @Column(name = "moTa", nullable = false)
    private String moTa;
}