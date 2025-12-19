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
import entity.DonDatCho;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPhieuGiuCho;
import gui.application.form.banVe.BookingSession;
import gui.application.form.banVe.VeSession;

public class BanVe_BUS {
	private final DatCho_BUS datChoBUS = new DatCho_BUS();
	private final Ve_BUS veBUS = new Ve_BUS();
	private final PhieuDungPhongVIP_BUS phieuDungPhongChoVIPBUS = new PhieuDungPhongVIP_BUS();
	private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
	private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();
	private final SuDungKhuyenMai_BUS suDungKhuyenMaiBUS = new SuDungKhuyenMai_BUS();
	private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();

	/**
	 * Gói toàn bộ nghiệp vụ thanh toán vào một Transaction CSDL duy nhất.
	 * 
	 * @param session Chứa toàn bộ thông tin (vé, khách hàng, người mua, PGC...)
	 * @return true nếu tất cả các bước thành công
	 */
	public boolean thucHienBanVe(BookingSession session) throws Exception {
		Connection conn = null;
		try {
			// 1. Lấy kết nối và BẮT ĐẦU TRANSACTION
			conn = ConnectDB.getInstance().getConnection();
			conn.setAutoCommit(false);

			// --- BẮT ĐẦU CHUỖI GIAO DỊCH ---
			// 2. Lưu/Cập nhật Khách Hàng (Người mua + các Hành khách)
			khachHangBUS.themHoacCapNhatKhachHang(conn, session.getKhachHang());
			for (VeSession v : session.getAllSelectedTickets()) {
				khachHangBUS.themHoacCapNhatKhachHang(conn, v.getVe().getKhachHang());
			}

			// 3. Tạo và Lưu Đơn Đặt Chỗ
			DonDatCho donDatCho = datChoBUS.taoDonDatCho(session.getNhanVien(), session.getKhachHang());
			datChoBUS.themDonDatCho(conn, donDatCho);
			session.setDonDatCho(donDatCho);

			// 4. Tạo và Lưu Hóa đơn
			HoaDon hoaDon = hoaDonBUS.taoHoaDon(session);
			hoaDonBUS.themHoaDon(conn, hoaDon);
			session.setHoaDon(hoaDon);

			// 6. Tạo và Lưu Vé (Batch Insert)
			List<Ve> dsVe = veBUS.taoCacVeVaThemVaoBookingSession(session);
			veBUS.themCacVe(conn, dsVe);

			// 7. Tạo và Lưu Phiếu VIP (Batch Insert)
			List<PhieuDungPhongVIP> dsPhieu = phieuDungPhongChoVIPBUS
					.taoCacPhieuDungPhongChoVIP(session.getAllSelectedTickets());
			phieuDungPhongChoVIPBUS.themCacPhieuDungPhongChoVIP(conn, dsPhieu);

			// 8. Gán các sử dụng khuyến mãi cho các vé áp dụng khuyến mãi
			khuyenMaiBUS.ganDanhSachSuDungKhuyenMai(session.getAllSelectedTickets());

			// 9. Tạo và Lưu Hóa Đơn Chi Tiết (khi tạo các hóa đơn chi tiết đã bao gồm gán
			// nó cho sử dụng khuyến mãi tương ứng)
			List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonBUS.taoCacHoaDonChiTietBanVe(session.getHoaDon(),
					session.getAllSelectedTickets());
			hoaDonBUS.themCacHoaDonChiTiet(conn, listHoaDonChiTiet);

			// 10. Lưu các sử dụng khuyến mãi (đã kèm giảm số lượng khuyến mãi tương ứng)
			khuyenMaiBUS.themDanhSachSuDungKhuyenMai(conn, session.getAllSelectedTickets());

			// 11. Cập nhật Phiếu Giữ Chỗ (sau khi mọi thứ thành công)
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
					datChoBUS.hoanTacGiuCho(session.getPhieuGiuCho());
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
