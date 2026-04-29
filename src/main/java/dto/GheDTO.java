package dto;

import entity.type.TrangThaiGhe;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GheDTO implements Serializable {
    private String id;
    private ToaDTO toa;
    private int soGhe;
    private TrangThaiGhe trangThai;
}
