package gui.application.form.banVe;

/*
 * @(#) PanelBuoc2DoanTau.java  1.0  [12:51:09 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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

public class PanelDoanTau extends JPanel {
	private JPanel flow;
	private PanelBuoc2Controller controller;
	private JButton selectedButton = null;

	// ảnh nguồn (gốc, trong suốt)
	private BufferedImage baseToaImage;

	// cache icon theo (color + w + h) để không phải tạo lại mỗi lần
	private final Map<String, ImageIcon> iconCache = new HashMap<>();

	// màu mặc định / selected (bạn có thể thay)
	private final Color colorDefault = new Color(220, 220, 220); // màu khi chưa chọn
	private final Color colorSelected = new Color(40, 167, 69); // màu khi chọn
	private final Color colorHover = colorSelected.brighter();

	public PanelDoanTau() {
		setBorder(new TitledBorder("Sơ đồ đoàn tàu"));
		setLayout(new BorderLayout());
		flow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
		JScrollPane scr = new JScrollPane(flow, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scr.setBorder(BorderFactory.createEmptyBorder());
		add(scr, BorderLayout.CENTER);
		setPreferredSize(new Dimension(10, 20));

		// load ảnh gốc một lần
		try {
			baseToaImage = ImageIO.read(getClass().getResourceAsStream("/gui/icon/png/toa-tau.png"));
			// nếu ảnh lớn, bạn có thể scale xuống kích thước nút
		} catch (IOException | NullPointerException ex) {
			ex.printStackTrace();
			baseToaImage = null;
		}
	}

	public void setController(PanelBuoc2Controller controller) {
		this.controller = controller;
	}

	// --- showToaList: dùng preferredSize thay vì getWidth/getHeight trực tiếp ---
	public void showToaList(List<Toa> list, Consumer<Toa> onSelect) {
		flow.removeAll();
		selectedButton = null;

		if (list == null || list.isEmpty()) {
			flow.add(new JLabel("Không có toa"));
		} else {
			for (Toa t : list) {
				JButton btn = new JButton(String.valueOf(t.getSoToa()));
				Dimension pref = new Dimension(60, 40); // kích thước nút / icon mong muốn
				btn.setPreferredSize(pref);
				btn.putClientProperty("toaID", t.getToaID());

				if (baseToaImage != null) {
					// dùng preferred size (không dùng getWidth/getHeight gây 0 nếu chưa hiển thị)
					int iconW = pref.width;
					int iconH = pref.height;

					ImageIcon iconDefault = getTintedIcon(baseToaImage, colorDefault, iconW, iconH);
					ImageIcon iconSelected = getTintedIcon(baseToaImage, colorSelected, iconW, iconH);
					ImageIcon iconHover = getTintedIcon(baseToaImage, colorHover, iconW, iconH);

					btn.setIcon(iconDefault);
					btn.setHorizontalTextPosition(SwingConstants.CENTER);
					btn.setVerticalTextPosition(SwingConstants.CENTER);
					btn.setBorderPainted(false);
					btn.setContentAreaFilled(false);
					btn.setFocusPainted(false);
					btn.setOpaque(false);

					btn.setRolloverEnabled(true);
					btn.setRolloverIcon(iconHover);
					btn.setPressedIcon(iconSelected);

					btn.putClientProperty("iconDefault", iconDefault);
					btn.putClientProperty("iconSelected", iconSelected);

					// Nếu bạn thật sự muốn icon theo kích thước thực tế sau layout,
					// uncomment block dưới để set lại icon sau khi component đã được hiển thị:
					/*
					 * SwingUtilities.invokeLater(() -> { int realW = btn.getWidth() > 0 ?
					 * btn.getWidth() : iconW; int realH = btn.getHeight() > 0 ? btn.getHeight() :
					 * iconH; ImageIcon id = getTintedIcon(baseToaImage, colorDefault, realW,
					 * realH); ImageIcon is = getTintedIcon(baseToaImage, colorSelected, realW,
					 * realH); btn.setIcon(id); btn.putClientProperty("iconDefault", id);
					 * btn.putClientProperty("iconSelected", is); });
					 */
				} else {
					btn.setOpaque(true);
					btn.setBorderPainted(true);
					btn.setBackground(colorDefault);
				}

				btn.addActionListener(e -> {
					onSelect.accept(t);
					highlightButton(btn);
				});

				flow.add(btn);
			}
		}

		flow.revalidate();
		flow.repaint();
	}

	private void highlightButton(JButton selected) {
		Component[] comps = flow.getComponents();
		for (Component c : comps) {
			if (c instanceof JButton) {
				JButton b = (JButton) c;
				if (baseToaImage != null) {
					ImageIcon iconDefault = (ImageIcon) b.getClientProperty("iconDefault");
					if (iconDefault != null)
						b.setIcon(iconDefault);
				} else {
					b.setBackground(colorDefault);
				}
			}
		}
		selectedButton = selected;
		if (selectedButton != null) {
			if (baseToaImage != null) {
				ImageIcon iconSel = (ImageIcon) selectedButton.getClientProperty("iconSelected");
				if (iconSel != null)
					selectedButton.setIcon(iconSel);
			} else {
				selectedButton.setBackground(colorSelected);
			}
		}
	}

	// controller có thể gọi để chọn tự động
	public void selectToaById(String toaID) {
		if (toaID == null)
			return;
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
		if (iconCache.containsKey(key))
			return iconCache.get(key);

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

		// tạo ảnh result có kích thước đúng target và vẽ ảnh scaled ở giữa (transparent padding)
		BufferedImage result = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = result.createGraphics();
		// làm mịn
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.setComposite(AlphaComposite.Clear);
		g2.fillRect(0, 0, targetW, targetH);
		g2.setComposite(AlphaComposite.SrcOver);

		int x = (targetW - newW) / 2;
		int y = (targetH - newH) / 2;

		Image scaled = src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		g2.drawImage(scaled, x, y, null);
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
		g.setComposite(AlphaComposite.DstIn); // giữ phần alpha của src
		g.drawImage(src, 0, 0, null);

		g.dispose();
		return out;
	}
}