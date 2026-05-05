package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HeSoGiaHangToaDTO implements Serializable {
    private String id;
    private String hangToaID;
    private double hsg;
    private boolean coHieuLuc;
}
