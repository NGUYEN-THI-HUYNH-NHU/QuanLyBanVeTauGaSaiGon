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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import entity.type.LoaiDoiTuong;

public class PassengerCellPanel extends JPanel {
	private final JTextField txtTen = new JTextField();
	private final JTextField txtID = new JTextField();
	String[] types = { "Người lớn", "Trẻ em", "Người cao tuổi" };
	private final JComboBox<String> cbType = new JComboBox<String>(types);

	// === THÊM THAM CHIẾU ĐỂ ĐIỀU HƯỚNG ===
	private JTable table;
	private PanelBuoc3 panelBuoc3;

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

		// 1. Enter trên txtTen -> focus cbType
		txtTen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cbType.requestFocusInWindow();
			}
		});

		// 2. Enter trên cbType -> focus txtID
		cbType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Chỉ di chuyển focus nếu sự kiện là "Enter" (thường là "comboBoxEdited"
				// hoặc "comboBoxChanged" nhưng addActionListener cũng bắt)
				// Cần kiểm tra kỹ, nhưng cách đơn giản nhất là cứ focus
				txtID.requestFocusInWindow();
			}
		});

		// 3. Enter trên txtID -> nhảy dòng hoặc nhảy ra form
		txtID.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleFinalEnter();
			}
		});
	}

	/**
	 * Xử lý khi Enter ở trường cuối cùng (txtID)
	 */
	private void handleFinalEnter() {
		if (table == null || panelBuoc3 == null) {
			return;
		}

		// Lấy hàng đang chỉnh sửa (trước khi dừng)
		int currentRow = table.getEditingRow();

		// Dừng chỉnh sửa ô hiện tại (quan trọng để lưu dữ liệu)
		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}

		int nextRow = currentRow + 1;

		if (nextRow < table.getRowCount()) {
			// 1. Nếu còn dòng tiếp theo:
			table.editCellAt(nextRow, 0);

			// (Việc focus txtTen của dòng mới đã được xử lý
			// trong invokeLater của PassengerCellEditor)

		} else {
			// 2. Nếu hết dòng: focus vào txtTen của form người mua
			try {
				panelBuoc3.getTxtTenNguoiMua().requestFocusInWindow();
			} catch (Exception e) {
				System.err.println("Lỗi khi focus txtTenNguoiMua. " + e.getMessage());
			}
		}
	}

	@Override
	public void updateUI() {
		super.updateUI(); // Cập nhật UI cho chính panel này (nền,...)

		// Phải kiểm tra null vì updateUI có thể được gọi
		// trong constructor của JPanel (trước khi txtTen được khởi tạo)
		if (txtTen != null) {
			// Yêu cầu các con cũng cập nhật UI của chúng
			SwingUtilities.updateComponentTreeUI(this);
		}
	}

	// === THÊM SETTERS ĐỂ NHẬN THAM CHIẾU ===
	public void setTable(JTable table) {
		this.table = table;
	}

	public void setPanelBuoc3(PanelBuoc3 panelBuoc3) {
		this.panelBuoc3 = panelBuoc3;
	}

	public void setData(PassengerRow p) {
		if (p == null) {
			return;
		}
		getTxtTen().setText(p.getFullName());
		txtID.setText(p.getIdNumber());
		if (p.getType() == null) {
			cbType.setSelectedIndex(0);
			return;
		}
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