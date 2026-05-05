package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GaDTO implements Serializable {
    private String id;
    private String tenGa;
    private boolean gaLon;
    private String tinhThanh;
}
