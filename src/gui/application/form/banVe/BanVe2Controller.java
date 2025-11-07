package gui.application.form.banVe;
/*
 * @(#) PanelBanVe2Controller.java  1.0  [12:05:37 PM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import bus.DatCho_BUS;
import bus.HoaDon_BUS;
import bus.KhachHang_BUS;
import bus.PhieuDungPhongVIP_BUS;
import bus.ThanhToan_BUS;
import bus.Ve_BUS;
import entity.DonDatCho;
import entity.GiaoDichThanhToan;
import entity.HoaDon;
import entity.HoaDonChiTiet;
import entity.PhieuDungPhongVIP;
import entity.Ve;
import entity.type.LoaiDoiTuong;
import gui.application.PdfTicketExporter;

/**
 * Controller (Mediator) cho PanelBanVe2. Nhiệm vụ: 1. Lấy dữ liệu từ
 * BookingSession. 2. Đổ dữ liệu vào PanelBuoc4 (Xác nhận) và PanelBuoc5 (Chi
 * tiết giá). 3. Lắng nghe sự kiện "Xác nhận Thanh toán" từ PanelBuoc5. 4. Báo
 * cho Wizard (PanelBanVe) khi thanh toán hoàn tất.
 */
public class BanVe2Controller {
	private final PanelBanVe wizardView;
	private final PanelBanVe2 view;
	private final PanelBuoc4 p4;
	private final PanelBuoc5 p5;

	private final DatCho_BUS datChoBUS = new DatCho_BUS();
	private final Ve_BUS veBUS = new Ve_BUS();
	private final PhieuDungPhongVIP_BUS phieuDungPhongChoVIPBUS = new PhieuDungPhongVIP_BUS();
	private final ThanhToan_BUS thanhToanBUS = new ThanhToan_BUS();
	private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
	private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();

	private final BookingSession bookingSession;

	// Listener để báo cho wizard chính (PanelBanVe) biết khi thanh toán xong
	private Runnable onPaymentSuccessListener;

	public BanVe2Controller(PanelBanVe wizardView, PanelBanVe2 view, BookingSession session) {
		this.wizardView = wizardView;

		this.view = view;
		this.bookingSession = session;

		this.p4 = view.getPanelBuoc4();
		this.p5 = view.getPanelBuoc5();

		// Khởi tạo logic liên kết
		initMediatorLogic();
	}

	/**
	 * Được gọi bởi PanelBanVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
	 * liệu từ session, tính toán và đổ vào Buoc4, Buoc5.
	 */
	public void loadDataForConfirmation() {
		// 1. Đặt lại trạng thái
		p4.setComponentsEnabled(true);
		p5.setComponentsEnabled(true);

		// 2. Tải dữ liệu vào bảng xác nhận (Buoc4)
		p4.hienThiThongTin(bookingSession);

		// 3. Tính toán chi tiết thanh toán
		int tongTienVe = 0;
		double giamGiaDT = 0;
		int khuyenMai = 0;
		int dichVu = 0;

		List<VeSession> allTickets = new ArrayList<>(bookingSession.getOutboundSelectedTickets());
		if (bookingSession.isRoundTrip()) {
			allTickets.addAll(bookingSession.getReturnSelectedTickets());
		}

		for (VeSession ve : allTickets) {
			tongTienVe += ve.getGia();
			dichVu += ve.getPhongChoVIP();
			khuyenMai += ve.getGiam();

			// Giảm giá đối tượng ở đây
			if (ve.getHanhKhach().getLoaiDoiTuong() == LoaiDoiTuong.TRE_EM) {
				ve.setGiamDoiTuong((int) (Math.round((ve.getGia() * 0.25) / 1000) * 1000));
				giamGiaDT += ve.getGiamDoiTuong();
			}
		}

		// 4. Đẩy chi tiết thanh toán vào Buoc5
		p5.setChiTietThanhToan(tongTienVe, (int) giamGiaDT, khuyenMai, dichVu);
	}

	/**
	 * Hàm nội bộ để kết nối logic giữa Buoc4 và Buoc5
	 */
	private void initMediatorLogic() {
		// Lắng nghe nút thanh toán từ PanelBuoc5
		JButton payButtonCash = p5.getBtnXacNhanVaInCash();
		JButton payButtonQR = p5.getBtnXacNhanVaInQR();

		ActionListener paymentListener = e -> {
			// TODO: Gọi BUS để thực hiện lưu CSDL (lưu Vé, Hóa đơn, Giao dịch...)
			boolean isThanhToanTienMat = true;
			if (payButtonQR.isSelected()) {
				isThanhToanTienMat = false;
			}

			// Lưu thông tin thanh toán
			double tongTien = p5.getTongThanhToan();
			double tienNhan = p5.getTienKhachDua();
			double tienHoan = tienNhan - tongTien;
			// TODO: xu ly lay ma giao dich
			String maGiaoDich = "GDTEST";
			boolean trangThai = true;
			GiaoDichThanhToan giaoDichThanhToan = new GiaoDichThanhToan(tienNhan, tienHoan, maGiaoDich, tongTien,
					isThanhToanTienMat, trangThai);
			thanhToanBUS.luuThongTinThanhToan(giaoDichThanhToan);
			bookingSession.setGiaoDichThanhToan(giaoDichThanhToan);

			// Lưu đơn đặt chỗ
			DonDatCho donDatCho = datChoBUS.taoDonDatCho(bookingSession);
			datChoBUS.themDonDatCho(donDatCho);
			bookingSession.setDonDatCho(donDatCho);

			// Lưu các vé
			List<Ve> dsVe = veBUS.taoCacVeVaThemVaoBookingSession(donDatCho, bookingSession);
			veBUS.themCacVe(dsVe);

			// Lưu các phiếu dùng phòng VIP
			List<PhieuDungPhongVIP> dsPhieu = phieuDungPhongChoVIPBUS.taoCacPhieuDungPhongChoVIP(bookingSession);
			phieuDungPhongChoVIPBUS.themCacPhieuDungPhongChoVIP(dsPhieu);

			// Lưu hóa đơn
			HoaDon hoaDon = hoaDonBUS.taoHoaDon(bookingSession);
			hoaDonBUS.themHoaDon(hoaDon);
			bookingSession.setHoaDon(hoaDon);

			// Lưu hóa đơn chi tiết
			List<HoaDonChiTiet> dsHoaDonChiTiet = hoaDonBUS.taoCacHoaDonChiTiet(bookingSession, giaoDichThanhToan);
			hoaDonBUS.themCacHoaDonChiTiet(dsHoaDonChiTiet);

			// Giả sử lưu thành công
			boolean saveSuccess = true;

			for (VeSession v : bookingSession.getOutboundSelected()) {
				if (khachHangBUS.timKiemKhachHangTheoSoGiayTo(v.getHanhKhach().getSoGiayTo()) == null) {
					khachHangBUS.themKhachHang(v.getHanhKhach());
				}
			}
			for (VeSession v : bookingSession.getReturnSelected()) {
				if (khachHangBUS.timKiemKhachHangTheoSoGiayTo(v.getHanhKhach().getSoGiayTo()) == null) {
					khachHangBUS.themKhachHang(v.getHanhKhach());
				}
			}

			if (saveSuccess) {
				// a. Vô hiệu hóa PanelBuoc5
				p5.setComponentsEnabled(false);

				// Xuất file pdf
				PdfTicketExporter exporter = new PdfTicketExporter();
				exporter.exportTicketsToPdf(bookingSession);

				// b. Báo cho wizard chính (PanelBanVe) biết để chuyển sang bước Hoàn tất
				if (wizardView != null) {
					// Cho panelBuoc6 load data ở đây nếu cần
					// wizardView.getPanelBuoc6().loadCompletionData(bookingSession);

					// Yêu cầu PanelBanVe chuyển sang card "complete"
					wizardView.showPanel("complete");
				}
			} else {
				JOptionPane.showMessageDialog(view, "Lỗi khi lưu thông tin thanh toán!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		};

		if (payButtonCash != null) {
			payButtonCash.addActionListener(paymentListener);
		}
		if (payButtonQR != null) {
			payButtonQR.addActionListener(paymentListener);
		}
	}
}