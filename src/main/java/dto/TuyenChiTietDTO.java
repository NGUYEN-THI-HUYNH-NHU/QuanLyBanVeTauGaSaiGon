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
    private TuyenDTO tuyen;
    private GaDTO ga;
    private int thuTu;
    private int khoangCachTuGaXuatPhatKm;
    private String tuyenID;
    private String gaID;
}
