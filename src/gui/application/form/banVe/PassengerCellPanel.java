package gui.application.form.banVe;
/*
 * @(#) PassengerCellPanel.java  1.0  [10:13:12 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

//PassengerCellPanel.java
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import entity.type.LoaiDoiTuong;

public class PassengerCellPanel extends JPanel {
	private final JTextField txtTen = new JTextField();
	private final JTextField txtID = new JTextField();
	String[] types = { "Người lớn", "Trẻ em", "Người cao tuổi" };
	private final JComboBox<String> cbType = new JComboBox<String>(types);

	public PassengerCellPanel() {
		setLayout(new GridBagLayout());
		setOpaque(true);

		// layout
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;

		gbc.gridy = 0;
		gbc.weightx = 1.0;
		add(txtTen, gbc);

		gbc.gridy = 1;
		add(cbType, gbc);

		gbc.gridy = 2;
		add(txtID, gbc);
	}

	public void setData(PassengerRow p) {
		if (p == null) {
			return;
		}
		getTxtTen().setText(p.getFullName());
		txtID.setText(p.getIdNumber());
		switch (p.getType()) {
		case NGUOI_LON:
			cbType.setSelectedIndex(0);
			break;
		case TRE_EM:
			cbType.setSelectedIndex(1);
			break;
		case NGUOI_CAO_TUOI:
			cbType.setSelectedIndex(2);
			break;
		}
	}

	public PassengerRow getData(PassengerRow base) {
		if (base == null) {
			return null;
		}
		base.setFullName(getTxtTen().getText().trim());
		base.setIdNumber(txtID.getText().trim());
		int idx = cbType.getSelectedIndex();
		base.setType(idx == 1 ? LoaiDoiTuong.TRE_EM : idx == 2 ? LoaiDoiTuong.NGUOI_CAO_TUOI : LoaiDoiTuong.NGUOI_LON);
		return base;
	}

	public void setEditable(boolean editable) {
		getTxtTen().setEditable(editable);
		cbType.setEnabled(editable);
		txtID.setEditable(editable);
	}

	// helper: ensure the ui of child comps uses current LAF (call when LAF changes)
	public void updateChildUI() {
		getTxtTen().setBorder(UIManager.getBorder("TextField.border"));
		txtID.setBorder(UIManager.getBorder("TextField.border"));
		cbType.setBorder(UIManager.getBorder("ComboBox.border"));
	}

	public JTextField getTxtTen() {
		return txtTen;
	}

	public JTextField getTxtID() {
		return txtID;
	}

	public JComboBox<String> getCbType() {
		return cbType;
	}
}