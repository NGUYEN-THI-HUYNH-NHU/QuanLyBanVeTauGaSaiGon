package gui.application.form.thongKe;

import dao.ThongKeNhanVien_DAO;
import entity.NhanVien;
import entity.type.VaiTroNhanVien; // <--- CẦN THÊM IMPORT NÀY
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class PanelBaoCao extends JPanel {

    private final BaoCaoGiaoCaModel giaoCaModel;
    private final ThongKeNhanVien_DAO thongKeNhanVienDAO;
    private final NhanVien nhanVien;

    private final DecimalFormat currencyFormatter = new DecimalFormat("#,##0");

    // =================================================================
    // KHAI BÁO FIELD
    // =================================================================

    private JLabel lblNVInfo, lblCaInfo, lblNgayInfo;

    private JLabel lblTongTTHuyetThong, lblTongCKReport, lblTongThuReport;
    private JLabel lblTongTienMatKet;
    private JLabel lblTongTienHienTai;
    private JLabel lblTongTienChenhLech;
    private JLabel lblTongValue;
    private JTextArea txtGhiChuReport;

    private String tenNV, caLV, ngayLV;
    private double cashSystem, transferSystem, totalSystem;
    private List<Object[]> hoaDonList;

    private JButton btnNhapTienMat;
    private JButton btnExport;

    private DefaultTableModel reportTableModel;
    private JTable reportTable;

    // =================================================================
    // CONSTRUCTORS
    // =================================================================

    public PanelBaoCao() {
        // Lấy nhân viên hiện tại từ AuthService
        NhanVien current = AuthService.getInstance().getCurrentUser();
        if (current == null) {
            // Giả lập nhân viên sử dụng constructor 11 tham số (không có avatar)
            // Đã sửa để khớp với constructor NhanVien(ID, VaiTro, HoTen, isNu, NgaySinh, SDT, Email, DiaChi, NgayTG, HoatDong, CaLam)
            current = new NhanVien(
                    "NV001",
                    null, // VaiTroNhanVien
                    "Nhân Viên Test",
                    false, // isNu
                    null,  // ngaySinh (LocalDate)
                    "0000000000", // soDienThoai (Cần giá trị vì setter/DB không cho phép trống)
                    null,  // email
                    null,  // diaChi
                    null,  // ngayThamGia (LocalDate)
                    true,  // isHoatDong
                    null   // caLam
            );
        }
        this.nhanVien = current;
        this.tenNV = nhanVien.getHoTen() != null ? nhanVien.getHoTen() : "Không xác định";

        this.thongKeNhanVienDAO = new ThongKeNhanVien_DAO();
        this.giaoCaModel = new BaoCaoGiaoCaModel();

        this.caLV = "Đang tải...";
        this.ngayLV = "Đang tải...";
        this.cashSystem = 0;
        this.transferSystem = 0;
        this.totalSystem = 0;
        this.hoaDonList = new ArrayList<>();

        initComponents();
        loadBaoCaoData();
    }

    public PanelBaoCao(String tenNV, String caLV, String ngayLV,
                       double cashSystem, double transferSystem, double totalSystem,
                       BaoCaoGiaoCaModel model, List<Object[]> hoaDonList) {

        this.thongKeNhanVienDAO = new ThongKeNhanVien_DAO();
        // Cần đảm bảo this.nhanVien được khởi tạo, dùng AuthService hoặc null nếu không thể
        NhanVien tempUser = AuthService.getInstance().getCurrentUser();
        if (tempUser == null) {
            // Dùng constructor 1 tham số nếu không tìm thấy User
            tempUser = new NhanVien(null);
        }
        this.nhanVien = tempUser;

        this.tenNV = tenNV;
        this.caLV = caLV;
        this.ngayLV = ngayLV;
        this.cashSystem = cashSystem;
        this.transferSystem = transferSystem;
        this.totalSystem = totalSystem;
        this.giaoCaModel = model;
        this.hoaDonList = hoaDonList;

        initComponents();
        updateSummaryPanel();
    }

    // =================================================================
    // CÁC PHƯƠNG THỨC UI
    // =================================================================

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlTitle = createTitlePanel();
        JPanel pnlTableAndTotal = createTablePanel();
        JPanel pnlSummaryAndButtons = createSummaryAndButtonPanel();

        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);

        pnlCenter.add(pnlTitle);
        pnlCenter.add(pnlTableAndTotal);
        pnlCenter.add(Box.createVerticalStrut(10));
        pnlCenter.add(pnlSummaryAndButtons);

        add(pnlCenter, BorderLayout.CENTER);

        updateTitleLabels();
        updateTable(this.hoaDonList);
        updateSummaryPanel();
    }

    private JPanel createTitlePanel() {
        JLabel lblTitle = new JLabel("BÁO CÁO CUỐI CA", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel pnlNVInfo = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        pnlNVInfo.setOpaque(false);

        lblNVInfo = new JLabel("Nhân viên: " + tenNV);
        lblCaInfo = new JLabel("Ca làm việc: " + caLV);
        lblNgayInfo = new JLabel("Ngày làm việc: " + ngayLV);

        pnlNVInfo.add(lblNVInfo);
        pnlNVInfo.add(lblCaInfo);
        pnlNVInfo.add(lblNgayInfo);

        JPanel pnlContainer = new JPanel(new BorderLayout());
        pnlContainer.setOpaque(false);
        pnlContainer.add(lblTitle, BorderLayout.NORTH);
        pnlContainer.add(pnlNVInfo, BorderLayout.CENTER);

        return pnlContainer;
    }

    private void updateTitleLabels() {
        if (lblNVInfo != null) {
            lblNVInfo.setText("Nhân viên: " + tenNV);
            lblCaInfo.setText("Ca làm việc: " + caLV);
            lblNgayInfo.setText("Ngày làm việc: " + ngayLV);
        }
    }

    private JPanel createTablePanel() {
        String[] columnNames = {"STT", "Mã HĐ", "Thời Điểm Tạo", "Hình Thức TT", "Trạng Thái", "Tổng Tiền"};

        reportTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(reportTableModel);
        reportTable.setRowHeight(25);
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JPanel pnlTongRow = new JPanel(new BorderLayout());
        pnlTongRow.setBackground(new Color(235, 235, 235));
        pnlTongRow.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel lblTongText = new JLabel("Tổng Doanh thu Hệ thống (B):");
        lblTongText.setFont(new Font("Arial", Font.BOLD, 14));

        lblTongValue = new JLabel(currencyFormatter.format(totalSystem).replace(",", "."));
        lblTongValue.setFont(new Font("Arial", Font.BOLD, 14));
        lblTongValue.setHorizontalAlignment(SwingConstants.RIGHT);

        pnlTongRow.add(lblTongText, BorderLayout.WEST);
        pnlTongRow.add(lblTongValue, BorderLayout.EAST);

        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        pnlTableContainer.add(scrollPane, BorderLayout.CENTER);
        pnlTableContainer.add(pnlTongRow, BorderLayout.SOUTH);

        return pnlTableContainer;
    }

    private JPanel createSummaryAndButtonPanel() {
        JPanel pnlContainer = new JPanel(new BorderLayout(0, 10));
        pnlContainer.setOpaque(false);

        JPanel pnlSummary = new JPanel(new GridLayout(6, 2, 10, 5));
        pnlSummary.setOpaque(false);
        pnlSummary.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTongTTHuyetThong = new JLabel(currencyFormatter.format(cashSystem).replace(",", "."));
        lblTongCKReport = new JLabel(currencyFormatter.format(transferSystem).replace(",", "."));
        lblTongThuReport = new JLabel(currencyFormatter.format(totalSystem).replace(",", "."));
        lblTongTienMatKet = new JLabel(currencyFormatter.format(giaoCaModel.getTienMatTaiKetValue()).replace(",", "."));
        lblTongTienHienTai = new JLabel("0");
        lblTongTienChenhLech = new JLabel("0");

        txtGhiChuReport = new JTextArea(3, 10);
        txtGhiChuReport.setEditable(false);
        txtGhiChuReport.setBackground(new Color(245, 245, 245));

        lblTongTienMatKet.setFont(new Font("Arial", Font.BOLD, 14));

        pnlSummary.add(new JLabel("Tổng tiền mặt (Hệ thống):"));
        pnlSummary.add(lblTongTTHuyetThong);
        pnlSummary.add(new JLabel("Tổng tiền chuyển khoản:"));
        pnlSummary.add(lblTongCKReport);
        pnlSummary.add(new JLabel("Tổng doanh thu trên hệ thống (B):"));
        pnlSummary.add(lblTongThuReport);
        pnlSummary.add(new JLabel("Tổng tiền mặt tại két (Thực tế):"));
        pnlSummary.add(lblTongTienMatKet);
        pnlSummary.add(new JLabel("Tổng doanh thu hiện tại (A):"));
        pnlSummary.add(lblTongTienHienTai);
        pnlSummary.add(new JLabel("Chênh lệnh (A - B):"));
        pnlSummary.add(lblTongTienChenhLech);

        pnlContainer.add(pnlSummary, BorderLayout.NORTH);

        JPanel pnlGhiChuContainer = new JPanel(new BorderLayout(5, 0));
        pnlGhiChuContainer.setOpaque(false);
        pnlGhiChuContainer.setBorder(BorderFactory.createTitledBorder("Ghi chú"));
        pnlGhiChuContainer.add(new JScrollPane(txtGhiChuReport), BorderLayout.CENTER);

        pnlContainer.add(pnlGhiChuContainer, BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlLeftButton = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnNhapTienMat = new JButton("Nhập tiền mặt");
        btnNhapTienMat.setBackground(new Color(70, 130, 180));
        btnNhapTienMat.setForeground(Color.WHITE);
        btnNhapTienMat.setPreferredSize(new Dimension(130, 30));
        btnNhapTienMat.addActionListener(e -> xuLyNhapTienMat());
        pnlLeftButton.add(btnNhapTienMat);

        JPanel pnlRightButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        btnExport = new JButton("Xuất File Báo Cáo");
        btnExport.setBackground(new Color(255, 153, 51));
        btnExport.setForeground(Color.WHITE);
        btnExport.setPreferredSize(new Dimension(160, 30));

        pnlBottom.add(pnlLeftButton, BorderLayout.WEST);
        pnlBottom.add(pnlRightButton, BorderLayout.EAST);

        pnlContainer.add(pnlBottom, BorderLayout.SOUTH);

        return pnlContainer;
    }

    // =================================================================
    // LOGIC TẢI DỮ LIỆU
    // =================================================================

    private void loadBaoCaoData() {
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

                result.tongTienChuyenKhoan = thongKeNhanVienDAO.getTongTienChuyenKhoan(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienMat = thongKeNhanVienDAO.getTongTienMat(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);
                result.danhSachHoaDonChiTiet = thongKeNhanVienDAO.getListHoaDonTrongCa(
                        finalMaNhanVien, currentDay, finalGioBatDauCa, finalGioKetThucCa);

                result.caLamViecText = finalCaLamViecText;
                result.ngayLamViecDate = currentDay;
                result.tongThuDuoc = result.tongTienChuyenKhoan + result.tongTienMat;

                return result;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ThongKeResult result = get();

                    // Cập nhật dữ liệu vào Field của Panel
                    tenNV = nhanVien.getHoTen();
                    caLV = result.caLamViecText;
                    ngayLV = result.ngayLamViecDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    cashSystem = result.tongTienMat;
                    transferSystem = result.tongTienChuyenKhoan;
                    hoaDonList = result.danhSachHoaDonChiTiet;

                    updateTitleLabels();
                    updateTable(hoaDonList);
                    updateSummaryPanel();

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            PanelBaoCao.this,
                            "Lỗi khi tải dữ liệu báo cáo: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private static class ThongKeResult {
        double tongTienChuyenKhoan;
        double tongTienMat;
        double tongThuDuoc;
        String caLamViecText;
        LocalDate ngayLamViecDate;
        List<Object[]> danhSachHoaDonChiTiet;
    }

    // =================================================================
    // CÁC PHƯƠNG THỨC XỬ LÝ SỰ KIỆN VÀ CẬP NHẬT
    // =================================================================

    private void xuLyNhapTienMat() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);

        NhapTienMat nhapTienMatDialog = new NhapTienMat(
                owner,
                tenNV,
                caLV,
                ngayLV,
                cashSystem
        );

        nhapTienMatDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (nhapTienMatDialog.isGiaoCaConfirmed()) {
                    giaoCaModel.setGiaoCaData(
                            nhapTienMatDialog.getTienMatThucTeDaNhap(),
                            nhapTienMatDialog.getGhiChuDaNhap()
                    );
                    updateSummaryPanel();
                }
            }
        });

        nhapTienMatDialog.setVisible(true);
    }

    private void updateTable(List<Object[]> list) {
        if (reportTableModel == null) return;
        reportTableModel.setRowCount(0);

        double currentTotalSystem = 0;

        if (list != null) {
            int stt = 1;
            for (Object[] row : list) {
                Object[] newRow = new Object[6];
                newRow[0] = stt++;
                newRow[1] = row[0];
                newRow[2] = row[1];
                newRow[3] = row[3];
                newRow[4] = row[4];
                double tongTien = (double) row[2];
                newRow[5] = currencyFormatter.format(tongTien).replace(",", ".");
                reportTableModel.addRow(newRow);

                String trangThai = (String) row[4];
                if (trangThai != null && trangThai.equals("Hoàn thành")) {
                    currentTotalSystem += tongTien;
                }
            }
        }

        this.totalSystem = currentTotalSystem;
    }


    public void updateSummaryPanel() {
        double tienMatKet = giaoCaModel.getTienMatTaiKetValue();
        double totalCurrent = tienMatKet + transferSystem;
        double difference = totalCurrent - totalSystem;

        if (lblTongValue != null) {
            lblTongValue.setText(currencyFormatter.format(totalSystem).replace(",", "."));
        }

        if (lblTongTTHuyetThong != null) {
            lblTongTTHuyetThong.setText(currencyFormatter.format(cashSystem).replace(",", "."));
            lblTongCKReport.setText(currencyFormatter.format(transferSystem).replace(",", "."));
            lblTongThuReport.setText(currencyFormatter.format(totalSystem).replace(",", "."));

            lblTongTienMatKet.setText(currencyFormatter.format(tienMatKet).replace(",", "."));
            lblTongTienHienTai.setText(currencyFormatter.format(totalCurrent).replace(",", "."));
            lblTongTienChenhLech.setText(currencyFormatter.format(difference).replace(",", "."));
            txtGhiChuReport.setText(giaoCaModel.getGhiChu());

            if (Math.abs(difference) > 0.001) {
                lblTongTienChenhLech.setForeground(difference > 0 ? new Color(0, 102, 0) : Color.RED);
            } else {
                lblTongTienChenhLech.setForeground(Color.BLACK);
            }

            revalidate();
            repaint();
        }
    }

    public void updateData(String tenNV, String caLV, String ngayLV,
                           double cashSystem, double transferSystem, double totalSystem,
                           List<Object[]> hoaDonList) {

        this.tenNV = tenNV;
        this.caLV = caLV;
        this.ngayLV = ngayLV;
        this.cashSystem = cashSystem;
        this.transferSystem = transferSystem;
        this.totalSystem = totalSystem;
        this.hoaDonList = hoaDonList;

        updateTitleLabels();
        updateTable(this.hoaDonList);
        updateSummaryPanel();

        revalidate();
        repaint();
    }
}