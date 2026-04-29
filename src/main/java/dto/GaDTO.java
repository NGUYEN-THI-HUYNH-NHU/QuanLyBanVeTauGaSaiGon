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
public class GaDTO implements Serializable {
    private String id;
    private String tenGa;
    private boolean isGaLon;
    private String tinhThanh;
}
