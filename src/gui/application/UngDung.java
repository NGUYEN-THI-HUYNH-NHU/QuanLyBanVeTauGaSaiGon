package gui.application;
/*
@ (#) UngDung.java   1.0     25/09/2025
package gui;
					
						
/*** @description :
* @author : Vy, Pham Kha Vy
* @version 1.0
* @created : 25/09/2025
*/

import java.awt.Component;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import entity.NhanVien;
import gui.application.form.FormDangNhap;
import gui.application.form.GiaoDienChinh;
import gui.application.form.banVe.PanelBanVe;
import gui.application.form.doiVe.PanelDoiVe;
import gui.application.form.hoanVe.PanelHoanVe;
import gui.application.form.quanLyTuyen.PanelThemTuyen;
import gui.application.paymentHelper.NgrokRunner;

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

//		this.addWindowListener(new WindowAdapter() {
//			@Override
//			public void windowClosing(WindowEvent e) {
//				xuLyThoatChuongTrinh();
//			}
//		});
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
		ungDung.formDangNhap.getTxtTenDangNhap().requestFocusInWindow();
		SwingUtilities.updateComponentTreeUI(ungDung.formDangNhap);
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
	}

	public static void main(String args[]) {
//		NgrokRunner.startNgrok();

		FlatRobotoFont.install();
		FlatLaf.registerCustomDefaultsSource("gui.theme");
		UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
		UIManager.put("PasswordField.showRevealButton", true);
		FlatMacLightLaf.setup();
		SwingUtilities.invokeLater(() -> new UngDung().setVisible(true));

		try {
			AppHttpServer mobileServer = new AppHttpServer();
			mobileServer.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Xử lý logic khi người dùng nhất nút "X" để thoát.
	 */
	private void xuLyThoatChuongTrinh() {
		Component panelHienTai = null;
		if (giaoDienChinh != null) {
			panelHienTai = giaoDienChinh.getHienThiPanel();
		}
		if (panelHienTai instanceof PanelThemTuyen) {
			int choice = JOptionPane.showConfirmDialog(this,
					"Bạn có chắc chắn muốn thoát không?\n Mọi thay đổi chưa lưu (Thêm Tuyên) sẽ bị mất.",
					"Xác nhận thoát", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		} else {
			int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát ứng dụng không?",
					"Xác nhận thoát", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (choice == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}

	public static void reloadPanelBanVe() {
		showGiaoDienChinh(new PanelBanVe());
	}

	public static void reloadPanelHoanVe() {
		showGiaoDienChinh(new PanelHoanVe());
	}

	public static void reloadPanelDoiVe() {
		showGiaoDienChinh(new PanelDoiVe());
	}
}