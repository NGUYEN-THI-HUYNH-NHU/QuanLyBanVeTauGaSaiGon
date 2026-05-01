package gui.application;
/*
@ (#) UngDung.java   1.0     25/09/2025
package gui;
					
						
/*** @description :
* @author : Vy, Pham Kha Vy
* @version 1.0
* @created : 25/09/2025
*/

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import db.JPAUtil;
import dto.NhanVienDTO;
import gui.application.form.FormDangNhap;
import gui.application.form.GiaoDienChinh;
import gui.application.form.banVe.PanelBanVe;
import gui.application.form.doiVe.PanelDoiVe;
import gui.application.form.hoanVe.PanelHoanVe;
import gui.application.form.khachHang.PanelQuanLyKhachHang;
import gui.application.form.quanLyTuyen.PanelThemTuyen;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UngDung extends JFrame {
    private static final long serialVersionUID = 1L;
    private static UngDung ungDung;
    private final FormDangNhap formDangNhap;
    private GiaoDienChinh giaoDienChinh;

    // Biến để lưu trữ các màn hình cần giữ trạng thái
    private Map<String, Component> panelCache = new HashMap<>();

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
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("theme");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        UIManager.put("PasswordField.showRevealButton", true);
        FlatMacLightLaf.setup();

        //Warming up Hibernate
        new Thread(() -> {
            try {
                JPAUtil.getEntityManager().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        SwingUtilities.invokeLater(() -> new UngDung().setVisible(true));

        try {
            AppHttpServer mobileServer = new AppHttpServer();
            mobileServer.startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadPanelBanVe() {
        UngDung.getInstance().removePanelFromCache("PanelBanVe");
        UngDung.showGiaoDienChinh(UngDung.getInstance().getOrCreatePanel("PanelBanVe", () -> new PanelBanVe()));
    }

    public static void reloadPanelHoanVe() {
        UngDung.getInstance().removePanelFromCache("PanelHoanVe");
        UngDung.showGiaoDienChinh(UngDung.getInstance().getOrCreatePanel("PanelHoanVe", () -> new PanelHoanVe()));
    }

    public static void reloadPanelDoiVe() {
        UngDung.getInstance().removePanelFromCache("PanelDoiVe");
        UngDung.showGiaoDienChinh(UngDung.getInstance().getOrCreatePanel("PanelDoiVe", () -> new PanelDoiVe()));
    }

    public static void loadDataForCreatingNewKhachHang(NhanVienDTO nhanVien, String cccd) {
        setSelectedMenu(10, 0);
        UngDung.showGiaoDienChinh(new PanelQuanLyKhachHang(nhanVien, cccd));
    }

    public GiaoDienChinh getGiaoDienChinh() {
        return giaoDienChinh;
    }

    public FormDangNhap getFormDangNhap() {
        return formDangNhap;
    }

    public void createGiaoDienChinh(NhanVienDTO nhanVien) {
        giaoDienChinh = new GiaoDienChinh(nhanVien);
        setContentPane(giaoDienChinh);
        giaoDienChinh.applyComponentOrientation(getComponentOrientation());
        SwingUtilities.updateComponentTreeUI(giaoDienChinh);
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

    // Hỗ trợ lấy Panel từ Cache
    // key: Tên định danh
    // creator: Hàm tạo mới nếu chưa có trong cache
    public Component getOrCreatePanel(String key, Supplier<Component> creator) {
        if (!panelCache.containsKey(key)) {
            Component newComp = creator.get();
            panelCache.put(key, newComp);
        }

        return panelCache.get(key);
    }

    public void removePanelFromCache(String key) {
        panelCache.remove(key);
    }
}