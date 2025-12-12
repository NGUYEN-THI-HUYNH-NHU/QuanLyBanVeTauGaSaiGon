package gui.application.menu;
/*
 * @(#) Menu.java  1.0  [12:06:09 PM] Sep 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 26, 2025
 * @version: 1.0
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import com.formdev.flatlaf.FlatClientProperties;

import entity.NhanVien;
import entity.type.VaiTroNhanVien;

public class Menu extends JPanel {

	private static final long serialVersionUID = 1L;
	private final List<SuKienMenu> suKienMenuList = new ArrayList<>();
	private boolean menuFull = true;
	private final String headerName = "Ga Sài Gòn";
	protected final boolean hideMenuTitleOnMinimum = true;
	protected final int menuTitleLeftInset = 5;
	protected final int menuTitleVgap = 5;
	protected final int menuMaxWidth = 210;
	protected final int menuMinWidth = 70;
	protected final int headerFullHgap = 5;
	private final String[][] menuItems = { { "~Quản Lý~" }, // 0
			{ "Dashboard" }, // 1
			{ "Quản lý ga" }, // 2
			{ "Bán vé" }, // 3
			{ "Quản lý vé", "Hoàn vé", "Đổi vé" }, // 4
			{ "Quản lý hóa đơn" }, // 5
			{ "Quản lý đơn đặt chỗ" }, // 6
			{ "Quản lý tuyến" }, // 7
			{ "Quản lý chuyến" }, // 8
			{ "Quản lý biểu giá" }, // 9
			{ "Quản lý khuyến mãi" }, // 10
			{ "Quản lý khách hàng" }, // 11
			{ "Quản lý nhân viên" }, // 12
			{ "Quản lý tài khoản" }, // 13
			{ "~Khác~" }, // 14
			{ "Thống kê & Báo cáo ", "Thống kê", "Báo cáo" }, // 15
			{ "Thống Kê & Báo cáo", "Doanh thu", "Vé", "Khách hàng" }, // 16
			{ "Tài khoản cá nhân", "Thông tin", "Đổi Mật Khẩu" }, // 17
			{ "About us" }, // 18
			{ "Trợ giúp" }, // 19
			{ "Đăng Xuất" } }; // 20
	private JLabel header;
	private JScrollPane scroll;
	private JPanel panelMenu;
	private ImageIcon avatarIcon;
	private final int AVATAR_SIZE = 40;

	public Menu(NhanVien nhanVien) {
		setLayout(new BorderLayout());
		putClientProperty(FlatClientProperties.STYLE,
				"" + "border:20,2,2,2;" + "background:$Menu.background;" + "arc:10");

		init(nhanVien.getVaiTroNhanVien());
	}

	private void init(VaiTroNhanVien vaiTroNhanVien) {
		header = new JLabel(headerName);
		header.putClientProperty(FlatClientProperties.STYLE,
				"" + "font:$Menu.header.font;" + "foreground:$Menu.foreground");

		panelMenu = new JPanel();
		panelMenu.setLayout(new LayoutThanhPhanMenu(this));
		panelMenu.putClientProperty(FlatClientProperties.STYLE, "" + "border:5,5,5,5;" + "background:$Menu.background");

		scroll = new JScrollPane(panelMenu);
		scroll.putClientProperty(FlatClientProperties.STYLE, "" + "border:null");
		JScrollBar vscroll = scroll.getVerticalScrollBar();
		vscroll.setUnitIncrement(10);
		vscroll.putClientProperty(FlatClientProperties.STYLE,
				"" + "width:$Menu.scroll.width;" + "trackInsets:$Menu.scroll.trackInsets;"
						+ "thumbInsets:$Menu.scroll.thumbInsets;" + "background:$Menu.ScrollBar.background;"
						+ "thumb:$Menu.ScrollBar.thumb");

		createMenu(vaiTroNhanVien);

		add(header, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
	}

	private void createMenu(VaiTroNhanVien vaiTroNhanVien) {
		for (int i = 0; i < menuItems.length; i++) {
			String menuName = menuItems[i][0];
			if (menuName.startsWith("~") && menuName.endsWith("~")) {
				panelMenu.add(createTitle(menuName));
			} else {
				if (vaiTroNhanVien == VaiTroNhanVien.NHAN_VIEN) {
					if (i == 1 || i == 2 || i == 7 || i == 8 || i == 9 || i == 12 || i == 13 || i == 14 || i == 16) {
						continue;
					}
				} else {
					if (i == 3 || i == 4 || i == 5 || i == 15) {
						continue;
					}
				}
				ThanhPhanMenu menuItem = new ThanhPhanMenu(this, menuItems[i], i, suKienMenuList,
						vaiTroNhanVien.toString());
				panelMenu.add(menuItem);
			}
		}
	}

	private JLabel createTitle(String title) {
		String menuName = title.substring(1, title.length() - 1);
		JLabel lbTitle = new JLabel(menuName);
		lbTitle.putClientProperty(FlatClientProperties.STYLE,
				"" + "font:$Menu.label.font;" + "foreground:$Menu.title.foreground");
		return lbTitle;
	}

	public void setSelectedMenu(int index, int subIndex) {
		runEvent(index, subIndex);
	}

	protected void setSelected(int index, int subIndex) {
		int size = panelMenu.getComponentCount();
		for (int i = 0; i < size; i++) {
			Component com = panelMenu.getComponent(i);
			if (com instanceof ThanhPhanMenu) {
				ThanhPhanMenu item = (ThanhPhanMenu) com;
				if (item.getMenuIndex() == index) {
					item.setIndexDuocChon(subIndex);
				} else {
					item.setIndexDuocChon(-1);
				}
			}
		}
	}

	protected void runEvent(int index, int subIndex) {
		HanhDongMenu hanhDongMenu = new HanhDongMenu();
		for (SuKienMenu sk : suKienMenuList) {
			sk.menuSelected(index, subIndex, hanhDongMenu);
		}
		if (!hanhDongMenu.isCancel()) {
			setSelected(index, subIndex);
		}
	}

	public void addSuKienMenu(SuKienMenu sk) {
		suKienMenuList.add(sk);
	}

	public void hideThanhPhanMenu() {
		for (Component com : panelMenu.getComponents()) {
			if (com instanceof ThanhPhanMenu) {
				((ThanhPhanMenu) com).hideThanhPhanMenu();
			}
		}
		revalidate();
	}

	public boolean isMenuFull() {
		return menuFull;
	}

	public void setMenuFull(boolean menuFull) {
		this.menuFull = menuFull;
		if (menuFull) {
			header.setText(headerName);
			header.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? JLabel.LEFT : JLabel.RIGHT);
			if (avatarIcon != null) {
				header.setIcon(avatarIcon);
			}
		} else {
			header.setText("");
			header.setHorizontalAlignment(JLabel.CENTER);
			if (avatarIcon != null) {
				header.setIcon(avatarIcon);
			}
		}
		for (Component com : panelMenu.getComponents()) {
			if (com instanceof ThanhPhanMenu) {
				((ThanhPhanMenu) com).setFull(menuFull);
			}
		}
	}

	public boolean isHideMenuTitleOnMinimum() {
		return hideMenuTitleOnMinimum;
	}

	public int getMenuTitleLeftInset() {
		return menuTitleLeftInset;
	}

	public int getMenuTitleVgap() {
		return menuTitleVgap;
	}

	public int getMenuMaxWidth() {
		return menuMaxWidth;
	}

	public int getMenuMinWidth() {
		return menuMinWidth;
	}
}