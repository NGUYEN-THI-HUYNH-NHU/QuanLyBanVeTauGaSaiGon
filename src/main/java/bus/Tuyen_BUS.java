package bus;/*
			* @ (#) Tuyen_BUS.java   1.0     30/09/2025


			/**
			* @description :
			* @author : Vy, Pham Kha Vy
			* @version 1.0
			* @created : 30/09/2025
			*/

import dao.IGaDAO;
import dao.IKhoangCachChuanDAO;
import dao.ITuyenChiTietDAO;
import dao.ITuyenDAO;
import dao.impl.Ga_DAO;
import dao.impl.KhoangCachChuan_DAO;
import dao.impl.TuyenChiTiet_DAO;
import dao.impl.Tuyen_DAO;
import dto.NhanVienDTO;
import dto.TuyenChiTietDTO;
import dto.TuyenDTO;
import entity.*;
import mapper.TuyenChiTietMapper;
import mapper.TuyenMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Tuyen_BUS implements ITuyenBUS {
    private final ITuyenDAO tuyen_dao;
    private final IGaDAO ga_dao;
    private final ITuyenChiTietDAO tuyenChiTietDao;
    private final IKhoangCachChuanDAO khoangCachChuanDao;
    private final Map<String, Map<String, Integer>> graphKhoangCachChuan;
    private final NhatKyAudit_BUS nhatKyAuditBus;

    public Tuyen_BUS() {
        tuyen_dao = new Tuyen_DAO();
        ga_dao = new Ga_DAO();
        tuyenChiTietDao = new TuyenChiTiet_DAO();
        khoangCachChuanDao = new KhoangCachChuan_DAO();
        nhatKyAuditBus = new NhatKyAudit_BUS();

        // Tải đồ thị khoảng cách vào bộ nhớ khi BUS khởi động
        graphKhoangCachChuan = khoangCachChuanDao.getAllKhoangCachMap();
    }

    @Override
    public List<TuyenDTO> getAllTuyen() {
        return tuyen_dao.getAllTuyen().stream()
                .map(TuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TuyenDTO> getTuyenByID(String tuyenID) {
        return tuyen_dao.getTuyenByID(tuyenID).stream()
                .map(TuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> timIDTuyenChoGoiY(String input) {
        if (input == null || input.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<Tuyen> dsTuyen = tuyen_dao.getTuyenByID(input.trim());
        return dsTuyen.stream().map(Tuyen::getTuyenID).collect(Collectors.toList());
    }

    @Override
    public List<TuyenDTO> timTuyenTheoGa(String gaDi, String gaDen) {
        if ((gaDi == null || gaDi.trim().isEmpty()) && (gaDen == null || gaDen.trim().isEmpty())) {
            return new ArrayList<>();
        }
        return tuyen_dao.getTuyenTheoGa(gaDi.trim(), gaDen.trim()).stream()
                .map(TuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Object[]> getDuLieuBang() {
        List<entity.Tuyen> dsEntity = tuyen_dao.getAllTuyen();

        List<dto.TuyenDTO> dsDTO = dsEntity.stream()
                .map(TuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());

        return convertTuyenListToTableData(dsDTO);
    }

    /**
     * Lấy dữ liệu bảng cho GUI dựa trên Mã Tuyến gần đúng.
     *
     * @param tuyenID Mã tuyến cần tìm kiếm.
     * @return List<Object[]> dữ liệu bảng.
     */
    @Override
    public List<Object[]> getDuLieuBangTheoTuyenID(String tuyenID) {
        if (tuyenID == null || tuyenID.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Lấy List<Tuyen> từ DAO
        List<Tuyen> dsTuyen = tuyen_dao.getTuyenByID(tuyenID.trim());

        List<dto.TuyenDTO> dsTuyenDTO = dsTuyen.stream()
                .map(TuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
        // 2. Chuyển đổi sang List<Object[]> (Sử dụng logic tương tự hàm getDuLieuBang)
        return convertTuyenListToTableData(dsTuyenDTO);
    }

    /**
     * Lấy dữ liệu bảng cho GUI dựa trên Ga Đi và Ga Đến.
     *
     * @param gaDi  Tên ga xuất phát.
     * @param gaDen Tên ga đích.
     * @return List<Object[]> dữ liệu bảng.
     */
    @Override
    public List<Object[]> getDuLieuBangTheoGa(String gaDi, String gaDen) {
        // 1. Lọc Tuyến thỏa mãn điều kiện Ga Đi/Ga Đến từ DAO
        List<Tuyen> dsTuyen = tuyen_dao.getTuyenTheoGa(gaDi.trim(), gaDen.trim());

        List<dto.TuyenDTO> dsTuyenDTO = dsTuyen.stream()
                .map(TuyenMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
        // 2. Chuyển đổi sang List<Object[]>
        return convertTuyenListToTableData(dsTuyenDTO);
    }

    /**
     * Lấy thông tin chi tiết của tuyến.
     */
    @Override
    public String getChiTietTuyen(String tuyenID) {
        if (tuyenID == null || tuyenID.isEmpty()) {
            return "Không tìm thấy tuyến";
        }
        List<TuyenChiTiet> dsTuyenChiTiet = tuyenChiTietDao.layDanhSachTheoTuyenID(tuyenID);
        if (dsTuyenChiTiet == null || dsTuyenChiTiet.isEmpty()) {
            return "Không tìm thấy thông tin chi tiết của tuyến này!";
        }


        Tuyen tuyen = dsTuyenChiTiet.get(0).getTuyen();
        String trangThaiStr = tuyen.isTrangThai() ? "Hoạt động" : "Không hoạt động";
        StringBuilder sb = new StringBuilder();
        sb.append(
                "__________________________________________THÔNG TIN CHI TIẾT CỦA TUYẾN__________________________________________\n");
        sb.append("Mã Tuyến: ").append(tuyen.getTuyenID()).append("\n");
        sb.append("Mô Tả: ").append(tuyen.getMoTa()).append("\n");
        sb.append("Trạng Thái: ").append(trangThaiStr).append("\n");
        sb.append("Khoảng cách từ ga xuất phát đến ga đích: ")
                .append(dsTuyenChiTiet.get(dsTuyenChiTiet.size() - 1).getKhoangCachTuGaXuatPhatKm()).append(" km\n");
        sb.append("\n Danh sách các ga trung gian trên tuyến:\n");


        return sb.toString();
    }

    /**
     * Lấy chi tiết các ga trung gian của một tuyến để hiển thị thông tin chi tiết
     * cho bảng tuyến.
     *
     * @param tuyenID Mã tuyến cần lấy chi tiết.
     * @return List<Object[]> danh sách chi tiết ga trung gian.
     */
    @Override
    public List<Object[]> getDuLieuGaTrungGianChiTiet(String tuyenID) {
        List<Object[]> dsChiTietBang = new ArrayList<>();
        List<TuyenChiTiet> dsTuyenChiTiet = tuyenChiTietDao.layDanhSachTheoTuyenID(tuyenID);
        if (dsTuyenChiTiet == null || dsTuyenChiTiet.isEmpty()) {
            return dsChiTietBang;
        }
        int soLuongGa = dsTuyenChiTiet.size();
        for (int i = 0; i < soLuongGa; i++) {
            TuyenChiTiet tct = dsTuyenChiTiet.get(i);
            String loaiGa;
            if (i == 0) {
                loaiGa = "Ga Xuất Phát";
            } else if (i == soLuongGa - 1) {
                loaiGa = "Ga Đích";
            } else {
                loaiGa = "Ga Trung Gian";
            }

            Object[] rowData = new Object[]{tct.getGa().getTenGa(), loaiGa, tct.getKhoangCachTuGaXuatPhatKm()};
            dsChiTietBang.add(rowData);
        }
        return dsChiTietBang;
    }

    /**
     * Lấy danh sách TuyenChiTiet của một tuyến. Dùng để tải dữ liệu lên form cập
     * nhật
     *
     * @param tuyenID Mã tuyến cần lấy chi tiết.
     * @return List<TuyenChiTiet> danh sách chi tiết tuyến.
     */
    @Override
    public List<TuyenChiTietDTO> getDanhSachTuyenChiTiet(String tuyenID) {
        return tuyenChiTietDao.layDanhSachTheoTuyenID(tuyenID).stream()
                .map(TuyenChiTietMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * tạo mã tuyến
     */
    @Override
    public String taoMaTuyenCoSo(String tenGaDi, String tenGaDen) {
        Ga gaDi = ga_dao.getGaByTenGa(tenGaDi);
        Ga gaDen = ga_dao.getGaByTenGa(tenGaDen);

        if (gaDi == null || gaDen == null) {
            return "";
        }

        String maTuyen = gaDi.getGaID() + "-" + gaDen.getGaID();
        return maTuyen;
    }

    /**
     * Kiểm tra xem một Mã Tuyến đã tồn tại trong CSDL hay chưa
     *
     * @param maTuyen Ma Tuyen can kiem tra
     * @return true nếu mã đã tồn tại, false nếu chưa.
     */
    @Override
    public boolean kiemTraMaTuyuenDaTonTai(String maTuyen) {
        if (maTuyen == null || maTuyen.isEmpty()) {
            return false;
        }
        Tuyen tuyen = tuyen_dao.getTuyenByExactID(maTuyen);
        return (tuyen != null);
    }

    @Override
    public boolean themTuyen(TuyenDTO tuyenMoiDTO, List<TuyenChiTietDTO> dsTCTDTO, NhanVienDTO nvDTO) {
        Tuyen tuyenMoi = TuyenMapper.INSTANCE.toEntity(tuyenMoiDTO);
        List<TuyenChiTiet> dsTCT = dsTCTDTO.stream().map(TuyenChiTietMapper.INSTANCE::toEntity).collect(Collectors.toList());

        boolean thanhCong = false;
        if (tuyen_dao.themTuyenMoi(tuyenMoi)) {
            if (tuyenChiTietDao.themDanhSachChiTiet(dsTCT)) {
                thanhCong = true;
                String log = String.format("%s %s Thêm Tuyến Mới: %s", nvDTO.getVaiTroNhanVienID(), nvDTO.getHoTen(), tuyenMoi.getMoTa());
                ghiNhatKy(tuyenMoi.getTuyenID(), nvDTO, entity.type.NhatKyAudit.THEM, log);
            } else {
                tuyen_dao.xoaTuyen(tuyenMoi.getTuyenID());
            }
        }
        return thanhCong;
    }

    @Override
    public boolean capNhatTuyen(TuyenDTO tuyenCapNhatDTO, List<TuyenChiTietDTO> dsChiTietMoiDTO, NhanVienDTO nvDTO) {
        Tuyen tuyenCapNhat = TuyenMapper.INSTANCE.toEntity(tuyenCapNhatDTO);
        List<TuyenChiTiet> dsChiTietMoi = dsChiTietMoiDTO.stream().map(TuyenChiTietMapper.INSTANCE::toEntity).collect(Collectors.toList());

        String tuyenID = tuyenCapNhat.getTuyenID();
        Tuyen tuyenCu = tuyen_dao.getTuyenByExactID(tuyenID);
        List<TuyenChiTiet> dsChiTietCu = tuyenChiTietDao.layDanhSachTheoTuyenID(tuyenID);

        if (tuyen_dao.capNhatTuyen(tuyenCapNhat)) {
            tuyenChiTietDao.xoaChiTietTheoTuyenID(tuyenID);
            tuyenChiTietDao.themDanhSachChiTiet(dsChiTietMoi);

            List<String> cacThayDoi = new ArrayList<>();
            if (tuyenCu != null && !tuyenCu.getMoTa().equals(tuyenCapNhat.getMoTa())) {
                cacThayDoi.add(String.format("Cập nhật mô tả (Cũ: '%s' -> Mới: '%s')", tuyenCu.getMoTa(), tuyenCapNhat.getMoTa()));
            }
            if (tuyenCu != null && tuyenCu.isTrangThai() != tuyenCapNhat.isTrangThai()) {
                cacThayDoi.add(String.format("Cập nhật trạng thái (%s -> %s)", tuyenCu.isTrangThai() ? "Hoạt động" : "Không", tuyenCapNhat.isTrangThai() ? "Hoạt động" : "Không"));
            }

            String chuoiGaCu = layChuoiGaTrungGian(dsChiTietCu);
            String chuoiGaMoi = layChuoiGaTrungGian(dsChiTietMoi);
            if (!chuoiGaCu.equals(chuoiGaMoi)) {
                cacThayDoi.add(String.format("Cập nhật lộ trình (Ga TG Cũ: [%s] -> Mới: [%s])", chuoiGaCu.isEmpty() ? "Không" : chuoiGaCu, chuoiGaMoi.isEmpty() ? "Không" : chuoiGaMoi));
            }

            String logMsg = String.format("%s %s Cập nhật tuyến %s : %s", nvDTO.getVaiTroNhanVienID(), nvDTO.getHoTen(), tuyenID, String.join(", ", cacThayDoi));
            ghiNhatKy(tuyenID, nvDTO, entity.type.NhatKyAudit.SUA, logMsg);
            return true;
        }
        return false;
    }


    /**
     * Tính khoảng cách tổng Sử dụng thuật toán Dijkstra để tìm đường đi ngắn nhất
     * nếu không có đoạn trực tiếp.
     *
     * @param gaID_Dau  Ga xuất phát
     * @param gaID_Cuoi Ga đích
     * @return Khoảng cách tổng, hoặc -1 nếu không tìm thấy đường đi.
     */
    @Override
    public int tinhKhoangCachTongDijsktra(String gaID_Dau, String gaID_Cuoi) {
        if (!graphKhoangCachChuan.containsKey(gaID_Dau) || !graphKhoangCachChuan.containsKey(gaID_Cuoi)) {
            return -1;
        }
        if (graphKhoangCachChuan.get(gaID_Dau).containsKey(gaID_Cuoi)) {
            return graphKhoangCachChuan.get(gaID_Dau).get(gaID_Cuoi);
        }

        Map<String, Integer> distances = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> pq = new PriorityQueue<>(Map.Entry.comparingByValue());
        Set<String> visited = new HashSet<>();

        for (String gaID : graphKhoangCachChuan.keySet()) {
            distances.put(gaID, Integer.MAX_VALUE);
        }
        distances.put(gaID_Dau, 0);
        pq.offer(new AbstractMap.SimpleEntry<>(gaID_Dau, 0));

        while (!pq.isEmpty()) {
            Map.Entry<String, Integer> entry = pq.poll();
            String u = entry.getKey();
            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            if (u.equals(gaID_Cuoi)) {
                return distances.get(u);
            }

            if (graphKhoangCachChuan.get(u) == null) {
                continue;
            }

            for (Map.Entry<String, Integer> neighbor : graphKhoangCachChuan.get(u).entrySet()) {
                String v = neighbor.getKey();
                int weight = neighbor.getValue();
                if (!visited.contains(v) && distances.get(u) != Integer.MAX_VALUE
                        && distances.get(u) + weight < distances.get(v)) {
                    distances.put(v, distances.get(u) + weight);
                    pq.offer(new AbstractMap.SimpleEntry<>(v, distances.get(v)));
                }
            }
        }
        return -1; // Không tìm thấy đường đi
    }

    /**
     * Cung cấp đồ thị khoảng cách (Graph) đã được tải vào bộ nhớ
     *
     * @return Map<String, Map<String, Integer>> (Đồ thị GaID -> (GaID liền kề ->
     * KC)
     */
    @Override
    public Map<String, Map<String, Integer>> getGraphKhoangCachChuan() {
        return graphKhoangCachChuan;
    }

    @Override
    public List<TuyenDTO> layKiemTop10Tuyen(String keyword) {
        return tuyen_dao.getTop10Tuyen(keyword).stream().map(TuyenMapper.INSTANCE::toDTO).collect(Collectors.toList());
    }

    @Override
    public TuyenDTO getTuyenTheoMa(String maTuyen) {
        Tuyen tuyen = tuyen_dao.layTuyenTheoMa(maTuyen);
        return tuyen != null ? TuyenMapper.INSTANCE.toDTO(tuyen) : null;
    }

    @Override
    public List<TuyenChiTietDTO> layDanhSachTuyenChiTiet(String maTuyen) {
        return tuyen_dao.layDanhSachTuyenChiTiet(maTuyen).stream()
                .map(TuyenChiTietMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllMaVaTenTuyen() {
        List<Tuyen> dsTuyen = tuyen_dao.getAllTuyen();
        List<String> dsHienThi = new ArrayList<>();

        if (dsTuyen != null) {
            for (Tuyen t : dsTuyen) {
                String moTa = (t.getMoTa() != null) ? t.getMoTa() : "Không có mô tả";
                // Định dạng: "SE1 (Sài Gòn - Hà Nội)"
                String item = String.format("%s (%s)", t.getTuyenID(), moTa);
                dsHienThi.add(item);
            }
        }
        return dsHienThi;
    }


    @Override
    public int getKhoangCachGiuaHaiGa(String gaDiID, String gaDenID) {
        if (graphKhoangCachChuan != null && graphKhoangCachChuan.containsKey(gaDiID)) {
            Map<String, Integer> neighbors = graphKhoangCachChuan.get(gaDiID);
            if (neighbors != null && neighbors.containsKey(gaDenID)) {
                return neighbors.get(gaDenID);
            }
        }

        return tinhKhoangCachTongDijsktra(gaDiID, gaDenID);
    }

}
