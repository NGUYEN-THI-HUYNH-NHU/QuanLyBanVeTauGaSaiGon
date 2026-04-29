/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/29/2026
 */

package mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;
import java.util.stream.Collectors;

public class Mapper {

    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) return null;
        return mapper.convertValue(source, targetClass);
    }

    public static <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(source -> map(source, targetClass))
                .collect(Collectors.toList());
    }
}