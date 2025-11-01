package gui.application.form;
/*
 * @ (#) GiaoDienChinh.java   1.0     25/09/2025
package gui.application.form;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 25/09/2025
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;

import entity.NhanVien;
import gui.application.UngDung;
import gui.application.form.KhuyenMai.QuanLyKhuyenMai;
import gui.application.form.NhanVien.QuanLyNhanVien;
import gui.application.form.about.PanelAbout;
import gui.application.form.banVe.PanelBanVe;
import gui.application.form.dashboard.Dashboard;
import gui.application.form.khachHang.QuanLyKhachHang;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;
import gui.application.form.thongKe.PanelBaoCao;
import gui.application.form.thongKe.PanelThongKe;
import gui.application.form.thongKe.PanelThongKeDoanhThu;
import gui.application.form.thongKe.PanelThongKeVe;
import gui.application.form.thongTin.FormDoiMatKhau;
import gui.application.form.thongTin.FormThongTinCaNhan;
import gui.application.form.troGiup.PanelTroGiup;
import gui.application.menu.HanhDongMenu;
import gui.application.menu.Menu;

public class GiaoDienChinh extends JLayeredPane {
	private static final long serialVersionUID = 1L;
	private Menu menu;
	private JPanel panelBody;
	private JButton menuButton;

	public GiaoDienChinh(NhanVien nhanVien) {
		init(nhanVien);
	}

	private void init(NhanVien nhanVien) {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GiaoDienChinhLayout());
		menu = new Menu(nhanVien.getVaiTroNhanVien().toString());
		panelBody = new JPanel(new BorderLayout());
		initMenuArrowIcon();
		menuButton.putClientProperty(FlatClientProperties.STYLE,
				"" + "background:$Menu.button.background;" + "arc:999;" + "focusWidth:0;" + "borderWidth:0");
		menuButton.addActionListener((ActionEvent e) -> {
			setMenuFull(!menu.isMenuFull());
		});
		initMenuEvent(nhanVien);
		setLayer(menuButton, JLayeredPane.POPUP_LAYER);
		add(menuButton);
		add(menu);
		add(panelBody);
	}

	@Override
	public void applyComponentOrientation(ComponentOrientation o) {
		super.applyComponentOrientation(o);
		initMenuArrowIcon();
	}

	private void initMenuArrowIcon() {
		if (menuButton == null) {
			menuButton = new JButton();
		}
		String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
		menuButton.setIcon(new FlatSVGIcon("gui/icon/svg/" + icon, 0.8f));
	}

	private void initMenuEvent(NhanVien nhanVien) {
		menu.addSuKienMenu((int index, int subIndex, HanhDongMenu action) -> {

			switch (index) {
			case 1 -> UngDung.showGiaoDienChinh(new Dashboard());
			// UC cua NHAN_VIEN
			case 2 -> UngDung.showGiaoDienChinh(new PanelBanVe(nhanVien));
//			case 3 -> {
//				switch (subIndex) {
//					case 1 -> UngDung.showGiaoDienChinh(new PanelHoanVe(nhanVien));
//					case 2 -> UngDung.showGiaoDienChinh(new PanelDoiVe(nhanVien));
//					default -> action.cancel();
//				}
//			}
//			case 4 -> UngDung.showGiaoDienChinh(new PanelQuanLyHoaDon(nhanVien));
			case 13 -> {
				switch (subIndex) {
				case 1 -> UngDung.showGiaoDienChinh(new PanelThongKe(nhanVien));
				case 2 -> UngDung.showGiaoDienChinh(new PanelBaoCao(nhanVien));
				}
			}

			// UC cua QUAN_LY
			case 5 -> UngDung.showGiaoDienChinh(new PanelQuanLyTuyen(nhanVien));
//			case 6 -> UngDung.showGiaoDienChinh(new PanelQuanLyChuyen(nhanVien));
//			case 7 -> UngDung.showGiaoDienChinh(new PanelQuanLyBieuGia(nhanVien));
			case 8 -> UngDung.showGiaoDienChinh(new QuanLyKhuyenMai(nhanVien));
			case 9 -> UngDung.showGiaoDienChinh(new QuanLyKhachHang(nhanVien));
			case 10 -> UngDung.showGiaoDienChinh(new QuanLyNhanVien(nhanVien));
//			case 11 -> UngDung.showGiaoDienChinh(new Tai(nhanVien));

			// UC dung chung
			case 14 -> {
				switch (subIndex) {
				case 1 -> UngDung.showGiaoDienChinh(new PanelThongKeDoanhThu());
				case 2 -> UngDung.showGiaoDienChinh(new PanelThongKeVe());
				// case 3 -> UngDung.showGiaoDienChinh(new PanelThongKeKhachHang());
				default -> action.cancel();
				}
			}
			case 15 -> {
				switch (subIndex) {
				case 1 -> UngDung.showGiaoDienChinh(new FormThongTinCaNhan(nhanVien));
				case 2 -> UngDung.showGiaoDienChinh(new FormDoiMatKhau(nhanVien));
				default -> action.cancel();
				}
			}
			case 16 -> UngDung.showGiaoDienChinh(new PanelAbout());
			case 17 -> UngDung.showGiaoDienChinh(new PanelTroGiup());
			case 18 -> UngDung.dangXuat();
			default -> action.cancel();
			}
		});
	}

	private void setMenuFull(boolean full) {
		String icon;
		if (getComponentOrientation().isLeftToRight()) {
			icon = (full) ? "menu_left.svg" : "menu_right.svg";
		} else {
			icon = (full) ? "menu_right.svg" : "menu_left.svg";
		}
		menuButton.setIcon(new FlatSVGIcon("gui/icon/svg/" + icon, 0.8f));
		menu.setMenuFull(full);
		revalidate();
	}

	public void hideMenu() {
		menu.hideThanhPhanMenu();
		;
	}

	public void showForm(Component component) {
		panelBody.removeAll();
		panelBody.add(component);
		panelBody.repaint();
		panelBody.revalidate();
	}

	public void setSelectedMenu(int index, int subIndex) {
		menu.setSelectedMenu(index, subIndex);
	}

	private class GiaoDienChinhLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				return new Dimension(5, 5);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				return new Dimension(0, 0);
			}
		}

		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				boolean ltr = parent.getComponentOrientation().isLeftToRight();
				Insets insets = UIScale.scale(parent.getInsets());
				int x = insets.left;
				int y = insets.top;
				int width = parent.getWidth() - (insets.left + insets.right);
				int height = parent.getHeight() - (insets.top + insets.bottom);
				int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
				int menuX = ltr ? x : x + width - menuWidth;
				menu.setBounds(menuX, y, menuWidth, height);
				int menuButtonWidth = menuButton.getPreferredSize().width;
				int menuButtonHeight = menuButton.getPreferredSize().height;
				int menubX;
				if (ltr) {
					menubX = (int) (x + menuWidth - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
				} else {
					menubX = (int) (menuX - (menuButtonWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
				}
				menuButton.setBounds(menubX, UIScale.scale(30), menuButtonWidth, menuButtonHeight);
				int gap = UIScale.scale(5);
				int bodyWidth = width - menuWidth - gap;
				int bodyHeight = height;
				int bodyx = ltr ? (x + menuWidth + gap) : x;
				int bodyy = y;
				panelBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
			}
		}
	}
}