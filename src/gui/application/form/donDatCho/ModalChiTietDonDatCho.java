package gui.application.form.donDatCho;
/*
 * @(#) ModalChiTietDonDatCho.java  1.0  [12:43:31 PM] Dec 12, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 12, 2025
 * @version: 1.0
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import entity.DonDatCho;
import entity.Ve;
import entity.type.TrangThaiVe;
import gui.tuyChinh.CurrencyRenderer;

public class ModalChiTietDonDatCho extends JDialog {
	private final DonDatCho donDatCho;
	private final List<Ve> listVe;
	private final Font fontBold = new Font(getFont().getFontName(), Font.BOLD, 12);

	public ModalChiTietDonDatCho(Frame parent, DonDatCho donDatCho, List<Ve> listVe) {
		super(parent, "Chi Tiết Đơn Đặt Chỗ: " + donDatCho.getDonDatChoID(), true);
		this.donDatCho = donDatCho;
		this.listVe = listVe;

		setSize(850, 600);
		setLocationRelativeTo(parent);
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		add(createHeader(), BorderLayout.NORTH);
		add(createBody(), BorderLayout.CENTER);
		add(createFooter(), BorderLayout.SOUTH);
	}

	private JPanel createHeader() {
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBackground(new Color(245, 245, 245));
		pnl.setBorder(new EmptyBorder(15, 20, 15, 20));

		JLabel lblTitle = new JLabel("THÔNG TIN ĐƠN ĐẶT CHỖ");
		lblTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 20));
		lblTitle.setForeground(new Color(36, 104, 155));

		JPanel pnlSub = new JPanel(new GridLayout(2, 1));
		pnlSub.setOpaque(false);
		pnlSub.add(new JLabel("Mã đơn: " + donDatCho.getDonDatChoID()));
		pnlSub.add(new JLabel(
				"Ngày lập: " + donDatCho.getThoiDiemDatCho().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));

		pnl.add(lblTitle, BorderLayout.WEST);
		pnl.add(pnlSub, BorderLayout.EAST);
		return pnl;
	}

	private JPanel createBody() {
		JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBorder(new EmptyBorder(10, 20, 10, 20));
		pnl.setBackground(Color.WHITE);

		// 1. Thông tin khách hàng
		JPanel pnlInfo = new JPanel(new GridLayout(2, 2, 10, 5));
		pnlInfo.setBackground(Color.WHITE);
		pnlInfo.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

		pnlInfo.add(createLabelInfo("Họ tên:", donDatCho.getKhachHang().getHoTen()));
		pnlInfo.add(createLabelInfo("Số giấy tờ:", donDatCho.getKhachHang().getSoGiayTo()));
		pnlInfo.add(createLabelInfo("Số điện thoại:", donDatCho.getKhachHang().getSoDienThoai()));
		pnlInfo.add(createLabelInfo("Email:", donDatCho.getKhachHang().getEmail()));

		// 2. Bảng vé
		ChiTietDonDatChoTableModel tableModel = new ChiTietDonDatChoTableModel();
		tableModel.setRows(listVe);
		JTable table = new JTable(tableModel);
		styleTable(table);

		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(BorderFactory.createTitledBorder("Danh sách vé trong đơn"));
		scroll.getViewport().setBackground(Color.WHITE);

		pnl.add(pnlInfo, BorderLayout.NORTH);
		pnl.add(scroll, BorderLayout.CENTER);
		return pnl;
	}

	private JPanel createFooter() {
		JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
		pnl.setBackground(new Color(245, 245, 245));

		// Tính tổng tiền
		double total = listVe.stream().mapToDouble(Ve::getGia).sum();

		JLabel lblTotal = new JLabel(
				"Tổng giá trị đơn hàng: " + NumberFormat.getInstance(new Locale("vi", "VN")).format(total) + " đ");
		lblTotal.setFont(new Font(getFont().getName(), Font.BOLD, 14));

		JButton btnClose = new JButton("Đóng");
		btnClose.addActionListener(e -> dispose());

		pnl.add(lblTotal);
		pnl.add(btnClose);

		return pnl;
	}

	private JPanel createLabelInfo(String title, String value) {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.setOpaque(false);
		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(fontBold);
		JLabel lblValue = new JLabel(value == null ? "" : value);

		p.add(lblTitle);
		p.add(lblValue);
		return p;
	}

	private void styleTable(JTable table) {
		table.setRowHeight(30);
		table.getTableHeader().setFont(fontBold);
		table.getTableHeader().setBackground(new Color(230, 230, 230));

		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_STT).setMaxWidth(30);
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_VE_ID).setMinWidth(150);
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_GA_DI).setMaxWidth(70);
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_GA_DEN).setMaxWidth(70);
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_SO_GHE).setMinWidth(110);
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_GIA).setMaxWidth(80);
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_LOAI_VE).setMaxWidth(70);
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_TRANG_THAI).setMinWidth(50);

		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_GIA).setCellRenderer(new CurrencyRenderer());

		// Custom Renderer cho cột Trạng Thái để làm nổi bật vé đã hoàn/hủy
		table.getColumnModel().getColumn(ChiTietDonDatChoTableModel.COL_TRANG_THAI)
				.setCellRenderer(new DefaultTableCellRenderer() {
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
								column);
						String status = (String) value;
						if (status.equals(TrangThaiVe.DA_BAN.getDescription())) {
							c.setForeground(Color.GREEN);
							setFont(getFont().deriveFont(Font.ITALIC));
						} else if (status.equals(TrangThaiVe.DA_DUNG.getDescription())) {
							c.setForeground(Color.BLACK);
						} else if (status.equals(TrangThaiVe.DA_HOAN.getDescription())) {
							c.setForeground(Color.RED);
						} else if (status.equals(TrangThaiVe.DA_DOI.getDescription())) {
							c.setForeground(Color.ORANGE);
						}
						return c;
					}
				});
	}
}