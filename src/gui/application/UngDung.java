package gui.application;/*
 * @ (#) UngDung.java   1.0     25/09/2025
package gui;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 25/09/2025
 */

import javax.swing.*;


import java.awt.Component;
import java.awt.Font;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import entity.NhanVien;
import gui.application.form.FormDangNhap;
import gui.application.form.GiaoDienChinh;
//import gui.application.form.GiaoDienChinh;

public class UngDung extends JFrame {
	private static final long serialVersionUID = 1L;
	private static UngDung ungDung;
	private final FormDangNhap formDangNhap;
	private GiaoDienChinh giaoDienChinh;

	private UngDung() {
		super("Quản Lý Bán Vé Tàu Ga Sài Gòn");
		ungDung = this;
		formDangNhap = new FormDangNhap();
		
		setSize(900, 600);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setContentPane(formDangNhap);
	}

	public static UngDung getInstance() {
		return ungDung;
	}

	public GiaoDienChinh getGiaoDienChinh() {
		return giaoDienChinh;
	}

	public FormDangNhap getFormDangNhap() {
		return formDangNhap;
	}

	public void createGiaoDienChinh(NhanVien nhanVien) {
		giaoDienChinh = new GiaoDienChinh(nhanVien);
		setContentPane(giaoDienChinh);
		giaoDienChinh.applyComponentOrientation(getComponentOrientation());
		SwingUtilities.updateComponentTreeUI(giaoDienChinh);
	}

	public static void showGiaoDienChinh(Component component) {
		component.applyComponentOrientation(ungDung.getComponentOrientation());
		ungDung.giaoDienChinh.showForm(component);
	}

	public static void setSelectedMenu(int index, int subIndex) {
		ungDung.giaoDienChinh.setSelectedMenu(index, subIndex);
	}

	public static void dangXuat() {
		ungDung.formDangNhap.resetDangNhap();
		FlatAnimatedLafChange.showSnapshot();
		ungDung.setContentPane(ungDung.formDangNhap);
		ungDung.formDangNhap.applyComponentOrientation(ungDung.getComponentOrientation());
		SwingUtilities.updateComponentTreeUI(ungDung.formDangNhap);
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
	}

	public static void main(String args[]) {
		FlatRobotoFont.install();
		FlatLaf.registerCustomDefaultsSource("gui.theme");
		UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 16));
		FlatMacLightLaf.setup();
		SwingUtilities.invokeLater(() -> new UngDung().setVisible(true));
	}
}