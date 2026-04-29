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
public class ToaDTO implements Serializable {
    private String id;
    private TauDTO tau;
    private HangToaDTO hangToa;
    private int sucChua;
    private int soToa;
    private Set<GheDTO> ghes;
}
