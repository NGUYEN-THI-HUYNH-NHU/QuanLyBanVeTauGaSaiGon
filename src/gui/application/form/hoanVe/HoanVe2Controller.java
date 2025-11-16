package gui.application.form.hoanVe;
/*
 * @(#) HoanVe2Controller.java  1.0  [3:22:09 PM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import bus.DatCho_BUS;
import bus.HoaDon_BUS;
import bus.PhieuDungPhongVIP_BUS;
import bus.ThanhToan_BUS;
import bus.Ve_BUS;
import entity.KhachHang;
import gui.application.PdfTicketExporter;

public class HoanVe2Controller {
	private final PanelHoanVe2 view;
	private final PanelHoanVeBuoc4 p4;
	private final PanelHoanVeBuoc5 p5;

	private final DatCho_BUS datChoBUS = new DatCho_BUS();
	private final Ve_BUS veBUS = new Ve_BUS();
	private final PhieuDungPhongVIP_BUS phieuDungPhongChoVIPBUS = new PhieuDungPhongVIP_BUS();
	private final ThanhToan_BUS thanhToanBUS = new ThanhToan_BUS();
	private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();

	private KhachHang khachHang;
	private List<VeHoanRow> listVeHoanRow;

	// Listener để báo cho wizard chính (PanelHoanVe) biết khi thanh toán xong
	private Runnable onPaymentSuccessListener;

	protected void addPanel2PaymentSuccessListener(Runnable listener) {
		this.onPaymentSuccessListener = listener;
	}

	public HoanVe2Controller(PanelHoanVe2 view) {
		this.view = view;

		this.p4 = view.getPanelHoanVeBuoc4();
		this.p5 = view.getPanelHoanVeBuoc5();

		// Khởi tạo logic liên kết
		initMediatorLogic();
	}

	/**
	 * Được gọi bởi PanelHoanVe TRƯỚC KHI panel này được hiển thị. Nhiệm vụ: Lấy dữ
	 * liệu từ session, tính toán và đổ vào Buoc4, Buoc5.
	 * 
	 */
	public void loadDataForConfirmation(KhachHang khachHang, List<VeHoanRow> listVeHoanRow) {
		this.khachHang = khachHang;
		this.listVeHoanRow = listVeHoanRow;

		// 1. Đặt lại trạng thái
		p4.setComponentsEnabled(true);
		p5.setComponentsEnabled(true);

		// 2. Tải dữ liệu vào bảng xác nhận (Buoc4)
		p4.hienThiThongTin(listVeHoanRow);

		// 3. Tính toán chi tiết thanh toán
		int tongTienVe = 0;
		int tongPhiHoan = 0;

		for (VeHoanRow row : listVeHoanRow) {
			tongTienVe += row.getVe().getGia();
			tongPhiHoan += row.getLePhiHoanVe();
		}

		// 4. Đẩy chi tiết thanh toán vào Buoc5
		p5.setChiTietThanhToan(tongTienVe, tongPhiHoan);
	}

	/**
	 * Hàm nội bộ để kết nối logic giữa Buoc4 và Buoc5
	 */
	private void initMediatorLogic() {
		// Lắng nghe nút thanh toán từ PanelBuoc5
		JButton payButtonCash = p5.getBtnXacNhanVaInCash();

		ActionListener paymentListener = e -> {
			boolean isThanhToanTienMat = true;

			// TODO: thuc hien luu thay doi tren DB
//			--set lai trang thai ve thanh 'DA_HOAN'
//			select * from Ve
//			--tao hoa don co tienNhan = 0 va tienHoan = @tienHoan
//			select * from HoaDon
//			--tao cac hoa don chi tiet
//			select * from HoaDonChiTiet
//			--tao giao dich hoan doi 
//			select * from GiaoDichHoanDoi

			// Giả sử lưu thành công
			boolean saveSuccess = true;

			if (saveSuccess) {
				// a. Vô hiệu hóa PanelBuoc5
				p5.setComponentsEnabled(false);

				// Xuất file pdf
				PdfTicketExporter exporter = new PdfTicketExporter();
//				exporter.exportTicketsToPdf(bookingSession);

				// b. Báo cho wizard chính (PanelBanVe) biết để chuyển sang bước Hoàn tất
				if (onPaymentSuccessListener != null) {
					onPaymentSuccessListener.run();
				}
			} else {
				JOptionPane.showMessageDialog(view, "Lỗi khi lưu thông tin thanh toán!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
			}
		};

		if (payButtonCash != null) {
			payButtonCash.addActionListener(paymentListener);
		}
	}
}