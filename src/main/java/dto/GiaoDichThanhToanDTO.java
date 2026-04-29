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
public class GiaoDichThanhToanDTO implements Serializable {
    private double tienNhan;
    private double tienHoan;
    private String maGD;
    private double tongTien;
    private boolean isThanhToanTienMat;
    private boolean trangThai;
}
