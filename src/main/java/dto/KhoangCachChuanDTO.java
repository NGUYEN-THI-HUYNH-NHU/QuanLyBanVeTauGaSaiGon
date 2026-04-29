package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class KhoangCachChuanDTO implements Serializable {
    private String gaDauID;
    private String tenGaDau;
    private String gaCuoiID;
    private String tenGaCuoi;
    private int khoangCachKm;

}
