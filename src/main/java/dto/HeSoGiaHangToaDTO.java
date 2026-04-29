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
public class HeSoGiaHangToaDTO implements Serializable {
    private String id;
    private HangToaDTO hangToa;
    private double hsg;
    private Boolean isCoHieuLuc;
}
