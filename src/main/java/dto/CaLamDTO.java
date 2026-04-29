package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CaLamDTO implements Serializable {
    private String id;
    private LocalTime gioVaoCa;
    private LocalTime gioKetCa;
}
