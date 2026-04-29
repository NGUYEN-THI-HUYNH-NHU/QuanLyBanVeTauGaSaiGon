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
public class HangToaDTO implements Serializable {
    private String id;
    private String moTa;
}
