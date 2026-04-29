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
    private String chuyenID;
    private String gaID;
    private int thuTu;
    private LocalDate ngayDen;
    private LocalTime gioDen;
    private LocalDate ngayDi;
    private LocalTime gioDi;
}
