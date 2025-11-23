package bus;
/*
 * @(#) HoanVe_BUS.java  1.0  [11:03:30 AM] Nov 16, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 16, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.DonDatCho;
import entity.GiaoDichHoanDoi;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.KhachHang;
import entity.Ve;
import entity.type.TrangThaiPDPVIP;
import entity.type.TrangThaiPhieuGiuCho;
import entity.type.TrangThaiVe;
import gui.application.AuthService;
import gui.application.form.hoanVe.VeHoanRow;

public class HoanVe_BUS {
	private final Ve_BUS veBUS = new Ve_BUS();
	private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
	private final PhieuDungPhongVIP_BUS phieuDungPhongVIPBUS = new PhieuDungPhongVIP_BUS();
	private final GiaoDichHoanDoi_BUS giaoDichHoanDoiBUS = new GiaoDichHoanDoi_BUS();
	private final PhieuGiuChoChiTiet_BUS phieuGiuChoChiTietBUS = new PhieuGiuChoChiTiet_BUS();

	/**
	 * @param donDatCho
	 * @param khachHang
	 * @param listVeHoanRow
	 * @param tongTienHoan
	 * @return
	 */
	public boolean thucHienHoanVe(DonDatCho donDatCho, KhachHang khachHang, List<VeHoanRow> listVeHoanRow,
			double tongTienHoan) throws Exception {
		Connection conn = null;
		try {
			// 1. Lấy kết nối và BẮT ĐẦU TRANSACTION
			conn = ConnectDB.getInstance().getConnection();
			conn.setAutoCommit(false);

			// --- BẮT ĐẦU CHUỖI GIAO DỊCH ---
			List<Ve> listVe = new ArrayList<Ve>();
			for (VeHoanRow r : listVeHoanRow) {
				listVe.add(r.getVe());
			}

			// 1. Cập nhật trạng thái vé (và phiếu dùng phòng chờ VIP nếu có)
			veBUS.capNhatTrangThaiVe(conn, listVe, TrangThaiVe.DA_HOAN);
			phieuDungPhongVIPBUS.capNhatPhieuDungPhongChoVIP(conn, listVe, TrangThaiPDPVIP.DA_HUY);

			// 2. Tạo hóa đơn
			HoaDon hoaDon = hoaDonBUS.taoHoaDonHoanVe(donDatCho, khachHang, AuthService.getInstance().getCurrentUser(),
					tongTienHoan);
			hoaDonBUS.themHoaDon(conn, hoaDon);

			// 3. Tạo và Lưu Hóa Đơn Chi Tiết
			List<HoaDonChiTiet> dsHDCT = hoaDonBUS.taoCacHoaDonChiTiet(conn, hoaDon, listVe);
			hoaDonBUS.themCacHoaDonChiTiet(conn, dsHDCT);

			// 4. Tạo và lưu Giao dịch hoàn đổi
			List<GiaoDichHoanDoi> dsGdhd = giaoDichHoanDoiBUS.taoCacGiaoDichHoanVe(hoaDon,
					AuthService.getInstance().getCurrentUser(), listVeHoanRow);
			giaoDichHoanDoiBUS.themCacGiaoDichHoanDoi(conn, dsGdhd);

			// 5. Set trạng thái các phiếu giữ chỗ thành HET_GIU
			phieuGiuChoChiTietBUS.huyCacPhieuGiuChoChiTiet(conn, listVe, TrangThaiPhieuGiuCho.HET_GIU);

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
