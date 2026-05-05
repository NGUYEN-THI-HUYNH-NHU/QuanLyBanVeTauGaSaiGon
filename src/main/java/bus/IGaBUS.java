package bus;/*
 * @ (#) IGaBUS.java   1.0     05/05/2026
package bus;



/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 05/05/2026
 */

import dto.GaDTO;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public interface IGaBUS {
    List<String> timTenGaChoGoiY(String input);

    GaDTO getGaByTenGa(String tenGa);

    List<String> getDanhSachTenGa();

    List<GaDTO> getAllGa();

    /**
     * Loại bỏ dấu tiếng việt
     *
     * @param input Chuỗi cần loại bỏ dấu
     * @return Chuỗi đã loại bỏ dấu
     */
    default String removeAccents(String input) {
        if (input == null) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        withoutAccents = withoutAccents.replace("[^a-zA-Z\\s]", "");
        return withoutAccents.toUpperCase(Locale.ROOT);
    }

    /**
     * Hàm tạo mã rút gọn 3 ký tự
     *
     * @param tenGa Tên Ga đầy đủ
     * @return Mã rút gọn 3 ký tự
     */
    default String taoMaRutGon(String tenGa) {
        String normalizedName = removeAccents(tenGa);
        String[] words = normalizedName.split("\\s+");
        List<String> validWords = Arrays.stream(words).filter(word -> !word.isEmpty()).collect(Collectors.toList());

        int numWords = validWords.size();
        StringBuilder ma = new StringBuilder();

        if (numWords == 0) {
            return "";
        }

        if (numWords == 1) {
            ma.append(validWords.get(0).charAt(0));
            ma.append("XX");
        } else if (numWords == 2) {
            ma.append(validWords.get(0).charAt(0));
            ma.append(validWords.get(0).charAt(1));
            ma.append(validWords.get(1).charAt(0));
        } else if (numWords == 3) {
            ma.append(validWords.get(0).charAt(0));
            ma.append(validWords.get(1).charAt(0));
            ma.append(validWords.get(2).charAt(0));
        } else {
            // Nhiều hơn 3 từ, lấy ký tự đầu tiên của từ đầu tiên, ký tự đầu tiên của từ thứ hai và ký tự đầu tiên của từ cuối cùng
            ma.append(validWords.get(0).charAt(0));
            ma.append(validWords.get(1).charAt(0));
            ma.append(validWords.get(2).charAt(0));
        }
        return ma.toString();
    }

    List<Object[]> getAllGaSortedByKhoangCachChuan();
}
