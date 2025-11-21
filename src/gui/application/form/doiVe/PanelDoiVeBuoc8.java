package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVeBuoc8.java  1.0  [11:19:13 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PanelDoiVeBuoc8 extends JPanel {
	private JRadioButton radTienMat, radChuyenKhoan;
	private JLabel lblTongTienVeCu;
	private JLabel lblTongTienVeMoi;
	private JLabel lblTongPhiDoiVe;
	private JLabel lblTongThanhToan;
	private JTextField txtTienKhachDua;
	private JLabel lblTienThoiLai;
	private JButton btnXacNhanVaInCash;
	private JButton btnXacNhanVaInQR;

	private JPanel pnlTienDua;
	private JPanel pnlQRCode;
	private JPanel pnlPaymentMethodContainer;
	private CardLayout paymentCardLayout;

	private JPanel pnlGoiY;
	private final List<JButton> suggestionButtons = new ArrayList<>();

	private DecimalFormat currencyFormat;
	private final DecimalFormat btnFormat;

	private int tongThanhToan = 0;
	private JPanel pnlChiTiet;

	private static final int[] MENHGIAVND = { 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000 };
	private static final String TIEN_MAT_CARD = "TienMat";
	private static final String QR_CODE_CARD = "QRCode";

	public PanelDoiVeBuoc8() {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createTitledBorder("Thanh Toán"));

		currencyFormat = new DecimalFormat("#,### VND");
		btnFormat = new DecimalFormat("#,###");

		// Panel Phương thức thanh toán
		JPanel pnlPhuongThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
		radTienMat = new JRadioButton("Tiền mặt", true);
		radChuyenKhoan = new JRadioButton("Chuyển khoản");
		ButtonGroup bgPayment = new ButtonGroup();
		bgPayment.add(radTienMat);
		bgPayment.add(radChuyenKhoan);
		pnlPhuongThuc.add(radTienMat);
		pnlPhuongThuc.add(radChuyenKhoan);
		add(pnlPhuongThuc, BorderLayout.NORTH);

		JPanel pnlMain = new JPanel(new GridLayout(1, 2, 10, 0));
		pnlChiTiet = createChiTietPanel();

		paymentCardLayout = new CardLayout();
		pnlPaymentMethodContainer = new JPanel(paymentCardLayout);

		pnlTienDua = createTienDuaPanel();
		pnlQRCode = createQRCodePanel();

		pnlPaymentMethodContainer.add(pnlTienDua, TIEN_MAT_CARD);
		pnlPaymentMethodContainer.add(pnlQRCode, QR_CODE_CARD);

		pnlMain.add(pnlChiTiet);
		pnlMain.add(pnlPaymentMethodContainer);

		add(pnlMain, BorderLayout.CENTER);

		// Logic nội bộ
		addInternalLogic();

		// Show cash panel initially
		paymentCardLayout.show(pnlPaymentMethodContainer, TIEN_MAT_CARD);
	}

	private JPanel createChiTietPanel() {
		JPanel pnl = new JPanel(new GridBagLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("Chi tiết thanh toán"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnl.add(new JLabel("Tổng tiền vé cũ:"), gbc);
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongTienVeCu = new JLabel("0 VND");
		lblTongTienVeCu.setForeground(Color.GREEN);
		pnl.add(lblTongTienVeCu, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(new JLabel("Tổng tiền vé mới:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongTienVeMoi = new JLabel("0 VND", JLabel.RIGHT);
		lblTongTienVeMoi.setForeground(Color.RED);
		pnl.add(lblTongTienVeMoi, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.WEST;
		pnl.add(new JLabel("Tổng phí đổi vé:"), gbc);
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		lblTongPhiDoiVe = new JLabel("0 VND");
		lblTongPhiDoiVe.setForeground(Color.RED);
		pnl.add(lblTongPhiDoiVe, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weighty = 1.0;
		pnl.add(Box.createVerticalGlue(), gbc);

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

		gbc.gridy = 5;
		gbc.weighty = 1.0;
		pnl.add(new JLabel(), gbc);

		return pnl;
	}

	private JPanel createTienDuaPanel() {
		pnlTienDua = new JPanel(new GridBagLayout());
		pnlTienDua.setBorder(BorderFactory.createTitledBorder("Tiền mặt"));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		pnlTienDua.add(new JLabel("Tiền khách đưa:"), gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		txtTienKhachDua = new JTextField(15);
		pnlTienDua.add(txtTienKhachDua, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		pnlTienDua.add(new JLabel("Gợi ý mệnh giá:"), gbc);

		gbc.gridy = 2;
		gbc.insets = new Insets(0, 5, 5, 5);
		pnlGoiY = new JPanel(new GridLayout(2, 4, 5, 5));
		pnlTienDua.add(pnlGoiY, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.weightx = 0;
		gbc.insets = new Insets(10, 5, 5, 5);
		JLabel thoiLaiLabel = new JLabel("Tiền thừa:");
		thoiLaiLabel.setFont(thoiLaiLabel.getFont().deriveFont(Font.BOLD));
		pnlTienDua.add(thoiLaiLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		lblTienThoiLai = new JLabel("0 VND");
		lblTienThoiLai.setFont(lblTienThoiLai.getFont().deriveFont(Font.BOLD, 14f));
		lblTienThoiLai.setForeground(Color.BLUE);
		pnlTienDua.add(lblTienThoiLai, gbc);
		// --- Hàng 4: Nút Xác nhận (Cash version) ---
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(15, 5, 5, 5);
		btnXacNhanVaInCash = new JButton("Xác nhận và in vé");
		btnXacNhanVaInCash.setFont(btnXacNhanVaInCash.getFont().deriveFont(Font.BOLD, 14f));
		btnXacNhanVaInCash.setBackground(new Color(0, 153, 51));
		btnXacNhanVaInCash.setForeground(Color.WHITE);
		pnlTienDua.add(btnXacNhanVaInCash, gbc);

		gbc.gridy = 5;
		gbc.weighty = 1.0;
		pnlTienDua.add(new JLabel(), gbc);

		return pnlTienDua;
	}

	private JPanel createQRCodePanel() {
		pnlQRCode = new JPanel();
		pnlQRCode.setLayout(new BoxLayout(pnlQRCode, BoxLayout.Y_AXIS)); // Vertical layout
		pnlQRCode.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Chuyển khoản"),
				BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
		));

		// --- QR Code Image (Placeholder) ---
		// TODO: Replace with actual QR code generation
		// For now, using a placeholder text or a sample image if you have one
		JLabel lblQRCodePlaceholder = new JLabel(
				"<html><center>[QR Code Image Placeholder]<br/>Quét mã để thanh toán</center></html>");
		lblQRCodePlaceholder.setHorizontalAlignment(SwingConstants.CENTER);
		lblQRCodePlaceholder.setFont(lblQRCodePlaceholder.getFont().deriveFont(Font.PLAIN, 16f));
		lblQRCodePlaceholder.setPreferredSize(new Dimension(200, 200)); // Adjust size as needed
		lblQRCodePlaceholder.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		lblQRCodePlaceholder.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlQRCode.add(lblQRCodePlaceholder);

		pnlQRCode.add(Box.createVerticalStrut(10)); // Spacer

		JLabel lblQRInfo1 = new JLabel("Thanh toán đổi vé");
		lblQRInfo1.setFont(lblQRInfo1.getFont().deriveFont(Font.BOLD, 14f));
		lblQRInfo1.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlQRCode.add(lblQRInfo1);

		JLabel lblQRAmount = new JLabel(currencyFormat.format(tongThanhToan)); // Display amount
		lblQRAmount.setFont(lblQRAmount.getFont().deriveFont(Font.BOLD, 18f));
		lblQRAmount.setForeground(Color.RED);
		lblQRAmount.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlQRCode.add(lblQRAmount);

		JLabel lblQRInfo2 = new JLabel("Nhà ga Sài Gòn");
		lblQRInfo2.setFont(lblQRInfo2.getFont().deriveFont(Font.PLAIN, 12f));
		lblQRInfo2.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlQRCode.add(lblQRInfo2);

		pnlQRCode.add(Box.createVerticalStrut(15));

		btnXacNhanVaInQR = new JButton("Xác nhận và in vé");
		btnXacNhanVaInQR.setFont(btnXacNhanVaInQR.getFont().deriveFont(Font.BOLD, 14f));
		btnXacNhanVaInQR.setBackground(new Color(0, 153, 51));
		btnXacNhanVaInQR.setForeground(Color.WHITE);
		btnXacNhanVaInQR.setAlignmentX(Component.CENTER_ALIGNMENT);
		pnlQRCode.add(btnXacNhanVaInQR);

		pnlQRCode.add(Box.createVerticalGlue());

		return pnlQRCode;
	}

	private void addInternalLogic() {
		ActionListener paymentMethodListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (radTienMat.isSelected()) {
					paymentCardLayout.show(pnlPaymentMethodContainer, TIEN_MAT_CARD);
				} else if (radChuyenKhoan.isSelected()) {
					// Update amount on QR panel before showing
					updateQRCodePanelAmount();
					paymentCardLayout.show(pnlPaymentMethodContainer, QR_CODE_CARD);
				}
			}
		};
		radTienMat.addActionListener(paymentMethodListener);
		radChuyenKhoan.addActionListener(paymentMethodListener);

		txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				updateChange();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				updateChange();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				updateChange();
			}

			public void updateChange() {
				try {
					String text = txtTienKhachDua.getText().replace(".", "").replace(",", "");
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
					lblTienThoiLai.setText("Số không hợp lệ");
					lblTienThoiLai.setForeground(Color.RED);
				}
			}
		});
	}

	public int getTongThanhToan() {
		return tongThanhToan;
	}

	public int getTienKhachDua() {
		String text = txtTienKhachDua.getText().replace(".", "").replace(",", "");
		return Integer.parseInt(text);
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

		// Nếu tắt (chuyển khoản), xóa text và reset tiền thừa
		if (!enabled) {
			txtTienKhachDua.setText("");
			lblTienThoiLai.setText("0 VND");
			lblTienThoiLai.setForeground(Color.BLUE);
		}
	}

	private List<Integer> generateSuggestions(int total) {
		Set<Integer> suggestions = new LinkedHashSet<>();

		// 0. Xử lý trường hợp total = 0 hoặc âm
		if (total <= 0) {
			suggestions.add(0); // Chỉ gợi ý 0
			// Thêm một vài mệnh giá nhỏ nếu muốn
			suggestions.add(10000);
			suggestions.add(50000);
			return suggestions.stream().limit(6).collect(Collectors.toList());
		}

		// 1. Luôn thêm số tiền chính xác
		suggestions.add(total);

		// 2. Tìm các số làm tròn gần nhất lớn hơn total
		// Làm tròn lên 1.000 gần nhất
		int roundUp1000 = (int) (Math.ceil(total / 1000.0) * 1000);
		if (roundUp1000 > total) {
			suggestions.add(roundUp1000);
		}

		// Làm tròn lên 2.000 gần nhất
		int roundUp2000 = (int) (Math.ceil(total / 2000.0) * 2000);
		if (roundUp2000 > total) {
			suggestions.add(roundUp2000);
		}

		// Làm tròn lên 5.000 gần nhất
		int roundUp5000 = (int) (Math.ceil(total / 5000.0) * 5000);
		if (roundUp5000 > total) {
			suggestions.add(roundUp5000);
		}

		// Làm tròn lên 10.000 gần nhất
		int roundUp10000 = (int) (Math.ceil(total / 10000.0) * 10000);
		if (roundUp10000 > total) {
			suggestions.add(roundUp10000);
		}

		// Làm tròn lên 20.000 gần nhất
		int roundUp20000 = (int) (Math.ceil(total / 20000.0) * 20000);
		if (roundUp20000 > total) {
			suggestions.add(roundUp20000);
		}

		// Làm tròn lên 50.000 gần nhất
		int roundUp50000 = (int) (Math.ceil(total / 50000.0) * 50000);
		if (roundUp50000 > total) {
			suggestions.add(roundUp50000);
		}

		// Làm tròn lên 100.000 gần nhất
		int roundUp100000 = (int) (Math.ceil(total / 100000.0) * 100000);
		if (roundUp100000 > total) {
			suggestions.add(roundUp100000);
		}

		// Làm tròn lên 200.000 gần nhất
		int roundUp200000 = (int) (Math.ceil(total / 200000.0) * 200000);
		if (roundUp200000 > total) {
			suggestions.add(roundUp200000);
		}

		// Làm tròn lên 500.000 gần nhất
		int roundUp500000 = (int) (Math.ceil(total / 500000.0) * 500000);
		if (roundUp500000 > total) {
			suggestions.add(roundUp500000);
		}

		// 3. Thêm các mệnh giá chuẩn lớn hơn total gần nhất
		for (int denom : MENHGIAVND) {
			if (denom > total && suggestions.size() < 6) { // Chỉ thêm nếu lớn hơn và chưa đủ 6 gợi ý
				suggestions.add(denom);
			}
		}

		// 4. Nếu vẫn chưa đủ 6, thêm các mệnh giá lớn hơn tiếp theo
		// (Lấy từ cuối mảng mệnh giá)
		for (int i = MENHGIAVND.length - 1; i >= 0 && suggestions.size() < 6; i--) {
			if (MENHGIAVND[i] > total) {
				suggestions.add(MENHGIAVND[i]); // add sẽ tự bỏ qua nếu đã tồn tại
			}
		}

		// 5. Chuyển thành List, sắp xếp và lấy tối đa 6
		return suggestions.stream().sorted().limit(6).collect(Collectors.toList());
	}

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

	private void updateQRCodePanelAmount() {
		// Find the JLabel responsible for displaying the amount within pnlQRCode
		// This relies on the structure created in createQRCodePanel()
		for (Component comp : pnlQRCode.getComponents()) {
			// A bit fragile, better to store a direct reference if possible
			if (comp instanceof JLabel && comp.getForeground() == Color.RED) {
				((JLabel) comp).setText(currencyFormat.format(tongThanhToan));
				break; // Found it
			}
		}
	}

	public void setChiTietThanhToan(int tongTienVeCu, int tongTienVeMoi, int tongPhiDoiVe) {
		this.tongThanhToan = tongTienVeMoi + tongPhiDoiVe - tongTienVeCu;

		if (this.tongThanhToan < 0) {
			this.tongThanhToan = 0;
		}

		lblTongTienVeCu.setText(currencyFormat.format(tongTienVeCu));
		lblTongTienVeMoi.setText(currencyFormat.format(tongTienVeMoi));
		lblTongPhiDoiVe.setText(currencyFormat.format(tongPhiDoiVe));
		lblTongThanhToan.setText(currencyFormat.format(this.tongThanhToan));

		updateSuggestionButtons(generateSuggestions(this.tongThanhToan));

		// Cập nhật lại tiền thừa (giữ nguyên tiền khách nhập)
		txtTienKhachDua.setText(txtTienKhachDua.getText());
	}

	public void setComponentsEnabled(boolean enabled) {
		super.setEnabled(enabled);
		radTienMat.setEnabled(enabled);
		radChuyenKhoan.setEnabled(enabled);

		// Disable detail panel components
		for (Component c : pnlChiTiet.getComponents()) {
			c.setEnabled(enabled);
		}

		// Disable components within the currently visible payment card
		if (radTienMat.isSelected()) {
			setTienMatPanelEnabled(enabled);
		} else {
			setQRCodePanelEnabled(enabled);
		}

		// Disable confirm buttons specifically if panel is disabled
		btnXacNhanVaInCash.setEnabled(enabled && radTienMat.isSelected());
		btnXacNhanVaInQR.setEnabled(enabled && radChuyenKhoan.isSelected());
	}

	private void setTienMatPanelEnabled(boolean enabled) {
		for (Component c : pnlTienDua.getComponents()) {
			if (!(c instanceof JLabel)) { // Keep labels visible
				c.setEnabled(enabled);
			}
		}
		for (JButton btn : suggestionButtons) {
			btn.setEnabled(enabled);
		}
		txtTienKhachDua.setEnabled(enabled);
		btnXacNhanVaInCash.setEnabled(enabled);
		// Don't disable lblTienThoiLai visually
		// lblTienThoiLai.setEnabled(enabled);
	}

	/** Helper to enable/disable QR panel components */
	private void setQRCodePanelEnabled(boolean enabled) {
		for (Component c : pnlQRCode.getComponents()) {
			if (!(c instanceof JLabel || c instanceof Box)) { // Keep labels and spacers visible
				c.setEnabled(enabled);
			}
		}
		btnXacNhanVaInQR.setEnabled(enabled);
	}

	public JButton getBtnXacNhanVaInCash() {
		return btnXacNhanVaInCash;
	}

	public JButton getBtnXacNhanVaInQR() {
		return btnXacNhanVaInQR;
	}

	public boolean isThanhToanTienMat() {
		return radTienMat.isSelected();
	}
}