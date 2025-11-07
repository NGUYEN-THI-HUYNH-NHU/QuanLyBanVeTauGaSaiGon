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
//		List<VeSession> dsVeDi = bookingSession.getOutboundSelected();
//		List<VeSession> dsVeVe = bookingSession.getReturnSelected();
//		int i;
//		for (i = 0; i < dsVeDi.size(); i++) {
//			String hdctID = bookingSession.getHoaDon().getHoaDonID() + "-" + (i + 1);
//			HoaDonChiTiet hdct = new HoaDonChiTiet(hdctID, bookingSession.getHoaDon(), dsVeDi.get(i).getVe(),
//					"Vé HK: " + dsVeDi.get(i).toString(), LoaiDichVu.VE_BAN, "Vé", 1, dsVeDi.get(i).getGia(),
//					dsVeDi.get(i).getGia());
//			if (dsVeDi.get(i).getPhongChoVIP() > 0) {
//				
//			}
//			dsHoaDonChiTiet.add(hdct);
//		}
//		for (int j = 0; j < dsVeVe.size(); j++) {
//			String hdctID = bookingSession.getHoaDon().getHoaDonID() + "-" + (i + j + 1);
//			HoaDonChiTiet hdct = new HoaDonChiTiet(hdctID, bookingSession.getHoaDon(), dsVeVe.get(j).getVe(),
//					"Vé HK: " + dsVeVe.get(j).toString(), LoaiDichVu.VE_BAN, "Vé", 1, dsVeVe.get(j).getGia(),
//					dsVeVe.get(j).getGia());
//			dsHoaDonChiTiet.add(hdct);
//		}
//		return dsHoaDonChiTiet;
		List<VeSession> dsVe = bookingSession.getAllSelectedTickets();
		int stt = 0;
		for (int i = 0; i < dsVe.size(); i++) {
			String hdctVeID = bookingSession.getHoaDon().getHoaDonID() + "-" + (++stt);
			HoaDonChiTiet hdctVe = new HoaDonChiTiet(hdctVeID, bookingSession.getHoaDon(), dsVe.get(i).getVe(),
					"Vé HK: " + dsVe.get(i).toString(), LoaiDichVu.VE_BAN, "Vé", 1, dsVe.get(i).getGia(),
					dsVe.get(i).getGia() - dsVe.get(i).getGiam() - dsVe.get(i).getGiamDoiTuong());
			dsHoaDonChiTiet.add(hdctVe);
			if (dsVe.get(i).getPhieuDungPhongVIP() != null) {
				String hdctPhieuID = bookingSession.getHoaDon().getHoaDonID() + "-" + (++stt);
				HoaDonChiTiet hdctPhieu = new HoaDonChiTiet(hdctPhieuID, bookingSession.getHoaDon(),
						dsVe.get(i).getPhieuDungPhongVIP(), "Phiếu dùng phòng chờ VIP Ga Sài Gòn", LoaiDichVu.PHONG_VIP,
						"Phiếu", 1, dsVe.get(i).getPhongChoVIP(), dsVe.get(i).getPhongChoVIP());
				dsHoaDonChiTiet.add(hdctPhieu);
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