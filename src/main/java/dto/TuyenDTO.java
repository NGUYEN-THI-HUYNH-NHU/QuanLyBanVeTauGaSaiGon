package dto;

import lombok.*;

import java.io.Serializable;

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
}
