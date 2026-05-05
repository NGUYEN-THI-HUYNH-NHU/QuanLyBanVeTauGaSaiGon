package gui.application;
/*
@ (#) UngDung.java   1.0     25/09/2025
package gui;
					
						
/*** @description :
* @author : Nguyen Thi Huynh Nhu
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
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UngDung extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String BASE_TITLE = "Quản Lý Bán Vé Tàu Ga Sài Gòn";
    private static UngDung ungDung;
    private static JWindow splashScreen;
    private static JProgressBar progressBar;
    private static JLabel lblStatus;
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
        // 1. Cấu hình FlatLaf chạy đầu tiên trên luồng chính
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("theme");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 12));
        UIManager.put("PasswordField.showRevealButton", true);
        FlatMacLightLaf.setup();

        // 2. Mở Splash Screen và bắt đầu tải dữ liệu trên EDT
        SwingUtilities.invokeLater(() -> {
            createSplashScreen();
            chayCacTacVuNen();
        });
    }

    // Hàm phụ trợ quản lý việc load ngầm
    private static void chayCacTacVuNen() {
        new SwingWorker<Void, Integer>() {
            private String textTrangThai = "";

            @Override
            protected Void doInBackground() throws Exception {
                textTrangThai = "Đang chuẩn bị hệ thống...";
                publish(10);

                textTrangThai = "Đang chuẩn bị dữ liệu (quá trình này có thể mất chút thời gian)...";
                publish(30);
                try {
                    JPAUtil.getEntityManager().close();
                } catch (Exception e) {
                    System.err.println("Lỗi Hibernate: " + e.getMessage());
                }

                textTrangThai = "Đang khởi động dịch vụ máy chủ...";
                publish(70);
                try {
                    AppHttpServer mobileServer = new AppHttpServer();
                    mobileServer.startServer();
                } catch (Exception e) {
                    System.err.println("Lỗi Server: " + e.getMessage());
                }

                textTrangThai = "Hoàn tất!";
                publish(100);
                Thread.sleep(500);

                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                // Chạy trên luồng giao diện, dùng để update UI
                int progress = chunks.get(chunks.size() - 1);
                if (progressBar != null) {
                    progressBar.setValue(progress);
                }
                if (lblStatus != null) {
                    lblStatus.setText(" " + textTrangThai);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (splashScreen != null) splashScreen.dispose();
                new UngDung().setVisible(true);
            }
        }.execute();
    }

    private static void createSplashScreen() {
        splashScreen = new JWindow();
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        int TARGET_WIDTH = 560;
        int TARGET_HEIGHT = 385;

        // 1. Cấu hình Label trạng thái
        lblStatus = new JLabel(" Đang khởi động hệ thống...");
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 10));
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setOpaque(false);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

        // 2. Thêm ảnh Splash Screen và lồng lblStatus vào trong ảnh
        URL imgURL = UngDung.class.getResource("/icon/png/splash-screen.png");
        ImageIcon originalIcon = new ImageIcon(imgURL);
        Image scaledImage = originalIcon.getImage().getScaledInstance(TARGET_WIDTH, TARGET_HEIGHT, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setLayout(new BorderLayout());
        imageLabel.add(lblStatus, BorderLayout.SOUTH);

        contentPane.add(imageLabel, BorderLayout.CENTER);

        // 3. Progress Bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 95, 159));
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(TARGET_WIDTH, 12));

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