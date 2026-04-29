package gui.application.form.doiVe;
/*
 * @(#) PanelDoanTauDoiVe.java  1.0  [3:27:38 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import entity.Toa;

public class PanelDoanTauDoiVe extends JPanel {
	private JPanel flow;
	private DoiVeBuoc5Controller controller;
	private JButton selectedButton = null;
	// ảnh nguồn (gốc, trong suốt)
	private BufferedImage baseToaImage;
	// cache icon theo (color + w + h) để không phải tạo lại mỗi lần
	private final Map<String, ImageIcon> iconCache = new HashMap<>();

	private final Color colorDefault = new Color(50, 150, 210); // màu khi chưa chọn
	private final Color colorSelected = new Color(22, 171, 56); // màu khi chọn
	private final Color colorHover = colorSelected.brighter();
	private BufferedImage baseDauTauImage;

	public PanelDoanTauDoiVe() {
		setBorder(new TitledBorder("Sơ đồ đoàn tàu"));
		setLayout(new BorderLayout());
		flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
		JScrollPane scr = new JScrollPane(flow, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scr.setBorder(BorderFactory.createEmptyBorder());
		// Tăng tốc độ cuộn (tùy chọn)
		scr.getVerticalScrollBar().setUnitIncrement(16);
		scr.getHorizontalScrollBar().setUnitIncrement(16);

		add(scr, BorderLayout.CENTER);
		setPreferredSize(new Dimension(10, 80));

		// load ảnh gốc một lần
		try {
			baseToaImage = ImageIO.read(getClass().getResourceAsStream("/icon/png/toa-tau.png"));
			baseDauTauImage = ImageIO.read(getClass().getResourceAsStream("/icon/png/dau-tau-trai.png"));
		} catch (IOException | NullPointerException ex) {
			ex.printStackTrace();
			baseToaImage = null;
			baseDauTauImage = null;
		}
	}

	public void setController(DoiVeBuoc5Controller controller) {
		this.controller = controller;
	}

	public void showToaList(List<Toa> list, Consumer<Toa> onSelect) {
		flow.removeAll();
		selectedButton = null;

		if (list == null || list.isEmpty()) {
			flow.add(new JLabel("Không có toa"));
		} else {
			flow.add(drawDauTau());
			for (Toa t : list) {
				JButton btnToa = drawToa(t);
				btnToa.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				btnToa.addActionListener(e -> {
					onSelect.accept(t);
					highlightButton(btnToa);
				});
				btnToa.addMouseListener(new java.awt.event.MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent evt) {
						// Chỉ đổi icon HVER nếu nút này KHÔNG đang được CHỌN
						if (btnToa != selectedButton) {
							ImageIcon iconHov = (ImageIcon) btnToa.getClientProperty("iconHover");
							if (iconHov != null) {
								btnToa.setIcon(iconHov);
							}
						}
					}

					@Override
					public void mouseExited(MouseEvent evt) {
						// Chỉ đổi về icon MẶC ĐỊNH nếu nút này KHÔNG đang được CHỌN
						if (btnToa != selectedButton) {
							ImageIcon iconDef = (ImageIcon) btnToa.getClientProperty("iconDefault");
							if (iconDef != null) {
								btnToa.setIcon(iconDef);
							}
						}
					}
				});
				flow.add(btnToa);
			}
		}

		flow.revalidate();
		flow.repaint();
	}

	private JLabel drawDauTau() {
		if (baseDauTauImage != null) {
			try {
				int fixedCarWidth = 42;
				int baseCarW = baseToaImage.getWidth(null);
				int baseCarH = baseToaImage.getHeight(null);
				double carAspectRatio = (baseCarW > 0) ? (double) baseCarH / (double) baseCarW : 1.0;
				int standardHeight = Math.max(1, (int) (fixedCarWidth * carAspectRatio));

				// Tính chiều rộng cho đầu tàu (dựa trên chiều cao chuẩn)
				int baseDauTauW = baseDauTauImage.getWidth(null);
				int baseDauTauH = baseDauTauImage.getHeight(null);
				double dauTauAspectRatio = (baseDauTauH > 0) ? (double) baseDauTauW / (double) baseDauTauH : 1.0;
				int newDauTauWidth = Math.max(1, (int) (standardHeight * dauTauAspectRatio));

				// Scale ảnh đầu tàu (dùng hàm scaleToSize "vừa khít" của bạn)
				BufferedImage scaledDauTauImg = scaleToSize(baseDauTauImage, newDauTauWidth, standardHeight);
				ImageIcon dauTauIcon = new ImageIcon(scaledDauTauImg);

				// Tạo JLabel và thêm vào panel
				JLabel lblDauTau = new JLabel(dauTauIcon);

				lblDauTau.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 2));
				return lblDauTau;
			} catch (Exception ex) {
				System.out.println("PanelDoanTau: showDauTau() - Loi xu ly anh dau tau");
				ex.printStackTrace();
			}
		}
		return null;
	}

	private JButton drawToa(Toa t) {
		JButton btnToa = new JButton(String.valueOf(t.getSoToa()));
		btnToa.setFont(new Font("Roboto", Font.PLAIN, 10));
		btnToa.putClientProperty("toaID", t.getToaID());
		btnToa.setToolTipText(String.format("<html>Toa %d - %s</html>", t.getSoToa(), t.getHangToa().getDescription()));

		if (baseToaImage != null) {
			int fixedIconWidth = 42;

			// Lấy kích thước ảnh gốc
			int baseW = baseToaImage.getWidth(null);
			int baseH = baseToaImage.getHeight(null);

			// Tính chiều cao mới (iconH) dựa trên tỷ lệ
			double aspectRatio = (double) baseH / (double) baseW;
			int newIconHeight = Math.max(1, (int) (fixedIconWidth * aspectRatio));

			// Sử dụng kích thước "vừa khít" mới
			int iconW = fixedIconWidth;
			int iconH = newIconHeight;

			ImageIcon iconDefault = getTintedIcon(baseToaImage, colorDefault, iconW, iconH);
			ImageIcon iconSelected = getTintedIcon(baseToaImage, colorSelected, iconW, iconH);
			ImageIcon iconHover = getTintedIcon(baseToaImage, colorHover, iconW, iconH);

			btnToa.setIcon(iconDefault);
			btnToa.setMargin(new Insets(0, 0, 0, 0));
			btnToa.setHorizontalAlignment(SwingConstants.CENTER);
			btnToa.setVerticalAlignment(SwingConstants.TOP);
			btnToa.setHorizontalTextPosition(SwingConstants.CENTER);
			btnToa.setVerticalTextPosition(SwingConstants.BOTTOM);

			btnToa.setIconTextGap(1);

			btnToa.setBorderPainted(false);
			btnToa.setContentAreaFilled(false);
			btnToa.setFocusPainted(false);
			btnToa.setOpaque(false);

			btnToa.setRolloverEnabled(false);

			btnToa.putClientProperty("iconDefault", iconDefault);
			btnToa.putClientProperty("iconSelected", iconSelected);
			btnToa.putClientProperty("iconHover", iconHover);
		} else {
			btnToa.setOpaque(true);
			btnToa.setBorderPainted(true);
			btnToa.setBackground(colorDefault);
		}

		return btnToa;
	}

	private void highlightButton(JButton selected) {
		Component[] comps = flow.getComponents();

		for (Component c : comps) {
			if (c instanceof JButton && c != selected) {
				JButton b = (JButton) c;
				ImageIcon iconDefault = (ImageIcon) b.getClientProperty("iconDefault");

				if (baseToaImage != null && iconDefault != null) {
					b.setIcon(iconDefault);
				} else {
					b.setBackground(colorDefault);
				}
			}
		}

		selectedButton = selected;
		if (selectedButton != null) {
			ImageIcon iconSel = (ImageIcon) selectedButton.getClientProperty("iconSelected");

			if (baseToaImage != null && iconSel != null) {
				selectedButton.setIcon(iconSel);
			} else {
				selectedButton.setBackground(colorSelected);
			}
		}
	}

	// controller có thể gọi để chọn tự động
	public void selectToaById(String toaID) {
		if (toaID == null) {
			return;
		}
		for (Component c : flow.getComponents()) {
			if (c instanceof JButton) {
				Object id = ((JButton) c).getClientProperty("toaID");
				if (toaID.equals(id)) {
					highlightButton((JButton) c);
					break;
				}
			}
		}
	}

	// --- getTintedIcon (không đổi logic nhiều, chỉ đảm bảo width/height >= 1) ---
	private ImageIcon getTintedIcon(BufferedImage base, Color tint, int width, int height) {
		// ràng buộc tối thiểu để tránh error
		int w = Math.max(1, width);
		int h = Math.max(1, height);

		String key = tint.getRGB() + "x" + w + "x" + h;
		if (iconCache.containsKey(key)) {
			return iconCache.get(key);
		}

		BufferedImage scaled = scaleToSize(base, w, h);
		BufferedImage tinted = tintImageWithMask(scaled, tint);

		ImageIcon icon = new ImageIcon(tinted);
		iconCache.put(key, icon);
		return icon;
	}

	// --- Hàm scaleToSize được sửa để an toàn và giữ tỉ lệ, căn giữa --- //
	private static BufferedImage scaleToSize(BufferedImage src, int targetW, int targetH) {
		int srcW = src.getWidth();
		int srcH = src.getHeight();

		// nếu cả hai target đều <= 0, trả về ảnh gốc
		if (targetW <= 0 && targetH <= 0) {
			return src;
		}

		// nếu chỉ có 1 chiều được chỉ định, tính chiều còn lại theo tỉ lệ
		if (targetW <= 0) {
			targetW = Math.max(1, (int) Math.round((double) targetH * srcW / srcH));
		}
		if (targetH <= 0) {
			targetH = Math.max(1, (int) Math.round((double) targetW * srcH / srcW));
		}

		// tính scale giữ tỉ lệ để ảnh không bị méo
		double scale = Math.min((double) targetW / srcW, (double) targetH / srcH);
		int newW = Math.max(1, (int) Math.round(srcW * scale));
		int newH = Math.max(1, (int) Math.round(srcH * scale));

		// tạo ảnh result có kích thước đúng target và vẽ ảnh scaled ở giữa (transparent
		// padding)
		BufferedImage result = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = result.createGraphics();
		// làm mịn
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, newW, newH);
		g2.setComposite(AlphaComposite.SrcOver);
		Image scaled = src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		g2.drawImage(scaled, 0, 0, null);

		g2.dispose();
		return result;
	}

	// Tô màu: vẽ một rectangle fill = tint, rồi dùng alpha mask của ảnh gốc
	private static BufferedImage tintImageWithMask(BufferedImage src, Color tint) {
		int w = src.getWidth();
		int h = src.getHeight();
		BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = out.createGraphics();

		// 1) vẽ màu đầy
		g.setComposite(AlphaComposite.Src);
		g.setColor(tint);
		g.fillRect(0, 0, w, h);

		// 2) giữ alpha của src (lấy src alpha làm mask)
		g.setComposite(AlphaComposite.DstIn);
		g.drawImage(src, 0, 0, null);

		g.dispose();
		return out;
	}
}