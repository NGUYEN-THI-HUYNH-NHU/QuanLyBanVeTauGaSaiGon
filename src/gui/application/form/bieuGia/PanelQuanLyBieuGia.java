package gui.application.form.bieuGia;
/*
 * @(#) PanelQuanLyBieuGia.java  1.0  [8:31:14 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import entity.BieuGiaVe;

public class PanelQuanLyBieuGia extends JPanel {
	private JTable table;
	private BieuGiaVeTableModel tableModel;
	private JTextField txtTimKiem;
	private JComboBox cboLocTuyen;
	private JComboBox cboLocTau;
	private JButton btnLamMoi;
	private JButton btnThemMoi;
	private JButton btnTimKiem;

	private final BieuGiaController controller;

	public PanelQuanLyBieuGia() {
		setLayout(new BorderLayout(10, 10));
		initUI();
		this.controller = new BieuGiaController(this);
	}

	private void initUI() {
		// --- 1. PANEL LỌC ---
		JPanel pnlLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		pnlLoc.setBorder(new TitledBorder("Bộ lọc tìm kiếm"));

		txtTimKiem = new JTextField(15);
		txtTimKiem.putClientProperty("JTextField.placeholderText", "Nhập mã biểu giá...");

		cboLocTuyen = new JComboBox<>(new String[] { "Tất cả Tuyến", "Bắc Nam", "Hà Nội - Hải Phòng" });
		cboLocTau = new JComboBox<>(new String[] { "Tất cả Tàu", "SE1", "TN1", "Tất cả" });

		btnTimKiem = new JButton("Tìm");
		btnTimKiem.setIcon(new FlatSVGIcon("gui/icon/svg/search.svg", 0.8f));
		btnTimKiem.setBackground(new Color(36, 104, 155));
		btnTimKiem.setForeground(Color.WHITE);

		btnLamMoi = new JButton("Làm mới");
		btnLamMoi.setIcon(new FlatSVGIcon("gui/icon/svg/refresh-1.svg", 0.8f));

		btnThemMoi = new JButton("Thêm biểu giá");
		btnThemMoi.setIcon(new FlatSVGIcon("gui/icon/svg/add-1.svg", 0.8f));
		btnThemMoi.setBackground(new Color(0, 128, 0));
		btnThemMoi.setForeground(Color.WHITE);

		pnlLoc.add(new JLabel("Từ khóa:"));
		pnlLoc.add(txtTimKiem);
		pnlLoc.add(new JLabel("Tuyến:"));
		pnlLoc.add(cboLocTuyen);
		pnlLoc.add(new JLabel("Tàu:"));
		pnlLoc.add(cboLocTau);
		pnlLoc.add(btnTimKiem);
		pnlLoc.add(btnLamMoi);
		pnlLoc.add(Box.createHorizontalStrut(140));
		pnlLoc.add(btnThemMoi);

		add(pnlLoc, BorderLayout.NORTH);

		// --- 2. TABLE ---
		tableModel = new BieuGiaVeTableModel();
		table = new JTable(tableModel);
		table.setRowHeight(40);

		add(new JScrollPane(table), BorderLayout.CENTER);
	}

	public void hienThiDanhSach(List<BieuGiaVe> list) {
		tableModel.setRowCount(0);

		for (BieuGiaVe bg : list) {
			tableModel.addRow(bg);
		}
	}

	public int getSelectedRow() {
		return table.getSelectedRow();
	}

	public String getSelectedID() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return null;
		}
		return tableModel.getValueAt(row, 0).toString();
	}

	public JTable getTable() {
		return table;
	}

	public BieuGiaVeTableModel getTableModel() {
		return tableModel;
	}

	public JButton getBtnLamMoi() {
		return btnLamMoi;
	}

	public JButton getBtnThemMoi() {
		return btnThemMoi;
	}

	public void setTxtTimKiem(JTextField txtTimKiem) {
		this.txtTimKiem = txtTimKiem;
	}

	public void setBtnLamMoi(JButton btnLamMoi) {
		this.btnLamMoi = btnLamMoi;
	}

	public void setBtnThemMoi(JButton btnThemMoi) {
		this.btnThemMoi = btnThemMoi;
	}

	public void setBtnTimKiem(JButton btnTimKiem) {
		this.btnTimKiem = btnTimKiem;
	}

	public JButton getBtnTimKiem() {
		return btnTimKiem;
	}

	public JTextField getTxtTimKiem() {
		return txtTimKiem;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public void setTableModel(BieuGiaVeTableModel tableModel) {
		this.tableModel = tableModel;
	}
}