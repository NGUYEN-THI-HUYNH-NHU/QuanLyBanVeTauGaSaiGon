package gui.application.form.banVe;
/*
 * @(#) PanelBuoc3.java  1.0  [10:39:57 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class PanelBuoc3 extends JPanel {
 private final HanhKhachTableModel model;
 private final JTable table;
 private final JButton btnConfirm;
 private final JButton btnCancel;
 private final JLabel lblInfo;

 public PanelBuoc3() {
     setLayout(new BorderLayout(8,8));
     setBorder(BorderFactory.createTitledBorder("Nhập thông tin hành khách"));

     model = new HanhKhachTableModel();
     table = new JTable(model);
     table.setRowHeight(110);     
     // column 0 custom
     table.getColumnModel().getColumn(0).setCellRenderer(new PassengerCellRenderer());
     table.getColumnModel().getColumn(0).setCellEditor(new PassengerCellEditor());
     // optional: make columns 2..4 right-aligned
     DefaultTableCellRenderer center = new DefaultTableCellRenderer();
     center.setHorizontalAlignment(SwingConstants.CENTER);
     table.getColumnModel().getColumn(2).setCellRenderer(center);
     table.getColumnModel().getColumn(3).setCellRenderer(center);
     table.getColumnModel().getColumn(4).setCellRenderer(center);

     JScrollPane sp = new JScrollPane(table);
     add(sp, BorderLayout.CENTER);

     JPanel south = new JPanel(new BorderLayout());
     lblInfo = new JLabel("Nhập thông tin hành khách cho các vé đã chọn", SwingConstants.LEFT);
     south.add(lblInfo, BorderLayout.WEST);

     JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
     btnConfirm = new JButton("Xác nhận");
     btnCancel = new JButton("Hủy");
     btns.add(btnCancel);
     btns.add(btnConfirm);
     south.add(btns, BorderLayout.EAST);

     add(south, BorderLayout.SOUTH);
 }

 /**
  * Khởi tạo bảng từ BookingSession (lấy VeSession cho tripIndex)
  */
 public void initFromBookingSession(BookingSession session, int tripIndex) {
	    model.clear();
	    if (session == null) return;
	    List<VeSession> vs = session.getSelectedTicketsForTrip(tripIndex);
	    if (vs == null || vs.isEmpty()) {
	        lblInfo.setText("Không có vé nào để nhập hành khách.");
	        return;
	    }
	    List<PassengerRow> rows = new ArrayList<>();
	    for (VeSession v : vs) {
	        PassengerRow r = new PassengerRow(v);
	        rows.add(r);
	    }
	    model.setRows(rows);

	    // --- FIX: force UI delegate refresh on EDT so FlatLaf re-applies rounded corners ---
	    SwingUtilities.invokeLater(() -> {
	        // update UI for this panel only (cheaper than update entire app)
	        SwingUtilities.updateComponentTreeUI(this);
	        // optional: revalidate/repaint to ensure layout and painting refreshed
	        this.revalidate();
	        this.repaint();
	    });
	}


 public List<PassengerRow> getPassengerRows() {
     // stop editor if any
     if (table.isEditing()) table.getCellEditor().stopCellEditing();
     return model.getRowsCopy();
 }

 public JButton getConfirmButton() { return btnConfirm; }
 public JButton getCancelButton() { return btnCancel; }

 // optional helper to validate form quickly
 public boolean validateRows() {
     for (PassengerRow r : model.getRowsCopy()) {
         if (r.getFullName() == null || r.getFullName().trim().isEmpty()) return false;
         // add more rules as needed
     }
     return true;
 }

    public void setComponentsEnabled(boolean enabled) {
        // Vô hiệu hóa chính PanelBuoc3
        super.setEnabled(enabled);
        for (Component comp : this.getComponents()) {
            comp.setEnabled(enabled);
        }
    }
}