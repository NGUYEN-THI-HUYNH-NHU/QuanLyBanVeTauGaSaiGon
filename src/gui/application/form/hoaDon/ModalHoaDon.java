package gui.application.form.hoaDon;

/*
 * @(#) ModalHoaDon.java  1.0  [9:05:33 PM] Nov 24, 2025
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import entity.HoaDon;
import entity.HoaDonChiTiet;
import gui.tuyChinh.CurrencyRenderer;
import gui.tuyChinh.LeftCenterAlignRenderer;

public class ModalHoaDon extends JDialog {
	private final HoaDon hoaDon;
	private final List<HoaDonChiTiet> listChiTiet;
	private final Font fontRegular = new Font(getFont().getFontName(), Font.PLAIN, 12);
	private final Font fontBold = new Font(getFont().getFontName(), Font.BOLD, 12);

	public ModalHoaDon(Frame parent, HoaDon hoaDon, List<HoaDonChiTiet> listChiTiet) {
		super(parent, "Xem Hóa Đơn: " + hoaDon.getHoaDonID(), true);
		this.hoaDon = hoaDon;
		this.listChiTiet = listChiTiet;

		setSize(900, 700); // Kích thước khổ giấy A4 tỉ lệ màn hình
		setLocationRelativeTo(parent);

		// Main Panel giả lập tờ giấy trắng
		JPanel pnlPaper = new JPanel(new BorderLayout());
		pnlPaper.setBackground(Color.WHITE);
		pnlPaper.setBorder(new EmptyBorder(20, 30, 20, 30)); // Căn lề giấy

		// Thêm nội dung vào tờ giấy
		pnlPaper.add(createHeader(), BorderLayout.NORTH);
		pnlPaper.add(createBody(), BorderLayout.CENTER);
		pnlPaper.add(createFooter(), BorderLayout.SOUTH);

		// Cho vào ScrollPane để cuộn nếu hóa đơn dài
		JScrollPane scroll = new JScrollPane(pnlPaper);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(scroll);
	}

	// --- PHẦN 1: HEADER (Logo, Tên cty, Tiêu đề hóa đơn) ---
	private JPanel createHeader() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);

		// Bên Trái: Thông tin người bán
		JPanel pLeft = new JPanel();
		pLeft.setLayout(new BoxLayout(pLeft, BoxLayout.Y_AXIS));
		pLeft.setBackground(Color.WHITE);

		// Giả lập Logo
		JLabel lblLogo = new JLabel("[LOGO ĐSVN]");
		lblLogo.setFont(new Font(getFont().getName(), Font.BOLD, 16));
		lblLogo.setForeground(new Color(0, 102, 204));

		pLeft.add(lblLogo);
		pLeft.add(createTextLine("Đơn vị bán hàng: CÔNG TY CỔ PHẦN VẬN TẢI ĐƯỜNG SẮT", true));
		pLeft.add(createTextLine("Mã số thuế: 010010XXXX", false));
		pLeft.add(createTextLine("Địa chỉ: 12, Nguyễn Văn Bảo", false));
		pLeft.add(createTextLine("Điện thoại: 0389390381", false));

		// Bên Phải: Thông tin hóa đơn
		JPanel pRight = new JPanel();
		pRight.setLayout(new BoxLayout(pRight, BoxLayout.Y_AXIS));
		pRight.setBackground(Color.WHITE);

		JLabel lblTitle = null;
		if (hoaDon.getHoaDonID().startsWith("HDDV")) {
			lblTitle = new JLabel("HÓA ĐƠN ĐỔI VÉ");
		} else if (hoaDon.getHoaDonID().startsWith("HDHV")) {
			lblTitle = new JLabel("HÓA ĐƠN HOÀN VÉ");
		} else {
			lblTitle = new JLabel("HÓA ĐƠN MUA VÉ");
		}
		lblTitle.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblTitle.setForeground(Color.RED);
		lblTitle.setAlignmentX(Component.RIGHT_ALIGNMENT);

		pRight.add(lblTitle);
		pRight.add(Box.createVerticalStrut(5));
		pRight.add(createRightAlignLabel("Số: " + hoaDon.getHoaDonID()));
		pRight.add(createRightAlignLabel("Ngày: " + formatDateTime(hoaDon.getThoiDiemTao())));

		p.add(pLeft, BorderLayout.WEST);
		p.add(pRight, BorderLayout.EAST);
		p.add(new JSeparator(), BorderLayout.SOUTH);

		return p;
	}

	// --- PHẦN 2: BODY (Thông tin khách hàng + Bảng) ---
	private JPanel createBody() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);
		p.setBorder(new EmptyBorder(10, 0, 10, 0));

		// 2a. Thông tin người mua
		JPanel pInfo = new JPanel(new GridBagLayout());
		pInfo.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 0, 2, 0);
		gbc.gridx = 0;
		gbc.gridy = 0;

		// Helper add row
		addInfoRow(pInfo, gbc, "Họ tên người mua hàng: ", hoaDon.getKhachHang().getHoTen());
		addInfoRow(pInfo, gbc, "Số định danh/CCCD: ", hoaDon.getKhachHang().getSoGiayTo());
		addInfoRow(pInfo, gbc, "Địa chỉ: ", hoaDon.getKhachHang().getDiaChi());
		addInfoRow(pInfo, gbc, "Hình thức thanh toán: ", hoaDon.isThanhToanTienMat() ? "Tiền mặt" : "Chuyển khoản");

		// 2b. Bảng chi tiết
		HoaDonChiTietTableModel model = new HoaDonChiTietTableModel();
		model.setRows(listChiTiet);
		JTable table = new JTable(model);
		table.setEnabled(false);
		styleTable(table);

		JScrollPane scrollTable = new JScrollPane(table);
		scrollTable.getViewport().setBackground(Color.WHITE);
		scrollTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		scrollTable.setPreferredSize(new Dimension(800, 200));

		p.add(pInfo, BorderLayout.NORTH);
		p.add(Box.createVerticalStrut(10));
		p.add(scrollTable, BorderLayout.CENTER);

		return p;
	}

	// --- PHẦN 3: FOOTER (Tổng tiền, Chữ ký) ---
	private JPanel createFooter() {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);
		p.setBorder(new EmptyBorder(10, 0, 0, 0));

		// 3a. Tổng cộng
		JPanel pTotal = new JPanel(new GridLayout(3, 1));
		pTotal.setBackground(Color.WHITE);

		// Tính tổng
		double tongTien = hoaDon.getTongTien();
		double tienNhan = hoaDon.getTienNhan();
		double tienHoan = hoaDon.getTienHoan();

		pTotal.add(createTotalRow("Tổng tiền:", tongTien));
		pTotal.add(createTotalRow("Tiền nhận:", tienNhan));
		pTotal.add(createTotalRow("Tiền hoàn", tienHoan));

		// Số tiền bằng chữ
		JLabel lblTextMoney = new JLabel("Số tiền viết bằng chữ: " + docSoThanhChu(tongTien));
		lblTextMoney.setFont(new Font("Times New Roman", Font.ITALIC, 14));
		lblTextMoney.setBorder(new EmptyBorder(5, 0, 15, 0));

		// 3b. Chữ ký
		JPanel pSign = new JPanel(new GridLayout(1, 2));
		pSign.setBackground(Color.WHITE);

		pSign.add(createSignBox("Người mua hàng", "(Ký, ghi rõ họ tên)"));
		pSign.add(createSignBox("Người bán hàng", "(Ký, đóng dấu, ghi rõ họ tên)"));

		JPanel pBottom = new JPanel(new BorderLayout());
		pBottom.setBackground(Color.WHITE);
		pBottom.add(pTotal, BorderLayout.NORTH);
		pBottom.add(lblTextMoney, BorderLayout.CENTER);
		pBottom.add(pSign, BorderLayout.SOUTH);

		p.add(pBottom, BorderLayout.CENTER);

		// Dòng tra cứu
		JLabel lblWeb = new JLabel(
				"Tra cứu tại website: https://hoadon.vtdsvn.vn - Mã tra cứu: " + hoaDon.getHoaDonID());
		lblWeb.setHorizontalAlignment(SwingConstants.CENTER);
		lblWeb.setFont(new Font(getFont().getName(), Font.ITALIC, 11));
		p.add(lblWeb, BorderLayout.SOUTH);

		return p;
	}

	private void addInfoRow(JPanel p, GridBagConstraints gbc, String label, String value) {
		gbc.gridy++;
		JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2));
		row.setBackground(Color.WHITE);

		JLabel l = new JLabel(label);
		l.setFont(fontBold);
		JLabel v = new JLabel(value);
		v.setFont(fontRegular);

		row.add(l);
		row.add(v);
		p.add(row, gbc);
	}

	private JPanel createTotalRow(String title, double value) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);
		JLabel l = new JLabel(title);
		l.setFont(fontBold);
		JLabel v = new JLabel(formatMoney(value));
		v.setFont(fontBold);
		p.add(l, BorderLayout.WEST);
		p.add(v, BorderLayout.EAST);
		return p;
	}

	private JPanel createSignBox(String role, String note) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Color.WHITE);

		JLabel l1 = new JLabel(role);
		l1.setFont(fontBold);
		l1.setAlignmentX(CENTER_ALIGNMENT);
		JLabel l2 = new JLabel(note);
		l2.setFont(new Font("Times New Roman", Font.ITALIC, 11));
		l2.setAlignmentX(CENTER_ALIGNMENT);

		p.add(l1);
		p.add(l2);
		p.add(Box.createVerticalStrut(60));
		return p;
	}

	private JLabel createTextLine(String text, boolean bold) {
		JLabel l = new JLabel(text);
		l.setFont(bold ? fontBold : fontRegular);
		return l;
	}

	private JLabel createRightAlignLabel(String text) {
		JLabel l = new JLabel(text);
		l.setFont(fontRegular);
		l.setAlignmentX(Component.RIGHT_ALIGNMENT);
		return l;
	}

	private void styleTable(JTable table) {
		table.setRowHeight(30);
		table.setFont(fontRegular);
		table.getTableHeader().setFont(fontBold);
		table.setShowGrid(true);
		table.setGridColor(Color.LIGHT_GRAY);

		table.getColumnModel().getColumn(0).setMaxWidth(32);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(240);
		table.getColumnModel().getColumn(3).setMaxWidth(40);
		table.getColumnModel().getColumn(4).setMaxWidth(40);
		table.getColumnModel().getColumn(5).setMaxWidth(70);
		table.getColumnModel().getColumn(6).setMaxWidth(70);

		// Căn phải cột tiền
		CurrencyRenderer currencyRenderer = new CurrencyRenderer();
		int[] moneyCols = { 3, 4, 5, 6 };
		for (int i : moneyCols) {
			table.getColumnModel().getColumn(i).setCellRenderer(currencyRenderer);
		}
		table.getColumnModel().getColumn(0).setCellRenderer(new LeftCenterAlignRenderer());

	}

	private String formatMoney(double money) {
		return NumberFormat.getInstance(new Locale("vi", "VN")).format(money) + " đ";
	}

	private String formatDateTime(LocalDateTime dt) {
		return dt == null ? "" : dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	}

	// Placeholder hàm đọc số
	private String docSoThanhChu(double number) {
		// Bạn có thể tích hợp thư viện đọc số tiếng Việt ở đây
		// Hiện tại return dummy text giống file PDF
		return "Bằng chữ: Bốn mươi mốt nghìn đồng.";
	}
}