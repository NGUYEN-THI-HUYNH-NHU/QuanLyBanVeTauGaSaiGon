package gui.application.form.hoaDon;

/*
 * @(#) PanelQuanLyHoaDon.java  1.0  [2:30:28 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.toedter.calendar.JDateChooser;

import gui.tuyChinh.DateTimeRenderer;

public class PanelQuanLyHoaDon extends JPanel {
	private JTextField txtTuKhoa;
	private JButton btnTraCuu;
	private JComboBox<String> cboLoaiTimKiem;

	private JComboBox<String> cboLoaiHoaDon;
	private JTextField txtKhachHangSuggest; // Giả lập auto-suggest
	private JDateChooser dateChooserTuNgay;
	private JDateChooser dateChooserDenNgay;
	private JComboBox<String> cboHinhThucTT;
	private JButton btnLoc;
	private JButton btnReset;

	private JTable table;
	private HoaDonTableModel tableModel;

	private final HoaDonController controller;

	public PanelQuanLyHoaDon() {
		setLayout(new BorderLayout());
		initUI();
		controller = new HoaDonController(this);
	}

//	private void initUI() {
//		JPanel pnlTop = new JPanel();
//		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
//
//		// 1. Panel Tra Cứu
//		JPanel pnlTraCuu = new JPanel(new FlowLayout(FlowLayout.LEFT));
//		pnlTraCuu.setBorder(new TitledBorder("Tra cứu nhanh"));
//
//		cboLoaiTimKiem = new JComboBox<>(new String[] { "Mã hóa đơn", "Mã khách hàng", "Mã giao dịch" });
//		txtTuKhoa = new JTextField(18);
//		btnTraCuu = new JButton("Tra cứu");
//		btnTraCuu.setIcon(UIManager.getIcon("FileView.directoryIcon"));
//
//		pnlTraCuu.add(new JLabel("Tìm theo: "));
//		pnlTraCuu.add(cboLoaiTimKiem);
//		pnlTraCuu.add(txtTuKhoa);
//		pnlTraCuu.add(btnTraCuu);
//
//		// 2. Panel Lọc
//		JPanel pnlLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
//		pnlLoc.setBorder(new TitledBorder("Bộ lọc chi tiết"));
//
//		// Loại hóa đơn
//		cboLoaiHoaDon = new JComboBox<>(
//				new String[] { "Tất cả", "Hóa đơn bán vé", "Hóa đơn hoàn vé", "Hóa đơn đổi vé" });
//
//		// Khách hàng (Auto-suggest UI)
//		txtKhachHangSuggest = new JTextField(14);
//		txtKhachHangSuggest.setToolTipText("Nhập tên, SĐT hoặc CCCD");
//
//		// Ngày tháng (Giả lập UI)
//		dateChooserTuNgay = new JDateChooser();
//		dateChooserTuNgay.setDateFormatString("dd/MM/yyyy");
//		dateChooserTuNgay.setDate(new Date());
//
//		dateChooserDenNgay = new JDateChooser();
//		dateChooserDenNgay.setDateFormatString("dd/MM/yyyy");
//		dateChooserDenNgay.setDate(new Date());
//
//		// Hình thức thanh toán
//		cboHinhThucTT = new JComboBox<>(new String[] { "Tất cả", "Tiền mặt", "Chuyển khoản" });
//
//		btnReset = new JButton("Làm mới");
//		btnReset.setIcon(UIManager.getIcon("FileView.fileIcon")); // Thay bằng icon refresh
//
//		btnLoc = new JButton("Lọc");
//		btnLoc.setIcon(UIManager.getIcon("FileView.floppyDriveIcon")); // Thay bằng icon cái phễu nếu có
//		btnLoc.setBackground(new Color(0, 153, 255)); // Màu xanh nổi bật
//		btnLoc.setForeground(Color.WHITE);
//
//		pnlLoc.add(new JLabel("Loại HĐ:"));
//		pnlLoc.add(cboLoaiHoaDon);
//		pnlLoc.add(new JLabel("Khách hàng:"));
//		pnlLoc.add(txtKhachHangSuggest);
//		pnlLoc.add(new JLabel("Từ ngày:"));
//		pnlLoc.add(dateChooserTuNgay);
//		pnlLoc.add(new JLabel("Đến ngày:"));
//		pnlLoc.add(dateChooserDenNgay);
//		pnlLoc.add(new JLabel("Thanh toán:"));
//		pnlLoc.add(cboHinhThucTT);
//		pnlLoc.add(Box.createHorizontalStrut(10));
//		pnlLoc.add(btnLoc);
//		pnlLoc.add(btnReset);
//
//		pnlTop.add(pnlTraCuu);
//		pnlTop.add(pnlLoc);
//
//		tableModel = new HoaDonTableModel();
//		table = new JTable(tableModel);
//		table.setRowHeight(30);
//
//		JScrollPane scrollPane = new JScrollPane(table);
//
//		add(pnlTop, BorderLayout.NORTH);
//		add(scrollPane, BorderLayout.CENTER);
//	}
	private void initUI() {
		JPanel pnlTop = new JPanel();
		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));

		// 1. PANEL TRA CỨU (giữ nguyên)
		JPanel pnlTraCuu = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlTraCuu.setBorder(new TitledBorder("Tra cứu nhanh"));

		cboLoaiTimKiem = new JComboBox<>(new String[] { "Mã hóa đơn", "Mã khách hàng", "Mã giao dịch" });
		txtTuKhoa = new JTextField(18);
		btnTraCuu = new JButton("Tra cứu");
		btnTraCuu.setIcon(UIManager.getIcon("FileView.directoryIcon"));

		pnlTraCuu.add(new JLabel("Tìm theo: "));
		pnlTraCuu.add(cboLoaiTimKiem);
		pnlTraCuu.add(txtTuKhoa);
		pnlTraCuu.add(btnTraCuu);

		// 2. PANEL LỌC
		JPanel pnlLoc = new JPanel();
		pnlLoc.setLayout(new BoxLayout(pnlLoc, BoxLayout.Y_AXIS));
		pnlLoc.setBorder(new TitledBorder("Bộ lọc chi tiết"));

		// --- ROW 1: Tất cả các trường nằm trên 1 dòng ---
		JPanel rowFields = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));

		cboLoaiHoaDon = new JComboBox<>(
				new String[] { "Tất cả", "Hóa đơn bán vé", "Hóa đơn hoàn vé", "Hóa đơn đổi vé" });

		txtKhachHangSuggest = new JTextField(14);
		txtKhachHangSuggest.setToolTipText("Nhập tên, SĐT hoặc CCCD");

		dateChooserTuNgay = new JDateChooser();
		dateChooserTuNgay.setDateFormatString("dd/MM/yyyy");
		dateChooserTuNgay.setDate(new Date());

		dateChooserDenNgay = new JDateChooser();
		dateChooserDenNgay.setDateFormatString("dd/MM/yyyy");
		dateChooserDenNgay.setDate(new Date());

		cboHinhThucTT = new JComboBox<>(new String[] { "Tất cả", "Tiền mặt", "Chuyển khoản" });

		rowFields.add(new JLabel("Loại HĐ:"));
		rowFields.add(cboLoaiHoaDon);

		rowFields.add(new JLabel("Khách hàng:"));
		rowFields.add(txtKhachHangSuggest);

		rowFields.add(new JLabel("Từ ngày:"));
		rowFields.add(dateChooserTuNgay);

		rowFields.add(new JLabel("Đến ngày:"));
		rowFields.add(dateChooserDenNgay);

		rowFields.add(new JLabel("Thanh toán:"));
		rowFields.add(cboHinhThucTT);

		// --- ROW 2: Hai nút, căn phải ---
		JPanel rowButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		btnReset = new JButton("Làm mới");
		btnReset.setIcon(UIManager.getIcon("FileView.fileIcon"));

		btnLoc = new JButton("Lọc");
		btnLoc.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
		btnLoc.setBackground(new Color(38, 117, 191));
		btnLoc.setForeground(Color.WHITE);

		rowButtons.add(btnLoc);
		rowButtons.add(btnReset);

		// Gộp vào panel lọc
		pnlLoc.add(rowFields);
		pnlLoc.add(rowButtons);

		// Add top
		pnlTop.add(pnlTraCuu);
		pnlTop.add(pnlLoc);

		// Bảng
		tableModel = new HoaDonTableModel();
		table = new JTable(tableModel);
		table.setRowHeight(30);

		table.getColumnModel().getColumn(HoaDonTableModel.COL_THOI_DIEM_TAO).setCellRenderer(new DateTimeRenderer());

		JScrollPane scrollPane = new JScrollPane(table);

		add(pnlTop, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	public JTextField getTxtTuKhoa() {
		return txtTuKhoa;
	}

	public JButton getBtnTraCuu() {
		return btnTraCuu;
	}

	public JComboBox<String> getCboLoaiTimKiem() {
		return cboLoaiTimKiem;
	}

	public JComboBox<String> getCboLoaiHoaDon() {
		return cboLoaiHoaDon;
	}

	public JTextField getTxtKhachHangSuggest() {
		return txtKhachHangSuggest;
	}

	public JDateChooser getDateChooserTuNgay() {
		return dateChooserTuNgay;
	}

	public JDateChooser getDateChooserDenNgay() {
		return dateChooserDenNgay;
	}

	public JComboBox<String> getCboHinhThucTT() {
		return cboHinhThucTT;
	}

	public JTable getTable() {
		return table;
	}

	public HoaDonTableModel getTableModel() {
		return tableModel;
	}

	public HoaDonController getController() {
		return controller;
	}

	public void setTxtTuKhoa(JTextField txtTuKhoa) {
		this.txtTuKhoa = txtTuKhoa;
	}

	public void setBtnTraCuu(JButton btnTraCuu) {
		this.btnTraCuu = btnTraCuu;
	}

	public void setCboLoaiTimKiem(JComboBox<String> cboLoaiTimKiem) {
		this.cboLoaiTimKiem = cboLoaiTimKiem;
	}

	public void setCboLoaiHoaDon(JComboBox<String> cboLoaiHoaDon) {
		this.cboLoaiHoaDon = cboLoaiHoaDon;
	}

	public void setTxtKhachHangSuggest(JTextField txtKhachHangSuggest) {
		this.txtKhachHangSuggest = txtKhachHangSuggest;
	}

	public void setDateChooserTuNgay(JDateChooser dateChooserTuNgay) {
		this.dateChooserTuNgay = dateChooserTuNgay;
	}

	public void setDateChooserDenNgay(JDateChooser dateChooserDenNgay) {
		this.dateChooserDenNgay = dateChooserDenNgay;
	}

	public void setCboHinhThucTT(JComboBox<String> cboHinhThucTT) {
		this.cboHinhThucTT = cboHinhThucTT;
	}

	public void setTable(JTable table) {
		this.table = table;
	}

	public void setTableModel(HoaDonTableModel tableModel) {
		this.tableModel = tableModel;
	}

	public JButton getBtnReset() {
		return btnReset;
	}

	public JButton getBtnLoc() {
		return btnLoc;
	}

	public static void autoResizeColumn(JTable table) {
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for (int column = 0; column < table.getColumnCount(); column++) {
			TableColumn tableColumn = table.getColumnModel().getColumn(column);
			int preferredWidth = tableColumn.getMinWidth();
			int maxWidth = 300; // giới hạn tránh quá rộng

			// Fit theo tiêu đề cột
			TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
			Component headerComp = headerRenderer.getTableCellRendererComponent(table, tableColumn.getHeaderValue(),
					false, false, 0, column);
			preferredWidth = Math.max(preferredWidth, headerComp.getPreferredSize().width);

			// Fit theo từng dòng
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
				Component c = table.prepareRenderer(cellRenderer, row, column);
				int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
				preferredWidth = Math.max(preferredWidth, width);

				if (preferredWidth >= maxWidth) {
					preferredWidth = maxWidth;
					break;
				}
			}

			tableColumn.setPreferredWidth(preferredWidth + 4); // padding 4px
		}
	}

}