package dto;

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
    private String toaID;
    private Integer soGhe;
    private String trangThai;
}
