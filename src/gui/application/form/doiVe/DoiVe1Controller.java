package gui.application.form.doiVe;
/*
 * @(#) DoiVe1Controller.java  1.0  [5:31:37 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */
import java.util.List;

import entity.DonDatCho;
import entity.KhachHang;
import entity.Ve;
import gui.application.form.doiVe.DoiVeBuoc1Controller.SearchListener;
import gui.application.form.doiVe.DoiVeBuoc2Controller.ContinueListener;
import gui.application.form.doiVe.DoiVeBuoc3Controller.ConfirmListener;
import gui.application.form.doiVe.DoiVeBuoc3Controller.RowSelectionChangeListener;

public class DoiVe1Controller {
	private final PanelDoiVe1 view;
	private final PanelDoiVeBuoc1 p1;
	private final PanelDoiVeBuoc2 p2;
	private final PanelDoiVeBuoc3 p3;

	private final DoiVeBuoc1Controller p1Controller;
	private final DoiVeBuoc2Controller p2Controller;
	private final DoiVeBuoc3Controller p3Controller;

	private final ExchangeSession exchangeSession;
	private DonDatCho ddc;
	private List<Ve> listVe;
	private KhachHang nguoiMua;
	private List<VeDoiRow> listRowDoi;

	private Runnable onPanel1CompleteListener;

	protected void addPanel1CompleteListener(Runnable listener) {
		this.onPanel1CompleteListener = listener;
	}

	public DoiVe1Controller(PanelDoiVe1 view, ExchangeSession exchangeSession) {
		this.view = view;
		this.exchangeSession = exchangeSession;

		this.p1 = view.getPanelDoiVeBuoc1();
		this.p2 = view.getPanelDoiVeBuoc2();
		this.p3 = view.getPanelDoiVeBuoc3();

		this.p1Controller = new DoiVeBuoc1Controller(this.p1);
		this.p1.setController(this.p1Controller);
		this.p2Controller = new DoiVeBuoc2Controller(this.p2);
		this.p3Controller = new DoiVeBuoc3Controller(this.p3, exchangeSession);

		initMediatorLogic();
	}

	public DonDatCho getDonDatCho() {
		return this.ddc;
	}

	public KhachHang getNguoiMua() {
		return this.nguoiMua;
	}

	public List<VeDoiRow> getListRowDoi() {
		return this.listRowDoi;
	}

	private void initMediatorLogic() {

		// Lắng nghe sự kiện từ Buoc1 (Tra cứu đơn đặt chỗ)
		this.p1Controller.addSearchListener(new SearchListener() {
			@Override
			public void onSearchSuccess(DonDatCho donDatCho, List<Ve> danhSachVe, KhachHang khachHang) {
				ddc = donDatCho;
				listVe = danhSachVe;
				nguoiMua = khachHang;

				if (listRowDoi != null) {
					listRowDoi.clear();
				}

				view.setBuoc2Enabled(true);
				view.setBuoc3Enabled(false);

				p2Controller.disPlayDonDatCho(listVe, nguoiMua);
			}

			@Override
			public void onSearchFailure() {
				view.setBuoc2Enabled(false);
				view.setBuoc3Enabled(false);
			}
		});

		this.p2Controller.addContinueListener(new ContinueListener() {

			@Override
			public void onContinue(List<VeDoiRow> selectedRows) {
				// 1. Lưu trạng thái các vé được chọn
				listRowDoi = selectedRows;

				// 2. Đẩy dữ liệu vào P3 Controller
				p3Controller.displayConfirmationData(listRowDoi);

				// 3. Kích hoạt Bước 3
				view.setBuoc3Enabled(true);

				// 4. (Nên) Tự động chuyển tab sang Bước 3
				// view.setSelectedPanel(p3);
			}
		});

		this.p3Controller.addRowSelectionChangeListener(new RowSelectionChangeListener() {
			@Override
			public void onRowSelectionChanged(VeDoiRow row) {
				// Yêu cầu Controller 2 cập nhật lại View 2
				// Dữ liệu trong model của P2 đã tự động cập nhật
				// (vì p2.model và p3.model cùng tham chiếu đến object 'row')
				listRowDoi.remove(row);
				p2Controller.refreshRowDisplay(row);
			}
		});

		this.p3Controller.addConfirmListener(new ConfirmListener() {

			@Override
			public void onConfirm() {
				if (onPanel1CompleteListener != null) {
					onPanel1CompleteListener.run();
				}
			}
		});
	}
}
