package gui.tuyChinh;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/*
 * @(#) ColorIcon.java  1.0  [6:28:17 PM] Dec 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 20, 2025
 * @version: 1.0
 */

public class ColorIcon implements Icon {
	private final Color color;
	private final int width;
	private final int height;

	public ColorIcon(Color color, int width, int height) {
		this.color = color;
		this.width = width;
		this.height = height;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRoundRect(x, y, width, height, 10, 10);
	}

	@Override
	public int getIconWidth() {
		return width;
	}

	@Override
	public int getIconHeight() {
		return height;
	}
}