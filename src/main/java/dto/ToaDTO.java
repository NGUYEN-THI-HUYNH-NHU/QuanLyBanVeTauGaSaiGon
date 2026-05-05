package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ToaDTO implements Serializable {
    private String id;
    private String tauID;
    private String loaiTauID;
    private String hangToaID;
    private String moTa;
    private Integer sucChua;
    private Integer soToa;
}
