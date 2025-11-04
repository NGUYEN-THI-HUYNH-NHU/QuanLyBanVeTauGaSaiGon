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
		List<VeSession> dsVeDi = bookingSession.getOutboundSelected();
		List<VeSession> dsVeVe = bookingSession.getReturnSelected();
		for (int i = 0; i < dsVeDi.size(); i++) {
			String hdctID = bookingSession.getHoaDon().getHoaDonID() + "-" + i;
			HoaDonChiTiet hdct = new HoaDonChiTiet(hdctID, bookingSession.getHoaDon(), dsVeDi.get(i).getVe(),
					"Vé HK: " + dsVeDi.get(i).toString(), LoaiDichVu.VE_BAN, "Vé", 1, dsVeDi.get(i).getGia(),
					dsVeDi.get(i).getGia());
			dsHoaDonChiTiet.add(hdct);
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