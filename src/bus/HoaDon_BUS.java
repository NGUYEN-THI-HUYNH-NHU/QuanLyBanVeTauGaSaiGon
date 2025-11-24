package bus;

/*
 * @(#) HoaDon_BUS.java  1.0  [1:06:29 PM] Nov 2, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 2, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dao.HoaDonChiTiet_DAO;
import dao.HoaDon_DAO;
import dao.PhieuDungPhongVIP_DAO;
import entity.DonDatCho;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDungPhongVIP;
import entity.type.LoaiDichVu;
import entity.type.LoaiDoiTuong;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;
import gui.application.form.doiVe.VeDoiRow;
import gui.application.form.hoanVe.VeHoanRow;

public class HoaDon_BUS {
	private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
	private final HoaDonChiTiet_DAO hoaDonChiTietDAO = new HoaDonChiTiet_DAO();
	private final PhieuDungPhongVIP_DAO phieuDungPhongVIPDAO = new PhieuDungPhongVIP_DAO();

	/**
	 * @param bookingSession
	 * @return
	 */
	public HoaDon taoHoaDon(BookingSession bookingSession) {
		String hdID = "HD-" + bookingSession.getDonDatCho().getDonDatChoID();
		LocalDateTime now = LocalDateTime.now();
		HoaDon hoaDon = new HoaDon(hdID, bookingSession.getKhachHang(), bookingSession.getNhanVien(), now,
				bookingSession.getGiaoDichThanhToan().getTongTien(), bookingSession.getGiaoDichThanhToan().getMaGD(),
				bookingSession.getGiaoDichThanhToan().getTienNhan(),
				bookingSession.getGiaoDichThanhToan().getTienHoan(), true);

		return hoaDon;
	}

	/**
	 * @param exchangeSession
	 * @return
	 */
	public HoaDon taoHoaDonDoiVe(ExchangeSession exchangeSession) {
		String hdID = "HDDV-" + exchangeSession.getDonDatChoMoi().getDonDatChoID();
		LocalDateTime now = LocalDateTime.now();
		HoaDon hoaDon = new HoaDon(hdID, exchangeSession.getKhachHang(), exchangeSession.getNhanVien(), now,
				exchangeSession.getGiaoDichThanhToan().getTongTien(), exchangeSession.getGiaoDichThanhToan().getMaGD(),
				exchangeSession.getGiaoDichThanhToan().getTienNhan(),
				exchangeSession.getGiaoDichThanhToan().getTienHoan(), true);

		return hoaDon;
	}

	/**
	 * @param donDatCho
	 * @param khachHang
	 * @param nhanVien
	 * @param tongTienHoan
	 * @return
	 */
	public HoaDon taoHoaDonHoanVe(DonDatCho donDatCho, KhachHang khachHang, NhanVien nhanVien, double tongTienHoan) {
		HoaDon hoaDon = new HoaDon();
		hoaDon.setHoaDonID("HDHV-" + donDatCho.getDonDatChoID().substring(4));
		hoaDon.setKhachHang(khachHang);
		hoaDon.setNhanVien(nhanVien);
		hoaDon.setThoiDiemTao(LocalDateTime.now());
		hoaDon.setTongTien(-tongTienHoan);
		hoaDon.setMaGD(null);
		hoaDon.setTienNhan(0);
		hoaDon.setTienHoan(tongTienHoan);
		hoaDon.setThanhToanTienMat(true);

		return hoaDon;
	}

	/**
	 * @param hoaDon
	 * @param listVeMoi
	 * @return
	 */
	public List<HoaDonChiTiet> taoCacHoaDonChiTietBanVe(HoaDon hoaDon, List<VeSession> listVeMoi) {
		List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
		int stt = 0;
		for (VeSession ve : listVeMoi) {
			String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, hoaDon, ve.getVe(), "Vé HK: " + ve.toString(),
					LoaiDichVu.VE_BAN, "Vé", 1, ve.getVe().getGia(), ve.getVe().getGia());
			dsHoaDonChiTiet.add(hdctVe);

			if (ve.getPhieuDungPhongVIP() != null) {
				String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, ve.getPhieuDungPhongVIP(),
						"Phiếu dùng phòng chờ VIP Ga Sài Gòn", LoaiDichVu.PHONG_VIP, "Phiếu", 1,
						ve.getPhiPhieuDungPhongChoVIP(), ve.getPhiPhieuDungPhongChoVIP());
				dsHoaDonChiTiet.add(hdctPhieu);
			}

			if (ve.getVe().getKhachHang().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
				String hdctGiamDTID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctGiamDT = new HoaDonChiTiet(hdctGiamDTID, hoaDon, "Giảm giá đối tượng trẻ em",
						LoaiDichVu.KHUYEN_MAI, "", 1, -ve.getGiamDoiTuong(), -ve.getGiamDoiTuong());
				dsHoaDonChiTiet.add(hdctGiamDT);

			}
		}
		return dsHoaDonChiTiet;
	}

	public List<HoaDonChiTiet> taoCacHoaDonChiTietHoanVe(Connection conn, HoaDon hoaDon,
			List<VeHoanRow> listVeHoanRow) {
		List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
		int stt = 0;

		for (VeHoanRow row : listVeHoanRow) {
			String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, hoaDon, row.getVe(),
					"Điều chỉnh giảm theo BB trả vé số: 2177975", LoaiDichVu.VE_HOAN, "Vé", 1, -row.getVe().getGia(),
					-row.getVe().getGia());
			dsHoaDonChiTiet.add(hdctVe);

			PhieuDungPhongVIP phieu = phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(conn, row.getVe().getVeID());
			if (phieu != null) {
				String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, phieu,
						"Hủy phiếu dùng phòng chờ VIP theo vé hoàn", LoaiDichVu.PHIEU_HOAN, "Phiếu", 1, 0, 0);
				dsHoaDonChiTiet.add(hdctPhieu);
			}

			String hdctLePhiID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctLePhi = new HoaDonChiTiet(hdctLePhiID, hoaDon, "Lệ phí hoàn vé", LoaiDichVu.PHI_HOAN, 1,
					row.getLePhiHoanVe(), row.getLePhiHoanVe());
			dsHoaDonChiTiet.add(hdctLePhi);
		}

		return dsHoaDonChiTiet;
	}

	/**
	 * @param hoaDon
	 * @param listVeMoi
	 * @return
	 */
	public List<HoaDonChiTiet> taoCacHoaDonChiTietDoiVe(Connection conn, HoaDon hoaDon,
			ExchangeSession exchangeSession) {
		List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
		List<VeDoiRow> listVeDoi = exchangeSession.getListVeCuCanDoi();
		List<VeSession> listVeMoi = exchangeSession.getListVeMoiDangChon();
		int soLuongVe = listVeDoi.size();
		int stt = 0;
		for (int i = 0; i < soLuongVe; i++) {
			// Dòng vé đổi
			String hdctVeDoiID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVeDoi = new HoaDonChiTiet(hdctVeDoiID, hoaDon, listVeDoi.get(i).getVe(),
					"Điều chỉnh giảm theo BB trả vé số: 2177975", LoaiDichVu.VE_DOI, "Vé", 1,
					-listVeDoi.get(i).getVe().getGia(), -listVeDoi.get(i).getVe().getGia());
			dsHoaDonChiTiet.add(hdctVeDoi);

			// Dòng phiếu đổi (hủy)
			PhieuDungPhongVIP phieuDoi = listVeDoi.get(i).getPhieuDungPhongVIP();
			if (phieuDoi != null) {
				String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, phieuDoi,
						"Hủy phiếu dùng phòng chờ VIP theo vé đổi", LoaiDichVu.PHONG_VIP, "Phiếu", 1, 0, 0);
				dsHoaDonChiTiet.add(hdctPhieu);
			}

			// Dòng vé mới
			String hdctVeMoiID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVeMoi = new HoaDonChiTiet(hdctVeMoiID, hoaDon, listVeMoi.get(i).getVe(),
					"Vé HK: " + listVeMoi.get(i).toString(), LoaiDichVu.VE_BAN, "Vé", 1,
					listVeMoi.get(i).getVe().getGia(), listVeMoi.get(i).getVe().getGia());
			dsHoaDonChiTiet.add(hdctVeMoi);

			// Dòng phiếu mới (nếu có)
			PhieuDungPhongVIP phieuMoi = listVeMoi.get(i).getPhieuDungPhongVIP();
			if (phieuMoi != null) {
				if (phieuDungPhongVIPDAO.getPhieuDungPhongVIPByID(conn, phieuMoi.getPhieuDungPhongChoVIPID()) == null) {
					String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
					HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, phieuMoi,
							"Phiếu dùng phòng chờ VIP Ga Sài Gòn", LoaiDichVu.PHONG_VIP, "Phiếu", 1, 20000, 20000);
					dsHoaDonChiTiet.add(hdctPhieu);
				}
			}

			// Dòng giảm giá đối tượng trẻ em
			if (listVeMoi.get(i).getVe().getKhachHang().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
				String hdctGiamDTID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctGiamDT = new HoaDonChiTiet(hdctGiamDTID, hoaDon, "Giảm giá đối tượng trẻ em",
						LoaiDichVu.KHUYEN_MAI, "", 1, listVeMoi.get(i).getGiamDoiTuong(),
						listVeMoi.get(i).getGiamDoiTuong());
				dsHoaDonChiTiet.add(hdctGiamDT);

			}

			// Dòng phí đổi vé
			String hdctLePhiID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctLePhi = new HoaDonChiTiet(hdctLePhiID, hoaDon, "Lệ phí đổi vé", LoaiDichVu.PHI_DOI, 1,
					listVeDoi.get(i).getLePhiDoiVe(), listVeDoi.get(i).getLePhiDoiVe());
			dsHoaDonChiTiet.add(hdctLePhi);
		}
		return dsHoaDonChiTiet;
	}

	/**
	 * @param conn
	 * @param bookingSession
	 */
	public boolean themHoaDon(Connection conn, HoaDon hoaDon) throws Exception {
		return hoaDonDAO.insertHoaDon(conn, hoaDon);
	}

	/**
	 * @param conn
	 * @param dsHoaDonChiTiet
	 */
	public void themCacHoaDonChiTiet(Connection conn, List<HoaDonChiTiet> dsHoaDonChiTiet) throws Exception {
		for (HoaDonChiTiet hdct : dsHoaDonChiTiet) {
			hoaDonChiTietDAO.insertHoaDonChiTiet(conn, hdct);
		}
	}

	/**
	 * @param nhanVien
	 * @return
	 */
	public List<HoaDon> layCacHoaDonTheoNhanVienID(NhanVien nhanVien) {
		return hoaDonDAO.getHoaDonByNhanVien(nhanVien);
	}

	/**
	 * @param loaiHD
	 * @param khachHang
	 * @param searchID
	 * @param tuNgay
	 * @param denNgay
	 * @param hinhThucTT
	 * @return
	 */
	public List<HoaDon> locHoaDonTheoCacTieuChi(NhanVien nhanVien, String loaiHD, String khachHang, String khachHangID,
			Date tuNgay, Date denNgay, String hinhThucTT) {
		return hoaDonDAO.searchHoaDonByFilter(nhanVien, loaiHD, khachHang, khachHangID, tuNgay, denNgay, hinhThucTT);
	}

	/**
	 * @param nhanVien
	 * @param keyword
	 * @param type
	 * @return
	 */
	public List<HoaDon> layHoaDonTheoKeyWord(NhanVien nhanVien, String keyword, String type) {
		return hoaDonDAO.searchHoaDonByKeyword(nhanVien, keyword, type);
	}

	/**
	 * @param keyword
	 * @return
	 */
	public List<String> layTop10HoaDonID(String keyword) {
		return hoaDonDAO.getTop10HoaDonID(keyword);
	}

	/**
	 * @param keyword
	 * @return
	 */
	public List<String> layTop10KhachHangID(String keyword) {
		return hoaDonDAO.getTop10KhachHangID(keyword);

	}

	/**
	 * @param keyword
	 * @return
	 */
	public List<String> layTop10MaGD(String keyword) {
		return hoaDonDAO.getTop10MaGD(keyword);
	}

	/**
	 * @param hoaDonID
	 * @return
	 */
	public List<HoaDonChiTiet> layCacHoaDonChiTietTheoHoaDonID(String hoaDonID) {
		return hoaDonChiTietDAO.getHoaDonChiTietByHoaDonID(hoaDonID);
	}
}