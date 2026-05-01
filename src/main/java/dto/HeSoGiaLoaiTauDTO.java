package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HeSoGiaLoaiTauDTO implements Serializable {
    private String id;
    private String loaiTauID;
    private double hsg;
    private boolean isCoHieuLuc;
}
