package bus;
/*
 * @(#) HoaDon_BUS.java  1.0  [1:06:29 PM] Nov 2, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.HoaDonChiTiet_DAO;
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 2, 2025
 * @version: 1.0
 */
import dao.HoaDon_DAO;
import entity.GiaoDichThanhToan;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.type.LoaiDichVu;
import entity.type.LoaiDoiTuong;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;

public class HoaDon_BUS {
	private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
	private final HoaDonChiTiet_DAO hoaDonChiTietDAO = new HoaDonChiTiet_DAO();

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
	 * @param bookingSession
	 */
	public boolean themHoaDon(HoaDon hoaDon) {
		return hoaDonDAO.createHoaDon(hoaDon);
	}

	/**
	 * @param giaoDichThanhToan
	 * @param bookingSession
	 * @return
	 */
	public List<HoaDonChiTiet> taoCacHoaDonChiTiet(BookingSession bookingSession, GiaoDichThanhToan giaoDichThanhToan) {
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

	/**
	 * @param dsHoaDonChiTiet
	 */
	public void themCacHoaDonChiTiet(List<HoaDonChiTiet> dsHoaDonChiTiet) {
		for (HoaDonChiTiet hdct : dsHoaDonChiTiet) {
			hoaDonChiTietDAO.createHoaDonChiTiet(hdct);
		}
	}
}