package bus;
/*
 * @(#) DoiVe_BUS.java  1.0  [8:17:38 PM] Nov 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 21, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import connectDB.ConnectDB;
import entity.DonDatCho;
import entity.GiaoDichHoanDoi;
import entity.GiaoDichThanhToan;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.TrangThaiPDPVIP;
import entity.type.TrangThaiPhieuGiuCho;
import entity.type.TrangThaiVe;
import gui.application.form.doiVe.ExchangeSession;
import gui.application.form.doiVe.VeDoiRow;

public class DoiVe_BUS {
	private final BanVe_BUS banVe_BUS = new BanVe_BUS();
	private final DatCho_BUS datChoBUS = new DatCho_BUS();
	private final Ve_BUS veBUS = new Ve_BUS();
	private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
	private final PhieuDungPhongVIP_BUS phieuDungPhongVIPBUS = new PhieuDungPhongVIP_BUS();
	private final GiaoDichHoanDoi_BUS giaoDichHoanDoiBUS = new GiaoDichHoanDoi_BUS();
	private final PhieuGiuCho_BUS phieuGiuChoBUS = new PhieuGiuCho_BUS();
	private final KhuyenMai_BUS khuyenMaiBUS = new KhuyenMai_BUS();

	/**
	 * @param exchangeSession
	 * @return
	 */
	public boolean thucHienDoiVe(ExchangeSession exchangeSession) throws Exception {
		Connection conn = null;
		try {
			// 1. Lấy kết nối và BẮT ĐẦU TRANSACTION
			conn = ConnectDB.getInstance().getConnection();
			conn.setAutoCommit(false);

			// --- BẮT ĐẦU CHUỖI GIAO DỊCH ---
			List<Ve> listVeDoi = new ArrayList<Ve>();
			for (VeDoiRow r : exchangeSession.getListVeCuCanDoi()) {
				listVeDoi.add(r.getVe());
			}
			KhachHang khachHang = exchangeSession.getKhachHang();
			NhanVien nhanVien = exchangeSession.getNhanVien();

			// 1. Tạo và Lưu Đơn Đặt Chỗ cho các vé mới
			DonDatCho donDatChoMoi = datChoBUS.taoDonDatCho(nhanVien, khachHang);
			datChoBUS.themDonDatCho(conn, donDatChoMoi);
			exchangeSession.setDonDatChoMoi(donDatChoMoi);

			// 2. Tạo và Lưu Hóa đơn đổi vé
			HoaDon hoaDon = hoaDonBUS.taoHoaDonDoiVe(exchangeSession);
			hoaDonBUS.themHoaDon(conn, hoaDon);
			exchangeSession.setHoaDon(hoaDon);

			// 3. Lưu GiaoDichThanhToan
			GiaoDichThanhToan gdtt = exchangeSession.getGiaoDichThanhToan();
			banVe_BUS.luuThongTinThanhToan(conn, gdtt);

			// 4. Tạo và Lưu Vé mới (Batch Insert)
			List<Ve> dsVe = veBUS.taoCacVeVaThemVaoExchangeSession(exchangeSession);
			veBUS.themCacVe(conn, dsVe);

			// 5. Tạo và Lưu Phiếu VIP (Batch Insert)
			List<PhieuDungPhongVIP> dsPhieu = phieuDungPhongVIPBUS
					.taoCacPhieuDungPhongChoVIP(exchangeSession.getListVeMoiDangChon());
			phieuDungPhongVIPBUS.themCacPhieuDungPhongChoVIP(conn, dsPhieu);

			// 6. Gán các sử dụng khuyến mãi cho các vé áp dụng khuyến mãi
			khuyenMaiBUS.ganDanhSachSuDungKhuyenMai(exchangeSession.getListVeMoiDangChon());

			// 7. Tạo và Lưu Hóa Đơn Chi Tiết (Batch Insert)
			List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonBUS.taoCacHoaDonChiTietDoiVe(conn, hoaDon, exchangeSession);
			hoaDonBUS.themCacHoaDonChiTiet(conn, listHoaDonChiTiet);

			// 8. Lưu các sử dụng khuyến mãi cho vé mới (đã kèm giảm số lượng khuyến mãi
			// tương ứng)
			khuyenMaiBUS.themDanhSachSuDungKhuyenMai(conn, exchangeSession.getListVeMoiDangChon());

			// 9. Cập nhật Phiếu Giữ Chỗ cho vé mới (sau khi mọi thứ thành công)
			datChoBUS.capNhatPhieuGiuCho(conn, exchangeSession.getPhieuGiuCho(), TrangThaiPhieuGiuCho.XAC_NHAN);
			datChoBUS.capNhatCacPhieuGiuChoChiTiet(conn, exchangeSession.getPhieuGiuCho(),
					TrangThaiPhieuGiuCho.XAC_NHAN);

			// 10. Cập nhật trạng thái các vé cũ (và phiếu dùng phòng chờ VIP nếu có)
			veBUS.capNhatTrangThaiVe(conn, listVeDoi, TrangThaiVe.DA_DOI);
			phieuDungPhongVIPBUS.capNhatPhieuDungPhongChoVIP(conn, listVeDoi, TrangThaiPDPVIP.DA_HUY);

			// 11. Tạo và lưu các Giao dịch đổi vé (giao dịch hoàn đổi)
			List<GiaoDichHoanDoi> dsGdhd = giaoDichHoanDoiBUS.taoCacGiaoDichDoiVe(exchangeSession);
			giaoDichHoanDoiBUS.themCacGiaoDichHoanDoi(conn, dsGdhd);

			// 12. Set trạng thái các phiếu giữ chỗ thành HET_GIU
			phieuGiuChoBUS.huyCacPhieuGiuChoChiTiet(conn, listVeDoi, TrangThaiPhieuGiuCho.HET_GIU);

			// 13. Cập nhật các khuyến mãi đã sử dụng ở các vé cũ
			Map<String, Integer> mapKhuyenMaiHoan = khuyenMaiBUS.layDanhSachKhuyenMaiCanHoan(conn, listVeDoi);
			khuyenMaiBUS.capNhatTrangThaiSDKMCuaVe(conn, listVeDoi);
			for (Map.Entry<String, Integer> entry : mapKhuyenMaiHoan.entrySet()) {
				String kmID = entry.getKey();
				int soLuongCanCong = entry.getValue();
				khuyenMaiBUS.congSoLuongKhuyenMai(conn, kmID, soLuongCanCong);
			}

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
			throw new Exception("Lỗi khi xử lý đổi vé: " + e.getMessage());
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
