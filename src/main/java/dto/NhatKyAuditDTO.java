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
public class NhatKyAuditDTO implements Serializable {
    private String nhatKyAuditID;
    private String doiTuongID;
    private String nhanVienID;
    private LocalDateTime thoiDiemThaoTac;
    private String chiTiet;
    private String doiTuongThaoTac;
}
