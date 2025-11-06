package gui.application.form.thongKe;
/*
 * @(#) PanelThongKe.java  1.0  [10:07:31 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 1, 2025
 * @version: 1.0
 */

import entity.NhanVien;
import gui.application.AuthService;

/**
 * Panel Thống Kê (Dashboard) - Chỉ hiển thị, không nhập liệu. Sử dụng 6
 * StatCard và một khu vực cho biểu đồ.
 */
public class PanelThongKe extends JPanel {

	private final NhanVien nhanVien;
	// 6 ô cards
	private StatCard cardTongHoaDon, cardHoaDonDoiTra, cardSoVeBanDuoc;
	private StatCard cardChuyenKhoan, cardTienMat, cardTongThuDuoc;

	// Các nhãn thông tin
	private JLabel lblTenNhanVien, lblCaLamViec, lblNgayLamViec;

	private DecimalFormat df = new DecimalFormat("#,##0 VNĐ");

	public PanelThongKe() {
		nhanVien = AuthService.getInstance().getCurrentUser();
		initComponents();

		System.out.println(nhanVien);

		// Tải dữ liệu thống kê (giả lập)
		loadDashboardData();
	}

	private void initComponents() {
		setLayout(new BorderLayout(15, 15)); // Layout chính
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Viền trống xung quanh
		setBackground(Color.WHITE);

		// === PHẦN 1: THÔNG TIN (NORTH) ===
		add(createTitlePanel(), BorderLayout.NORTH);

		// === PHẦN 2: NỘI DUNG CHÍNH (CENTER) ===
		// Panel này lại chia làm 2: Card ở trên, Biểu đồ ở dưới
		JPanel pnlMainContent = new JPanel(new BorderLayout(15, 15));
		pnlMainContent.setOpaque(false); // Trong suốt để lấy màu nền của cha

		// 2.1 Panel 6 ô Cards (GridLayout)
		pnlMainContent.add(createCardPanel(), BorderLayout.NORTH);

		// 2.2 Panel Biểu đồ (Placeholder)
		pnlMainContent.add(createChartPanel(), BorderLayout.CENTER);

		add(pnlMainContent, BorderLayout.CENTER);
	}

	/**
	 * Tạo Panel tiêu đề và thông tin nhân viên
	 */
	private JPanel createTitlePanel() {
		JPanel pnlTitle = new JPanel(new GridBagLayout());
		pnlTitle.setOpaque(false); // Trong suốt
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		// Tiêu đề chính
		JLabel lblMainTitle = new JLabel("Thống kê cuối ca");
		lblMainTitle.setFont(new Font("Arial", Font.BOLD, 24));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4; // Chiếm 4 cột
		pnlTitle.add(lblMainTitle, gbc);

		// Thông tin nhân viên
		gbc.gridwidth = 1; // Reset
		gbc.gridx = 0;
		gbc.gridy = 1;
		pnlTitle.add(new JLabel("Tên nhân viên:"), gbc);

		gbc.gridx = 1;
		lblTenNhanVien = new JLabel(nhanVien.getHoTen()); // Sẽ được cập nhật
		lblTenNhanVien.setFont(new Font("Arial", Font.BOLD, 14));
		pnlTitle.add(lblTenNhanVien, gbc);

		gbc.gridx = 2;
		pnlTitle.add(new JLabel("Ca làm việc:"), gbc);

		gbc.gridx = 3;
		lblCaLamViec = new JLabel("Ca 1 (08:00 - 16:00)"); // Sẽ được cập nhật
		lblCaLamViec.setFont(new Font("Arial", Font.BOLD, 14));
		pnlTitle.add(lblCaLamViec, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		pnlTitle.add(new JLabel("Ngày làm việc:"), gbc);

		gbc.gridx = 1;
		lblNgayLamViec = new JLabel("dd/MM/yyyy"); // Sẽ được cập nhật
		lblNgayLamViec.setFont(new Font("Arial", Font.BOLD, 14));
		pnlTitle.add(lblNgayLamViec, gbc);

		return pnlTitle;
	}

	/**
	 * Tạo Panel chứa 6 ô card thống kê
	 */
	private JPanel createCardPanel() {
		JPanel pnlCards = new JPanel(new GridLayout(2, 3, 15, 15)); // 2 dòng, 3 cột, khoảng cách 15px
		pnlCards.setOpaque(false); // Trong suốt

		// Khởi tạo 6 cards
		cardTongHoaDon = new StatCard("Tổng hóa đơn bán được", "0", new Color(0, 102, 204));
		cardHoaDonDoiTra = new StatCard("Tổng hóa đơn đổi trả", "0", new Color(204, 102, 0));
		cardSoVeBanDuoc = new StatCard("Tổng số vé bán được", "0", new Color(0, 102, 51));

		cardChuyenKhoan = new StatCard("Tổng chuyển khoản", df.format(0), Color.BLACK);
		cardTienMat = new StatCard("Tổng tiền mặt (Hệ thống)", df.format(0), Color.BLACK);
		cardTongThuDuoc = new StatCard("Tổng tiền thu được", df.format(0), new Color(204, 0, 0));

		// Thêm 6 cards vào panel
		pnlCards.add(cardTongHoaDon);
		pnlCards.add(cardHoaDonDoiTra);
		pnlCards.add(cardSoVeBanDuoc);
		pnlCards.add(cardChuyenKhoan);
		pnlCards.add(cardTienMat);
		pnlCards.add(cardTongThuDuoc);

		return pnlCards;
	}

	/**
	 * Tạo Panel placeholder cho biểu đồ (lấp đầy khoảng trống)
	 */
	private JPanel createChartPanel() {
		JPanel pnlChart = new JPanel(new BorderLayout());
		pnlChart.setBorder(new TitledBorder("Biểu đồ"));
		pnlChart.setOpaque(false);

		// Đây là nơi bạn sẽ thêm thư viện biểu đồ (ví dụ: JFreeChart)
		JLabel lblPlaceholder = new JLabel("Biểu đồ (ví dụ: Doanh thu theo giờ) sẽ hiển thị ở đây",
				SwingConstants.CENTER);
		lblPlaceholder.setFont(new Font("Arial", Font.ITALIC, 16));
		lblPlaceholder.setForeground(Color.GRAY);
		pnlChart.add(lblPlaceholder, BorderLayout.CENTER);

		return pnlChart;
	}

	/**
	 * Hàm giả lập tải dữ liệu từ DAO và cập nhật lên các Card
	 */
	private void loadDashboardData() {
		// --- Giả lập dữ liệu ---
		String tenNhanVien = "Trần Thị B";
		String caLamViec = "Ca 2 (16:00 - 22:00)";
		long soHoaDon = 120;
		long soHoaDonHuy = 5;
		long soVe = 250;
		long tienChuyenKhoan = 45000000;
		long tienMat = 15000000;
		long tongDoanhThu = tienChuyenKhoan + tienMat; // (Doanh thu = 60tr)
		// --- Kết thúc giả lập ---

		// Cập nhật lên Giao diện
		lblTenNhanVien.setText(tenNhanVien);
		lblCaLamViec.setText(caLamViec);
		lblNgayLamViec.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

		cardTongHoaDon.setValue(soHoaDon, " hóa đơn");
		cardHoaDonDoiTra.setValue(soHoaDonHuy, " hóa đơn");
		cardSoVeBanDuoc.setValue(soVe, " vé");

		cardChuyenKhoan.setValue(df.format(tienChuyenKhoan));
		cardTienMat.setValue(df.format(tienMat));
		cardTongThuDuoc.setValue(df.format(tongDoanhThu));
	}

	/**
	 * Hàm main để chạy thử và xem giao diện
	 */
	public static void main(String[] args) {
		// Đảm bảo chạy trên Event Dispatch Thread
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Test Panel Thống Kê");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// Đây là Panel chính của bạn (giống trong Figma)
			JPanel mainAppPanel = new JPanel(new BorderLayout());

			// 1. Giả lập Menu bên trái
			JPanel pnlMenu = new JPanel();
			pnlMenu.setBackground(new Color(34, 49, 63)); // Màu xanh đậm
			pnlMenu.setPreferredSize(new Dimension(220, 0));
			pnlMenu.add(new JLabel(new ImageIcon("path/to/your/logo.png"))); // Thêm logo của bạn
			// ... Thêm các nút menu ...
			mainAppPanel.add(pnlMenu, BorderLayout.WEST);

			// 2. Thêm Panel Thống Kê (nội dung bên phải)
			PanelThongKe pnlThongKe = new PanelThongKe();
			mainAppPanel.add(pnlThongKe, BorderLayout.CENTER);

			frame.setContentPane(mainAppPanel);
			frame.setSize(1280, 720); // Kích thước cửa sổ
			frame.setLocationRelativeTo(null); // Giữa màn hình
			frame.setVisible(true);
		});
	}

}