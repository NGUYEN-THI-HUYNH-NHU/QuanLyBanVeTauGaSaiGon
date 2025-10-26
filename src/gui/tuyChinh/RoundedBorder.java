package gui.tuyChinh;
/*
 * @(#) RoundedBorder.java  1.0  [9:09:10 PM] Oct 23, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 23, 2025
 * @version: 1.0
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.border.AbstractBorder;

public class RoundedBorder extends AbstractBorder {
	private int radius; // Độ bo tròn góc
	private Color borderColor; // Màu border
	private int thickness; // Độ dày border
	private boolean filled; // Có tô nền hay không
	private Color fillColor; // Màu nền (nếu có)

	/**
	 * Constructor cơ bản (bo tròn, màu, độ dày)
	 */
	public RoundedBorder(int radius, Color borderColor, int thickness) {
		this(radius, borderColor, thickness, false, null);
	}

	/**
	 * Constructor đầy đủ (bo tròn, màu, độ dày, có nền hay không)
	 */
	public RoundedBorder(int radius, Color borderColor, int thickness, boolean filled, Color fillColor) {
		this.radius = radius;
		this.borderColor = borderColor;
		this.thickness = thickness;
		this.filled = filled;
		this.fillColor = fillColor;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Tính toán vùng vẽ bo tròn
		int arc = radius * 2;
		int innerX = x + thickness / 2;
		int innerY = y + thickness / 2;
		int innerW = width - thickness;
		int innerH = height - thickness;

		// Nếu có tô nền bo tròn
		if (filled && fillColor != null) {
			g2.setColor(fillColor);
			g2.fillRoundRect(innerX, innerY, innerW, innerH, arc, arc);
		}

		// Vẽ viền bo tròn
		g2.setColor(borderColor);
		g2.setStroke(new BasicStroke(thickness));
		g2.drawRoundRect(innerX, innerY, innerW, innerH, arc, arc);

		g2.dispose();
	}

	@Override
	public Insets getBorderInsets(Component c) {
		int pad = thickness + radius / 2;
		return new Insets(pad, pad, pad, pad);
	}

	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		int pad = thickness + radius / 2;
		insets.left = insets.right = insets.top = insets.bottom = pad;
		return insets;
	}
}