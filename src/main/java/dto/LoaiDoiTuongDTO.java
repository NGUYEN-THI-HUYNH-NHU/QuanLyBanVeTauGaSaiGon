package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoaiDoiTuongDTO implements Serializable {
    private String id;
    private String moTa;
}
