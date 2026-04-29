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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;

import gui.tuyChinh.CurrencyRenderer;
import gui.tuyChinh.DateTimeRenderer;
import gui.tuyChinh.LeftCenterAlignRenderer;

public class PanelQuanLyHoaDon extends JPanel {
	private JTextField txtTuKhoa;
	private JButton btnTraCuu;
	private JButton btnRefresh;
	private JComboBox<String> cboLoaiTimKiem;

	private JComboBox<String> cboLoaiHoaDon;
	private JTextField txtKhachHangSuggest;
	private JDateChooser dateChooserTuNgay;
	private JDateChooser dateChooserDenNgay;
	private JComboBox<String> cboHinhThucTT;
	private JButton btnLoc;
	private JButton btnReset;

	private JTable table;
	private HoaDonTableModel tableModel;

	private final HoaDonController controller;
	private JCheckBox checkBoxTatCaNgay;

	public PanelQuanLyHoaDon() {
		setLayout(new BorderLayout());
		initUI();
		controller = new HoaDonController(this);
	}

	private void initUI() {
		JPanel pnlTop = new JPanel();
		pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));

		// 1. PANEL TRA CỨU
		JPanel pnlTraCuu = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlTraCuu.setBorder(new TitledBorder("Tra cứu nhanh"));

		cboLoaiTimKiem = new JComboBox<>(new String[] { "Mã hóa đơn", "Mã khách hàng", "Mã giao dịch" });
		txtTuKhoa = new JTextField(18);
		btnTraCuu = new JButton("Tra cứu");
		btnTraCuu.setBackground(new Color(36, 104, 155));
		btnTraCuu.setForeground(Color.WHITE);
		btnTraCuu.setIcon(new FlatSVGIcon("icon/svg/search.svg", 0.8f));
		btnRefresh = new JButton("Làm mới");
		btnRefresh.setIcon(new FlatSVGIcon("icon/svg/refresh-1.svg", 0.8f));

		pnlTraCuu.add(new JLabel("Tìm theo: "));
		pnlTraCuu.add(cboLoaiTimKiem);
		pnlTraCuu.add(txtTuKhoa);
		pnlTraCuu.add(btnTraCuu);
		pnlTraCuu.add(btnRefresh);

		// 2. PANEL LỌC
		JPanel pnlLoc = new JPanel();
		pnlLoc.setLayout(new BorderLayout());
		pnlLoc.setBorder(new TitledBorder("Bộ lọc chi tiết"));

		JPanel pnlInput = new JPanel(new GridBagLayout());
		pnlInput.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);

		// 1. Loại HĐ
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		pnlInput.add(new JLabel("Loại HĐ:"), gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.2;
		cboLoaiHoaDon = new JComboBox<>(
				new String[] { "Tất cả", "Hóa đơn bán vé", "Hóa đơn hoàn vé", "Hóa đơn đổi vé" });
		pnlInput.add(cboLoaiHoaDon, gbc);

		// 2. Khách hàng
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0;
		pnlInput.add(new JLabel("Khách hàng:"), gbc);

		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.weightx = 0.2;
		txtKhachHangSuggest = new JTextField(14);
		txtKhachHangSuggest.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Họ tên/SĐT/CCCD/ID");
		txtKhachHangSuggest.setToolTipText("Nhập tên, SĐT, CCCD hoặc ID");
		pnlInput.add(txtKhachHangSuggest, gbc);

		// 3. Hình thức thanh toán
		gbc.gridx = 4;
		gbc.gridy = 0;
		gbc.weightx = 0;
		pnlInput.add(new JLabel("Thanh toán:"), gbc);

		gbc.gridx = 5;
		gbc.gridy = 0;
		gbc.weightx = 0.2;
		cboHinhThucTT = new JComboBox<>(new String[] { "Tất cả", "Tiền mặt", "Chuyển khoản" });
		pnlInput.add(cboHinhThucTT, gbc);

		// 4. Checkbox Tất cả ngày
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.gridwidth = 2;
		checkBoxTatCaNgay = new JCheckBox("Tất cả ngày");
		checkBoxTatCaNgay.setSelected(true);
		checkBoxTatCaNgay.setBackground(Color.WHITE);
		pnlInput.add(checkBoxTatCaNgay, gbc);
		gbc.gridwidth = 1;

		// 5. Từ ngày
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.weightx = 0;
		pnlInput.add(new JLabel("Từ ngày:"), gbc);

		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.weightx = 0.2;
		dateChooserTuNgay = new JDateChooser();
		dateChooserTuNgay.setDateFormatString("dd/MM/yyyy");
		dateChooserTuNgay.setDate(new Date());
		dateChooserTuNgay.setEnabled(false);
		pnlInput.add(dateChooserTuNgay, gbc);

		// 6. Đến ngày
		gbc.gridx = 4;
		gbc.gridy = 1;
		gbc.weightx = 0;
		pnlInput.add(new JLabel("Đến ngày:"), gbc);

		gbc.gridx = 5;
		gbc.gridy = 1;
		gbc.weightx = 0.2;
		dateChooserDenNgay = new JDateChooser();
		dateChooserDenNgay.setDateFormatString("dd/MM/yyyy");
		dateChooserDenNgay.setDate(new Date());
		dateChooserDenNgay.setEnabled(false);
		pnlInput.add(dateChooserDenNgay, gbc);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
		pnlButtons.setBackground(Color.WHITE);

		btnReset = new JButton("Xóa bộ lọc");
		btnReset.setIcon(new FlatSVGIcon("icon/svg/reset.svg", 0.8f));

		btnLoc = new JButton("Lọc");
		btnLoc.setIcon(new FlatSVGIcon("icon/svg/filter.svg", 0.8f));
		btnLoc.setBackground(new Color(38, 117, 191));
		btnLoc.setForeground(Color.WHITE);

		pnlButtons.add(btnLoc);
		pnlButtons.add(btnReset);

		pnlLoc.add(pnlInput, BorderLayout.CENTER);
		pnlLoc.add(pnlButtons, BorderLayout.SOUTH);

		pnlTop.add(pnlTraCuu);
		pnlTop.add(pnlLoc);

		// Bảng
		tableModel = new HoaDonTableModel();
		table = new JTable(tableModel);
		table.getTableHeader().setFont(new Font(getFont().getFontName(), Font.BOLD, getFont().getSize()));
		table.setRowHeight(36);

		table.getColumnModel().getColumn(0).setMaxWidth(34);
		table.getColumnModel().getColumn(1).setMinWidth(170);
		table.getColumnModel().getColumn(2).setPreferredWidth(70);
		table.getColumnModel().getColumn(3).setMinWidth(150);
		table.getColumnModel().getColumn(4).setMinWidth(96);
		table.getColumnModel().getColumn(5).setMinWidth(116);
		table.getColumnModel().getColumn(6).setMinWidth(76);
		table.getColumnModel().getColumn(7).setMinWidth(76);
		table.getColumnModel().getColumn(8).setMinWidth(76);
		table.getColumnModel().getColumn(9).setMinWidth(50);
		table.getColumnModel().getColumn(10).setMaxWidth(34);
		table.getColumnModel().getColumn(11).setMaxWidth(34);

		LeftCenterAlignRenderer leftCenterRenderer = new LeftCenterAlignRenderer();
		CurrencyRenderer currencyRenderer = new CurrencyRenderer();

		table.getColumnModel().getColumn(HoaDonTableModel.COL_THOI_DIEM_TAO).setCellRenderer(new DateTimeRenderer());

		table.getColumnModel().getColumn(HoaDonTableModel.COL_HOA_DON_ID).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(HoaDonTableModel.COL_KHACH_HANG_ID).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(HoaDonTableModel.COL_TEN_KHACH_HANG).setCellRenderer(leftCenterRenderer);
		table.getColumnModel().getColumn(HoaDonTableModel.COL_CCCD_KHACH_HANG).setCellRenderer(leftCenterRenderer);

		table.getColumnModel().getColumn(HoaDonTableModel.COL_TONG_TIEN).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(HoaDonTableModel.COL_TIEN_NHAN).setCellRenderer(currencyRenderer);
		table.getColumnModel().getColumn(HoaDonTableModel.COL_TIEN_HOAN).setCellRenderer(currencyRenderer);

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

	public JButton getBtnRefresh() {
		return btnRefresh;
	}

	public JCheckBox getCheckBoxTatCaNgay() {
		return checkBoxTatCaNgay;
	}
}