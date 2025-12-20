package gui.tuyChinh;
/*
 * @(#) ArowIcon.java  1.0  [1:21:26 AM] Dec 21, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 21, 2025
 * @version: 1.0
 */

public class ArrowIcon implements Icon {
	private final Color color;

	public ArrowIcon() {
		this(new Color(34, 106, 155));
	}

	public ArrowIcon(Color color) {
		this.color = color;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(color);
		// Vẽ tam giác hướng xuống
		int size = 8;
		int offset = (getIconHeight() - size) / 2;
		int[] xPoints = { x, x + size, x + size / 2 };
		int[] yPoints = { y + offset + 2, y + offset + 2, y + offset + 2 + size / 2 };
		g2.fillPolygon(xPoints, yPoints, 3);
		g2.dispose();
	}

	@Override
	public int getIconWidth() {
		return 10;
	}

	@Override
	public int getIconHeight() {
		return 16;
	}
}