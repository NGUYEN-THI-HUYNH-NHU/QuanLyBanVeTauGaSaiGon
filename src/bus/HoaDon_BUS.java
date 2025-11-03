package bus;
/*
 * @(#) HoaDon_BUS.java  1.0  [1:06:29 PM] Nov 2, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.time.LocalDateTime;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 2, 2025
 * @version: 1.0
 */
import dao.HoaDon_DAO;
import entity.GiaoDichThanhToan;
import entity.HoaDon;
import gui.application.form.banVe.BookingSession;

public class HoaDon_BUS {
	private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();

	/**
	 * @param bookingSession
	 */
	public boolean themHoaDon(BookingSession bookingSession, GiaoDichThanhToan giaoDichThanhToan) {
//		private String hoaDonID;
//		private KhachHang khachHang;
//		private NhanVien nhanVien;
//		private LocalDateTime thoiDiemTao;
//		private double tongTien;
//		private String maGD;
//		private double tienNhan;
//		private double tienHoan;
//		private boolean isThanhToanTienMat;
//		private boolean trangThai;

		String hdID = "HD-" + bookingSession.getDonDatCho().getDonDatChoID();
		LocalDateTime now = LocalDateTime.now();
		HoaDon hoaDon = new HoaDon(hdID, bookingSession.getKhachHang(), bookingSession.getNhanVien(), now,
				giaoDichThanhToan.getTongTien(), giaoDichThanhToan.getMaGD(), giaoDichThanhToan.getTienNhan(),
				giaoDichThanhToan.getTienHoan(), true, true);

		return hoaDonDAO.createHoaDon(hoaDon);
	}

}
