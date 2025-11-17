package bus;
/*
 * @(#) ThanhToan_BUS.java  1.0  [5:12:09 PM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 1, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import connectDB.ConnectDB;
import dao.GiaoDichThanhToan_DAO;
import entity.DonDatCho;
import entity.GiaoDichThanhToan;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;

public class BanVe_BUS {
	private final GiaoDichThanhToan_DAO gdttDAO = new GiaoDichThanhToan_DAO();

	private final DatCho_BUS datChoBUS = new DatCho_BUS();
	private final Ve_BUS veBUS = new Ve_BUS();
	private final PhieuDungPhongVIP_BUS phieuDungPhongChoVIPBUS = new PhieuDungPhongVIP_BUS();
	private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
	private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();

	/**
	 * @param giaoDichThanhToan
	 */
	public void luuThongTinThanhToan(Connection conn, GiaoDichThanhToan giaoDichThanhToan) {
		gdttDAO.createGiaoDichThanhToan(conn, giaoDichThanhToan);
	}

	/**
	 * Gói toàn bộ nghiệp vụ thanh toán vào một Transaction CSDL duy nhất.
	 * 
	 * @param session Chứa toàn bộ thông tin (vé, khách hàng, người mua, PGC...)
	 * @return true nếu tất cả các bước thành công
	 */
	public boolean xacNhanThanhToanVaLuuVe(BookingSession session) throws Exception {
		Connection conn = null;
		try {
			// 1. Lấy kết nối và BẮT ĐẦU TRANSACTION
			conn = ConnectDB.getInstance().getConnection();
			conn.setAutoCommit(false);

			// --- BẮT ĐẦU CHUỖI GIAO DỊCH ---

			// 2. Lưu/Cập nhật Khách Hàng (Người mua + các Hành khách)
			khachHangBUS.themHoacCapNhatKhachHang(conn, session.getKhachHang());
			for (VeSession v : session.getAllSelectedTickets()) {
				khachHangBUS.themHoacCapNhatKhachHang(conn, v.getHanhKhach());
			}

			// 3. Tạo và Lưu Đơn Đặt Chỗ
			DonDatCho donDatCho = datChoBUS.taoDonDatCho(session);
			datChoBUS.themDonDatCho(conn, donDatCho);
			session.setDonDatCho(donDatCho);

			// 4. Tạo và Lưu Hóa đơn
			HoaDon hoaDon = hoaDonBUS.taoHoaDon(session);
			hoaDonBUS.themHoaDon(conn, hoaDon);
			session.setHoaDon(hoaDon);

			// 5. Lưu GiaoDichThanhToan
			GiaoDichThanhToan gdtt = session.getGiaoDichThanhToan();
			luuThongTinThanhToan(conn, gdtt);

			// 6. Tạo và Lưu Vé (Batch Insert)
			List<Ve> dsVe = veBUS.taoCacVeVaThemVaoBookingSession(donDatCho, session);
			veBUS.themCacVe(conn, dsVe);

			// 7. Tạo và Lưu Phiếu VIP (Batch Insert)
			List<PhieuDungPhongVIP> dsPhieu = phieuDungPhongChoVIPBUS.taoCacPhieuDungPhongChoVIP(session);
			phieuDungPhongChoVIPBUS.themCacPhieuDungPhongChoVIP(conn, dsPhieu);

			// 8. Tạo và Lưu Hóa Đơn Chi Tiết (Batch Insert)
			List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonBUS.taoCacHoaDonChiTiet(session);
			hoaDonBUS.themCacHoaDonChiTiet(conn, listHoaDonChiTiet);

			// 9. Cập nhật Phiếu Giữ Chỗ (sau khi mọi thứ thành công)
			datChoBUS.capNhatPhieuGiuCho(conn, session.getPhieuGiuCho(), TrangThaiPhieuGiuCho.XAC_NHAN);
			datChoBUS.capNhatCacPhieuGiuChoChiTiet(conn, session.getPhieuGiuCho(), TrangThaiPhieuGiuCho.XAC_NHAN);

			// --- KẾT THÚC GIAO DỊCH ---
			// Hoàn tất giao dịch
			conn.commit();
			return true;

		} catch (Exception e) {
			// Nếu có bất kỳ lỗi nào, hoàn tác tất cả thay đổi
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
			throw new Exception("Lỗi khi xử lý thanh toán: " + e.getMessage());
		} finally {
			// Trả lại trạng thái AutoCommit cho kết nối
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
					conn.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

}
