package gui.application.form.doiVe;
/*
 * @(#) PanelBuoc3Controller.java  1.0  [8:06:26 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.util.List;
import java.util.function.Consumer;

import javax.swing.JOptionPane;

import bus.DatCho_BUS;
import gui.application.form.banVe.VeSession;

public class DoiVeBuoc6Controller {

	private final PanelDoiVeBuoc6 view;

	private final ExchangeSession exchangeSession;
	private final DatCho_BUS datChoBUS = new DatCho_BUS();

	// Listeners để báo cho Controller Mediator (BanVe1Controller)
	private Runnable onConfirmListener;
	private Runnable onCancelListener;

	private Consumer<VeSession> onDeleteListener;

	public DoiVeBuoc6Controller(PanelDoiVeBuoc6 view, ExchangeSession exchangeSession) {
		this.view = view;
		this.exchangeSession = exchangeSession;
		this.view.setController(this);
		attachListeners();
	}

	// Gắn listener vào các nút của View
	private void attachListeners() {
		view.getConfirmButton().addActionListener(e -> handleConfirm());
		view.setPassengerDeleteListener(row -> {
			handleDelete(row);
		});
	}

	private void handleDelete(MappingRow row) {
		if (row == null) {
			return;
		}

		VeSession veSession = row.getVeMoi();

		// Hiển thị xác nhận
		int choice = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa vé:\n" + veSession.prettyString(),
				"Xác nhận xóa vé", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (choice == JOptionPane.YES_OPTION) {
			// Nếu người dùng đồng ý, báo cho DoiVe2Controller
			if (onDeleteListener != null) {
				onDeleteListener.accept(veSession);
			}
		}
	}

	/**
	 * Xử lý logic khi bấm "Xác nhận"
	 */
	private void handleConfirm() {
		// 1. Lấy dữ liệu thô từ View
		List<MappingRow> rows = view.getMappingRows();

		// 2. Cập nhật Model (ExchangeSession)
		// 2a. Cập nhật thông tin Hành Khách vào từng VeSession mới
		for (MappingRow row : rows) {
			VeSession ve = row.getVeMoi();
			ve.setHanhKhach(row.getVeCu().getVe().getKhachHang());
		}
		// 2b. Cập nhật Khách hàng (Người Mua)

		System.out.println("ExchangeSession đã được cập nhật.");

		// 4. Báo cho Controller cha
		if (onConfirmListener != null) {
			onConfirmListener.run();
		}
	}

	/**
	 * Xử lý logic khi bấm "Hủy"
	 */
	private void handleCancel() {
		// 1. Gọi BUS để hủy phiếu giữ chỗ
		datChoBUS.xoaPhieuGiuChoChiTietByPgcID(exchangeSession.getPhieuGiuCho().getPhieuGiuChoID());

		// 2. Nếu sau khi xóa mà không còn vé nào thì xóa luôn Phiếu giữ chỗ
		datChoBUS.xoaPhieuGiuCho(exchangeSession.getPhieuGiuCho().getPhieuGiuChoID());

		// 3. Báo cho Controller cha biết
		if (onCancelListener != null) {
			onCancelListener.run();
		}
	}

	// Setter cho các listener
	public void setOnConfirmListener(Runnable listener) {
		this.onConfirmListener = listener;
	}

	public void setOnCancelListener(Runnable listener) {
		this.onCancelListener = listener;
	}

	public void setOnDeleteListener(Consumer<VeSession> listener) {
		this.onDeleteListener = listener;
	}
}