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
public class DichVuPhongChoVIPDTO implements Serializable {
    private String id;
    private double gia;
    private String moTa;
    private LocalDate ngayCoHieuLuc;
    private LocalDate ngayHetHieuLuc;
    private boolean trangThai;
}
