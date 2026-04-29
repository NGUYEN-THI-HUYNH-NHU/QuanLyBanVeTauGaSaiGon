package bus;/*
			* @ (#) Tuyen_BUS.java   1.0     30/09/2025


			/**
			* @description :
			* @author : Vy, Pham Kha Vy
			* @version 1.0
			* @created : 30/09/2025
			*/

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import dao.Ga_DAO;
import dao.KhoangCachChuan_DAO;
import dao.TuyenChiTiet_DAO;
import dao.Tuyen_DAO;
import entity.*;

public class Tuyen_BUS {
	private final Tuyen_DAO tuyen_dao;
	private final Ga_DAO ga_dao;
	private final TuyenChiTiet_DAO tuyenChiTietDao;
	private final KhoangCachChuan_DAO khoangCachChuanDao;
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

	public List<Tuyen> getAllTuyen() {
		return tuyen_dao.getAllTuyen();
	}

	public List<Tuyen> getTuyenByID(String tuyenID) {
		return tuyen_dao.getTuyenByID(tuyenID);
	}

	public List<String> timIDTuyenChoGoiY(String input) {
		if (input == null || input.trim().isEmpty()) {
			return new ArrayList<>();
		}

		// Gọi xuống DAO
		List<Tuyen> dsTuyen = tuyen_dao.getTuyenByID(input.trim());
		List<String> idTuyenList = new ArrayList<>();

		for (Tuyen tuyen : dsTuyen) {
			idTuyenList.add(tuyen.getTuyenID());
		}

		return idTuyenList;
	}

	public List<Tuyen> timTuyenTheoGa(String gaDi, String gaDen) {
		if ((gaDi == null || gaDi.trim().isEmpty()) && (gaDen == null || gaDen.trim().isEmpty())) {
			return new ArrayList<>();
		}
		return tuyen_dao.getTuyenTheoGa(gaDi.trim(), gaDen.trim());
	}

	public List<Object[]> getDuLieuBang() {
		return convertTuyenListToTableData(tuyen_dao.getAllTuyen());
	}

	private List<Object[]> convertTuyenListToTableData(List<Tuyen> dsTuyen) {
		List<Object[]> dsDuLieuBang = new ArrayList<>();

		for (Tuyen tuyen : dsTuyen) {
			List<TuyenChiTiet> dsTuyenChiTiet = tuyenChiTietDao.layDanhSachTheoTuyenID(tuyen.getTuyenID());

			if (dsTuyenChiTiet != null && dsTuyenChiTiet.size() >= 2) {
				TuyenChiTiet gaDiTCT = dsTuyenChiTiet.get(0);
				TuyenChiTiet gaDenTCT = dsTuyenChiTiet.get(dsTuyenChiTiet.size() - 1);
				int khoangCach = gaDenTCT.getKhoangCachTuGaXuatPhatKm();

				String gaTrungGian;
				if (dsTuyenChiTiet.size() > 2) {
					gaTrungGian = dsTuyenChiTiet.subList(1, dsTuyenChiTiet.size() - 1).stream()
							.map(tct -> tct.getGa().getTenGa()).collect(Collectors.joining(" -> "));
				} else {
					gaTrungGian = "-";
				}

				String trangThaiHienThi = tuyen.isTrangThai() ? "Hoạt động" : "Không hoạt động";

				Object[] rowData = new Object[] {
						tuyen.getTuyenID(),
						gaDiTCT.getGa().getTenGa(),
						gaDenTCT.getGa().getTenGa(),
						gaTrungGian,
						khoangCach,
						trangThaiHienThi
				};
				dsDuLieuBang.add(rowData);
			}
		}
		return dsDuLieuBang;
	}

	/**
	 * Lấy dữ liệu bảng cho GUI dựa trên Mã Tuyến gần đúng.
	 *
	 * @param tuyenID Mã tuyến cần tìm kiếm.
	 * @return List<Object[]> dữ liệu bảng.
	 */
	public List<Object[]> getDuLieuBangTheoTuyenID(String tuyenID) {
		if (tuyenID == null || tuyenID.trim().isEmpty()) {
			return new ArrayList<>();
		}

		// 1. Lấy List<Tuyen> từ DAO
		List<Tuyen> dsTuyen = tuyen_dao.getTuyenByID(tuyenID.trim());

		// 2. Chuyển đổi sang List<Object[]> (Sử dụng logic tương tự hàm getDuLieuBang)
		return convertTuyenListToTableData(dsTuyen);
	}

	/**
	 * Lấy dữ liệu bảng cho GUI dựa trên Ga Đi và Ga Đến.
	 *
	 * @param gaDi  Tên ga xuất phát.
	 * @param gaDen Tên ga đích.
	 * @return List<Object[]> dữ liệu bảng.
	 */
	public List<Object[]> getDuLieuBangTheoGa(String gaDi, String gaDen) {
		// 1. Lọc Tuyến thỏa mãn điều kiện Ga Đi/Ga Đến từ DAO
		List<Tuyen> dsTuyen = tuyen_dao.getTuyenTheoGa(gaDi.trim(), gaDen.trim());

		// 2. Chuyển đổi sang List<Object[]>
		return convertTuyenListToTableData(dsTuyen);
	}

	/**
	 * Lấy thông tin chi tiết của tuyến.
	 */
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

			Object[] rowData = new Object[] { tct.getGa().getTenGa(), loaiGa, tct.getKhoangCachTuGaXuatPhatKm() };
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
	public List<TuyenChiTiet> getDanhSachTuyenChiTiet(String tuyenID) {
		return tuyenChiTietDao.layDanhSachTheoTuyenID(tuyenID);
	}

	/**
	 * tạo mã tuyến
	 */
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
	public boolean kiemTraMaTuyuenDaTonTai(String maTuyen) {
		if (maTuyen == null || maTuyen.isEmpty()) {
			return false;
		}
		Tuyen tuyen = tuyen_dao.getTuyenByExactID(maTuyen);
		return (tuyen != null);
	}

	public boolean themTuyen(Tuyen tuyenMoi, List<TuyenChiTiet> dsTCT, NhanVien nhanVienThucHien) {
		if (tuyenMoi == null || dsTCT == null || dsTCT.isEmpty()) {
			return false;
		}
		boolean themTuyenThanhCong = false;
		try {
			boolean themTuyen = tuyen_dao.themTuyenMoi(tuyenMoi);
			if (themTuyen) {
				boolean themChiTiet = tuyenChiTietDao.themDanhSachChiTiet(dsTCT);
				if (themChiTiet) {
					themTuyenThanhCong = true;
					String chiTietLog = String.format("%s %s Thêm Tuyến Mới: %s",
							nhanVienThucHien.getVaiTroNhanVien(),
							nhanVienThucHien.getHoTen(),
							tuyenMoi.getMoTa());
					ghiNhatKy(tuyenMoi.getTuyenID(), nhanVienThucHien, entity.type.NhatKyAudit.THEM,chiTietLog);
				} else {
					tuyen_dao.xoaTuyen(tuyenMoi.getTuyenID());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return themTuyenThanhCong;
	}

	public boolean capNhatTuyen(Tuyen tuyenCapNhat, List<TuyenChiTiet> dsChiTietMoi, NhanVien nhanVienThucHien) {
		if (tuyenCapNhat == null || dsChiTietMoi == null || dsChiTietMoi.size() < 2) {
			return false;
		}
		String tuyenID = tuyenCapNhat.getTuyenID();

		Tuyen tuyenCu = tuyen_dao.getTuyenByExactID(tuyenID);
		List<TuyenChiTiet> dsChiTietCu = tuyenChiTietDao.layDanhSachTheoTuyenID(tuyenID);

		String chuoiGaCu = layChuoiGaTrungGian(dsChiTietCu);
		String chuoiGaMoi = layChuoiGaTrungGian(dsChiTietMoi);

		try{
			boolean updateTuyen = tuyen_dao.capNhatTuyen(tuyenCapNhat);
			if (!updateTuyen) return false;

			boolean xoaChiTietCu = tuyenChiTietDao.xoaChiTietTheoTuyenID(tuyenID);
			if (!xoaChiTietCu) return false;

			boolean themChiTietMoi = tuyenChiTietDao.themDanhSachChiTiet(dsChiTietMoi);
			if (!themChiTietMoi) return false;

			List<String> cacThayDoi = new ArrayList<>();

			if (tuyenCu != null && !tuyenCu.getMoTa().equals(tuyenCapNhat.getMoTa())) {
				cacThayDoi.add(String.format("Cập nhật mô tả (Cũ: '%s' -> Mới: '%s')",
						tuyenCu.getMoTa(), tuyenCapNhat.getMoTa()));
			}

			if (tuyenCu != null && tuyenCu.isTrangThai() != tuyenCapNhat.isTrangThai()) {
				String ttCu = tuyenCu.isTrangThai() ? "Hoạt động" : "Không hoạt động";
				String ttMoi = tuyenCapNhat.isTrangThai() ? "Hoạt động" : "Không hoạt động";
				cacThayDoi.add(String.format("Cập nhật trạng thái (%s -> %s)", ttCu, ttMoi));
			}

			if (!chuoiGaCu.equals(chuoiGaMoi)) {
				cacThayDoi.add(String.format("Cập nhật lộ trình (Ga TG Cũ: [%s] -> Mới: [%s])",
						chuoiGaCu.isEmpty() ? "Không có" : chuoiGaCu,
						chuoiGaMoi.isEmpty() ? "Không có" : chuoiGaMoi));
			}
			String tenChucVu = (nhanVienThucHien.getVaiTroNhanVien() != null) ? nhanVienThucHien.getVaiTroNhanVien().getDescription() : "";

			StringBuilder sbLog = new StringBuilder();
			sbLog.append(String.format("%s %s Cập nhật tuyến %s",
					tenChucVu,
					nhanVienThucHien.getHoTen(),
					tuyenID));

			if (!cacThayDoi.isEmpty()) {
				sbLog.append(" : ").append(String.join(", ", cacThayDoi));
			} else {
				sbLog.append(" : (Không có thay đổi nội dung)");
			}

			ghiNhatKy(tuyenID, nhanVienThucHien, entity.type.NhatKyAudit.SUA, sbLog.toString());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void ghiNhatKy(String doiTuongID, NhanVien nv, entity.type.NhatKyAudit loaiThaoTac, String chiTiet){
		if(nv == null) return;
		try{
			String maLog = nhatKyAuditBus.taoMaNhatKyAuditMoi();

			NhatKyAudit log = new NhatKyAudit(maLog,
					doiTuongID,
					nv.getNhanVienID(),
					LocalDateTime.now(),
					loaiThaoTac,
					chiTiet,
					"Tuyen");
			nhatKyAuditBus.ghiNhatKyAudit(log);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private String layChuoiGaTrungGian(List<TuyenChiTiet> list) {
		if (list == null || list.size() <= 2) {
			return "";
		}
		return list.subList(1, list.size() - 1).stream()
				.map(tct -> tct.getGa().getTenGa())
				.collect(Collectors.joining(", "));
	}


	/**
	 * Tính khoảng cách tổng Sử dụng thuật toán Dijkstra để tìm đường đi ngắn nhất
	 * nếu không có đoạn trực tiếp.
	 *
	 * @param gaID_Dau  Ga xuất phát
	 * @param gaID_Cuoi Ga đích
	 * @return Khoảng cách tổng, hoặc -1 nếu không tìm thấy đường đi.
	 */
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
	 *         KC)
	 */
	public Map<String, Map<String, Integer>> getGraphKhoangCachChuan() {
		return graphKhoangCachChuan;
	}

	public List<Tuyen> layKiemTop10Tuyen(String keyword) {
		return tuyen_dao.getTop10Tuyen(keyword);
	}

	public Tuyen getTuyenTheoMa(String maTuyen) {
		return tuyen_dao.layTuyenTheoMa(maTuyen);
	}

	public List<TuyenChiTiet> layDanhSachTuyenChiTiet(String maTuyen) {
		return tuyen_dao.layDanhSachTuyenChiTiet(maTuyen);
	}

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
