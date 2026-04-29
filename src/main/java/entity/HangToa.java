/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/29/2026
 */

package entity;

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
@Table(name = "HangToa")
public class HangToa implements Serializable {
    @Id
    @Column(name = "hangToaID", length = 50)
    private String id;

    @Column(name = "moTa", nullable = false)
    private String moTa;
}