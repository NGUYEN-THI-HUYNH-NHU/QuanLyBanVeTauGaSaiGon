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
@Table(name = "LoaiDoiTuong")
public class LoaiDoiTuong implements Serializable {
    @Id
    @Column(name = "loaiDoiTuongID", length = 50)
    private String id;

    @Column(name = "moTa", nullable = false)
    private String moTa;
}