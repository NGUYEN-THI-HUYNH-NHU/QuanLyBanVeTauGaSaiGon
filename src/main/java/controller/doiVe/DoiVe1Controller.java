package controller.doiVe;
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

import controller.doiVe.DoiVeBuoc1Controller.SearchListener;
import controller.doiVe.DoiVeBuoc2Controller.ContinueListener;
import controller.doiVe.DoiVeBuoc3Controller.ConfirmListener;
import controller.doiVe.DoiVeBuoc3Controller.RowSelectionChangeListener;
import dto.DonDatChoDTO;
import dto.KhachHangDTO;
import dto.PhieuDungPhongVIPDTO;
import dto.VeDTO;
import gui.application.form.doiVe.*;

import java.util.ArrayList;
import java.util.List;

public class DoiVe1Controller {
    private final PanelDoiVe1 view;
    private final PanelDoiVeBuoc1 p1;
    private final PanelDoiVeBuoc2 p2;
    private final PanelDoiVeBuoc3 p3;

    private final DoiVeBuoc1Controller p1Controller;
    private final DoiVeBuoc2Controller p2Controller;
    private final DoiVeBuoc3Controller p3Controller;

    private final ExchangeSession exchangeSession;

    private Runnable onPanel1RefreshListener;
    private Runnable onPanel1CompleteListener;

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

        view.setBuoc2Enabled(false);
        view.setBuoc3Enabled(false);

        initMediatorLogic();
    }

    public void addPanel1CompleteListener(Runnable listener) {
        this.onPanel1CompleteListener = listener;
    }

    public void addPanel1RefreshListner(Runnable listener) {
        this.onPanel1RefreshListener = listener;
    }

    private void initMediatorLogic() {

        // Lắng nghe sự kiện từ Buoc1 (Tra cứu đơn đặt chỗ)
        this.p1Controller.addSearchListener(new SearchListener() {
            @Override
            public void onSearchSuccess(DonDatChoDTO donDatCho, List<VeDTO> danhSachVe, List<PhieuDungPhongVIPDTO> danhSachPhieu,
                                        KhachHangDTO khachHang) {
                List<VeDoiRow> listVeTimDuoc = new ArrayList<VeDoiRow>();
                int soLuongVe = danhSachVe.size();
                for (int i = 0; i < soLuongVe; i++) {
                    listVeTimDuoc.add(new VeDoiRow(danhSachVe.get(i), danhSachPhieu.get(i)));
                }
                exchangeSession.setDonDatChoCu(donDatCho);
                exchangeSession.setKhachHang(khachHang);
                exchangeSession.setListVeTimDuoc(listVeTimDuoc);

                if (exchangeSession.getListVeCuCanDoi() != null) {
                    exchangeSession.getListVeCuCanDoi().clear();
                }

                view.setBuoc2Enabled(true);
                view.setBuoc3Enabled(false);

                p2Controller.disPlayDonDatCho(danhSachVe, danhSachPhieu, khachHang);
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
                exchangeSession.setListVeCuCanDoi(selectedRows);

                // 2. Đẩy dữ liệu vào P3 Controller
                p3Controller.displayConfirmationData(selectedRows);

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
                exchangeSession.getListVeCuCanDoi().remove(row);
                p2Controller.refreshRowDisplay(row);
            }
        });

        this.p3Controller.addRefreshListener(() -> {
            if (onPanel1RefreshListener != null) {
                onPanel1RefreshListener.run();
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