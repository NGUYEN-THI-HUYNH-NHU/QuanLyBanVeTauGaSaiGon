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
public class ChuyenDTO implements Serializable {
    private String id;
    private TuyenDTO tuyen;
    private TauDTO tau;
    private LocalDate ngayDi;
    private LocalTime gioDi;
    private Set<ChuyenGaDTO> chuyenGas;
}
