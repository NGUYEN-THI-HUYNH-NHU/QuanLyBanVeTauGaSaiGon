package gui.application.form.thongKe;

import connectDB.ConnectDB;
import dao.ThongKeNhanVien_DAO;
import entity.NhanVien;
import entity.type.VaiTroNhanVien; // Đã thêm import này
import gui.application.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

/**
 * Panel Thống Kê (Dashboard) - Hiển thị số liệu thống kê cuối ca cho nhân viên.
 * - Sử dụng 6 StatCard để trình bày các chỉ số chính.
 * - Tích hợp với ThongKeNhanVien_DAO để lấy dữ liệu từ CSDL.
 * - Xử lý việc tải dữ liệu trên thread nền (SwingWorker) để giữ cho UI phản hồi.
 */
public class PanelThongKe extends JPanel {

    private final NhanVien nhanVien;
    private final ThongKeNhanVien_DAO thongKeNhanVienDAO;

    // 6 ô cards hiển thị các số liệu thống kê
    private StatCard cardTongHoaDon, cardHoaDonDoiTra, cardSoVeBanDuoc;
    private StatCard cardChuyenKhoan, cardTienMat, cardTongThuDuoc;

    // Các nhãn hiển thị thông tin ca làm việc và nhân viên
    private JLabel lblTenNhanVien, lblCaLamViec, lblNgayLamViec;

    // Định dạng cho tiền tệ và số nguyên
    private final DecimalFormat currencyFormatter = new DecimalFormat("#,##0 VNĐ");
    private final DecimalFormat numberFormatter = new DecimalFormat("#,##0");

    /**
     * Constructor của PanelThongKe.
     * Khởi tạo DAO, lấy thông tin nhân viên và khởi tạo giao diện.
     */
    public PanelThongKe() {
        this.thongKeNhanVienDAO = new ThongKeNhanVien_DAO();

        // Lấy thông tin nhân viên đăng nhập từ AuthService
        NhanVien loggedInNhanVien = AuthService.getInstance().getCurrentUser();

        // Nếu chưa có nhân viên nào đăng nhập (ví dụ: chạy độc lập để test),
        // tạo một đối tượng NhanVien giả lập với dữ liệu phù hợp với constructor của bạn.
        if (loggedInNhanVien == null) {
            loggedInNhanVien = new NhanVien(
                    "NV001",                                      // nhanVienID
                    VaiTroNhanVien.NHAN_VIEN,                     // vaiTroNhanVien (ví dụ: NHAN_VIEN)
                    "Trần Thị B",                                 // hoTen
                    false,                                        // isNu (false = Nam, true = Nữ)
                    LocalDate.of(1995, 8, 20),                    // ngaySinh
                    "0912345678",                                 // soDienThoai
                    "tran.b@example.com",                         // email
                    "123 Đường ABC, Quận XYZ, TP.HCM",            // diaChi
                    LocalDate.of(2023, 1, 15),                    // ngayThamGia
                    true,                                         // isHoatDong (true: Đang hoạt động)
                    "Ca 2"                                        // caLam (ví dụ: Ca 2)
            );
            AuthService.getInstance().setCurrentUser(loggedInNhanVien);
            System.out.println("⚠️ Chạy ở chế độ TEST với nhân viên giả định.");
        }
        this.nhanVien = loggedInNhanVien;

        initComponents(); // Khởi tạo các thành phần UI
        loadDashboardData(); // Tải dữ liệu thống kê
    }

    /**
     * Khởi tạo và sắp xếp các thành phần giao diện của Panel.
     */
    private void initComponents() {
        setLayout(new BorderLayout(15, 15)); // Layout chính với khoảng cách 15px
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Viền trống 10px xung quanh
        setBackground(new Color(240, 242, 245)); // Màu nền xám nhạt

        // Phần tiêu đề và thông tin nhân viên
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Phần nội dung chính (bao gồm các thẻ thống kê và khu vực biểu đồ)
        JPanel pnlMainContent = new JPanel(new BorderLayout(15, 15));
        pnlMainContent.setOpaque(false); // Đặt trong suốt để màu nền của Panel cha hiển thị

        // Panel chứa 6 thẻ thống kê
        pnlMainContent.add(createCardPanel(), BorderLayout.NORTH);

        // Panel Placeholder cho biểu đồ
        pnlMainContent.add(createChartPanel(), BorderLayout.CENTER);

        add(pnlMainContent, BorderLayout.CENTER);
    }

    /**
     * Tạo Panel chứa tiêu đề "Thống kê cuối ca" và thông tin chi tiết nhân viên, ca làm việc, ngày làm việc.
     * @return JPanel đã được cấu hình.
     */
    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 10)); // Tiêu đề và thông tin cách nhau 10px dọc
        pnlHeader.setOpaque(false);

        // Tiêu đề chính của trang thống kê
        JLabel lblMainTitle = new JLabel("Thống kê cuối ca");
        lblMainTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblMainTitle.setForeground(new Color(30, 30, 30));
        pnlHeader.add(lblMainTitle, BorderLayout.NORTH);

        // Panel chứa các thông tin phụ như tên nhân viên, ca, ngày làm việc
        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0)); // Các thành phần nằm ngang, cách nhau 20px
        pnlInfo.setOpaque(false);

        // Tên nhân viên
        pnlInfo.add(new JLabel("Tên nhân viên:"));
        lblTenNhanVien = new JLabel(nhanVien != null ? nhanVien.getHoTen() : "Không xác định");
        lblTenNhanVien.setFont(new Font("Arial", Font.BOLD, 13));
        pnlInfo.add(lblTenNhanVien);

        // Ca làm việc (sẽ được cập nhật sau khi tải dữ liệu)
        pnlInfo.add(new JLabel("Ca làm việc:"));
        lblCaLamViec = new JLabel("Đang tải...");
        lblCaLamViec.setFont(new Font("Arial", Font.BOLD, 13));
        pnlInfo.add(lblCaLamViec);

        // Ngày làm việc (sẽ được cập nhật sau khi tải dữ liệu)
        pnlInfo.add(new JLabel("Ngày làm việc:"));
        lblNgayLamViec = new JLabel("Đang tải...");
        lblNgayLamViec.setFont(new Font("Arial", Font.BOLD, 13));
        pnlInfo.add(lblNgayLamViec);

        pnlHeader.add(pnlInfo, BorderLayout.CENTER);

        return pnlHeader;
    }

    /**
     * Tạo Panel chứa 6 thẻ thống kê (StatCard) với bố cục GridLayout.
     * @return JPanel chứa các thẻ thống kê.
     */
    private JPanel createCardPanel() {
        JPanel pnlCards = new JPanel(new GridLayout(2, 3, 15, 15)); // 2 dòng, 3 cột, khoảng cách 15px
        pnlCards.setOpaque(false);

        // Khởi tạo 6 thẻ thống kê với trạng thái "Đang tải..." ban đầu
        cardTongHoaDon = new StatCard("Tổng hóa đơn bán được", "Đang tải...", "hóa đơn");
        cardHoaDonDoiTra = new StatCard("Tổng hóa đơn đổi trả", "Đang tải...", "hóa đơn");
        cardSoVeBanDuoc = new StatCard("Tổng số vé bán được", "Đang tải...", "vé");

        cardChuyenKhoan = new StatCard("Tổng chuyển khoản", "Đang tải...", "VNĐ");
        cardTienMat = new StatCard("Tổng tiền mặt (Hệ thống)", "Đang tải...", "VNĐ");
        cardTongThuDuoc = new StatCard("Tổng tiền thu được", "Đang tải...", "VNĐ");

        // Thêm các thẻ vào Panel
        pnlCards.add(cardTongHoaDon);
        pnlCards.add(cardHoaDonDoiTra);
        pnlCards.add(cardSoVeBanDuoc);
        pnlCards.add(cardChuyenKhoan);
        pnlCards.add(cardTienMat);
        pnlCards.add(cardTongThuDuoc);

        return pnlCards;
    }

    /**
     * Tạo Panel placeholder cho khu vực biểu đồ.
     * @return JPanel placeholder.
     */
    private JPanel createChartPanel() {
        JPanel pnlChart = new JPanel(new BorderLayout());
        pnlChart.setOpaque(true);
        pnlChart.setBackground(Color.WHITE); // Nền trắng cho khu vực biểu đồ
        pnlChart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Viền mỏng
                new EmptyBorder(15, 15, 15, 15) // Padding bên trong
        ));

        // Tiêu đề nhỏ cho phần biểu đồ
        JLabel lblChartSectionTitle = new JLabel("Biểu đồ");
        lblChartSectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblChartSectionTitle.setBorder(new EmptyBorder(0,0,10,0));
        pnlChart.add(lblChartSectionTitle, BorderLayout.NORTH);

        // Nhãn placeholder hiển thị thông báo khu vực biểu đồ
        JLabel lblPlaceholder = new JLabel("Biểu đồ (ví dụ: Doanh thu theo giờ) sẽ hiển thị ở đây", SwingConstants.CENTER);
        lblPlaceholder.setFont(new Font("Arial", Font.ITALIC, 14));
        lblPlaceholder.setForeground(new Color(150, 150, 150));
        pnlChart.add(lblPlaceholder, BorderLayout.CENTER);

        return pnlChart;
    }

    /**
     * Lớp tĩnh để đóng gói kết quả thống kê từ thread nền.
     */
    private static class ThongKeResult {
        int tongHoaDonBan;
        int tongHoaDonDoiTra;
        int tongSoVeBan;
        double tongTienChuyenKhoan;
        double tongTienMat;
        double tongThuDuoc;
        String caLamViecText;
        LocalDate ngayLamViecDate;
    }

    /**
     * Tải dữ liệu thống kê từ DAO và cập nhật giao diện.
     * Sử dụng SwingWorker để thực hiện tác vụ CSDL trên một thread nền, tránh làm đơ UI.
     */
    private void loadDashboardData() {
        // Hiển thị trạng thái "Đang tải..." trên tất cả các nhãn và card
        lblCaLamViec.setText("Đang tải...");
        lblNgayLamViec.setText("Đang tải...");
        cardTongHoaDon.setValue("Đang tải...");
        cardHoaDonDoiTra.setValue("Đang tải...");
        cardSoVeBanDuoc.setValue("Đang tải...");
        cardChuyenKhoan.setValue("Đang tải...");
        cardTienMat.setValue("Đang tải...");
        cardTongThuDuoc.setValue("Đang tải...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // Hiển thị con trỏ chờ

        // Xác định ngày và thời gian hiện tại
        LocalDate currentDay = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // --- LOGIC XÁC ĐỊNH CA LÀM VIỆC (Cần điều chỉnh theo logic nghiệp vụ của bạn) ---
        String caLamViecText = "Ngoài ca làm việc"; // Mặc định
        LocalTime gioBatDauCa = LocalTime.MIN; // Mặc định từ đầu ngày
        LocalTime gioKetThucCa = LocalTime.MAX; // Mặc định đến cuối ngày

        // Ví dụ logic cho 2 ca: Ca 1 (8h-16h), Ca 2 (16h-22h)
        if (currentTime.isAfter(LocalTime.of(8, 0)) && currentTime.isBefore(LocalTime.of(16, 0))) {
            caLamViecText = "Ca 1 (08:00 - 16:00)";
            gioBatDauCa = LocalTime.of(8, 0);
            gioKetThucCa = LocalTime.of(16, 0).minusSeconds(1); // Kết thúc trước 16:00
        } else if (currentTime.isAfter(LocalTime.of(16, 0)) && currentTime.isBefore(LocalTime.of(22, 0))) {
            caLamViecText = "Ca 2 (16:00 - 22:00)";
            gioBatDauCa = LocalTime.of(16, 0);
            gioKetThucCa = LocalTime.of(22, 0).minusSeconds(1); // Kết thúc trước 22:00
        }
        // --- KẾT THÚC LOGIC CA LÀM VIỆC ---

        // Lưu trữ các giá trị final để sử dụng trong SwingWorker
        final String finalMaNhanVien = nhanVien.getNhanVienID(); // Lấy từ đối tượng NhanVien của bạn
        final LocalDate finalNgayLamViec = currentDay;
        final LocalTime finalGioBatDauCa = gioBatDauCa;
        final LocalTime finalGioKetThucCa = gioKetThucCa;
        final String finalCaLamViecText = caLamViecText;

        // Khởi tạo SwingWorker để thực hiện các thao tác CSDL
        SwingWorker<ThongKeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeResult doInBackground() throws Exception {
                // Thực hiện các truy vấn CSDL trên thread nền
                ThongKeResult result = new ThongKeResult();

                result.tongHoaDonBan = thongKeNhanVienDAO.getTongSoHoaDonBanDuoc(finalMaNhanVien, finalNgayLamViec, finalGioBatDauCa, finalGioKetThucCa);
                result.tongHoaDonDoiTra = thongKeNhanVienDAO.getTongSoHoaDonDoiTra(finalMaNhanVien, finalNgayLamViec, finalGioBatDauCa, finalGioKetThucCa);
                result.tongSoVeBan = thongKeNhanVienDAO.getTongSoVeBanDuoc(finalMaNhanVien, finalNgayLamViec, finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienChuyenKhoan = thongKeNhanVienDAO.getTongTienChuyenKhoan(finalMaNhanVien, finalNgayLamViec, finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienMat = thongKeNhanVienDAO.getTongTienMat(finalMaNhanVien, finalNgayLamViec, finalGioBatDauCa, finalGioKetThucCa);
                result.tongThuDuoc = result.tongTienChuyenKhoan + result.tongTienMat;

                result.caLamViecText = finalCaLamViecText;
                result.ngayLamViecDate = finalNgayLamViec;

                return result;
            }

            @Override
            protected void done() {
                // Trở lại thread sự kiện của Swing để cập nhật UI
                setCursor(Cursor.getDefaultCursor()); // Đặt lại con trỏ chuột
                try {
                    ThongKeResult result = get(); // Lấy kết quả từ doInBackground

                    // Cập nhật thông tin header
                    lblCaLamViec.setText(result.caLamViecText);
                    lblNgayLamViec.setText(result.ngayLamViecDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                    // Cập nhật các thẻ thống kê với dữ liệu từ CSDL
                    cardTongHoaDon.setValue(numberFormatter.format(result.tongHoaDonBan));
                    cardHoaDonDoiTra.setValue(numberFormatter.format(result.tongHoaDonDoiTra));
                    cardSoVeBanDuoc.setValue(numberFormatter.format(result.tongSoVeBan));
                    cardChuyenKhoan.setValue(currencyFormatter.format(result.tongTienChuyenKhoan));
                    cardTienMat.setValue(currencyFormatter.format(result.tongTienMat));
                    cardTongThuDuoc.setValue(currencyFormatter.format(result.tongThuDuoc));

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelThongKe.this, "Lỗi khi tải dữ liệu thống kê: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    // Cập nhật UI để hiển thị lỗi nếu có
                    lblCaLamViec.setText("Lỗi");
                    lblNgayLamViec.setText("Lỗi");
                    cardTongHoaDon.setValue("Lỗi");
                    cardHoaDonDoiTra.setValue("Lỗi");
                    cardSoVeBanDuoc.setValue("Lỗi");
                    cardChuyenKhoan.setValue("Lỗi");
                    cardTienMat.setValue("Lỗi");
                    cardTongThuDuoc.setValue("Lỗi");
                }
            }
        };
        worker.execute(); // Chạy SwingWorker
    }

    // =========================================================================
    // INNER CLASS: StatCard (Thẻ thống kê)
    // Lớp này định nghĩa giao diện cho mỗi ô hiển thị số liệu thống kê.
    // =========================================================================
    private class StatCard extends JPanel {
        private JLabel lblValue; // Nhãn hiển thị giá trị số liệu
        private JLabel lblUnit;  // Nhãn hiển thị đơn vị (ví dụ: "hóa đơn", "VNĐ")

        /**
         * Constructor của StatCard.
         * @param title Tiêu đề của thẻ (ví dụ: "Tổng hóa đơn bán được").
         * @param initialValue Giá trị ban đầu hiển thị (ví dụ: "0" hoặc "Đang tải...").
         * @param unit Đơn vị của giá trị (ví dụ: "hóa đơn", "VNĐ").
         */
        public StatCard(String title, String initialValue, String unit) {
            setLayout(new BorderLayout(5, 5)); // Bố cục với khoảng cách 5px
            setBackground(Color.WHITE); // Nền thẻ màu trắng
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Viền mỏng màu xám
                    new EmptyBorder(15, 15, 15, 15) // Padding bên trong thẻ
            ));

            // Nhãn tiêu đề của thẻ
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
            lblTitle.setForeground(new Color(100, 100, 100)); // Màu chữ xám
            add(lblTitle, BorderLayout.NORTH);

            // Panel chứa giá trị và đơn vị, sắp xếp FlowLayout căn trái
            JPanel pnlValueUnit = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            pnlValueUnit.setOpaque(false);

            // Nhãn hiển thị giá trị số liệu
            lblValue = new JLabel(initialValue);
            lblValue.setFont(new Font("Arial", Font.BOLD, 18));
            lblValue.setForeground(new Color(50, 50, 50)); // Màu chữ đậm
            pnlValueUnit.add(lblValue);

            // Nhãn hiển thị đơn vị (nếu có)
            if (unit != null && !unit.isEmpty()) {
                lblUnit = new JLabel(unit);
                lblUnit.setFont(new Font("Arial", Font.PLAIN, 12));
                lblUnit.setForeground(new Color(120, 120, 120));
                pnlValueUnit.add(lblUnit);
            }

            add(pnlValueUnit, BorderLayout.SOUTH);
        }

        /**
         * Cập nhật giá trị hiển thị trên thẻ.
         * @param value Chuỗi giá trị mới.
         */
        public void setValue(String value) {
            lblValue.setText(value);
        }
    }

    /**
     * Hàm main để chạy thử PanelThongKe độc lập.
     * Đảm bảo kết nối CSDL trước khi chạy.
     */
    public static void main(String[] args) {
        // --- QUAN TRỌNG: Kết nối CSDL trước khi khởi tạo Panel ---
        ConnectDB.getInstance().connect();
        if (ConnectDB.getInstance().getConnection() == null) {
            System.err.println("Không thể kết nối CSDL. Vui lòng kiểm tra cấu hình ConnectDB.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Bán vé Tàu Ga Sài Gòn");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainAppPanel = new JPanel(new BorderLayout());
            mainAppPanel.setBackground(new Color(240, 242, 245));

            // 1. Giả lập Menu bên trái (Sidebar)
            JPanel pnlMenu = new JPanel();
            pnlMenu.setBackground(new Color(34, 49, 63)); // Màu xanh đậm
            pnlMenu.setPreferredSize(new Dimension(200, 0)); // Rộng 200px
            pnlMenu.setLayout(new BorderLayout());

            JLabel lblLogo = new JLabel("Ga Sài Gòn", SwingConstants.CENTER);
            lblLogo.setForeground(Color.WHITE);
            lblLogo.setFont(new Font("Arial", Font.BOLD, 20));
            lblLogo.setBorder(new EmptyBorder(10,0,20,0));
            pnlMenu.add(lblLogo, BorderLayout.NORTH);

            JPanel menuItems = new JPanel(new GridLayout(0, 1, 0, 5));
            menuItems.setOpaque(false);
            String[] menuNames = {"Quản lý", "Bán vé", "Quản lý vé", "Quản lý hóa đơn", "Quản lý khách hàng", "Thống kê & Báo cáo", "About", "Trợ giúp", "Đăng xuất"};
            for (String name : menuNames) {
                JButton btn = new JButton(name);
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setBackground(new Color(34, 49, 63));
                btn.setForeground(Color.WHITE);
                btn.setBorder(new EmptyBorder(10, 20, 10, 20));
                btn.setFocusPainted(false);
                btn.setFont(new Font("Arial", Font.PLAIN, 14));
                if (name.equals("Thống kê & Báo cáo")) {
                    btn.setBackground(new Color(52, 73, 94)); // Highlight mục đang chọn
                }
                menuItems.add(btn);
            }
            pnlMenu.add(menuItems, BorderLayout.CENTER);

            mainAppPanel.add(pnlMenu, BorderLayout.WEST);

            // 2. Thêm Panel Thống Kê (nội dung bên phải)
            PanelThongKe pnlThongKe = new PanelThongKe();
            mainAppPanel.add(pnlThongKe, BorderLayout.CENTER);

            frame.setContentPane(mainAppPanel);
            frame.setSize(1200, 700); // Kích thước cửa sổ mặc định
            frame.setLocationRelativeTo(null); // Hiển thị giữa màn hình
            frame.setVisible(true);
        });
    }
}