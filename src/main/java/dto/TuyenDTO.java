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
public class TuyenDTO implements Serializable {
    private String id;
    private String moTa;
    private boolean trangThai;
    private Set<TuyenChiTietDTO> tuyenChiTiets;
    private Set<ChuyenDTO> chuyens;
}
