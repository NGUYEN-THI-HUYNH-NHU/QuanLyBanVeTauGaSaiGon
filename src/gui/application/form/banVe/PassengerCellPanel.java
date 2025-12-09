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
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.FlatClientProperties;

import entity.KhachHang;
import entity.type.LoaiDoiTuong;

public class PassengerCellPanel extends JPanel {
	private final JTextField txtTen = new JTextField();
	private final JTextField txtID = new JTextField();
	String[] types = { "Người lớn", "Trẻ em", "Người cao tuổi" };
	private final JComboBox<String> cbType = new JComboBox<String>(types);
	private final JLabel lblError = new JLabel();
	private PanelBuoc3Controller controller;
	private PassengerRow currentRowData;

	private JTable table;
	private PanelBuoc3 panelBuoc3;

	public PassengerCellPanel() {
		setLayout(new GridBagLayout());
		setOpaque(true);

		txtTen.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Họ và Tên");
		txtID.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "CCCD/Hộ chiếu");
		cbType.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Loại đối tượng");

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

		gbc.gridy = 3;
		gbc.insets = new Insets(0, 5, 2, 2);
		lblError.setForeground(Color.RED);
		lblError.setFont(new Font(lblError.getFont().getName(), Font.ITALIC, 11));
		lblError.setVisible(false);
		add(lblError, gbc);

		addClearErrorListener(txtID);
		addClearErrorListener(txtTen);

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
				if (validateFields()) {
					handleFinalEnter();
				}
			}
		});
	}

	private boolean isFullInfo() {
		if (txtID.getText().trim().isEmpty()) {
			return false;
		}
		if (txtTen.getText().trim().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Hàm validate dữ liệu đầu vào
	 * 
	 * @return true nếu hợp lệ, false nếu có lỗi
	 */
	public boolean validateFields() {
		String id = txtID.getText().trim();
		String name = txtTen.getText().trim();

		// 1. Check ID (CCCD/Hộ chiếu)
		if (id.isEmpty()) {
			showError("Vui lòng nhập CCCD/Hộ chiếu", txtID);
			return false;
		}
		// Regex: Chỉ chấp nhận số, độ dài 9-15 (CCCD VN là 12)
		if (!id.matches("^[0-9]{12}$")) {
			showError("CCCD/Hộ chiếu không đúng định dạng (12 ký số)", txtID);
			return false;
		}

		// 2. Check Tên
		if (name.isEmpty()) {
			showError("Vui lòng nhập họ tên", txtTen);
			return false;
		}
		// Regex: Chấp nhận chữ cái unicode (tiếng Việt), khoảng trắng, dấu chấm (nếu
		// cần)
		// [^0-9!@#...] -> Đơn giản là không chứa số và ký tự đặc biệt cơ bản
		if (name.matches(".*\\d.*") || name.matches(".*[!@#$%^&*()_+=<>?].*")) {
			showError("Tên không được chứa số hoặc ký tự đặc biệt", txtTen);
			return false;
		}

		hideError();
		return true;
	}

	private void showError(String msg, JTextField textField) {
		lblError.setText(msg);
		lblError.setVisible(true);
		textField.requestFocusInWindow();
	}

	private void hideError() {
		lblError.setVisible(false);
		lblError.setText("");
	}

	// Helper: Tự động ẩn lỗi khi user gõ
	private void addClearErrorListener(JTextField txt) {
		txt.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				hideError();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				hideError();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				hideError();
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
				currentRowData.setSoGiayTo(kh.getSoGiayTo());
				currentRowData.setHoTen(kh.getHoTen());
				currentRowData.setLoaiDoiTuong(kh.getLoaiDoiTuong());
				// Lưu entity KhachHang vào VeSession
				currentRowData.getVeSession().getVe().setKhachHang(kh);

				// Cập nhật View (các trường) từ Model vừa sửa
				setData(currentRowData);
			} else {
				// Không tìm thấy, set hành khách là null để controller thêm hành khách mới
				currentRowData.getVeSession().getVe().setKhachHang(null);
			}
		}
	}

	/**
	 * Xử lý khi Enter ở trường cuối cùng (txtID)
	 */
	private void handleFinalEnter() {
		// Validate trước khi cho phép nhảy dòng
		if (!validateFields()) {
			return;
		}

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
				System.err.println("PassengerCellPanel: Lỗi khi focus txtCccdNguoiMua: " + e.getMessage());
			}
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();

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
		txtTen.setText(p.getHoTen());
		txtID.setText(p.getSoGiayTo());
		if (p.getLoaiDoiTuong() == null) {
			cbType.setSelectedIndex(0);
			return;
		}
		switch (p.getLoaiDoiTuong()) {
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
		base.setHoTen(getTxtTen().getText().trim());
		base.setSoGiayTo(txtID.getText().trim());
		int idx = cbType.getSelectedIndex();
		base.setLoaiDoiTuong(
				idx == 1 ? LoaiDoiTuong.TRE_EM : idx == 2 ? LoaiDoiTuong.NGUOI_CAO_TUOI : LoaiDoiTuong.NGUOI_LON);
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