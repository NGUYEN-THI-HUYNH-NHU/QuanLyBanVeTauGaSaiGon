package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoaiTauDTO implements Serializable {
    private String id;
    private String moTa;

    public LoaiTauDTO(String id) {
        this.id = id;
    }
}
