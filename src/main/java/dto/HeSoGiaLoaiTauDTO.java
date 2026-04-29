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
public class HeSoGiaLoaiTauDTO implements Serializable {
    private String id;
    private LoaiTauDTO loaiTau;
    private double hsg;
    private Boolean isCoHieuLuc;
}
