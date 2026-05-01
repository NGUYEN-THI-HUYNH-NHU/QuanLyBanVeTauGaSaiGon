package dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDungPhongVIPDTO implements Serializable {
    private String id;
    private String dichVuPhongChoVIPID;
    private String veID;
    private String trangThai;
}
