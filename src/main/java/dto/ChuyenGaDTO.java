package dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChuyenGaDTO implements Serializable {
    private String id;
    private String gaID;
    private Integer thuTu;
    private LocalDate ngayDen;
    private LocalTime gioDen;
    private LocalDate ngayDi;
    private LocalTime gioDi;
}
