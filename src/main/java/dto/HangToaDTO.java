package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HangToaDTO implements Serializable {
    private String id;
    private String moTa;

    public HangToaDTO(String id) {
        this.id = id;
    }
}
