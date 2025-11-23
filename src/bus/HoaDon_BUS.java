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
import entity.Ve;
import entity.type.LoaiDichVu;
import entity.type.LoaiDoiTuong;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.ExchangeSession;

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

	/**
	 * @param exchangeSession
	 * @return
	 */
	public HoaDon taoHoaDon(ExchangeSession exchangeSession) {
		String hdID = "HD-" + exchangeSession.getDonDatCho().getDonDatChoID();
		LocalDateTime now = LocalDateTime.now();
		HoaDon hoaDon = new HoaDon(hdID, exchangeSession.getKhachHang(), exchangeSession.getNhanVien(), now,
				exchangeSession.getGiaoDichThanhToan().getTongTien(), exchangeSession.getGiaoDichThanhToan().getMaGD(),
				exchangeSession.getGiaoDichThanhToan().getTienNhan(),
				exchangeSession.getGiaoDichThanhToan().getTienHoan(), true, true);

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
	 * @param hoaDon
	 * @param listVeMoi
	 * @return
	 */
	public List<HoaDonChiTiet> taoCacHoaDonChiTiet(HoaDon hoaDon, List<VeSession> listVeMoi) {
		List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
		int stt = 0;
		for (VeSession ve : listVeMoi) {
			String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, hoaDon, ve.getVe(), "Vé HK: " + ve.toString(),
					LoaiDichVu.VE_BAN, "Vé", 1, ve.getGia(), ve.getGia());
			dsHoaDonChiTiet.add(hdctVe);

			if (ve.getPhieuDungPhongVIP() != null) {
				String hdctPhieuID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, hoaDon, ve.getPhieuDungPhongVIP(),
						"Phiếu dùng phòng chờ VIP Ga Sài Gòn", LoaiDichVu.PHONG_VIP, "Phiếu", 1, ve.getPhongChoVIP(),
						ve.getPhongChoVIP());
				dsHoaDonChiTiet.add(hdctPhieu);
			}

			if (ve.getHanhKhach().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
				String hdctGiamDTID = hoaDon.getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctGiamDT = new HoaDonChiTiet(hdctGiamDTID, hoaDon, "Giảm giá đối tượng trẻ em",
						LoaiDichVu.KHUYEN_MAI, "", 1, ve.getGiamDoiTuong(), ve.getGiamDoiTuong());
				dsHoaDonChiTiet.add(hdctGiamDT);

			}
		}
		return dsHoaDonChiTiet;
	}

	public List<HoaDonChiTiet> taoCacHoaDonChiTiet(Connection conn, HoaDon hoaDon, List<Ve> listVe) {
		List<HoaDonChiTiet> dsHoaDonChiTiet = new ArrayList<HoaDonChiTiet>();
		int stt = 0;

		for (Ve ve : listVe) {
			String hdctVeID = hoaDon.getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, hoaDon, ve, "Điều chỉnh giảm theo BB trả vé số: 2177975",
					LoaiDichVu.VE_HOAN, "Vé", 1, -ve.getGia(), -ve.getGia());
			dsHoaDonChiTiet.add(hdctVe);

			PhieuDungPhongVIP phieu = phieuDungPhongVIPDAO.getPhieuDungPhongVIPByVeID(conn, ve.getVeID());
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