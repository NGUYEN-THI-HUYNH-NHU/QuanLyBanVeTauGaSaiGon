package gui.application.form.thongKe;

import connectDB.ConnectDB;
import dao.ThongKeNhanVien_DAO;
import entity.NhanVien;
import entity.type.VaiTroNhanVien;
import gui.application.AuthService;
import gui.application.form.thongKe.StatCard;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Panel Thống Kê (Dashboard) - Hiển thị số liệu thống kê cuối ca cho nhân viên.
 */
public class PanelThongKe extends JPanel {

    // ====== FIELD CHÍNH ======
    private final NhanVien nhanVien;
    private final ThongKeNhanVien_DAO thongKeNhanVienDAO;

    // 6 ô cards hiển thị các số liệu thống kê
    private StatCard cardTongHoaDon, cardHoaDonDoiTra, cardSoVeBanDuoc;
    private StatCard cardChuyenKhoan, cardTienMat, cardTongThuDuoc;

    // TRƯỜNG MỚI: Thêm cờ và giá trị tiền mặt tại két
    private boolean isTienMatEntered = false;
    private double tienMatTaiKetValue = 0.0;

    // Các nhãn hiển thị thông tin ca làm việc và nhân viên
    private JLabel lblTenNhanVien, lblCaLamViec, lblNgayLamViec;

    // Thành phần biểu đồ và bảng
    private RevenueChartPanel revenueChartPanel;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;

    private JLabel lblTenNV_Report;
    private JLabel lblCaLV_Report;
    private JLabel lblNgayLV_Report;

    // THÀNH PHẦN MỚI: Các JLabel cho khu vực tổng kết dưới bảng
    private JLabel lblTongTienMatKet, lblTongCKReport, lblTongTTHuyetThong, lblTongThuReport;
    private JLabel lblTongValue; // Tổng tiền ở hàng 'Tổng'
    private JLabel lblTongTienHienTai;
    private JLabel lblTongTienChenhLenh;
    private JTextArea txtGhiChuReport;

    // Định dạng cho tiền tệ và số nguyên
    private final DecimalFormat currencyFormatter = new DecimalFormat("#,##0 VNĐ");
    private final DecimalFormat numberFormatter = new DecimalFormat("#,##0");

    /**
     * Constructor của PanelThongKe.
     */
    public PanelThongKe() {
        this.thongKeNhanVienDAO = new ThongKeNhanVien_DAO();

        // Lấy nhân viên hiện tại từ AuthService
        NhanVien current = AuthService.getInstance().getCurrentUser();
        if (current == null) {
            throw new IllegalStateException("Chưa có nhân viên đăng nhập! Hãy setCurrentUser trước khi mở PanelThongKe.");
        }
        this.nhanVien = current;

        initComponents();
        loadDashboardData();
    }

    /**
     * Khởi tạo và sắp xếp các thành phần giao diện của Panel.
     */
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        // Khởi tạo các JLabel dùng chung
        lblTenNhanVien = new JLabel(
                (nhanVien.getHoTen() != null && !nhanVien.getHoTen().isBlank())
                        ? nhanVien.getHoTen()
                        : "Không xác định"
        );
        lblTenNhanVien.setFont(new Font("Arial", Font.BOLD, 13));

        lblCaLamViec = new JLabel("Đang tải...");
        lblCaLamViec.setFont(new Font("Arial", Font.BOLD, 13));

        lblNgayLamViec = new JLabel("Đang tải...");
        lblNgayLamViec.setFont(new Font("Arial", Font.BOLD, 13));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Nội dung chính
        JPanel pnlMainContent = new JPanel(new BorderLayout(15, 15));
        pnlMainContent.setOpaque(false);

        pnlMainContent.add(createCardPanel(), BorderLayout.NORTH);
        pnlMainContent.add(createTabbedPanel(), BorderLayout.CENTER);

        add(pnlMainContent, BorderLayout.CENTER);
    }

    /**
     * Panel tiêu đề + thông tin nhân viên.
     */
    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 10));
        pnlHeader.setOpaque(false);

        JLabel lblMainTitle = new JLabel("Thống kê cuối ca");
        lblMainTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblMainTitle.setForeground(new Color(30, 30, 30));
        pnlHeader.add(lblMainTitle, BorderLayout.NORTH);

        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlInfo.setOpaque(false);

        pnlInfo.add(new JLabel("Tên nhân viên:"));
        pnlInfo.add(lblTenNhanVien);

        pnlInfo.add(new JLabel("Ca làm việc:"));
        pnlInfo.add(lblCaLamViec);

        pnlInfo.add(new JLabel("Ngày làm việc:"));
        pnlInfo.add(lblNgayLamViec);

        pnlHeader.add(pnlInfo, BorderLayout.CENTER);

        return pnlHeader;
    }
    /**
     * Panel 6 card thống kê.
     */
    private JPanel createCardPanel() {
        JPanel pnlCards = new JPanel(new GridLayout(2, 3, 15, 15));
        pnlCards.setOpaque(false);

        cardTongHoaDon = new StatCard("Tổng hóa đơn bán được", "Đang tải...", "hóa đơn");
        cardHoaDonDoiTra = new StatCard("Tổng hóa đơn đổi trả", "Đang tải...", "hóa đơn");
        cardSoVeBanDuoc = new StatCard("Tổng số vé bán được", "Đang tải...", "vé");

        cardChuyenKhoan = new StatCard("Tổng chuyển khoản", "Đang tải...", "VNĐ");
        cardTienMat = new StatCard("Tổng tiền mặt (Hệ thống)", "Đang tải...", "VNĐ");
        cardTongThuDuoc = new StatCard("Tổng tiền thu được", "Đang tải...", "VNĐ");

        pnlCards.add(cardTongHoaDon);
        pnlCards.add(cardHoaDonDoiTra);
        pnlCards.add(cardSoVeBanDuoc);
        pnlCards.add(cardChuyenKhoan);
        pnlCards.add(cardTienMat);
        pnlCards.add(cardTongThuDuoc);

        return pnlCards;
    }

    /**
     * Tab gồm: Biểu đồ – Bảng báo cáo chi tiết.
     */
    private JTabbedPane createTabbedPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("Biểu đồ Doanh thu", createChartTabPanel());
        tabbedPane.addTab("Bảng báo cáo chi tiết", createReportTabPanel());

        return tabbedPane;
    }

    /**
     * Tab biểu đồ hình tròn doanh thu.
     */
    private JPanel createChartTabPanel() {
        JPanel pnlChart = new JPanel(new BorderLayout());
        pnlChart.setOpaque(true);
        pnlChart.setBackground(Color.WHITE);
        pnlChart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblChartSectionTitle = new JLabel(
                "Cơ cấu Doanh thu theo Hình thức Thanh toán (Tiền mặt vs Chuyển khoản)");
        lblChartSectionTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblChartSectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlChart.add(lblChartSectionTitle, BorderLayout.NORTH);

        revenueChartPanel = new RevenueChartPanel();
        pnlChart.add(revenueChartPanel, BorderLayout.CENTER);

        return pnlChart;
    }

    /**
     * Tab bảng báo cáo chi tiết.
     */
    private JPanel createReportTabPanel() {
        JPanel pnlReport = new JPanel(new BorderLayout());
        pnlReport.setBackground(new Color(240, 242, 245));
        pnlReport.setBorder(new EmptyBorder(10, 10, 10, 10));

        // LABEL REPORT TAB
        lblTenNV_Report = new JLabel("Đang tải...");
        lblCaLV_Report = new JLabel("Đang tải...");
        lblNgayLV_Report = new JLabel("Đang tải...");

        JPanel pnlTitleContainer = new JPanel(new BorderLayout());
        pnlTitleContainer.setOpaque(false);
        pnlTitleContainer.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblMainTitle = new JLabel("BÁO CÁO CHI TIẾT DANH SÁCH HÓA ĐƠN TRONG CA", SwingConstants.CENTER);
        lblMainTitle.setFont(new Font("Arial", Font.BOLD, 16));
        pnlTitleContainer.add(lblMainTitle, BorderLayout.NORTH);

        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        pnlInfo.setOpaque(false);

        pnlInfo.add(new JLabel("Nhân viên:"));
        pnlInfo.add(lblTenNV_Report);
        pnlInfo.add(new JLabel("Ca làm việc:"));
        pnlInfo.add(lblCaLV_Report);
        pnlInfo.add(new JLabel("Ngày làm việc:"));
        pnlInfo.add(lblNgayLV_Report);

        pnlTitleContainer.add(pnlInfo, BorderLayout.CENTER);

        pnlReport.add(pnlTitleContainer, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"STT", "Mã HĐ", "Thời Điểm Tạo", "Hình Thức TT", "Trạng Thái", "Tổng Tiền"};
        reportTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable reportTable = new JTable(reportTableModel);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 12));
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Panel chứa Bảng và khu vực Tổng kết
        JPanel pnlCenterContent = new JPanel(new BorderLayout());
        pnlCenterContent.add(scrollPane, BorderLayout.CENTER);

        // ====================== PANEL TỔNG KẾT MỚI ======================
        JPanel pnlSummary = new JPanel(new BorderLayout(0, 10));
        pnlSummary.setOpaque(false);
        pnlSummary.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Hàng 1: Tổng cộng tiền (Giống hàng cuối của JTable)
        JLabel lblTongTitle = new JLabel("Tổng");
        lblTongTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTitle.setPreferredSize(new Dimension(80, 25));

        lblTongValue = new JLabel("0 VNĐ"); // Sẽ là tổng thu được
        lblTongValue.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongValue.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel pnlTongRow = new JPanel(new BorderLayout());
        pnlTongRow.setOpaque(true);
        pnlTongRow.setBackground(new Color(235, 235, 235));
        pnlTongRow.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        pnlTongRow.add(lblTongTitle, BorderLayout.WEST);
        pnlTongRow.add(lblTongValue, BorderLayout.CENTER);
        pnlSummary.add(pnlTongRow, BorderLayout.NORTH);

        // Hàng 2: Chi tiết các khoản (GridLayout 6x2)
        JPanel pnlDetails = new JPanel(new GridLayout(6, 2, 10, 5));
        pnlDetails.setOpaque(false);
        pnlDetails.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Khởi tạo các label chi tiết
        lblTongTTHuyetThong = new JLabel("0 VNĐ");
        lblTongCKReport = new JLabel("0 VNĐ");
        lblTongThuReport = new JLabel("0 VNĐ");
        lblTongTienMatKet = new JLabel("0 VNĐ");
        lblTongTienHienTai = new JLabel("0 VNĐ");
        lblTongTienChenhLenh = new JLabel("0 VNĐ");
        txtGhiChuReport = new JTextArea(3, 10);

        lblTongTienMatKet.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTienChenhLenh.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongTienHienTai.setFont(new Font("Arial", Font.BOLD, 14));

        // Dòng 1: Tiền mặt Hệ thống
        pnlDetails.add(new JLabel("Tổng tiền mặt (Hệ thống):"));
        pnlDetails.add(lblTongTTHuyetThong);

        // Dòng 2: Tiền chuyển khoản
        pnlDetails.add(new JLabel("Tổng tiền chuyển khoản:"));
        pnlDetails.add(lblTongCKReport);

        // Dòng 3: Tổng doanh thu trên hệ thống (B)
        pnlDetails.add(new JLabel("Tổng doanh thu trên hệ thống(B):"));
        pnlDetails.add(lblTongThuReport);

        // Dòng 4: Tiền mặt tại két
        pnlDetails.add(new JLabel("Tổng tiền mặt tại két:"));
        pnlDetails.add(lblTongTienMatKet);

        // Dòng 5: Tổng doanh thu hiện tại (A) (Tổng tiền mặt két + Tổng chuyển khoản)
        pnlDetails.add(new JLabel("Tổng doanh thu hiện tại (A):"));
        pnlDetails.add(lblTongTienHienTai);

        // Dòng 6: Chênh lệch (B - A)
        pnlDetails.add(new JLabel("Chênh lệnh (B-A):"));
        pnlDetails.add(lblTongTienChenhLenh);


        // Ghi chú (Đặt ở dòng 7, sử dụng thêm một panel nhỏ để JTextArea không bị kéo căng)
        JPanel pnlGhiChuContainer = new JPanel(new BorderLayout(5, 0));
        pnlGhiChuContainer.setOpaque(false);
        txtGhiChuReport.setLineWrap(true);
        txtGhiChuReport.setWrapStyleWord(true);
        txtGhiChuReport.setEditable(false);

        // Gắn Ghi Chú vào container
        pnlGhiChuContainer.add(new JLabel("Ghi Chú:"), BorderLayout.NORTH);
        pnlGhiChuContainer.add(new JScrollPane(txtGhiChuReport), BorderLayout.CENTER);


        // Cần thêm một container để Ghi chú chiếm hết chiều rộng
        JPanel pnlGhiChuFullWidth = new JPanel(new GridLayout(1, 1));
        pnlGhiChuFullWidth.setOpaque(false);
        pnlGhiChuFullWidth.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pnlGhiChuFullWidth.add(pnlGhiChuContainer);


        // Cấu trúc lại để Ghi chú hiển thị tốt hơn, nằm dưới 6 dòng:
        JPanel pnlCombinedDetails = new JPanel(new BorderLayout(0, 5));
        pnlCombinedDetails.setOpaque(false);
        pnlCombinedDetails.add(pnlDetails, BorderLayout.NORTH);
        pnlCombinedDetails.add(pnlGhiChuFullWidth, BorderLayout.CENTER);

        pnlSummary.add(pnlCombinedDetails, BorderLayout.CENTER);


        pnlCenterContent.add(pnlSummary, BorderLayout.SOUTH);
        pnlReport.add(pnlCenterContent, BorderLayout.CENTER); // Đặt panel chứa bảng và summary vào giữa

// ====================== PANEL NÚT DƯỚI ======================
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);

// ===== NÚT NHẬP TIỀN MẶT (TRÁI) =====
        JPanel pnlLeftButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlLeftButton.setOpaque(false);

        JButton btnNhapTienMat = new JButton("Nhập tiền mặt");
        btnNhapTienMat.setPreferredSize(new Dimension(130, 30));
        btnNhapTienMat.setBackground(new Color(70, 130, 180));
        btnNhapTienMat.setForeground(Color.WHITE);
        btnNhapTienMat.setFont(new Font("Arial", Font.BOLD, 12));

// Listener cho nút Nhập tiền mặt
        btnNhapTienMat.addActionListener(e -> {
            // Giả định: PanelBaoCao tồn tại và có constructor này
            PanelBaoCao baoCaoPanel = new PanelBaoCao(
                    nhanVien.getHoTen(),                     // tên NV
                    lblCaLamViec.getText(),                  // ca làm việc
                    lblNgayLamViec.getText(),                // ngày làm việc
                    cardTienMat.getNumericValue()          // doanh thu tiền mặt hệ thống (A trong PanelBaoCao)
            );

            JDialog dialog = new JDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Lập Báo Cáo Giao Ca", true
            );

            // CẬP NHẬT TRẠNG THÁI VÀ GIÁ TRỊ SAU KHI ĐÓNG DIALOG
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    // LẤY GIÁ TRỊ TIỀN MẶT ĐÃ ĐƯỢC LƯU VÀ GHI CHÚ
                    if (baoCaoPanel.isGiaoCaConfirmed()) {
                        double value = baoCaoPanel.getTienMatThucTeDaNhap();
                        String ghiChu = baoCaoPanel.getGhiChuDaNhap();

                        tienMatTaiKetValue = value;
                        isTienMatEntered = true;

                        txtGhiChuReport.setText(ghiChu); // CẬP NHẬT GHI CHÚ

                        updateSummaryPanel(); // GỌI CẬP NHẬT HIỂN THỊ
                    } else {
                        isTienMatEntered = false;
                    }
                }
            });

            dialog.setContentPane(baoCaoPanel);
            dialog.setSize(1000, 700);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });

        pnlLeftButton.add(btnNhapTienMat);

// ===== NÚT XUẤT FILE (PHẢI) - CÓ RÀNG BUỘC KIỂM TRA NHẬP TIỀN MẶT =====
        JPanel pnlRightButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlRightButton.setOpaque(false);

        JButton btnExport = new JButton("Xuất File Báo Cáo");
        btnExport.setPreferredSize(new Dimension(160, 30));
        btnExport.setBackground(new Color(255, 153, 51));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Arial", Font.BOLD, 12));

// Thêm Ràng buộc
        btnExport.addActionListener(ev -> {
            if (!isTienMatEntered) {
                JOptionPane.showMessageDialog(
                        PanelThongKe.this,
                        "Bạn phải nhập tiền mặt thực tế trước khi xuất báo cáo cuối ca.",
                        "Yêu cầu",
                        JOptionPane.WARNING_MESSAGE);
                return; // Ngăn không cho xuất báo cáo
            }
            exportToExcel();
        });

        pnlRightButton.add(btnExport);

// ===== GẮN VÀO BOTTOM =====
        pnlBottom.add(pnlLeftButton, BorderLayout.WEST);
        pnlBottom.add(pnlRightButton, BorderLayout.EAST);

        pnlReport.add(pnlBottom, BorderLayout.SOUTH);

        return pnlReport;
    }

    /**
     * Cập nhật khu vực hiển thị Tổng kết (dưới bảng)
     */
    private void updateSummaryPanel() {
        if (lblTongTienMatKet != null && cardTongThuDuoc != null) {
            double cashSystem = cardTienMat.getNumericValue();
            double transferSystem = cardChuyenKhoan.getNumericValue();
            double totalSystem = cardTongThuDuoc.getNumericValue();

            // 1. Tính toán
            // Tổng doanh thu hiện tại (A) = Tiền mặt tại két + Tổng chuyển khoản
            double totalCurrent = tienMatTaiKetValue + transferSystem;
            // Chênh lệch (B - A) = Tổng thu hệ thống - Tổng hiện tại
            double difference = totalSystem - totalCurrent;

            // 2. Cập nhật Hàng Tổng (Giá trị tổng thu được trên hệ thống)
            lblTongValue.setText(currencyFormatter.format(totalSystem).replace(" VNĐ", ""));

            // 3. Cập nhật Chi tiết

            // Tiền mặt (Hệ thống)
            lblTongTTHuyetThong.setText(currencyFormatter.format(cashSystem).replace(" VNĐ", ""));
            // Tiền chuyển khoản
            lblTongCKReport.setText(currencyFormatter.format(transferSystem).replace(" VNĐ", ""));
            // Tổng thu được (trên hệ thống) B
            lblTongThuReport.setText(currencyFormatter.format(totalSystem).replace(" VNĐ", ""));

            // TIỀN MẶT TẠI KÉT (Giá trị mới nhập)
            lblTongTienMatKet.setText(currencyFormatter.format(tienMatTaiKetValue).replace(" VNĐ", ""));

            // TỔNG HIỆN TẠI (A)
            lblTongTienHienTai.setText(currencyFormatter.format(totalCurrent).replace(" VNĐ", ""));

            // CHÊNH LỆCH (B - A)
            lblTongTienChenhLenh.setText(currencyFormatter.format(difference).replace(" VNĐ", ""));

            // Đổi màu chênh lệch
            if (difference < 0) {
                lblTongTienChenhLenh.setForeground(Color.RED);
            } else if (difference > 0) {
                lblTongTienChenhLenh.setForeground(new Color(0, 102, 0)); // Màu xanh lá cây
            } else {
                lblTongTienChenhLenh.setForeground(Color.BLACK);
            }

            revalidate();
            repaint();
        }
    }


    // =========================================================================
    // DATA LOADING LOGIC
    // =========================================================================

    private static class ThongKeResult {
        int tongHoaDonBan;
        int tongHoaDonDoiTra;
        int tongSoVeBan;
        double tongTienChuyenKhoan;
        double tongTienMat;
        double tongThuDuoc;
        String caLamViecText;
        LocalDate ngayLamViecDate;
        List<Object[]> danhSachHoaDonChiTiet;
    }

    /**
     * Tải dữ liệu thống kê và cập nhật UI.
     */
    private void loadDashboardData() {
        lblCaLamViec.setText("Đang tải...");
        lblNgayLamViec.setText("Đang tải...");
        cardTongHoaDon.setValue("Đang tải...");
        cardHoaDonDoiTra.setValue("Đang tải...");
        cardSoVeBanDuoc.setValue("Đang tải...");
        cardChuyenKhoan.setValue("Đang tải...");
        cardTienMat.setValue("Đang tải...");
        cardTongThuDuoc.setValue("Đang tải...");
        if (reportTableModel != null) {
            reportTableModel.setRowCount(0);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final LocalDate currentDay = LocalDate.now();
        final LocalTime currentTime = LocalTime.now();

        String tempCaLamViecText = "Ngoài ca làm việc";
        LocalTime tempGioBatDauCa = LocalTime.MIN;
        LocalTime tempGioKetThucCa = LocalTime.MAX;

        if (currentTime.isAfter(LocalTime.of(8, 0)) && currentTime.isBefore(LocalTime.of(16, 0))) {
            tempCaLamViecText = "Ca 1 (08:00 - 16:00)";
            tempGioBatDauCa = LocalTime.of(8, 0);
            tempGioKetThucCa = LocalTime.of(16, 0).minusSeconds(1);
        } else if (currentTime.isAfter(LocalTime.of(16, 0)) && currentTime.isBefore(LocalTime.of(22, 0))) {
            tempCaLamViecText = "Ca 2 (16:00 - 22:00)";
            tempGioBatDauCa = LocalTime.of(16, 0);
            tempGioKetThucCa = LocalTime.of(22, 0).minusSeconds(1);
        }

        final String finalCaLamViecText = tempCaLamViecText;
        final LocalTime finalGioBatDauCa = tempGioBatDauCa;
        final LocalTime finalGioKetThucCa = tempGioKetThucCa;
        final String finalMaNhanVien = nhanVien.getNhanVienID();

        SwingWorker<ThongKeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeResult doInBackground() throws Exception {
                ThongKeResult result = new ThongKeResult();

                result.tongHoaDonBan = thongKeNhanVienDAO.getTongSoHoaDonBanDuoc(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongHoaDonDoiTra = thongKeNhanVienDAO.getTongSoHoaDonDoiTra(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongSoVeBan = thongKeNhanVienDAO.getTongSoVeBanDuoc(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienChuyenKhoan = thongKeNhanVienDAO.getTongTienChuyenKhoan(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienMat = thongKeNhanVienDAO.getTongTienMat(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);

                result.danhSachHoaDonChiTiet = thongKeNhanVienDAO.getListHoaDonTrongCa(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);

                result.tongThuDuoc = result.tongTienChuyenKhoan + result.tongTienMat;
                result.caLamViecText = finalCaLamViecText;
                result.ngayLamViecDate = currentDay;

                return result;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeResult result = get();

                    // ===== CẬP NHẬT THÔNG TIN NHÂN VIÊN + CA LÀM VIỆC =====
                    String tenNV = (nhanVien.getHoTen() != null && !nhanVien.getHoTen().isBlank())
                            ? nhanVien.getHoTen()
                            : "Không xác định";
                    lblTenNhanVien.setText(nhanVien.getHoTen());
                    lblCaLamViec.setText(result.caLamViecText);
                    lblNgayLamViec.setText(result.ngayLamViecDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

// CẬP NHẬT TAB BÁO CÁO CHI TIẾT
                    lblTenNV_Report.setText(nhanVien.getHoTen());
                    lblCaLV_Report.setText(result.caLamViecText);
                    lblNgayLV_Report.setText(result.ngayLamViecDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));


                    // ===== CẬP NHẬT 6 CARD =====
                    cardTongHoaDon.setValue(numberFormatter.format(result.tongHoaDonBan));
                    cardHoaDonDoiTra.setValue(numberFormatter.format(result.tongHoaDonDoiTra));
                    cardSoVeBanDuoc.setValue(numberFormatter.format(result.tongSoVeBan));
                    cardChuyenKhoan.setValue(currencyFormatter.format(result.tongTienChuyenKhoan));
                    cardTienMat.setValue(currencyFormatter.format(result.tongTienMat));
                    cardTongThuDuoc.setValue(currencyFormatter.format(result.tongThuDuoc));

                    // Biểu đồ
                    revenueChartPanel.updateChartData(
                            result.tongTienMat, result.tongTienChuyenKhoan, result.tongThuDuoc);

                    // ===== CẬP NHẬT BẢNG (ĐÃ THAY ĐỔI THỨ TỰ CỘT) =====
                    reportTableModel.setRowCount(0);
                    if (result.danhSachHoaDonChiTiet != null) {
                        int stt = 1;
                        for (Object[] row : result.danhSachHoaDonChiTiet) {
                            Object[] newRow = new Object[6];
                            newRow[0] = stt++;        // STT
                            newRow[1] = row[0];       // Mã HĐ (row[0])
                            newRow[2] = row[1];       // Thời điểm tạo (row[1])
                            newRow[3] = row[3];       // Hình thức TT (row[3])
                            newRow[4] = row[4];       // Trạng thái (row[4])
                            newRow[5] = currencyFormatter.format((double) row[2]).replace(" VNĐ", ""); // Tổng Tiền (row[2])
                            reportTableModel.addRow(newRow);
                        }
                    }

                    // CẬP NHẬT PANEL TỔNG KẾT
                    updateSummaryPanel();

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            PanelThongKe.this,
                            "Lỗi khi tải dữ liệu thống kê: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);

                    lblTenNhanVien.setText("Lỗi tải dữ liệu");
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
        worker.execute();
    }
    // =========================================================================
    // INNER CLASSES (StatCard, RevenueChartPanel)
    // =========================================================================

    /**
     * Card hiển thị 1 chỉ số thống kê.
     */
    private class StatCard extends JPanel {
        private JLabel lblValue;
        private JLabel lblUnit;

        public StatCard(String title, String initialValue, String unit) {
            setLayout(new BorderLayout(5, 5));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(15, 15, 15, 15)
            ));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Arial", Font.PLAIN, 12));
            lblTitle.setForeground(new Color(100, 100, 100));
            add(lblTitle, BorderLayout.NORTH);

            JPanel pnlValueUnit = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            pnlValueUnit.setOpaque(false);

            lblValue = new JLabel(initialValue);
            lblValue.setFont(new Font("Arial", Font.BOLD, 18));
            lblValue.setForeground(new Color(50, 50, 50));
            pnlValueUnit.add(lblValue);

            if (unit != null && !unit.isEmpty()) {
                lblUnit = new JLabel(unit);
                lblUnit.setFont(new Font("Arial", Font.PLAIN, 12));
                lblUnit.setForeground(new Color(120, 120, 120));
                pnlValueUnit.add(lblUnit);
            }

            add(pnlValueUnit, BorderLayout.SOUTH);
        }

        public void setValue(String value) {
            lblValue.setText(value);
        }

        /** Lấy giá trị dạng số (xóa ký tự không phải số) - ĐÃ FIX LỖI */
        public double getNumericValue() {
            String raw = lblValue.getText();
            raw = raw.replaceAll("[^\\d]", ""); // Xóa hết ký tự không phải số
            if (raw.isEmpty()) return 0;
            return Double.parseDouble(raw);
        }
    }

    /**
     * Biểu đồ tròn thể hiện tỷ lệ Tiền mặt / Chuyển khoản.
     */
    private class RevenueChartPanel extends JPanel {
        private double totalRevenue = 1.0;
        private double cashRevenue = 0.0;
        private double transferRevenue = 0.0;

        private final Color COLOR_CASH = new Color(79, 143, 203);
        private final Color COLOR_TRANSFER = new Color(255, 179, 71);

        public RevenueChartPanel() {
            setPreferredSize(new Dimension(300, 300));
            setBackground(Color.WHITE);
        }

        public void updateChartData(double cash, double transfer, double total) {
            this.cashRevenue = cash;
            this.transferRevenue = transfer;
            this.totalRevenue = (total > 0) ? total : 1.0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int chartAreaHeight = height - 50;
            int chartSize = Math.min(width, chartAreaHeight);
            int x = (width - chartSize) / 2;
            int y = (chartAreaHeight - chartSize) / 2;

            double cashPercentage = cashRevenue / totalRevenue;
            double transferPercentage = transferRevenue / totalRevenue;

            int cashAngle = (int) Math.round(cashPercentage * 360);
            int transferAngle = (int) Math.round(transferPercentage * 360);

            if (cashRevenue <= 0 && transferRevenue <= 0) {
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillArc(x, y, chartSize, chartSize, 0, 360);
                g2d.setColor(Color.GRAY);
                g2d.drawString("Chưa có giao dịch", width / 2 - 50, height / 2);
                return;
            } else {
                transferAngle = 360 - cashAngle;
            }

            // Tiền mặt
            g2d.setColor(COLOR_CASH);
            g2d.fillArc(x, y, chartSize, chartSize, 90, -cashAngle);

            // Chuyển khoản
            g2d.setColor(COLOR_TRANSFER);
            g2d.fillArc(x, y, chartSize, chartSize, 90 - cashAngle, -transferAngle);

            // Chú giải
            int legendX = width / 2 - 150;
            int legendY = chartAreaHeight + 10;
            int boxSize = 10;

            DecimalFormat percentFormatter = new DecimalFormat("0.0%");
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));

            g2d.setColor(COLOR_CASH);
            g2d.fillRect(legendX, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Tiền mặt: " + percentFormatter.format(cashPercentage),
                    legendX + boxSize + 5, legendY + boxSize - 1);

            g2d.setColor(COLOR_TRANSFER);
            g2d.fillRect(legendX + 150, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Chuyển khoản: " + percentFormatter.format(transferPercentage),
                    legendX + 150 + boxSize + 5, legendY + boxSize - 1);
        }
    }
    private void exportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file báo cáo");
        fileChooser.setSelectedFile(new java.io.File("BaoCaoHoaDon.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("BaoCao");

            // Tạo header
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            for (int i = 0; i < reportTableModel.getColumnCount(); i++) {
                header.createCell(i).setCellValue(reportTableModel.getColumnName(i));
            }

            // Ghi dữ liệu từng dòng
            for (int row = 0; row < reportTableModel.getRowCount(); row++) {
                org.apache.poi.ss.usermodel.Row excelRow = sheet.createRow(row + 1);

                for (int col = 0; col < reportTableModel.getColumnCount(); col++) {
                    Object value = reportTableModel.getValueAt(row, col);
                    excelRow.createCell(col).setCellValue(value == null ? "" : value.toString());
                }
            }

            // Autosize cột
            for (int i = 0; i < reportTableModel.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this, "Xuất file thành công!\n" + filePath);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage());
        }
    }


    // =========================================================================
    // HÀM MAIN TEST ĐỘC LẬP
    // =========================================================================
    public static void main(String[] args) {
        // 1. Kết nối CSDL
        ConnectDB.getInstance().connect();
        if (ConnectDB.getInstance().getConnection() == null) {
            System.err.println("Không thể kết nối CSDL. Vui lòng kiểm tra cấu hình ConnectDB.");
            return;
        }


        // 3. Chạy giao diện
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý Bán vé Tàu Ga Sài Gòn");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JPanel mainAppPanel = new JPanel(new BorderLayout());
            mainAppPanel.setBackground(new Color(240, 242, 245));

            // Menu trái đơn giản
            JPanel pnlMenu = new JPanel();
            pnlMenu.setBackground(new Color(34, 49, 63));
            pnlMenu.setPreferredSize(new Dimension(200, 0));
            pnlMenu.setLayout(new BorderLayout());

            JLabel lblLogo = new JLabel("Ga Sài Gòn", SwingConstants.CENTER);
            lblLogo.setForeground(Color.WHITE);
            lblLogo.setFont(new Font("Arial", Font.BOLD, 20));
            lblLogo.setBorder(new EmptyBorder(10, 0, 20, 0));
            pnlMenu.add(lblLogo, BorderLayout.NORTH);

            JPanel menuItems = new JPanel(new GridLayout(0, 1, 0, 5));
            menuItems.setOpaque(false);
            String[] menuNames = {"Quản lý", "Bán vé", "Quản lý vé", "Quản lý hóa đơn",
                    "Quản lý khách hàng", "Thống kê & Báo cáo", "About", "Trợ giúp", "Đăng xuất"};
            for (String name : menuNames) {
                JButton btn = new JButton(name);
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setBackground(new Color(34, 49, 63));
                btn.setForeground(Color.WHITE);
                btn.setBorder(new EmptyBorder(10, 20, 10, 20));
                btn.setFocusPainted(false);
                btn.setFont(new Font("Arial", Font.PLAIN, 14));
                if (name.equals("Thống kê & Báo cáo")) {
                    btn.setBackground(new Color(52, 73, 94));
                }
                menuItems.add(btn);
            }
            pnlMenu.add(menuItems, BorderLayout.CENTER);

            PanelThongKe pnlThongKe = new PanelThongKe();
            mainAppPanel.add(pnlMenu, BorderLayout.WEST);
            mainAppPanel.add(pnlThongKe, BorderLayout.CENTER);

            frame.setContentPane(mainAppPanel);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}