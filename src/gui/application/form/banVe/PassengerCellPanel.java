
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

import entity.KhachHang;
import entity.type.LoaiDoiTuong;

public class PassengerCellPanel extends JPanel {
	private final JTextField txtTen = new JTextField();
	private final JTextField txtID = new JTextField();
	String[] types = { "Người lớn", "Trẻ em", "Người cao tuổi" };
	private final JComboBox<String> cbType = new JComboBox<String>(types);

	private PanelBuoc3Controller controller;
	private PassengerRow currentRowData;

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
		gbc.weightx = 1;

		gbc.gridy = 0;
		add(txtID, gbc);

		gbc.gridy = 1;
		add(txtTen, gbc);

		gbc.gridy = 2;
		add(cbType, gbc);

		// 1. Enter trên txtID -> Tìm kiếm VÀ focus txtTen
		txtID.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleFindHanhKhach();
				txtTen.requestFocusInWindow();
			}
		});

		// 2. Enter trên txtTen -> focus cbType
		txtTen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cbType.requestFocusInWindow();
			}
		});

		// 3. Enter trên cbType -> Nhảy dòng
		cbType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleFinalEnter();
			}
		});
	}

	/**
	 * Hàm tìm kiếm hành khách (gọi Controller)
	 */
	private void handleFindHanhKhach() {
		String id = txtID.getText().trim();
		if (controller != null && !id.isEmpty()) {
			KhachHang kh = controller.findKhachHangByID(id);
			if (kh != null) {
				// Tìm thấy -> Cập nhật Model của cell
				currentRowData.setIdNumber(kh.getSoGiayTo());
				currentRowData.setFullName(kh.getHoTen());
				currentRowData.setType(kh.getLoaiDoiTuong());
				// Lưu entity KhachHang vào VeSession
				currentRowData.getVeSession().setHanhKhach(kh);

				// Cập nhật View (các trường) từ Model vừa sửa
				setData(currentRowData);
			} else {
				// Không tìm thấy, đảm bảo VeSession không giữ khách cũ
				currentRowData.getVeSession().setHanhKhach(null);
			}
		}
	}

	/**
	 * Xử lý khi Enter ở trường cuối cùng (txtID)
	 */
	private void handleFinalEnter() {
		if (table == null || panelBuoc3 == null) {
			return;
		}
		int currentRow = table.getEditingRow();
		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
		int nextRow = currentRow + 1;

		if (nextRow < table.getRowCount()) {
			// Còn dòng tiếp theo -> Edit ô đầu tiên của dòng đó
			table.editCellAt(nextRow, 0);
		} else {
			// Hết dòng -> Focus vào trường CCCD của người mua
			try {
				panelBuoc3.getTxtCccdNguoiMua().requestFocusInWindow();
			} catch (Exception e) {
				System.err.println("Lỗi khi focus txtCccdNguoiMua: " + e.getMessage());
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

	public void setController(PanelBuoc3Controller controller) {
		this.controller = controller;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public void setPanelBuoc3(PanelBuoc3 panelBuoc3) {
		this.panelBuoc3 = panelBuoc3;
	}

	public void setData(PassengerRow p) {
		this.currentRowData = p;

		if (p == null) {
			txtID.setText("");
			txtTen.setText("");
			cbType.setSelectedIndex(0);
			return;
		}
		txtTen.setText(p.getFullName());
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