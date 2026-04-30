package gui.application.form.hoanVe;
/*
 * @(#) PanelHoanVeController.java  1.0  [2:51:17 PM] Nov 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 9, 2025
 * @version: 1.0
 */

import dto.DonDatChoDTO;
import dto.KhachHangDTO;
import dto.VeDTO;
import gui.application.form.hoanVe.HoanVeBuoc1Controller.SearchListener;
import gui.application.form.hoanVe.HoanVeBuoc2Controller.ContinueListener;
import gui.application.form.hoanVe.HoanVeBuoc3Controller.ConfirmListener;
import gui.application.form.hoanVe.HoanVeBuoc3Controller.RowSelectionChangeListener;

import javax.swing.*;
import java.util.List;

public class HoanVe1Controller {
    private final PanelHoanVe1 view;
    private final PanelHoanVeBuoc1 p1;
    private final PanelHoanVeBuoc2 p2;
    private final PanelHoanVeBuoc3 p3;

    private final HoanVeBuoc1Controller p1Controller;
    private final HoanVeBuoc2Controller p2Controller;
    private final HoanVeBuoc3Controller p3Controller;

    private DonDatChoDTO ddc;
    private List<VeDTO> listVe;
    private KhachHangDTO nguoiMua;
    private List<VeHoanRow> listRowHoan;

    private Runnable onPanel1CompleteListener;
    private Runnable onRefreshListener;

    public HoanVe1Controller(PanelHoanVe1 view) {
        this.view = view;
        this.p1 = view.getPanelHoanVeBuoc1();
        this.p2 = view.getPanelHoanVeBuoc2();
        this.p3 = view.getPanelHoanVeBuoc3();

        this.p1Controller = new HoanVeBuoc1Controller(this.p1);
        this.p1.setController(this.p1Controller);
        this.p2Controller = new HoanVeBuoc2Controller(this.p2);
        this.p3Controller = new HoanVeBuoc3Controller(this.p3);

        initMediatorLogic();
    }

    protected void addPanel1CompleteListener(Runnable listener) {
        this.onPanel1CompleteListener = listener;
    }

    protected void addRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }

    public DonDatChoDTO getDonDatCho() {
        return this.ddc;
    }

    public KhachHangDTO getNguoiMua() {
        return this.nguoiMua;
    }

    public List<VeHoanRow> getListRowHoan() {
        return this.listRowHoan;
    }

    private void initMediatorLogic() {
        // Lắng nghe sự kiện từ Buoc1 (Tra cứu đơn đặt chỗ)
        this.p1Controller.addSearchListener(new SearchListener() {
            @Override
            public void onSearchSuccess(DonDatChoDTO donDatCho, List<VeDTO> danhSachVe, KhachHangDTO khachHang) {
                ddc = donDatCho;
                listVe = danhSachVe;
                nguoiMua = khachHang;

                if (listRowHoan != null) {
                    listRowHoan.clear();
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
            public void onContinue(List<VeHoanRow> selectedRows) {
                // 1. Lưu trạng thái các vé được chọn
                listRowHoan = selectedRows;

                // 2. Đẩy dữ liệu vào P3 Controller
                p3Controller.displayConfirmationData(listRowHoan);

                // 3. Kích hoạt Bước 3
                view.setBuoc3Enabled(true);

                // 4. (Nên) Tự động chuyển tab sang Bước 3
                // view.setSelectedPanel(p3);
            }
        });

        this.p3Controller.addRowSelectionChangeListener(new RowSelectionChangeListener() {
            @Override
            public void onRowSelectionChanged(VeHoanRow row) {
                // Yêu cầu Controller 2 cập nhật lại View 2
                // Dữ liệu trong model của P2 đã tự động cập nhật
                // (vì p2.model và p3.model cùng tham chiếu đến object 'row')
                listRowHoan.remove(row);
                p2Controller.refreshRowDisplay(row);
            }
        });

        this.p3Controller.addRefreshListener(() -> {
            if (onRefreshListener != null) {
                onRefreshListener.run();
            }
        });

        this.p3Controller.addConfirmListener(new ConfirmListener() {
            @Override
            public void onConfirm() {
                if (listRowHoan == null || listRowHoan.size() == 0) {
                    JOptionPane.showMessageDialog(p3, "Chưa có vé nào được chọn. Vui lòng chọn vé cần hoàn!", "Lưu ý",
                            JOptionPane.CLOSED_OPTION);
                    return;
                }

                if (onPanel1CompleteListener != null) {
                    onPanel1CompleteListener.run();
                }
            }
        });
    }
}