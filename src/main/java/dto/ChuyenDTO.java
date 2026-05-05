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
public class ChuyenDTO implements Serializable {
    private String id;
    private String tuyenID;
    private String tauID;
    private String loaiTauID;
    private LocalDate ngayDi;
    private LocalTime gioDi;
    private LocalDate ngayDen;
    private LocalTime gioDen;
    private Integer soChoDat;
    private Integer soChoTrong;
}
