package gui.application.form.thongKe;

import bus.ThongKeNhanVien_BUS;
import dto.NhanVienDTO;
import gui.application.AuthService;

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

/**
 * Panel Thống Kê (Dashboard) - Hiển thị số liệu thống kê cuối ca cho nhân viên.
 */
public class PanelThongKe extends JPanel {

    // ====== FIELD CHÍNH ======
    private final NhanVienDTO nhanVien;
    private final ThongKeNhanVien_BUS thongKeNhanVienBUS;
    private final DecimalFormat currencyFormatter = new DecimalFormat("#,##0 VNĐ");
    private final DecimalFormat numberFormatter = new DecimalFormat("#,##0");
    private StatCard cardTongHoaDon, cardHoaDonDoiTra, cardSoVeBanDuoc;
    private StatCard cardChuyenKhoan, cardTienMat, cardTongThuDuoc;
    private BaoCaoGiaoCaModel giaoCaModel;
    private JLabel lblTenNhanVien, lblCaLamViec, lblNgayLamViec;
    private RevenueChartPanel revenueChartPanel;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JLabel lblTenNV_Report;
    private JLabel lblCaLV_Report;
    private JLabel lblNgayLV_Report;
    private JTabbedPane tabbedPane;

    public PanelThongKe() {
        this.thongKeNhanVienBUS = new ThongKeNhanVien_BUS();
        this.giaoCaModel = new BaoCaoGiaoCaModel();

        NhanVienDTO current = AuthService.getInstance().getCurrentUser();
        if (current == null) {
            throw new IllegalStateException(
                    "Chưa có nhân viên đăng nhập! Hãy setCurrentUser trước khi mở PanelThongKe.");
        }
        this.nhanVien = current;

        initComponents();
        loadDashboardData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(240, 242, 245));

        lblTenNhanVien = new JLabel(
                (nhanVien.getHoTen() != null && !nhanVien.getHoTen().isBlank()) ? nhanVien.getHoTen() : "Không xác định");
        lblTenNhanVien.setFont(new Font(getFont().getFontName(), Font.BOLD, 13));

        lblCaLamViec = new JLabel("Đang tải...");
        lblCaLamViec.setFont(new Font(getFont().getFontName(), Font.BOLD, 13));

        lblNgayLamViec = new JLabel("Đang tải...");
        lblNgayLamViec.setFont(new Font(getFont().getFontName(), Font.BOLD, 13));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel pnlMainContent = new JPanel(new BorderLayout(15, 15));
        pnlMainContent.setOpaque(false);
        pnlMainContent.add(createCardPanel(), BorderLayout.NORTH);
        pnlMainContent.add(createTabbedPanel(), BorderLayout.CENTER);
        add(pnlMainContent, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 10));
        pnlHeader.setOpaque(false);

        JLabel lblMainTitle = new JLabel("Thống kê cuối ca");
        lblMainTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 20));
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

    private JTabbedPane createTabbedPanel() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(getFont().getFontName(), Font.PLAIN, 14));
        tabbedPane.addTab("Biểu đồ Doanh thu", createChartTabPanel());
        tabbedPane.addTab("Bảng báo cáo chi tiết", createReportTabPanel());
        return tabbedPane;
    }

    private JPanel createChartTabPanel() {
        JPanel pnlChart = new JPanel(new BorderLayout());
        pnlChart.setOpaque(true);
        pnlChart.setBackground(Color.WHITE);
        pnlChart.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 15, 15, 15)));

        JLabel lblChartSectionTitle = new JLabel(
                "Cơ cấu Doanh thu theo Hình thức Thanh toán (Tiền mặt vs Chuyển khoản)");
        lblChartSectionTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 16));
        lblChartSectionTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlChart.add(lblChartSectionTitle, BorderLayout.NORTH);

        revenueChartPanel = new RevenueChartPanel();
        pnlChart.add(revenueChartPanel, BorderLayout.CENTER);

        return pnlChart;
    }

    private JPanel createReportTabPanel() {
        JPanel pnlReport = new JPanel(new BorderLayout());
        pnlReport.setBackground(new Color(240, 242, 245));
        pnlReport.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblTenNV_Report = new JLabel("Đang tải...");
        lblCaLV_Report = new JLabel("Đang tải...");
        lblNgayLV_Report = new JLabel("Đang tải...");

        JPanel pnlTitleContainer = new JPanel(new BorderLayout());
        pnlTitleContainer.setOpaque(false);
        pnlTitleContainer.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel lblMainTitle = new JLabel("BÁO CÁO CHI TIẾT DANH SÁCH HÓA ĐƠN TRONG CA", SwingConstants.CENTER);
        lblMainTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 16));
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

        String[] columnNames = {"STT", "Mã HĐ", "Thời Điểm Tạo", "Hình Thức TT", "Trạng Thái", "Tổng Tiền"};
        reportTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable reportTable = new JTable(reportTableModel);
        reportTable.setFont(new Font(getFont().getFontName(), Font.PLAIN, 12));
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setFont(new Font(getFont().getFontName(), Font.BOLD, 12));
        reportTable.getColumnModel().getColumn(0).setMaxWidth(34);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        pnlReport.add(scrollPane, BorderLayout.CENTER);

        return pnlReport;
    }

    // =========================================================================
    // DATA LOADING LOGIC
    // =========================================================================

    private void loadDashboardData() {
        lblCaLamViec.setText("Đang tải...");
        lblNgayLamViec.setText("Đang tải...");
        cardTongHoaDon.setValue("Đang tải...");
        cardHoaDonDoiTra.setValue("Đang tải...");
        cardSoVeBanDuoc.setValue("Đang tải...");
        cardChuyenKhoan.setValue("Đang tải...");
        cardTienMat.setValue("Đang tải...");
        cardTongThuDuoc.setValue("Đang tải...");
        if (reportTableModel != null) reportTableModel.setRowCount(0);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        final LocalDate currentDay = LocalDate.now();
        final String finalCaLamViecText = String.format("%s (%s - %s)", nhanVien.getCaLamID(),
                nhanVien.getGioVaoCa().format(DateTimeFormatter.ofPattern("HH:mm")),
                nhanVien.getGioKetCa().format(DateTimeFormatter.ofPattern("HH:mm")));
        final LocalTime finalGioBatDauCa = nhanVien.getGioVaoCa();
        final LocalTime finalGioKetThucCa = nhanVien.getGioKetCa();
        final String finalMaNhanVien = nhanVien.getId();

        SwingWorker<ThongKeResult, Void> worker = new SwingWorker<>() {
            @Override
            protected ThongKeResult doInBackground() {
                ThongKeResult result = new ThongKeResult();
                result.tongHoaDonBan = thongKeNhanVienBUS.getTongSoHoaDonBanDuoc(finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongHoaDonDoiTra = thongKeNhanVienBUS.getTongSoHoaDonDoiTra(finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongSoVeBan = thongKeNhanVienBUS.getTongSoVeBanDuoc(finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienChuyenKhoan = thongKeNhanVienBUS.getTongTienChuyenKhoan(finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienMat = thongKeNhanVienBUS.getTongTienMat(finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.danhSachHoaDonChiTiet = thongKeNhanVienBUS.getListHoaDonTrongCa(finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
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
                    String ngayLV = result.ngayLamViecDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    lblTenNhanVien.setText(nhanVien.getHoTen());
                    lblCaLamViec.setText(result.caLamViecText);
                    lblNgayLamViec.setText(ngayLV);
                    lblTenNV_Report.setText(nhanVien.getHoTen());
                    lblCaLV_Report.setText(result.caLamViecText);
                    lblNgayLV_Report.setText(ngayLV);

                    cardTongHoaDon.setValue(numberFormatter.format(result.tongHoaDonBan));
                    cardHoaDonDoiTra.setValue(numberFormatter.format(result.tongHoaDonDoiTra));
                    cardSoVeBanDuoc.setValue(numberFormatter.format(result.tongSoVeBan));
                    cardChuyenKhoan.setValue(currencyFormatter.format(result.tongTienChuyenKhoan));
                    cardTienMat.setValue(currencyFormatter.format(result.tongTienMat));
                    cardTongThuDuoc.setValue(currencyFormatter.format(result.tongThuDuoc));

                    revenueChartPanel.updateChartData(result.tongTienMat, result.tongTienChuyenKhoan, result.tongThuDuoc);

                    reportTableModel.setRowCount(0);
                    if (result.danhSachHoaDonChiTiet != null) {
                        int stt = 1;
                        for (Object[] row : result.danhSachHoaDonChiTiet) {
                            Object[] newRow = new Object[6];
                            newRow[0] = stt++;
                            newRow[1] = row[0];
                            newRow[2] = row[1];
                            newRow[3] = row[3];
                            newRow[4] = row[4];
                            newRow[5] = currencyFormatter.format((double) row[2]).replace(" VNĐ", "");
                            reportTableModel.addRow(newRow);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PanelThongKe.this,
                            "Lỗi khi tải dữ liệu thống kê: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

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

    private class StatCard extends JPanel {
        private JLabel lblValue;
        private JLabel lblUnit;

        public StatCard(String title, String initialValue, String unit) {
            setLayout(new BorderLayout(5, 5));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    new EmptyBorder(15, 15, 15, 15)));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font(getFont().getFontName(), Font.PLAIN, 12));
            lblTitle.setForeground(new Color(100, 100, 100));
            add(lblTitle, BorderLayout.NORTH);

            JPanel pnlValueUnit = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
            pnlValueUnit.setOpaque(false);

            lblValue = new JLabel(initialValue);
            lblValue.setFont(new Font(getFont().getFontName(), Font.BOLD, 18));
            lblValue.setForeground(new Color(50, 50, 50));
            pnlValueUnit.add(lblValue);

            if (unit != null && !unit.isEmpty()) {
                lblUnit = new JLabel(unit);
                lblUnit.setFont(new Font(getFont().getFontName(), Font.PLAIN, 12));
                lblUnit.setForeground(new Color(120, 120, 120));
                pnlValueUnit.add(lblUnit);
            }
            add(pnlValueUnit, BorderLayout.SOUTH);
        }

        public void setValue(String value) {
            lblValue.setText(value);
        }

        public double getNumericValue() {
            String raw = lblValue.getText().replaceAll("[^\\d]", "");
            return raw.isEmpty() ? 0 : Double.parseDouble(raw);
        }
    }

    private class RevenueChartPanel extends JPanel {
        private final Color COLOR_CASH = new Color(79, 143, 203);
        private final Color COLOR_TRANSFER = new Color(255, 179, 71);
        private double totalRevenue = 1.0;
        private double cashRevenue = 0.0;
        private double transferRevenue = 0.0;

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

            int width = getWidth(), height = getHeight();
            int chartAreaHeight = height - 50;
            int chartSize = Math.min(width, chartAreaHeight);
            int x = (width - chartSize) / 2;
            int y = (chartAreaHeight - chartSize) / 2;

            double cashPercentage = cashRevenue / totalRevenue;
            double transferPercentage = transferRevenue / totalRevenue;
            int cashAngle = (int) Math.round(cashPercentage * 360);
            int transferAngle;

            if (cashRevenue <= 0 && transferRevenue <= 0) {
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillArc(x, y, chartSize, chartSize, 0, 360);
                g2d.setColor(Color.GRAY);
                g2d.drawString("Chưa có giao dịch", width / 2 - 50, height / 2);
                return;
            } else {
                transferAngle = 360 - cashAngle;
            }

            g2d.setColor(COLOR_CASH);
            g2d.fillArc(x, y, chartSize, chartSize, 90, -cashAngle);
            g2d.setColor(COLOR_TRANSFER);
            g2d.fillArc(x, y, chartSize, chartSize, 90 - cashAngle, -transferAngle);

            int legendX = width / 2 - 150;
            int legendY = chartAreaHeight + 10;
            int boxSize = 10;
            DecimalFormat percentFormatter = new DecimalFormat("0.0%");
            g2d.setFont(new Font(getFont().getFontName(), Font.PLAIN, 12));

            g2d.setColor(COLOR_CASH);
            g2d.fillRect(legendX, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Tiền mặt: " + percentFormatter.format(cashPercentage), legendX + boxSize + 5, legendY + boxSize - 1);

            g2d.setColor(COLOR_TRANSFER);
            g2d.fillRect(legendX + 150, legendY, boxSize, boxSize);
            g2d.setColor(Color.BLACK);
            g2d.drawString("Chuyển khoản: " + percentFormatter.format(transferPercentage), legendX + 150 + boxSize + 5, legendY + boxSize - 1);
        }
    }
}