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
@EqualsAndHashCode
@Entity
@Table(name = "HangToa")
public class HangToa implements Serializable {
    @Id
    @Column(name = "hangToaID", length = 50)
    private String hangToaID;

    @Column(name = "moTa", nullable = false)
    private String moTa;

    public HangToa(String hangToaStr) {
        this.hangToaID = hangToaStr;
    }

    public String getDescription() {
        return this.moTa;
    }

    @Override
    public String toString() {
        return this.hangToaID;
    }
}