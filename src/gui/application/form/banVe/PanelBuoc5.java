package gui.application.form.banVe;
/*
 * @(#) PaymentPanel.java  1.0  [10:41:50 AM] Sep 28, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 28, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PanelBuoc5 extends JPanel {
	private JRadioButton radTienMat, radChuyenKhoan;
	private JLabel lblTongTienVe, lblGiamGiaDT, lblKhuyenMai, lblDichVu, lblTongThanhToan;
	private JTextField txtTienKhachDua;
	private JLabel lblTienThoiLai;
	private JButton btnXacNhanVaIn;

	private JPanel pnlTienDua;
	private JPanel pnlGoiY;
	private final List<JButton> suggestionButtons = new ArrayList<>();

	private final DecimalFormat currencyFormat;
	private final DecimalFormat btnFormat; // Format cho nút (không có "VND")

	private int tongThanhToan = 0;
	private JPanel pnlChiTiet;

	// Mệnh giá tiền VND
	private static final int[] MENHGIAVND = { 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000 };

	public PanelBuoc5() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Thanh Toán"));

		currencyFormat = new DecimalFormat("#,###");
		btnFormat = new DecimalFormat("#,###.##");

		// 1. Panel Phương thức thanh toán (TOP)
		JPanel pnlPhuongThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		radTienMat = new JRadioButton("Tiền mặt", true);
		radChuyenKhoan = new JRadioButton("Chuyển khoản");
		ButtonGroup bgPayment = new ButtonGroup();
		bgPayment.add(radTienMat);
		bgPayment.add(radChuyenKhoan);
		pnlPhuongThuc.add(radTienMat);
		pnlPhuongThuc.add(radChuyenKhoan);
		add(pnlPhuongThuc, BorderLayout.NORTH);

		// 2. Panel Thanh toán chính (CENTER)
		JPanel pnlMain = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 hàng, 2 cột

		pnlChiTiet = createChiTietPanel();
		pnlTienDua = createTienDuaPanel();

		pnlMain.add(pnlChiTiet);
		pnlMain.add(pnlTienDua);
		// --- KẾT THÚC SỬA LỖI ---

		add(pnlMain, BorderLayout.CENTER);

		// 3. Logic nội bộ
		addInternalLogic();
	}

	private JPanel createChiTietPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("Chi tiết thanh toán"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		// --- Hàng 1: Tổng tiền vé ---
		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel("Tổng tiền vé:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongTienVe = new JLabel("0 VND");
		pnl.add(lblTongTienVe, gbc);

		// --- Hàng 2: Giảm giá đối tượng ---
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(new JLabel("Giảm giá theo đối tượng:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblGiamGiaDT = new JLabel("0 VND", JLabel.RIGHT);
		lblGiamGiaDT.setForeground(Color.RED); // Màu đỏ như prototype
		pnl.add(lblGiamGiaDT, gbc);

		// --- Hàng 3: Khuyến mãi ---
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(new JLabel("Khuyến mãi:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblKhuyenMai = new JLabel("0 VND");
		lblKhuyenMai.setForeground(Color.RED);
		pnl.add(lblKhuyenMai, gbc);

		// --- Hàng 4: Dịch vụ đi kèm ---
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(new JLabel("Dịch vụ đi kèm:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblDichVu = new JLabel("0 VND");
		pnl.add(lblDichVu, gbc);

		// --- Hàng 5: Tổng thanh toán (TOTAL) ---
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.WEST;
		JLabel totalLabel = new JLabel("Tổng thanh toán:");
		totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
		pnl.add(totalLabel, gbc);

		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongThanhToan = new JLabel("0 VND");
		lblTongThanhToan.setFont(lblTongThanhToan.getFont().deriveFont(Font.BOLD, 14f));
		lblTongThanhToan.setForeground(Color.RED);
		pnl.add(lblTongThanhToan, gbc);

		// Spacer
		gbc.gridy = 5;
		gbc.weighty = 1.0;
		pnl.add(new JLabel(), gbc);

		return pnl;
	}

	/**
	 * SỬA 3: Hàm này giờ lưu các tham chiếu component
	 */
	private JPanel createTienDuaPanel() {
		pnlTienDua = new JPanel(new GridBagLayout()); // Lưu tham chiếu
		pnlTienDua.setBorder(BorderFactory.createTitledBorder("Tiền mặt"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// --- Hàng 0: Tiền khách đưa ---
		gbc.gridx = 0;
		gbc.gridy = 0;
		pnlTienDua.add(new JLabel("Tiền khách đưa:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		txtTienKhachDua = new JTextField(15);
		pnlTienDua.add(txtTienKhachDua, gbc);

		// --- Hàng 1: Gợi ý mệnh giá ---
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		pnlTienDua.add(new JLabel("Gợi ý mệnh giá:"), gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(0, 5, 5, 5);
		pnlGoiY = new JPanel(new GridLayout(2, 4, 5, 5)); // Lưu tham chiếu
		pnlTienDua.add(pnlGoiY, gbc);

		// (Các nút sẽ được thêm động)

		// --- Hàng 3: Tiền thối lại ---
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.insets = new Insets(10, 5, 5, 5);
		JLabel thoiLaiLabel = new JLabel("Tiền thối lại:");
		thoiLaiLabel.setFont(thoiLaiLabel.getFont().deriveFont(Font.BOLD));
		pnlTienDua.add(thoiLaiLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		lblTienThoiLai = new JLabel("0 VND");
		lblTienThoiLai.setFont(lblTienThoiLai.getFont().deriveFont(Font.BOLD, 14f));
		lblTienThoiLai.setForeground(Color.BLUE);
		pnlTienDua.add(lblTienThoiLai, gbc);

		// --- Hàng 4: Nút Xác nhận ---
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(15, 5, 5, 5);
		btnXacNhanVaIn = new JButton("Xác nhận và in vé");
		btnXacNhanVaIn.setFont(btnXacNhanVaIn.getFont().deriveFont(Font.BOLD, 14f));
		btnXacNhanVaIn.setBackground(new Color(0, 153, 51));
		btnXacNhanVaIn.setForeground(Color.WHITE);
		pnlTienDua.add(btnXacNhanVaIn, gbc);

		// Spacer
		gbc.gridy = 5;
		gbc.weighty = 1.0;
		pnlTienDua.add(new JLabel(), gbc);

		return pnlTienDua;
	}

	private void addInternalLogic() {
		radTienMat.addActionListener(e -> setTienMatEnabled(true));
		radChuyenKhoan.addActionListener(e -> setTienMatEnabled(false));

		txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				update();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				update();
			}

			public void update() {
				try {
					String text = txtTienKhachDua.getText().replace(".", "");
					if (text.isEmpty()) {
						lblTienThoiLai.setText("0 VND");
						return;
					}
					int tienDua = Integer.parseInt(text);
					int tienThoi = tienDua - tongThanhToan;

					if (tienThoi < 0) {
						lblTienThoiLai.setText("Không đủ");
						lblTienThoiLai.setForeground(Color.RED);
					} else {
						lblTienThoiLai.setText(currencyFormat.format(tienThoi));
						lblTienThoiLai.setForeground(Color.BLUE);
					}
				} catch (NumberFormatException ex) {
					lblTienThoiLai.setText("Không hợp lệ");
					lblTienThoiLai.setForeground(Color.RED);
				}
			}
		});
	}

	private void setTienMatEnabled(boolean enabled) {

		for (Component c : pnlTienDua.getComponents()) {
			if (c instanceof JTextField || c instanceof JPanel || c instanceof JButton) {
				c.setEnabled(enabled);
			}
		}
		for (JButton btn : suggestionButtons) {
			btn.setEnabled(enabled);
		}

		// Nếu tắt (chuyển khoản), xóa text và reset tiền thối
		if (!enabled) {
			txtTienKhachDua.setText("");
			lblTienThoiLai.setText("0 VND");
			lblTienThoiLai.setForeground(Color.BLUE);
		}
	}

	private List<Integer> generateSuggestions(int total) {
		Set<Integer> suggestions = new LinkedHashSet<>(); // Dùng Set để tránh trùng
		suggestions.add(total); // Thêm số tiền chính xác

		for (int denom : MENHGIAVND) {
			if (total < denom) {
				suggestions.add(denom);
			} else {

				int suggestion = (int) Math.ceil(total / denom) * denom;
				suggestions.add(suggestion);
			}
		}
		return suggestions.stream().limit(6).collect(Collectors.toList());
	}

	/**
	 * SỬA 2: Hàm cập nhật UI cho các nút gợi ý
	 */
	private void updateSuggestionButtons(List<Integer> suggestions) {
		pnlGoiY.removeAll();
		suggestionButtons.clear();

		for (Integer val : suggestions) {
			JButton btn = new JButton(btnFormat.format(val));
			btn.addActionListener(e -> txtTienKhachDua.setText(String.valueOf(val)));
			suggestionButtons.add(btn);
			pnlGoiY.add(btn);
		}

		// Nếu ít hơn 8 nút (layout 2x4), thêm các panel trống
		int placeholders = Math.max(0, 8 - suggestions.size());
		for (int i = 0; i < placeholders; i++) {
			pnlGoiY.add(new JPanel());
		}

		pnlGoiY.revalidate();
		pnlGoiY.repaint();
	}

	public void setChiTietThanhToan(int tongVe, int giamDT, int khuyenMai, int dichVu) {
		this.tongThanhToan = tongVe - giamDT - khuyenMai + dichVu;

		if (this.tongThanhToan < 0) {
			this.tongThanhToan = 0;
		}

		lblTongTienVe.setText(currencyFormat.format(tongVe) + " VND");
		lblGiamGiaDT.setText(currencyFormat.format(giamDT) + " VND");
		lblKhuyenMai.setText(currencyFormat.format(khuyenMai) + " VND");
		lblDichVu.setText(currencyFormat.format(dichVu) + " VND");
		lblTongThanhToan.setText(currencyFormat.format(this.tongThanhToan) + " VND");

		updateSuggestionButtons(generateSuggestions(this.tongThanhToan));

		// Cập nhật lại tiền thối (giữ nguyên tiền khách nhập)
		txtTienKhachDua.setText(txtTienKhachDua.getText());
	}

	public JButton getBtnThanhToan() {
		return btnXacNhanVaIn;
	}

	public void setComponentsEnabled(boolean enabled) {
		super.setEnabled(enabled);
		radTienMat.setEnabled(enabled);
		radChuyenKhoan.setEnabled(enabled);

		// Vô hiệu hóa panel ĐÃ LƯU (pnlChiTiet)
		for (Component c : pnlChiTiet.getComponents()) {
			c.setEnabled(enabled);
		}

		setTienMatEnabled(enabled);
		btnXacNhanVaIn.setEnabled(enabled);
	}
}