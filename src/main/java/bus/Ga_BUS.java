package bus;/*
 * @ (#) Ga_BUS.java   1.0     30/09/2025
package bus;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 30/09/2025
 */

import dao.impl.Ga_DAO;
import dao.impl.KhoangCachChuan_DAO;
import dao.impl.TuyenChiTiet_DAO;
import entity.Ga;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Ga_BUS {
    private final Ga_DAO ga_dao;
    private TuyenChiTiet_DAO tuyenChiTietDao;
    private KhoangCachChuan_DAO khoangCachChuanDao;
    private Tuyen_BUS tuyenBus;

    public Ga_BUS() {
        ga_dao = new Ga_DAO();
        tuyenChiTietDao = new TuyenChiTiet_DAO();
        khoangCachChuanDao = new KhoangCachChuan_DAO();
        tuyenBus = new Tuyen_BUS();
    }

    public List<String> timTenGaChoGoiY(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Ga> dsGa = ga_dao.getGaByTenGaList(input.trim());
        List<String> tenGaList = new ArrayList<>();

        for (Ga ga : dsGa) {
            tenGaList.add(ga.getTenGa());
        }
        return tenGaList;
    }

    public Ga getGaByTenGa(String tenGa) {
        return ga_dao.getGaByTenGa(tenGa);
    }

    /**
     * lấy danh sách tên tất cả ga để hiển thị lên combobox
     *
     * @return List<String> danh sách tên ga
     */
    public List<String> getDanhSachTenGa() {
        List<Ga> dsGa = ga_dao.getAllGa();
        List<String> dsTenGa = new ArrayList<>();
        for (Ga ga : dsGa) {
            dsTenGa.add(ga.getTenGa());
        }
        return dsTenGa;
    }

    public List<Ga> getAllGa() {
        return ga_dao.getAllGa();
    }

    /**
     * Loại bỏ dấu tiếng việt
     *
     * @param input Chuỗi cần loại bỏ dấu
     * @return Chuỗi đã loại bỏ dấu
     */
    private String removeAccents(String input) {
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
    private String taoMaRutGon(String tenGa) {
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

    public List<Object[]> getAllGaSortedByKhoangCachChuan() {
        final String START_GA_ID = "SGO";

        Map<String, Map<String, Integer>> graph = tuyenBus.getGraphKhoangCachChuan();
        Map<String, Ga> gaMap = ga_dao.getAllGa().stream()
                .collect(Collectors.toMap(Ga::getGaID, Function.identity()));

        List<Ga> sortedGaList = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        //Khởi tạo BFS từ Ga Sài Gòn
        if (gaMap.containsKey(START_GA_ID) && graph.containsKey(START_GA_ID)) {
            queue.add(START_GA_ID);
            visited.add(START_GA_ID);
        }

        while (!queue.isEmpty()) {
            String IDHienTai = queue.poll();
            Ga gaHienTai = gaMap.get(IDHienTai);

            if (gaHienTai != null) {
                sortedGaList.add(gaHienTai);
            }

            Map<String, Integer> lienKe = graph.getOrDefault(IDHienTai, Collections.emptyMap());

            lienKe.keySet().stream().sorted().forEach(lienKeID -> {
                if (!visited.contains(lienKeID)) {
                    visited.add(lienKeID);
                    queue.offer(lienKeID);
                }
            });
        }

        gaMap.values().stream()
                .filter(ga -> !visited.contains(ga.getGaID()))
                .sorted(Comparator.comparing(Ga::getTenGa))
                .forEach(sortedGaList::add);

        //Chuyển đổi sang Object
        List<Object[]> dsGaBang = new ArrayList<>();
        for (int i = 0; i < sortedGaList.size(); i++) {
            Ga gaHienTai = sortedGaList.get(i);


            if (i < sortedGaList.size() - 1) {
                Ga gaSau = sortedGaList.get(i + 1);

            }

            Object[] row = new Object[]{
                    gaHienTai.getTenGa(),
                    gaHienTai.getGaID(),
                    gaHienTai.getTinhThanh()
            };
            dsGaBang.add(row);
        }
        return dsGaBang;
    }

}
