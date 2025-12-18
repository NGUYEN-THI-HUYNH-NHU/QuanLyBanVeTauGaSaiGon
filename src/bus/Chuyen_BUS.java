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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.*;
import entity.*;

public class Chuyen_BUS {
	private Ghe_DAO gheDAO;
	private Toa_DAO toaDAO;
	private Chuyen_DAO chuyenDAO;
	private ChuyenGa_DAO chuyenGaDao;
	private Ga_DAO gaDAO;

	public Chuyen_BUS() {
		gheDAO = new Ghe_DAO();
		toaDAO = new Toa_DAO();
		chuyenDAO = new Chuyen_DAO();
		chuyenGaDao = new ChuyenGa_DAO();
		gaDAO = new Ga_DAO();
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

	public String themChuyen(Chuyen chuyen, List<ChuyenGa> lichTrinh){

		if (chuyenDAO.existsById(chuyen.getChuyenID())) {
			return "Đã tồn tại chuyến " + chuyen.getChuyenID();
		}

		boolean ok = chuyenDAO.themChuyenMoi(chuyen, lichTrinh);
		if (!ok) {
			return "Không thể thêm chuyến (lỗi lưu dữ liệu)";
		}

		return null;
	}


	public Map<String, String> getMapTenGaToID(){
		return chuyenDAO.getMapTenGaToID();
	}

	public boolean capNhatChuyen(Chuyen chuyen, List<ChuyenGa> lichTrinh){
		return chuyenDAO.capNhatChuyen(chuyen, lichTrinh);
	}

	public List<Ga> layDsGaCuaTuyen(String tuyenID) {
		return chuyenDAO.getDsGaTheoTuyen(tuyenID);
	}
}