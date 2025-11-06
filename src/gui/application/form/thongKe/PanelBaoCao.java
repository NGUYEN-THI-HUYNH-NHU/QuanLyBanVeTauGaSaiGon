package gui.application.form.thongKe;
/*
 * @(#) PanelBaoCao.java  1.0  [10:07:39 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 1, 2025
 * @version: 1.0
 */
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel Báo Cáo Giao Ca (Input Form) Nơi nhân viên nhập tiền mặt, xem chênh
 * lệch, ghi chú và xác nhận ca.
 */
public class PanelBaoCao extends JPanel {

	// Format tiền tệ
	private final DecimalFormat df = new DecimalFormat("#,##0 VNĐ");

	// Thông tin ca
	private JLabel lblTenNhanVien, lblCaLamViec, lblNgayLamViec;

	// Biến lưu trữ giá trị hệ thống (sẽ được lấy từ DAO)
	private double heThong_DoanhThuRong = 0;

	// === Panel 1: Kiểm Kê Tiền Mặt (Trái) ===
	// Dùng Hashtable để lưu trữ các components cho dễ quản lý
	private Hashtable<Integer, JSpinner> spinners = new Hashtable<>();
	private Hashtable<Integer, JLabel> labelsThanhTien = new Hashtable<>();
	private final int[] menhGiaArr = { 500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000 };
	private JSpinner spnTienLeKhac; // Tiền lẻ không theo mệnh giá
	private JLabel lblTongTienMatThucTe;

	// === Panel 2: Đối Soát & Ghi Chú (Phải) ===
	private JLabel lblTienHeThong_SoSanh;
	private JLabel lblTienThucTe_SoSanh;
	private JLabel lblChenhLech;
	private JLabel lblTrangThai;
	private JTextArea txtGhiChu;

	// === Panel 3: Nút Bấm (Nam) ===
	private JButton btnXacNhanGiaoCa;

	public PanelBaoCao() {
		initComponents();
		addEvents();

		// Tải dữ liệu ca làm việc (Doanh thu hệ thống, tên NV...)
		loadCaLamViecData();
	}

	private void initComponents() {
		setLayout(new BorderLayout(15, 15));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(Color.WHITE);

		// === PHẦN 1: TIÊU ĐỀ (NORTH) ===
		add(createTitlePanel(), BorderLayout.NORTH);

		// === PHẦN 2: NỘI DUNG CHÍNH (CENTER) ===
		// Sử dụng JSplitPane để chia đôi
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setResizeWeight(0.55); // Panel trái (nhập tiền) rộng hơn chút
		splitPane.setBorder(null); // Bỏ viền của JSplitPane
		splitPane.setOpaque(false);

		// 2.1 Panel Trái: Kiểm kê tiền
		splitPane.setLeftComponent(createKiemKePanel());

		// 2.2 Panel Phải: Đối soát và Ghi chú
		splitPane.setRightComponent(createDoiSoatPanel());

		add(splitPane, BorderLayout.CENTER);

		// === PHẦN 3: NÚT XÁC NHẬN (SOUTH) ===
		add(createButtonPanel(), BorderLayout.SOUTH);
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
		JLabel lblMainTitle = new JLabel("Lập Báo Cáo Giao Ca");
		lblMainTitle.setFont(new Font("Arial", Font.BOLD, 24));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		pnlTitle.add(lblMainTitle, gbc);

		// Thông tin
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		pnlTitle.add(new JLabel("Tên nhân viên:"), gbc);
		gbc.gridx = 1;
		lblTenNhanVien = new JLabel("...");
		lblTenNhanVien.setFont(new Font("Arial", Font.BOLD, 14));
		pnlTitle.add(lblTenNhanVien, gbc);

		gbc.gridx = 2;
		pnlTitle.add(new JLabel("Ca làm việc:"), gbc);
		gbc.gridx = 3;
		lblCaLamViec = new JLabel("...");
		lblCaLamViec.setFont(new Font("Arial", Font.BOLD, 14));
		pnlTitle.add(lblCaLamViec, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		pnlTitle.add(new JLabel("Ngày làm việc:"), gbc);
		gbc.gridx = 1;
		lblNgayLamViec = new JLabel("...");
		lblNgayLamViec.setFont(new Font("Arial", Font.BOLD, 14));
		pnlTitle.add(lblNgayLamViec, gbc);

		return pnlTitle;
	}

	/**
	 * Tạo Panel bên trái (Kiểm kê tiền mặt)
	 */
	private Component createKiemKePanel() {
		JPanel pnlKiemKe = new JPanel(new BorderLayout(10, 10));
		pnlKiemKe.setBorder(new TitledBorder("Kiểm kê tiền mặt trong két"));
		pnlKiemKe.setOpaque(false);

		// Panel chứa các dòng nhập liệu
		JPanel pnlInputs = new JPanel(new GridBagLayout());
		pnlInputs.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 8, 5, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Tiêu đề bảng
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		pnlInputs.add(new JLabel("Mệnh giá"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		pnlInputs.add(new JLabel("Số lượng"), gbc);
		gbc.gridx = 2;
		gbc.anchor = GridBagConstraints.EAST;
		pnlInputs.add(new JLabel("Thành tiền"), gbc);

		// Vòng lặp để tạo các dòng mệnh giá
		int gridY = 1;
		for (int menhGia : menhGiaArr) {
			gbc.gridy = gridY;

			// Mệnh giá
			gbc.gridx = 0;
			gbc.anchor = GridBagConstraints.WEST;
			pnlInputs.add(new JLabel(String.format("%,d", menhGia)), gbc);

			// JSpinner số lượng
			gbc.gridx = 1;
			gbc.anchor = GridBagConstraints.CENTER;
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
			spinners.put(menhGia, spinner); // Lưu vào Hashtable
			pnlInputs.add(spinner, gbc);

			// JLabel thành tiền
			gbc.gridx = 2;
			gbc.anchor = GridBagConstraints.EAST;
			JLabel lblThanhTien = new JLabel(df.format(0));
			lblThanhTien.setFont(new Font("Arial", Font.BOLD, 14));
			labelsThanhTien.put(menhGia, lblThanhTien); // Lưu vào Hashtable
			pnlInputs.add(lblThanhTien, gbc);

			gridY++;
		}

		// Dòng Tiền lẻ/khác
		gbc.gridy = gridY;
		gbc.gridx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		pnlInputs.add(new JLabel("Tiền lẻ/khác"), gbc);
		gbc.gridx = 1;
		gbc.gridwidth = 2; // Gộp 2 cột
		spnTienLeKhac = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 100.0));
		pnlInputs.add(spnTienLeKhac, gbc);

		// Panel Tổng
		JPanel pnlTong = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlTong.setOpaque(false);
		pnlTong.add(new JLabel("Tổng tiền mặt thực tế:"));
		lblTongTienMatThucTe = new JLabel(df.format(0));
		lblTongTienMatThucTe.setFont(new Font("Arial", Font.BOLD, 18));
		lblTongTienMatThucTe.setForeground(new Color(0, 102, 0)); // Xanh lá
		pnlTong.add(lblTongTienMatThucTe);

		pnlKiemKe.add(pnlInputs, BorderLayout.NORTH);
		pnlKiemKe.add(pnlTong, BorderLayout.SOUTH);

		// Dùng JScrollPane để nếu màn hình nhỏ vẫn cuộn được
		return new JScrollPane(pnlKiemKe);
	}

	/**
	 * Tạo Panel bên phải (Đối soát và Ghi chú)
	 */
	private JPanel createDoiSoatPanel() {
		JPanel pnlDoiSoat = new JPanel(new BorderLayout(10, 10));
		pnlDoiSoat.setBorder(new TitledBorder("Kết quả đối soát và Ghi chú"));
		pnlDoiSoat.setOpaque(false);

		// Panel con cho các nhãn đối soát
		JPanel pnlKetQua = new JPanel(new GridBagLayout());
		pnlKetQua.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.EAST;

		// Dòng 1: Tiền hệ thống
		gbc.gridx = 0;
		gbc.gridy = 0;
		pnlKetQua.add(new JLabel("Doanh thu hệ thống (A):"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		lblTienHeThong_SoSanh = new JLabel(df.format(0));
		lblTienHeThong_SoSanh.setFont(new Font("Arial", Font.BOLD, 16));
		lblTienHeThong_SoSanh.setForeground(Color.BLUE);
		pnlKetQua.add(lblTienHeThong_SoSanh, gbc);

		// Dòng 2: Tiền thực tế
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		pnlKetQua.add(new JLabel("Tiền mặt thực tế (B):"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		lblTienThucTe_SoSanh = new JLabel(df.format(0));
		lblTienThucTe_SoSanh.setFont(new Font("Arial", Font.BOLD, 16));
		lblTienThucTe_SoSanh.setForeground(new Color(0, 102, 0)); // Xanh lá
		pnlKetQua.add(lblTienThucTe_SoSanh, gbc);

		// Dòng 3: Chênh lệch
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		JLabel title3 = new JLabel("CHÊNH LỆCH (B - A):");
		title3.setFont(new Font("Arial", Font.BOLD, 16));
		pnlKetQua.add(title3, gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		lblChenhLech = new JLabel(df.format(0));
		lblChenhLech.setFont(new Font("Arial", Font.BOLD, 20));
		pnlKetQua.add(lblChenhLech, gbc);

		// Dòng 4: Trạng thái
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		lblTrangThai = new JLabel("(Chưa kiểm kê)");
		lblTrangThai.setFont(new Font("Arial", Font.ITALIC, 14));
		pnlKetQua.add(lblTrangThai, gbc);

		pnlDoiSoat.add(pnlKetQua, BorderLayout.NORTH);

		// Panel Ghi chú
		JPanel pnlGhiChu = new JPanel(new BorderLayout());
		pnlGhiChu.setOpaque(false);
		pnlGhiChu.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		pnlGhiChu.add(new JLabel("Ghi chú (Lý do chênh lệch, vấn đề phát sinh...):"), BorderLayout.NORTH);
		txtGhiChu = new JTextArea();
		txtGhiChu.setLineWrap(true); // Tự động xuống dòng
		txtGhiChu.setWrapStyleWord(true); // Xuống dòng tại từ
		pnlDoiSoat.add(new JScrollPane(txtGhiChu), BorderLayout.CENTER);

		return pnlDoiSoat;
	}

	/**
	 * Tạo Panel chứa nút bấm xác nhận
	 */
	private JPanel createButtonPanel() {
		JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlButton.setOpaque(false);

		btnXacNhanGiaoCa = new JButton("Lưu và Xác Nhận Giao Ca");
		btnXacNhanGiaoCa.setFont(new Font("Arial", Font.BOLD, 16));
		btnXacNhanGiaoCa.setBackground(new Color(0, 102, 51)); // Xanh đậm
		btnXacNhanGiaoCa.setForeground(Color.WHITE);
		// btnXacNhanGiaoCa.setIcon(new
		// ImageIcon(getClass().getResource("/icon/save.png")));

		pnlButton.add(btnXacNhanGiaoCa);
		return pnlButton;
	}

	// === PHẦN XỬ LÝ SỰ KIỆN ===

	private void addEvents() {
		// Tạo một listener chung cho TẤT CẢ các JSpinner mệnh giá
		ChangeListener spinnerListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				// Khi một spinner thay đổi, cập nhật tổng tiền mặt
				updateTongTienMat();
			}
		};

		// Gán listener cho các spinner
		for (JSpinner spinner : spinners.values()) {
			spinner.addChangeListener(spinnerListener);
		}
		spnTienLeKhac.addChangeListener(spinnerListener); // Cả tiền lẻ

		// Sự kiện nút "Xác nhận"
		btnXacNhanGiaoCa.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				xuLyXacNhanGiaoCa();
			}
		});
	}

	/**
	 * Tính toán và cập nhật tổng tiền mặt, sau đó cập nhật chênh lệch
	 */
	private void updateTongTienMat() {
		double tongTienMat = 0;

		// 1. Cập nhật từng dòng "Thành tiền" và cộng dồn
		for (int menhGia : menhGiaArr) {
			JSpinner spinner = spinners.get(menhGia);
			JLabel label = labelsThanhTien.get(menhGia);

			int soLuong = (Integer) spinner.getValue();
			double thanhTien = (double) soLuong * menhGia;

			label.setText(df.format(thanhTien)); // Cập nhật label thành tiền
			tongTienMat += thanhTien; // Cộng dồn
		}

		// 2. Cộng tiền lẻ
		tongTienMat += (Double) spnTienLeKhac.getValue();

		// 3. Cập nhật Label TỔNG tiền mặt
		lblTongTienMatThucTe.setText(df.format(tongTienMat));

		// 4. Gọi hàm cập nhật chênh lệch ở panel bên phải
		updateDoiSoat(this.heThong_DoanhThuRong, tongTienMat);
	}

	/**
	 * Tính toán và cập nhật chênh lệch
	 */
	private void updateDoiSoat(double tienHeThong, double tienThucTe) {
		// Cập nhật các label ở panel đối soát
		lblTienHeThong_SoSanh.setText(df.format(tienHeThong));
		lblTienThucTe_SoSanh.setText(df.format(tienThucTe));

		double chenhLech = tienThucTe - tienHeThong;
		lblChenhLech.setText(df.format(chenhLech));

		// Đổi màu label chênh lệch
		if (chenhLech < 0) {
			lblChenhLech.setForeground(Color.RED);
			lblTrangThai.setText("(Thiếu tiền)");
			lblTrangThai.setForeground(Color.RED);
		} else if (chenhLech > 0) {
			lblChenhLech.setForeground(new Color(0, 102, 0)); // Xanh lá
			lblTrangThai.setText("(Thừa tiền)");
			lblTrangThai.setForeground(new Color(0, 102, 0));
		} else {
			lblChenhLech.setForeground(Color.BLACK);
			lblTrangThai.setText("(Khớp)");
			lblTrangThai.setForeground(Color.BLACK);
		}
	}

	/**
	 * Hàm này sẽ gọi DAO để lấy dữ liệu ca làm việc (Doanh thu ròng, Tên NV...)
	 */
	private void loadCaLamViecData() {
		// --- GIẢ LẬP DỮ LIỆU TỪ DAO ---
		// Dữ liệu này sẽ được lấy từ DAO dựa trên nhân viên đang đăng nhập
		String tenNhanVien = "Trần Thị B";
		String caLamViec = "Ca 2 (16:00 - 22:00)";

		// Đây là con số quan trọng nhất: Tổng doanh thu hệ thống (đã trừ hoàn trả)
		// Nó sẽ được lấy từ DAO (giống như logic của 6 ô card bên PanelThongKe)
		this.heThong_DoanhThuRong = 7300000;
		// --- Kết thúc giả lập ---

		// Cập nhật thông tin lên giao diện
		lblTenNhanVien.setText(tenNhanVien);
		lblCaLamViec.setText(caLamViec);
		lblNgayLamViec.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

		// Cập nhật panel đối soát với giá trị ban đầu (tiền mặt = 0)
		updateDoiSoat(this.heThong_DoanhThuRong, 0);
	}

	/**
	 * Xử lý khi nhấn nút "Xác nhận Giao Ca"
	 */
	private void xuLyXacNhanGiaoCa() {
		// 1. Lấy tất cả dữ liệu
		double tienHeThong = this.heThong_DoanhThuRong;
		double tienThucTe = Double.parseDouble(lblTongTienMatThucTe.getText().replaceAll("[^\\d.-]", ""));
		double chenhLech = Double.parseDouble(lblChenhLech.getText().replaceAll("[^\\d.-]", ""));
		String ghiChu = txtGhiChu.getText();

		// 2. Kiểm tra ghi chú nếu có chênh lệch
		if (chenhLech != 0 && ghiChu.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Phát hiện chênh lệch. Vui lòng nhập lý do vào ô Ghi Chú!",
					"Yêu cầu Ghi Chú", JOptionPane.WARNING_MESSAGE);
			txtGhiChu.requestFocus(); // Focus vào ô ghi chú
			return;
		}

		// 3. Hiển thị hộp thoại xác nhận
		String message = String.format(
				"Bạn có chắc muốn xác nhận giao ca?\n\n" + "Doanh thu hệ thống: %s\n" + "Tiền mặt thực tế: %s\n"
						+ "Chênh lệch: %s\n\n" + "Ghi chú: %s",
				df.format(tienHeThong), df.format(tienThucTe), df.format(chenhLech),
				ghiChu.isEmpty() ? "(không có)" : ghiChu);

		int choice = JOptionPane.showConfirmDialog(this, message, "Xác nhận Giao Ca", JOptionPane.YES_NO_OPTION);

		if (choice == JOptionPane.YES_OPTION) {
			// == GỌI DAO ĐỂ LƯU BÁO CÁO GIAO CA ==
			// BaoCaoGiaoCa baoCao = new BaoCaoGiaoCa(...);
			// baoCao.setTienHeThong(tienHeThong);
			// baoCao.setTienThucTe(tienThucTe);
			// ...

			// GiaoCa_DAO giaoCaDAO = new GiaoCa_DAO();
			// boolean result = giaoCaDAO.luuBaoCao(baoCao);

			// if (result) {
			// JOptionPane.showMessageDialog(this, "Đã lưu báo cáo giao ca thành công!");
			// // TODO: Vô hiệu hóa panel, reset, hoặc đóng tab...
			// btnXacNhanGiaoCa.setEnabled(false);
			// } else {
			// JOptionPane.showMessageDialog(this, "Lỗi khi lưu báo cáo!", "Lỗi",
			// JOptionPane.ERROR_MESSAGE);
			// }

			JOptionPane.showMessageDialog(this, "Đã lưu báo cáo giao ca thành công! (Giả lập)");
			btnXacNhanGiaoCa.setEnabled(false); // Vô hiệu hóa nút sau khi lưu
			btnXacNhanGiaoCa.setText("Đã Xác Nhận");
		}
	}

	/**
	 * Hàm main để chạy thử và xem giao diện
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Test Panel Báo Cáo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// Đây là Panel chính của bạn (giống trong Figma)
			JPanel mainAppPanel = new JPanel(new BorderLayout());

			// 1. Giả lập Menu bên trái
			JPanel pnlMenu = new JPanel();
			pnlMenu.setBackground(new Color(34, 49, 63)); // Màu xanh đậm
			pnlMenu.setPreferredSize(new Dimension(220, 0));
			mainAppPanel.add(pnlMenu, BorderLayout.WEST);

			// 2. Thêm Panel Báo Cáo (nội dung bên phải)
			PanelBaoCao pnlBaoCao = new PanelBaoCao();
			mainAppPanel.add(pnlBaoCao, BorderLayout.CENTER);

			frame.setContentPane(mainAppPanel);
			frame.setSize(1280, 720); // Kích thước cửa sổ
			frame.setLocationRelativeTo(null); // Giữa màn hình
			frame.setVisible(true);
		});
	}
}
