package gui.application.menu;
/*
 * @(#) ThanhPhanMenu.java  1.0  [12:11:42 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.*;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

public class ThanhPhanMenu extends JPanel {
	private static final long serialVersionUID = 1L;
	private final List<SuKienMenu> events;
	private final Menu menu;
	private final String[] menus;
	private int menuIndex;
	private final int menuItemHeight = 38;
	private final int subThanhPhanMenuHeight = 35;
	private final int subMenuLeftGap = 34;
	private final int firstGap = 5;
	private final int bottomGap = 5;
	private boolean menuShow;
	private MenuCon menuCon;

	public ThanhPhanMenu(Menu menu, String[] menus, int menuIndex, List<SuKienMenu> events, String role) {
		this.menu = menu;
		this.menus = menus;
		this.menuIndex = menuIndex;
		this.events = events;
		init(role);
	}

	public boolean isMenuShow() {
		return menuShow;
	}

	public void setMenuShow(boolean menuShow) {
		this.menuShow = menuShow;
	}

	public String[] getMenus() {
		return menus;
	}

	public int getMenuIndex() {
		return menuIndex;
	}

//	private Icon getIcon(String role) {
//		if (role.equalsIgnoreCase("Employee") && menuIndex >= 2) {
//			menuIndex++;
//		}
//		Color lightColor = FlatUIUtils.getUIColor("Menu.icon.lightColor", Color.red);
//		Color darkColor = FlatUIUtils.getUIColor("Menu.icon.darkColor", Color.red);
//		FlatSVGIcon icon = new FlatSVGIcon("gui/menu/icon/" + menuIndex + ".svg");
//		FlatSVGIcon.ColorFilter f = new FlatSVGIcon.ColorFilter();
//		f.add(Color.decode("#969696"), lightColor, darkColor);
//		icon.setColorFilter(f);
//		return icon;
//	}

	private void init(String role) {
		setLayout(new MenuLayout());
		putClientProperty(FlatClientProperties.STYLE,
				"background:$Menu.background;foreground:$Menu.lineColor");
		for (int i = 0; i < menus.length; i++) {
			JButton menuItem = createButtonItem(menus[i]);
			menuItem.setHorizontalAlignment(
					menuItem.getComponentOrientation().isLeftToRight() ? JButton.LEADING : JButton.TRAILING);
			if (i == 0) {
//				menuItem.setIcon(getIcon(role));
				menuItem.addActionListener((ActionEvent e) -> {
					if (menus.length > 1) {
						if (menu.isMenuFull()) {
							menuShow = !menuShow;
							for (int j = 1; j < getComponentCount(); j++) {
								getComponent(j).setVisible(menuShow);
							}
							revalidate();
							repaint();
						} else {
							menuCon.show(ThanhPhanMenu.this, getWidth() + UIScale.scale(5),
									UIScale.scale(menuItemHeight) / 2);
						}
					} else {
						menu.runEvent(menuIndex, 0);
					}
				});
			} else {
				final int subIndex = i;
				menuItem.addActionListener((ActionEvent e) -> {
					menu.runEvent(menuIndex, subIndex);
				});
				menuItem.setVisible(false); // Submenu hidden by default
			}
			add(menuItem);
		}
		menuCon = new MenuCon(getComponentOrientation(), menu, menuIndex, menus);
	}

	protected void setSelectedIndex(int index) {
		int size = getComponentCount();
		boolean selected = false;
		for (int i = 0; i < size; i++) {
			Component com = getComponent(i);
			if (com instanceof JButton) {
				((JButton) com).setSelected(i == index);
				if (i == index) selected = true;
			}
		}
		((JButton) getComponent(0)).setSelected(selected);
		menuCon.setSelectedIndex(index);
	}

	private JButton createButtonItem(String text) {
		JButton button = new JButton(text);
		button.putClientProperty(FlatClientProperties.STYLE,
				"background:$Menu.background;" +
				"foreground:$Menu.foreground;" +
				"selectedBackground:$Menu.button.selectedBackground;" +
				"selectedForeground:$Menu.button.selectedForeground;" +
				"borderWidth:0;focusWidth:0;innerFocusWidth:0;" +
				"arc:10;iconTextGap:10;margin:3,11,3,11");
		return button;
	}

	public void hideThanhPhanMenu() {
		menuShow = false;
		for (int i = 1; i < getComponentCount(); i++) {
			getComponent(i).setVisible(false);
		}
		revalidate();
		repaint();
	}

	public void setFull(boolean full) {
		if (full) {
			for (int i = 0; i < getComponentCount(); i++) {
				Component com = getComponent(i);
				if (com instanceof JButton) {
					JButton button = (JButton) com;
					button.setText(menus[i]);
					button.setHorizontalAlignment(
							getComponentOrientation().isLeftToRight() ? JButton.LEFT : JButton.RIGHT);
				}
			}
		} else {
			for (Component com : getComponents()) {
				if (com instanceof JButton) {
					JButton button = (JButton) com;
					button.setText("");
					button.setHorizontalAlignment(JButton.CENTER);
					if (getComponentZOrder(button) != 0) {
						button.setVisible(false);
					}
				}
			}
			menuShow = false;
		}
		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (menus.length > 1 && menu.isMenuFull()) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2.setColor(FlatUIUtils.getUIColor("Menu.arrowColor", getForeground()));

			int smenuItemHeight = UIScale.scale(menuItemHeight);
			boolean ltr = getComponentOrientation().isLeftToRight();
			int arrowWidth = UIScale.scale(10);
			int arrowHeight = UIScale.scale(5);
			int ax = ltr ? (getWidth() - arrowWidth * 2) : arrowWidth;
			int ay = (smenuItemHeight - arrowHeight) / 2;

			Path2D p = new Path2D.Double();
			p.moveTo(0, 0);
			p.lineTo(arrowWidth / 2, arrowHeight);
			p.lineTo(arrowWidth, 0);

			g2.translate(ax, ay);
			g2.draw(p);
			g2.dispose();
		}
	}

	public List<SuKienMenu> getEvents() {
		return events;
	}

	private class MenuLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {}
		@Override
		public void removeLayoutComponent(Component comp) {}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				int width = parent.getWidth();
				int height = insets.top + insets.bottom + UIScale.scale(menuItemHeight);
				for (int i = 1; i < parent.getComponentCount(); i++) {
					Component com = parent.getComponent(i);
					if (com.isVisible()) {
						height += UIScale.scale(subThanhPhanMenuHeight);
					}
				}
				return new Dimension(width, height);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			return new Dimension(0, 0);
		}

		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				boolean ltr = parent.getComponentOrientation().isLeftToRight();
				Insets insets = parent.getInsets();
				int x = insets.left;
				int y = insets.top;
				int width = parent.getWidth() - (insets.left + insets.right);
				for (int i = 0; i < parent.getComponentCount(); i++) {
					Component com = parent.getComponent(i);
					if (com.isVisible()) {
						if (i == 0) {
							com.setBounds(x, y, width, UIScale.scale(menuItemHeight));
							y += UIScale.scale(menuItemHeight) + UIScale.scale(firstGap);
						} else {
							int gap = UIScale.scale(subMenuLeftGap);
							int subX = ltr ? gap : 0;
							com.setBounds(x + subX, y, width - gap, UIScale.scale(subThanhPhanMenuHeight));
							y += UIScale.scale(subThanhPhanMenuHeight);
						}
					}
				}
			}
		}
	}
}