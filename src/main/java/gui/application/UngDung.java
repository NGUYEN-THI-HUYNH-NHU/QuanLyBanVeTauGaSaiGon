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
    private static final String BASE_TITLE = "Quản Lý Bán Vé Tàu Ga Sài Gòn";
    private static UngDung ungDung;
    private static JWindow splashScreen;
    private static JProgressBar progressBar;
    private final FormDangNhap formDangNhap;
    private GiaoDienChinh giaoDienChinh;
    // Biến để lưu trữ các màn hình cần giữ trạng thái
    private Map<String, Component> panelCache = new HashMap<>();

    private UngDung() {
        super(BASE_TITLE);
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
        updateWindowTitle(null);
    }

    public static void main(String args[]) {
        // 1. Cấu hình FlatLaf PHẢI CHẠY ĐẦU TIÊN trên luồng chính
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("theme");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        UIManager.put("PasswordField.showRevealButton", true);
        FlatMacLightLaf.setup();

        // 2. Mở Splash Screen và bắt đầu tải dữ liệu trên luồng giao diện (EDT)
        SwingUtilities.invokeLater(() -> {
            createSplashScreen(); // Vẽ cửa sổ Splash Screen
            chayCacTacVuNen();    // Gọi hàm load dữ liệu ngầm
        });
    }

    // Hàm phụ trợ quản lý việc load ngầm
    private static void chayCacTacVuNen() {
        new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Tác vụ 1: Giả lập chờ giao diện vẽ xong (nửa giây)
                publish(10);
                if (progressBar != null) progressBar.setString("Đang chuẩn bị hệ thống...");
                Thread.sleep(500);

                // Tác vụ 2: Khởi động Hibernate (Nặng nhất)
                publish(30);
                if (progressBar != null) progressBar.setString("Đang kết nối cơ sở dữ liệu...");
                try {
                    JPAUtil.getEntityManager().close();
                } catch (Exception e) {
                    System.err.println("Lỗi Hibernate: " + e.getMessage());
                }

                // Tác vụ 3: Khởi động Máy chủ Mobile
                publish(70);
                if (progressBar != null) progressBar.setString("Đang khởi động dịch vụ máy chủ...");
                try {
                    AppHttpServer mobileServer = new AppHttpServer();
                    mobileServer.startServer();
                } catch (Exception e) {
                    System.err.println("Lỗi Server: " + e.getMessage());
                }

                // Hoàn tất
                publish(100);
                if (progressBar != null) progressBar.setString("Hoàn tất!");
                Thread.sleep(500); // Dừng nửa giây để người dùng kịp nhìn thấy 100%

                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                // Cập nhật thanh tiến trình an toàn trên luồng giao diện
                int progress = chunks.get(chunks.size() - 1);
                if (progressBar != null) {
                    progressBar.setValue(progress);
                }
            }

            @Override
            protected void done() {
                // Bắt lỗi nếu có ngoại lệ xảy ra trong luồng ngầm (tránh bị nuốt lỗi)
                try {
                    get();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Tắt Splash Screen
                if (splashScreen != null) {
                    splashScreen.dispose();
                }

                // Bật ứng dụng chính (Lúc này FlatLaf đã load đầy đủ)
                new UngDung().setVisible(true);
            }
        }.execute();
    }

    private static void createSplashScreen() {
        splashScreen = new JWindow();
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); // Viền mờ

        int TARGET_WIDTH = 600;
        int TARGET_HEIGHT = 350;

        // 1. Thêm ảnh Splash Screen
        java.net.URL imgURL = UngDung.class.getResource("/icon/png/splash-screen.png");
        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image originalImage = originalIcon.getImage();
        // Thu nhỏ ảnh
        Image scaledImage = originalImage.getScaledInstance(TARGET_WIDTH, TARGET_HEIGHT, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        JLabel imageLabel = new JLabel(scaledIcon);
        contentPane.add(imageLabel, BorderLayout.CENTER);

        // 2. Thêm thanh tiến trình
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // Hiển thị số %
        progressBar.setForeground(new Color(36, 104, 155));
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(progressBar.getWidth(), 12)); // Chiều cao thanh tiến trình

        contentPane.add(progressBar, BorderLayout.SOUTH);

        splashScreen.setContentPane(contentPane);
        splashScreen.pack();
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setVisible(true);
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

    public static void updateWindowTitle(String suffix) {
        if (suffix == null || suffix.isBlank()) ungDung.setTitle(BASE_TITLE);
        else ungDung.setTitle(BASE_TITLE + " - " + suffix);
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