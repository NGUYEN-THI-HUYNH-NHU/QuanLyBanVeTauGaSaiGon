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
import gui.application.form.hoanVe.VeHoanRow;

public class HoaDon_BUS {
	private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
	private final HoaDonChiTiet_DAO hoaDonChiTietDAO = new HoaDonChiTiet_DAO();
	private final PhieuDungPhongVIP_DAO phieuDungPhongVIPDAO = new PhieuDungPhongVIP_DAO();

	public HoaDon taoHoaDon(BookingSession bookingSession) {
		String hdID = "HD-" + bookingSession.getDonDatCho().getDonDatChoID();
		LocalDateTime now = LocalDateTime.now();
		HoaDon hoaDon = new HoaDon(hdID, bookingSession.getKhachHang(), bookingSession.getNhanVien(), now,
				bookingSession.getGiaoDichThanhToan().getTongTien(), bookingSession.getGiaoDichThanhToan().getMaGD(),
				bookingSession.getGiaoDichThanhToan().getTienNhan(),
				bookingSession.getGiaoDichThanhToan().getTienHoan(), true, true);

		return hoaDon;
	}

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
		hoaDon.setTrangThai(true);

		return hoaDon;
	}

	/**
	 * @param conn
	 * @param bookingSession
	 */
	public boolean themHoaDon(Connection conn, HoaDon hoaDon) {
		return hoaDonDAO.insertHoaDon(conn, hoaDon);
	}

	/**
	 * @param giaoDichThanhToan
	 * @param bookingSession
	 * @return
	 */
	public List<HoaDonChiTiet> taoCacHoaDonChiTiet(BookingSession bookingSession) {
		List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
		List<VeSession> dsVe = bookingSession.getAllSelectedTickets();
		int stt = 0;
		for (VeSession ve : dsVe) {
			String hdctVeID = bookingSession.getHoaDon().getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, bookingSession.getHoaDon(), ve.getVe(),
					"Vé HK: " + ve.toString(), LoaiDichVu.VE_BAN, "Vé", 1, ve.getGia(), ve.getGia());
			dsHoaDonChiTiet.add(hdctVe);

			if (ve.getPhieuDungPhongVIP() != null) {
				String hdctPhieuID = bookingSession.getHoaDon().getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, bookingSession.getHoaDon(),
						ve.getPhieuDungPhongVIP(), "Phiếu dùng phòng chờ VIP Ga Sài Gòn", LoaiDichVu.PHONG_VIP, "Phiếu",
						1, ve.getPhongChoVIP(), ve.getPhongChoVIP());
				dsHoaDonChiTiet.add(hdctPhieu);
			}

			if (ve.getHanhKhach().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
				String hdctGiamDTID = bookingSession.getHoaDon().getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctGiamDT = new HoaDonChiTiet(hdctGiamDTID, bookingSession.getHoaDon(),
						"Giảm giá đối tượng trẻ em", LoaiDichVu.KHUYEN_MAI, "", 1, ve.getGiamDoiTuong(),
						ve.getGiamDoiTuong());
				dsHoaDonChiTiet.add(hdctGiamDT);

			}
		}
		return dsHoaDonChiTiet;
	}

	public List<HoaDonChiTiet> taoCacHoaDonChiTiet(Connection conn, HoaDon hoaDon, List<VeHoanRow> listVeHoanRow) {
		List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
		int stt = 0;

		for (VeHoanRow r : listVeHoanRow) {
			String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, hoaDon, r.getVe(),
					"Điều chỉnh giảm theo BB trả vé số: 2177975", LoaiDichVu.VE_HOAN, "Vé", 1, -r.getVe().getGia(),
					-r.getVe().getGia());
			dsHoaDonChiTiet.add(hdctVe);

			PhieuDungPhongVIP phieu = phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(conn, r.getVe().getVeID());
			if (phieu != null) {
				String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, phieu,
						"Điều chỉnh giảm theo BB trả vé số: 2177975", LoaiDichVu.PHIEU_HOAN, "Phiếu", 1, -200000,
						-200000);
				dsHoaDonChiTiet.add(hdctPhieu);
			}
		}

		return dsHoaDonChiTiet;
	}

	/**
	 * @param conn
	 * @param dsHoaDonChiTiet
	 */
	public void themCacHoaDonChiTiet(Connection conn, List<HoaDonChiTiet> dsHoaDonChiTiet) {
		for (HoaDonChiTiet hdct : dsHoaDonChiTiet) {
			hoaDonChiTietDAO.insertHoaDonChiTiet(conn, hdct);
		}
	}
}