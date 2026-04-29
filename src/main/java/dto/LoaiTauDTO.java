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
public class LoaiTauDTO implements Serializable {
    private String id;
    private String moTa;
    private Set<TauDTO> taus;
}
