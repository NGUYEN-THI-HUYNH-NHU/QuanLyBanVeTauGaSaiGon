package gui.application.form.doiVe;
/*
 * @(#) DoiVeBuoc3Controller.java  1.0  [5:55:26 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class DoiVeBuoc3Controller {

    private final PanelDoiVeBuoc3 panel;

    private final ExchangeSession exchangeSession;
    private ConfirmListener confirmListener;
    private RowSelectionChangeListener selectionChangeListener;
    private Runnable onRefreshListener;

    public DoiVeBuoc3Controller(PanelDoiVeBuoc3 panel, ExchangeSession exchangeSession) {
        this.panel = panel;
        this.exchangeSession = exchangeSession;

        // Lắng nghe thay đổi trên row
        this.panel.addRowSelectionListener(new Consumer<VeDoiRow>() {
            @Override
            public void accept(VeDoiRow row) {
                // Báo sự kiện này lên cho Mediator
                if (JOptionPane.showConfirmDialog(panel, "Bạn xác nhận bỏ chọn hoàn vé này?", "Lưu ý",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    panel.removeRow(row);
                    if (selectionChangeListener != null) {
                        selectionChangeListener.onRowSelectionChanged(row);
                    }
                }
            }
        });

        this.panel.getBtnRefresh().addActionListener(e -> {
            if (onRefreshListener != null) {
                onRefreshListener.run();
            }
        });

        this.panel.getBtnXacNhan().addActionListener(e -> {
            List<VeDoiRow> listVeDoiRow = panel.getVeDoiRows();
            exchangeSession.setListVeCuCanDoi(listVeDoiRow);
            // Báo sự kiện này lên cho Mediator
            if (confirmListener != null) {
                confirmListener.onConfirm();
            }
        });
    }

    protected void addRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }

    /**
     * Được gọi bởi DoiVeController (Mediator) để hiển thị dữ liệu
     */
    public void displayConfirmationData(List<VeDoiRow> selectedRows) {
        panel.displayConfirmation(selectedRows);
    }

    public void addRowSelectionChangeListener(RowSelectionChangeListener listener) {
        this.selectionChangeListener = listener;
    }

    public void addConfirmListener(ConfirmListener listener) {
        this.confirmListener = listener;
    }

    protected interface RowSelectionChangeListener {
        void onRowSelectionChanged(VeDoiRow row);
    }

    protected interface ConfirmListener {
        void onConfirm();
    }
}
