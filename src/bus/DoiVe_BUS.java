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
	private final PhieuGiuChoChiTiet_BUS phieuGiuChoChiTietBUS = new PhieuGiuChoChiTiet_BUS();

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
			List<Ve> listVe = new ArrayList<Ve>();
			for (VeDoiRow r : exchangeSession.getListVeCuCanDoi()) {
				listVe.add(r.getVe());
			}
			KhachHang khachHang = exchangeSession.getKhachHang();
			NhanVien nhanVien = exchangeSession.getNhanVien();
			int tongTien = 0;

			// 3. Tạo và Lưu Đơn Đặt Chỗ
			DonDatCho donDatCho = datChoBUS.taoDonDatCho(nhanVien, khachHang);
			datChoBUS.themDonDatCho(conn, donDatCho);
			exchangeSession.setDonDatCho(donDatCho);

			// 4. Tạo và Lưu Hóa đơn
			HoaDon hoaDon = hoaDonBUS.taoHoaDon(exchangeSession);
			hoaDonBUS.themHoaDon(conn, hoaDon);
			exchangeSession.setHoaDon(hoaDon);

			// 5. Lưu GiaoDichThanhToan
			GiaoDichThanhToan gdtt = exchangeSession.getGiaoDichThanhToan();
			banVe_BUS.luuThongTinThanhToan(conn, gdtt);

			// 7. Tạo và Lưu Phiếu VIP (Batch Insert)
			List<PhieuDungPhongVIP> dsPhieu = phieuDungPhongVIPBUS.taoCacPhieuDungPhongChoVIP(exchangeSession);
			phieuDungPhongVIPBUS.themCacPhieuDungPhongChoVIP(conn, dsPhieu);

			// 6. Tạo và Lưu Vé (Batch Insert)
			List<Ve> dsVe = veBUS.taoCacVeVaThemVaoExchangeSession(exchangeSession);
			veBUS.themCacVe(conn, dsVe);

			// 8. Tạo và Lưu Hóa Đơn Chi Tiết (Batch Insert)
			List<HoaDonChiTiet> listHoaDonChiTiet = hoaDonBUS.taoCacHoaDonChiTiet(hoaDon,
					exchangeSession.getListVeMoiDangChon());
			hoaDonBUS.themCacHoaDonChiTiet(conn, listHoaDonChiTiet);

			// 9. Cập nhật Phiếu Giữ Chỗ (sau khi mọi thứ thành công)
			datChoBUS.capNhatPhieuGiuCho(conn, exchangeSession.getPhieuGiuCho(), TrangThaiPhieuGiuCho.XAC_NHAN);
			datChoBUS.capNhatCacPhieuGiuChoChiTiet(conn, exchangeSession.getPhieuGiuCho(),
					TrangThaiPhieuGiuCho.XAC_NHAN);

			// -----------------//

			// 1. Cập nhật trạng thái vé (và phiếu dùng phòng chờ VIP nếu có)
			veBUS.capNhatTrangThaiVe(conn, listVe, TrangThaiVe.DA_DOI);
			phieuDungPhongVIPBUS.capNhatPhieuDungPhongChoVIP(conn, listVe, TrangThaiPDPVIP.DA_HUY);

			// 2. Tạo hóa đơn
			HoaDon hoaDonHoanVe = hoaDonBUS.taoHoaDonHoanVe(donDatCho, khachHang, nhanVien, tongTien);
			hoaDonBUS.themHoaDon(conn, hoaDonHoanVe);

			// 3. Tạo và Lưu Hóa Đơn Chi Tiết
			List<HoaDonChiTiet> dsHDCT = hoaDonBUS.taoCacHoaDonChiTiet(conn, hoaDon, listVe);
			hoaDonBUS.themCacHoaDonChiTiet(conn, dsHDCT);

			// 4. Tạo và lưu Giao dịch hoàn đổi
			List<GiaoDichHoanDoi> dsGdhd = giaoDichHoanDoiBUS.taoCacGiaoDichDoiVe(exchangeSession);
			giaoDichHoanDoiBUS.themCacGiaoDichHoanDoi(conn, dsGdhd);

			// 5. Set trạng thái các phiếu giữ chỗ thành HET_GIU
			phieuGiuChoChiTietBUS.huyCacPhieuGiuChoChiTiet(conn, listVe, TrangThaiPhieuGiuCho.HET_GIU);

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
