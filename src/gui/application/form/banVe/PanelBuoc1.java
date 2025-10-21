package gui.application.form.banVe;
/*
 * @(#) PanelBuoc1.java  1.0  [10:38:53 AM] Sep 28, 2025
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.itextpdf.text.Font;
import com.toedter.calendar.JDateChooser;

public class PanelBuoc1 extends JPanel {
	private PanelBuoc1Controller panelBuoc1Controller;
	private JTextField txtGaDi, txtGaDen;
	private JDateChooser dateChooserNgayDi, dateChooserNgayVe;
	private JRadioButton radMotChieu, radKhuHoi;
	private JButton btnTimKiem;
	private JPanel pnlTimKiem;
	private JPanel pnlEast;
	private JPanel pnlCenter;

	public PanelBuoc1() {
		setLayout(new BorderLayout());
		setBorder(new TitledBorder("(1) Tìm kiếm chuyến"));

		// pnlCenter
		pnlCenter = new JPanel(new BorderLayout());

		// lblTieuDe
		JLabel lblTieuDe = new JLabel("Thông tin hành trình", SwingConstants.CENTER);
		lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 24f));
		lblTieuDe.setBackground(new Color(32, 83, 145));
		lblTieuDe.putClientProperty(FlatClientProperties.STYLE, "foreground:$Menu.foreground");
		lblTieuDe.setOpaque(true);

		// Form
		pnlTimKiem = new JPanel(new GridBagLayout());
		pnlTimKiem.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
		pnlTimKiem.setBackground(new Color(245, 245, 245));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 5, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Ga đi
		gbc.gridx = 0;
		gbc.gridy = 0;
		pnlTimKiem.add(new JLabel("Ga đi:"), gbc);
		gbc.gridx = 1;
		txtGaDi = new JTextField(15);
		txtGaDi.putClientProperty("FlatClientProperties.PLACEHOLDER_TEXT", "Nhập ga đi...");
		pnlTimKiem.add(txtGaDi, gbc);

		// Ga đến
		gbc.gridx = 0;
		gbc.gridy = 1;
		pnlTimKiem.add(new JLabel("Ga đến:"), gbc);
		gbc.gridx = 1;
		txtGaDen = new JTextField(15);
		txtGaDen.putClientProperty("FlatClientProperties.PLACEHOLDER_TEXT", "Nhập ga đến...");
		pnlTimKiem.add(txtGaDen, gbc);

		// Loại hành trình
		gbc.gridx = 0;
		gbc.gridy = 2;
		pnlTimKiem.add(new JLabel("Loại hành trình:"), gbc);
		gbc.gridx = 1;
		JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		radioPanel.setBackground(new Color(245, 245, 245));
		radMotChieu = new JRadioButton("Một chiều", true);
		radKhuHoi = new JRadioButton("Khứ hồi");
		ButtonGroup group = new ButtonGroup();
		group.add(radMotChieu);
		group.add(radKhuHoi);
		radioPanel.add(radMotChieu);
		radioPanel.add(radKhuHoi);
		pnlTimKiem.add(radioPanel, gbc);

		// Ngày đi
		gbc.gridx = 0;
		gbc.gridy = 3;
		pnlTimKiem.add(new JLabel("Ngày đi:"), gbc);
		gbc.gridx = 1;
		dateChooserNgayDi = new JDateChooser();
		dateChooserNgayDi.setDateFormatString("dd/MM/yyyy");
		dateChooserNgayDi.setDate(new java.util.Date());
		pnlTimKiem.add(dateChooserNgayDi, gbc);

		// Ngày về
		gbc.gridx = 0;
		gbc.gridy = 4;
		pnlTimKiem.add(new JLabel("Ngày về:"), gbc);
		gbc.gridx = 1;
		dateChooserNgayVe = new JDateChooser();
		dateChooserNgayVe.setDateFormatString("dd/MM/yyyy");
		dateChooserNgayVe.setEnabled(false);
		pnlTimKiem.add(dateChooserNgayVe, gbc);

		radMotChieu.addActionListener(e -> dateChooserNgayVe.setEnabled(false));
		radKhuHoi.addActionListener(e -> dateChooserNgayVe.setEnabled(true));

		// Nút tìm kiếm
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		btnTimKiem = new JButton("Tìm chuyến tàu");
		pnlTimKiem.add(btnTimKiem, gbc);

		pnlCenter.add(lblTieuDe, BorderLayout.NORTH);
		pnlCenter.add(pnlTimKiem, BorderLayout.CENTER);

		pnlEast = new JPanel(new BorderLayout());
		pnlEast.setPreferredSize(new Dimension(520, JFrame.HEIGHT));
		pnlEast.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

		BufferedImage img = null;
		try {
			URL url = getClass().getResource("/gui/icon/png/ban-do-tuyen-duong-sat-viet-nam.jpg");
			if (url != null) {
				img = ImageIO.read(url);
			} else {
				File f = new File("src/gui/icon/png/ban-do-tuyen-duong-sat-viet-nam.jpg");
				if (f.exists()) {
					img = ImageIO.read(f);
				} else {
					System.err.println("File image not found: " + f.getAbsolutePath());
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (img != null) {
			AspectRatioImageLabel lblImg = new AspectRatioImageLabel(img);
			lblImg.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			pnlEast.add(lblImg, BorderLayout.CENTER);

			pnlEast.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentResized(ComponentEvent e) {
					lblImg.revalidate();
					lblImg.repaint();
				}
			});
		} else {
			pnlEast.add(new JLabel("Không load được ảnh"), BorderLayout.CENTER);
		}

		add(pnlCenter, BorderLayout.CENTER);
		add(pnlEast, BorderLayout.EAST);

		panelBuoc1Controller = new PanelBuoc1Controller(this);
	}

	// ---------- Getters cho panelBuoc1Controller su dung ----------
	public JTextField getTxtGaDi() {
		return txtGaDi;
	}

	public JTextField getTxtGaDen() {
		return txtGaDen;
	}

	public JDateChooser getDateNgayDi() {
		return dateChooserNgayDi;
	}

	public JDateChooser getDateNgayVe() {
		return dateChooserNgayVe;
	}

	public boolean isKhuHoi() {
		return radKhuHoi.isSelected();
	}

	public JButton getBtnTimKiem() {
		return btnTimKiem;
	}

	public String getGaDi() {
		return txtGaDi.getText();
	}

	public String getGaDen() {
		return txtGaDen.getText();
	}

	public LocalDate getNgayDi() {
		java.util.Date d = dateChooserNgayDi.getDate();
		if (d == null)
			return null;
		Instant instant = d.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public LocalDate getNgayVe() {
		java.util.Date d = dateChooserNgayVe.getDate();
		if (d == null)
			return null;
		Instant instant = d.toInstant();
		return instant.atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public void setWizardController(WizardController wizardController) {
		if (panelBuoc1Controller != null) {
			panelBuoc1Controller.setWizardController(wizardController);
		}
	}

	class AspectRatioImageLabel extends JLabel {
		private Image image;

		public AspectRatioImageLabel(Image image) {
			this.image = image;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (image == null)
				return;

			int panelWidth = getWidth();
			int panelHeight = getHeight();
			int imgWidth = image.getWidth(this);
			int imgHeight = image.getHeight(this);

			double panelRatio = (double) panelWidth / panelHeight;
			double imgRatio = (double) imgWidth / imgHeight;

			int drawWidth = panelWidth;
			int drawHeight = panelHeight;

			if (panelRatio > imgRatio) {
				drawWidth = (int) (panelHeight * imgRatio);
			} else {
				drawHeight = (int) (panelWidth / imgRatio);
			}

			int x = (panelWidth - drawWidth) / 2;
			int y = (panelHeight - drawHeight) / 2;

			g.drawImage(image, x, y, drawWidth, drawHeight, this);
		}
	}
}