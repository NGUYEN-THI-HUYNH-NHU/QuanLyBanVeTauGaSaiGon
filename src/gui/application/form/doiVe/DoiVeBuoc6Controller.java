package gui.application.form.doiVe;
/*
 * @(#) DoiVeBuoc6Controller.java  1.0  [8:06:26 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import java.util.List;
import java.util.function.Consumer;

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
		view.getCancelButton().addActionListener(e -> handleCancel());
	}

	/**
	 * Xử lý logic khi bấm "Xác nhận"
	 */
	private void handleConfirm() {
		// 1. Lấy dữ liệu thô từ View
		List<MappingRow> rows = view.getMappingRows();

		if (rows.size() == 0) {
			return;
		}

		// 2. Cập nhật Model (ExchangeSession)
		for (MappingRow row : rows) {
			VeSession ve = row.getVeMoi();
			ve.setHanhKhach(row.getVeCu().getVe().getKhachHang());
		}

		// 4. Báo cho Controller cha
		if (onConfirmListener != null) {
			onConfirmListener.run();
		}
	}

	/**
	 * Xử lý logic khi bấm "Hủy"
	 */
	private void handleCancel() {
		if (exchangeSession.getPhieuGiuCho() != null) {

			// 1. Gọi BUS để hủy phiếu giữ chỗ chi tiết
			datChoBUS.xoaPhieuGiuChoChiTietByPgcID(exchangeSession.getPhieuGiuCho().getPhieuGiuChoID());

			// 2. Nếu sau khi xóa mà không còn vé nào thì xóa luôn Phiếu giữ chỗ
			datChoBUS.xoaPhieuGiuCho(exchangeSession.getPhieuGiuCho().getPhieuGiuChoID());
		}

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