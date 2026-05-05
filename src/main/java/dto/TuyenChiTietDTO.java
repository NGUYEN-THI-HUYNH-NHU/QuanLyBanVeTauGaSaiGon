package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TuyenChiTietDTO implements Serializable {
    private String tuyenID;
    private String gaID;
    private String tenGa;
    private Integer thuTu;
    private Integer khoangCachTuGaXuatPhatKm;
}
