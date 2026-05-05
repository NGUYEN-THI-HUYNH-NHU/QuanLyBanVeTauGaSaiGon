package bus;/*
 * @ (#) Ga_BUS.java   1.0     30/09/2025
package bus;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 30/09/2025
 */

import dao.IKhoangCachChuanDAO;
import dao.ITuyenChiTietDAO;
import dao.impl.Ga_DAO;
import dao.impl.KhoangCachChuan_DAO;
import dao.impl.TuyenChiTiet_DAO;
import dto.GaDTO;
import entity.Ga;
import mapper.GaMapper;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Ga_BUS implements IGaBUS {
    private Ga_DAO ga_dao;
    private ITuyenChiTietDAO tuyenChiTietDao;
    private IKhoangCachChuanDAO khoangCachChuanDao;
    private Tuyen_BUS tuyenBus;

    public Ga_BUS() {
        this.ga_dao = new Ga_DAO();
        this.tuyenChiTietDao = new TuyenChiTiet_DAO();
        this.khoangCachChuanDao = new KhoangCachChuan_DAO();
    }

    @Override
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

    @Override
    public GaDTO getGaByTenGa(String tenGa) {
        Ga ga = ga_dao.getGaByTenGa(tenGa);
        return ga != null ? GaMapper.INSTANCE.toDTO(ga) : null;
    }

    /**
     * lấy danh sách tên tất cả ga để hiển thị lên combobox
     *
     * @return List<String> danh sách tên ga
     */
    @Override
    public List<String> getDanhSachTenGa() {
        List<Ga> dsGa = ga_dao.getAllGa();
        List<String> dsTenGa = new ArrayList<>();
        for (Ga ga : dsGa) {
            dsTenGa.add(ga.getTenGa());
        }
        return dsTenGa;
    }

    @Override
    public List<GaDTO> getAllGa() {
        return ga_dao.getAllGa().stream()
                .map(GaMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
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
