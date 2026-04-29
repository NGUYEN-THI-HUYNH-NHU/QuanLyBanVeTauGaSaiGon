package dto;
import lombok.*;
import java.io.Serializable;
import java.util.*;
import java.time.*;
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChuyenGaDTO implements Serializable {
    private ChuyenDTO chuyen;
    private GaDTO ga;
    private int thuTu;
    private LocalDate ngayDen;
    private LocalTime gioDen;
    private LocalDate ngayDi;
    private LocalTime gioDi;
    private String chuyen;
    private String ga;
}
