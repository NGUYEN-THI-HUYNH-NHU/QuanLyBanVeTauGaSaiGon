package gui.application.form.banVe;
/*
 * @(#) PassengerCellPanel.java  1.0  [10:13:12 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

//PassengerCellPanel.java
import javax.swing.*;
import javax.swing.border.Border;

import entity.type.LoaiDoiTuong;

import java.awt.*;

public class PassengerCellPanel extends JPanel {
    private final JTextField tfName = new JTextField(10);
    String[] types = {"Người lớn", "Trẻ em", "Người cao tuổi"};
    private final JComboBox<String> cbType = new JComboBox<String>(types);
    private final JTextField tfId = new JTextField(10);

    public PassengerCellPanel() {
        super(new GridBagLayout());
        setOpaque(true); // rất quan trọng

        // Use LAF defaults for borders so FlatLaf renders rounded corners consistently
        Border tfBorder = UIManager.getBorder("TextField.border");
        getTfName().setBorder(tfBorder);
        tfId.setBorder(tfBorder);
        cbType.setBorder(UIManager.getBorder("ComboBox.border"));

        // layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2,2,2,2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        gbc.gridy = 0;
        gbc.weightx = 1.0;
        add(getTfName(), gbc);

        gbc.gridy = 1;
        add(cbType, gbc);

        gbc.gridy = 2;
        add(tfId, gbc);
    }

    public void setData(PassengerRow p) {
        if (p == null) return;
        getTfName().setText(p.getFullName());
        tfId.setText(p.getIdNumber());
        switch (p.getType()) {
            case NGUOI_LON: cbType.setSelectedIndex(0); break;
            case TRE_EM: cbType.setSelectedIndex(1); break;
            case NGUOI_CAO_TUOI: cbType.setSelectedIndex(2); break;
        }
    }

    public PassengerRow getData(PassengerRow base) {
        if (base == null) return null;
        base.setFullName(getTfName().getText().trim());
        base.setIdNumber(tfId.getText().trim());
        int idx = cbType.getSelectedIndex();
        base.setType(idx==1? LoaiDoiTuong.TRE_EM : idx==2? LoaiDoiTuong.NGUOI_CAO_TUOI : LoaiDoiTuong.NGUOI_LON);
        return base;
    }

    public void setEditable(boolean editable) {
        getTfName().setEditable(editable);
        cbType.setEnabled(editable);
        tfId.setEditable(editable);
    }

    // helper: ensure the ui of child comps uses current LAF (call when LAF changes)
    public void updateChildUI() {
        getTfName().setBorder(UIManager.getBorder("TextField.border"));
        tfId.setBorder(UIManager.getBorder("TextField.border"));
        cbType.setBorder(UIManager.getBorder("ComboBox.border"));
    }

	public JTextField getTfName() {
		return tfName;
	}
}