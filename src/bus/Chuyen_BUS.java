package bus;
/*
 * @(#) Chuyen_BUS.java  1.0  [12:42:29 PM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.*;
import entity.*;
import entity.type.NhatKyAudit;

public class Chuyen_BUS {
	private Ghe_DAO gheDAO;
	private Toa_DAO toaDAO;
	private Chuyen_DAO chuyenDAO;
	private ChuyenGa_DAO chuyenGaDao;
	private Ga_DAO gaDAO;

	private final NhatKyAudit_BUS nhatKyAuditBus;

	public Chuyen_BUS() {
		gheDAO = new Ghe_DAO();
		toaDAO = new Toa_DAO();
		chuyenDAO = new Chuyen_DAO();
		chuyenGaDao = new ChuyenGa_DAO();
		gaDAO = new Ga_DAO();

		nhatKyAuditBus = new NhatKyAudit_BUS();
	}

	public Map<String, String> layTrangThaiCacGheTrongToaCuaChuyen(String gaDiID, String gaDenID, String chuyenID,
			String toaID) {
		List<Ghe> gheList = gheDAO.getGheByGaDiGaDenChuyenToa(gaDiID, gaDenID, chuyenID, toaID);

		Map<String, String> result = new HashMap<>();
		if (gheList != null) {
			for (Ghe ghe : gheList) {
				result.put(ghe.getGheID(), ghe.toString());
			}
		}

		return result;
	}

	public List<Chuyen> timChuyenTheoGaDiGaDenNgayDi(String gaDi, String gaDen, LocalDate ngayDi) {
		List<Chuyen> dsChuyen = chuyenDAO.getChuyenByGaDiGaDenNgayDi(gaDi, gaDen, ngayDi);
		dsChuyen.removeIf(
				c -> !LocalDateTime.now().plusHours(1).isBefore(LocalDateTime.of(c.getNgayDi(), c.getGioDi())));
		return dsChuyen;
	}

	// Gợi ý ga đi (tên)
	public List<Ga> goiYGaDi(String prefix, int limit) {
		return gaDAO.searchGaByPrefix(prefix, limit);

	}

	// Gợi ý ga đến dựa trên ga đi đã chọn
	public List<Ga> goiYGaDenTheoGaDi(String gaDiID, String prefixGaDen, int limit) {
		return gaDAO.searchGaDenKhaThiByGaDi(gaDiID, prefixGaDen, limit);
	}

	public Ga timGaTheoTenGa(String tenGa) {
		return gaDAO.getGaByTenGa(tenGa);
	}

	public List<Toa> layCacToaTheoChuyen(String chuyenID) {
		return toaDAO.getToaByChuyenID(chuyenID);
	}

	public List<Ghe> layCacGheTrongToaTrenChuyen(String gaDiID, String gaDenID, String chuyenID, String toaID) {
		return gheDAO.getGheByGaDiGaDenChuyenToa(gaDiID, gaDenID, chuyenID, toaID);
	}

	public int layGiaGheTheoPhanDoan(String chuyenID, String gaDiID, String gaDenID, String loaiTauID,
			String hangToaID) {
		return gheDAO.calcGia(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);
	}

	public double layKhuyenMaiTheoGhe(String tuyenID, String loaiTauID, String hangToaID, String loaiDoiTuongID,
			LocalDate ngayDi, double giaGhe) {
		return 0;
	}

	public List<Chuyen> layDanhSachChuyen(){
		return chuyenDAO.getAllChuyen();
	}

	public List<ChuyenGa> layChiTietHanhTrinh(String maChuyen){
		return chuyenGaDao.getChiTietHanhTrinh(maChuyen);
	}

	public Chuyen layChuyenTheoMa(String maChuyen){
		if(maChuyen == null || maChuyen.isEmpty()){
			return null;
		}
		return chuyenDAO.layChuyenTheoMa(maChuyen);
	}

	public List<Chuyen> timKiemChuyen(String maChuyen, String gaDi, String gaDen, String tenTau, LocalDate ngayDi){
		if (maChuyen.isEmpty() && gaDi.isEmpty() && gaDen.isEmpty() && tenTau.isEmpty() && ngayDi == null) {
			return chuyenDAO.getAllChuyen();
		}

		return chuyenDAO.timKiemChuyen(maChuyen, gaDi, gaDen, tenTau, ngayDi);
	}

	public List<String> getListMaChuyen(){
		return chuyenDAO.getAllMaChuyenID();
	}

	public List<String> getListTenGa(){
		return chuyenDAO.getAllTenGa();
	}

	public List<String> getListTenTau(){
		return chuyenDAO.getAllTenTau();
	}

	public List<String> getAllTauID(){
		return chuyenDAO.getAllTauID();
	}

	public List<String> getAllTuyenID(){
		return chuyenDAO.getAllTuyenID();
	}

	public String themChuyen(Chuyen chuyen, List<ChuyenGa> lichTrinh, NhanVien nhanVienThucHien){

		if (chuyenDAO.existsById(chuyen.getChuyenID())) {
			return "Đã tồn tại chuyến " + chuyen.getChuyenID();
		}

		boolean ok = chuyenDAO.themChuyenMoi(chuyen, lichTrinh);
		if (!ok) {
			return "Không thể thêm chuyến (lỗi lưu dữ liệu)";
		}

		String tenChucVu = (nhanVienThucHien.getVaiTroNhanVien() != null)  ? nhanVienThucHien.getVaiTroNhanVien().getDescription() : "";
		String chiTietLog = String.format("%s %s Thêm Chuyến mới: %s (Tàu: %s, Ngày đi: %s, Giờ đi: %s)",
				tenChucVu,
				nhanVienThucHien.getHoTen(),
				chuyen.getChuyenID(),
				chuyen.getTau().getTauID(),
				chuyen.getNgayDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
				chuyen.getGioDi().toString());
		ghiLogAudit(chuyen.getChuyenID(), nhanVienThucHien, NhatKyAudit.THEM, chiTietLog);

		return null;
	}


	public Map<String, String> getMapTenGaToID(){
		return chuyenDAO.getMapTenGaToID();
	}

	public boolean capNhatChuyen(Chuyen chuyen, List<ChuyenGa> lichTrinh, NhanVien nhanVienThucHien){
		String chuyenID = chuyen.getChuyenID();
		Chuyen chuyenCu = chuyenDAO.layChuyenTheoMa(chuyenID);
		List<ChuyenGa> lichTrinhCu = chuyenGaDao.getChiTietHanhTrinh(chuyenID);

		String strLichTrinhCu = layChuoiLichTrinh(lichTrinhCu);
		String strLichTrinhMoi = layChuoiLichTrinh(lichTrinh);

		boolean ketQua = chuyenDAO.capNhatChuyen(chuyen, lichTrinh);

		if(ketQua){
			List<String> cacThayDoi = new ArrayList<>();
			if(chuyenCu != null && !chuyenCu.getTau().getTauID().equals(chuyen.getTau().getTauID())){
				cacThayDoi.add(String.format("Cập nhật tàu (Cũ: %s -> Mới: %s)", chuyenCu.getTau().getTauID(), chuyen.getTau().getTauID()));
			}

			boolean doiNgay = !chuyenCu.getNgayDi().equals(chuyen.getNgayDi());
			boolean doiGio = !chuyenCu.getGioDi().equals(chuyen.getGioDi());

			if (doiNgay || doiGio) {
				String thoiGianCu = chuyenCu.getNgayDi() + " " + chuyenCu.getGioDi();
				String thoiGianMoi = chuyen.getNgayDi() + " " + chuyen.getGioDi();
				cacThayDoi.add(String.format("Cập nhật Thời gian đi (Cũ: %s -> Mới: %s)", thoiGianCu, thoiGianMoi));
			}

			if (!strLichTrinhCu.equals(strLichTrinhMoi)) {
				cacThayDoi.add(String.format("Cập nhật thông tin chặng (Cũ: [%s] -> Mới: [%s])",
						strLichTrinhCu, strLichTrinhMoi));
			}

			else if (lichTrinhCu.size() == lichTrinh.size()) {
				if(kiemTraThayDoiGioChiTiet(lichTrinhCu, lichTrinh)){
					cacThayDoi.add("Điều chỉnh giờ đến/đi tại các ga trung gian");
				}
			}

			String tenChucVu = (nhanVienThucHien.getVaiTroNhanVien() != null) ? nhanVienThucHien.getVaiTroNhanVien().getDescription() : "";
			StringBuilder sbLog = new StringBuilder();

			sbLog.append(String.format("%s %s Cập nhật chuyến %s", tenChucVu, nhanVienThucHien.getHoTen(), chuyenID));

			if (!cacThayDoi.isEmpty()) {
				sbLog.append(" : ").append(String.join(", ", cacThayDoi));
			} else {
				sbLog.append(" : Không có thông tin thay đổi");
			}

			ghiLogAudit(chuyenID, nhanVienThucHien, NhatKyAudit.SUA, sbLog.toString());
		}
		return ketQua;
	}

	private String layChuoiLichTrinh(List<ChuyenGa> list) {
		if (list == null || list.isEmpty()) return "";
		list.sort((o1, o2) -> Integer.compare(o1.getThuTu(), o2.getThuTu()));

		return list.stream()
				.map(cg -> cg.getGa().getTenGa())
				.collect(Collectors.joining(" -> "));
	}

	private boolean kiemTraThayDoiGioChiTiet(List<ChuyenGa> cu, List<ChuyenGa> moi) {
		for(int i=0; i<cu.size(); i++) {
			ChuyenGa c = cu.get(i);
			ChuyenGa m = moi.get(i);
			// So sánh giờ đến hoặc giờ đi
			if((c.getGioDen() != null && !c.getGioDen().equals(m.getGioDen())) ||
					(c.getGioDi() != null && !c.getGioDi().equals(m.getGioDi()))) {
				return true;
			}
		}
		return false;
	}

	private void ghiLogAudit(String doiTuongID, NhanVien nv, entity.type.NhatKyAudit loaiThaoTac, String chiTiet) {
		if (nv == null) return;
		try {
			String maLog = nhatKyAuditBus.taoMaNhatKyAuditMoi();
			entity.NhatKyAudit log = new entity.NhatKyAudit(
					maLog,
					doiTuongID,
					nv.getNhanVienID(),
					LocalDateTime.now(),
					loaiThaoTac,
					chiTiet,
					"Chuyen"
			);
			nhatKyAuditBus.ghiNhatKyAudit(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public List<Ga> layDsGaCuaTuyen(String tuyenID) {
		return chuyenDAO.getDsGaTheoTuyen(tuyenID);
	}
}