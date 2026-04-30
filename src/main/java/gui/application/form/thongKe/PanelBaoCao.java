package gui.application.form.thongKe;

import dao.impl.ThongKeNhanVien_DAO;
import entity.NhanVien;
import gui.application.AuthService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

;

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
        NhanVien current = AuthService.getInstance().getCurrentUser();

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

    public PanelBaoCao(String tenNV, String caLV, String ngayLV, double cashSystem, double transferSystem,
                       double totalSystem, BaoCaoGiaoCaModel model, List<Object[]> hoaDonList) {

        this.thongKeNhanVienDAO = new ThongKeNhanVien_DAO();
        NhanVien tempUser = AuthService.getInstance().getCurrentUser();
        if (tempUser == null) {
            tempUser = new NhanVien();
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
        reportTable.getColumnModel().getColumn(0).setMaxWidth(34);

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
        btnExport.addActionListener(e -> exportToExcel());
        pnlRightButton.add(btnExport);

        pnlBottom.add(pnlLeftButton, BorderLayout.WEST);
        pnlBottom.add(pnlRightButton, BorderLayout.EAST);

        pnlContainer.add(pnlBottom, BorderLayout.SOUTH);

        return pnlContainer;
    }

    // =================================================================
    // PHƯƠNG THỨC XUẤT FILE EXCEL
    // =================================================================
    private void exportToExcel() {
        // =================================================================
        // 1. RÀNG BUỘC: BẮT BUỘC PHẢI NHẬP TIỀN MẶT MỚI ĐƯỢC XUẤT FILE
        // =================================================================
        if (!giaoCaModel.isGiaoCaConfirmed()) {
            JOptionPane.showMessageDialog(this,
                    "Bạn chưa thực hiện kiểm kê tiền mặt!\nVui lòng nhấn nút 'Nhập tiền mặt' và xác nhận trước khi xuất file báo cáo.",
                    "Chưa kiểm kê", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (reportTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu hóa đơn để xuất.");
            return;
        }

        // =================================================================
        // 2. CHỌN NƠI LƯU FILE
        // =================================================================
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu file báo cáo");
        String defaultFileName = "BaoCaoCuoiCa_" + ngayLV.replace("/", "-") + "_" + caLV.replace(" ", "_") + ".xlsx";
        fileChooser.setSelectedFile(new java.io.File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try (Workbook workbook = new XSSFWorkbook()) {

            // --- TẠO STYLE DÙNG CHUNG ---
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            tableHeaderStyle.cloneStyleFrom(headerStyle);
            tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
            tableHeaderStyle.setBorderTop(BorderStyle.THIN);
            tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
            tableHeaderStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle titleBigStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleBigStyle.setFont(titleFont);
            titleBigStyle.setAlignment(HorizontalAlignment.CENTER);

            // =================================================================
            // SHEET 1: CHI TIẾT HÓA ĐƠN (CÓ HEADER + BẢNG)
            // =================================================================
            org.apache.poi.ss.usermodel.Sheet sheet1 = workbook.createSheet("ChiTietHoaDon");

            // --- Phần Header Sheet 1 ---
            // Dòng 0: Tiêu đề lớn
            Row row0 = sheet1.createRow(0);
            row0.setHeightInPoints(30);
            Cell cellTitle = row0.createCell(0);
            cellTitle.setCellValue("DANH SÁCH HÓA ĐƠN CHI TIẾT");
            cellTitle.setCellStyle(titleBigStyle);
            sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, 5)); // Merge cột A-F

            // Dòng 2: Thông tin Nhân viên - Ca - Ngày
            Row row2 = sheet1.createRow(2);
            row2.setHeightInPoints(25);

            // Nhân viên
            Cell cellNV = row2.createCell(0);
            cellNV.setCellValue("NV: " + tenNV);
            cellNV.setCellStyle(headerStyle);
            sheet1.addMergedRegion(new CellRangeAddress(2, 2, 0, 1));

            // Ca làm việc
            Cell cellCa = row2.createCell(2);
            cellCa.setCellValue("Ca: " + caLV);
            cellCa.setCellStyle(headerStyle);
            sheet1.addMergedRegion(new CellRangeAddress(2, 2, 2, 3));

            // Ngày làm việc
            Cell cellNgay = row2.createCell(4);
            cellNgay.setCellValue("Ngày: " + ngayLV);
            cellNgay.setCellStyle(headerStyle);
            sheet1.addMergedRegion(new CellRangeAddress(2, 2, 4, 5));

            // --- Phần Bảng Sheet 1 ---
            int startRowSheet1 = 4; // Bắt đầu bảng từ dòng 5

            // Header Bảng
            org.apache.poi.ss.usermodel.Row header1 = sheet1.createRow(startRowSheet1);
            for (int i = 0; i < reportTableModel.getColumnCount(); i++) {
                Cell cell = header1.createCell(i);
                cell.setCellValue(reportTableModel.getColumnName(i));
                cell.setCellStyle(tableHeaderStyle);
            }

            // Dữ liệu Bảng
            for (int row = 0; row < reportTableModel.getRowCount(); row++) {
                org.apache.poi.ss.usermodel.Row excelRow = sheet1.createRow(startRowSheet1 + 1 + row);
                for (int col = 0; col < reportTableModel.getColumnCount(); col++) {
                    Object value = reportTableModel.getValueAt(row, col);
                    Cell cell = excelRow.createCell(col);
                    cell.setCellValue(value == null ? "" : value.toString());
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto size cột sheet 1
            for (int i = 0; i < reportTableModel.getColumnCount(); i++) {
                sheet1.autoSizeColumn(i);
            }

            // =================================================================
            // SHEET 2: TỔNG KẾT & BẢNG KÊ (GIỮ NGUYÊN CODE CŨ CỦA BẠN)
            // =================================================================
            org.apache.poi.ss.usermodel.Sheet sheet2 = workbook.createSheet("TongKet");

            int rowNum = 0;
            sheet2.createRow(rowNum++).createCell(0).setCellValue("BÁO CÁO TỔNG KẾT CA");
            sheet2.createRow(rowNum++).createCell(0).setCellValue("Nhân viên: " + tenNV);
            sheet2.createRow(rowNum++).createCell(0).setCellValue("Ca làm việc: " + caLV);
            sheet2.createRow(rowNum++).createCell(0).setCellValue("Ngày làm việc: " + ngayLV);
            rowNum++;

            // Phần tổng kết tài chính
            sheet2.createRow(rowNum++).createCell(0).setCellValue("TỔNG KẾT TÀI CHÍNH:");

            sheet2.createRow(rowNum++).createCell(0).setCellValue("Tổng tiền mặt (Hệ thống):");
            sheet2.getRow(rowNum - 1).createCell(1).setCellValue(lblTongTTHuyetThong.getText());

            sheet2.createRow(rowNum++).createCell(0).setCellValue("Tổng tiền chuyển khoản:");
            sheet2.getRow(rowNum - 1).createCell(1).setCellValue(lblTongCKReport.getText());

            sheet2.createRow(rowNum++).createCell(0).setCellValue("Tổng doanh thu trên hệ thống (B):");
            sheet2.getRow(rowNum - 1).createCell(1).setCellValue(lblTongThuReport.getText());

            sheet2.createRow(rowNum++).createCell(0).setCellValue("Tổng tiền mặt tại két (Thực tế):");
            sheet2.getRow(rowNum - 1).createCell(1).setCellValue(lblTongTienMatKet.getText());

            sheet2.createRow(rowNum++).createCell(0).setCellValue("Tổng doanh thu hiện tại (A):");
            sheet2.getRow(rowNum - 1).createCell(1).setCellValue(lblTongTienHienTai.getText());

            sheet2.createRow(rowNum++).createCell(0).setCellValue("Chênh lệnh (A - B):");
            sheet2.getRow(rowNum - 1).createCell(1).setCellValue(lblTongTienChenhLech.getText());

            // Phần chi tiết tiền mặt
            rowNum++;
            org.apache.poi.ss.usermodel.Row titleRow = sheet2.createRow(rowNum++);
            titleRow.createCell(0).setCellValue("CHI TIẾT TIỀN MẶT THỰC TẾ:");

            Map<Integer, Integer> mapTien = giaoCaModel.getChiTietTienMat();

            if (mapTien != null && !mapTien.isEmpty()) {
                List<Integer> sortedKeys = new ArrayList<>(mapTien.keySet());
                sortedKeys.sort((a, b) -> b - a);

                for (Integer menhGia : sortedKeys) {
                    int soLuong = mapTien.get(menhGia);
                    org.apache.poi.ss.usermodel.Row rowTien = sheet2.createRow(rowNum++);
                    rowTien.createCell(0).setCellValue(currencyFormatter.format(menhGia));
                    rowTien.createCell(1).setCellValue("x " + soLuong);
                    double thanhTien = (double) menhGia * soLuong;
                    rowTien.createCell(2).setCellValue("= " + currencyFormatter.format(thanhTien));
                }
            } else {
                sheet2.createRow(rowNum++).createCell(0).setCellValue("(Chưa có dữ liệu chi tiết)");
            }

            rowNum++;
            sheet2.createRow(rowNum++).createCell(0).setCellValue("Ghi chú:");
            sheet2.createRow(rowNum++).createCell(0).setCellValue(txtGhiChuReport.getText());

            sheet2.autoSizeColumn(0);
            sheet2.autoSizeColumn(1);
            sheet2.autoSizeColumn(2);

            // =================================================================
            // GHI FILE VÀ TỰ ĐỘNG MỞ
            // =================================================================
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            // Tự động mở file sau khi xuất
            java.io.File file = new java.io.File(filePath);
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                if (file.exists()) {
                    desktop.open(file);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Xuất file báo cáo thành công!\n" + filePath);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất file: " + ex.getMessage(), "Lỗi Xuất File",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =================================================================
    // CÁC PHƯƠNG THỨC LOGIC KHÁC
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
                result.tongTienChuyenKhoan = thongKeNhanVienDAO.getTongTienChuyenKhoan(finalMaNhanVien, currentDay,
                        finalGioBatDauCa, finalGioKetThucCa);
                result.tongTienMat = thongKeNhanVienDAO.getTongTienMat(finalMaNhanVien, currentDay, finalGioBatDauCa,
                        finalGioKetThucCa);
                result.danhSachHoaDonChiTiet = thongKeNhanVienDAO.getListHoaDonTrongCa(finalMaNhanVien, currentDay,
                        finalGioBatDauCa, finalGioKetThucCa);
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
                    JOptionPane.showMessageDialog(PanelBaoCao.this, "Lỗi khi tải dữ liệu báo cáo: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // ĐÃ SỬA: CẬP NHẬT ĐỂ TRUYỀN MAP
    private void xuLyNhapTienMat() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);

        NhapTienMat nhapTienMatDialog = new NhapTienMat(owner, tenNV, caLV, ngayLV, cashSystem);

        nhapTienMatDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                if (nhapTienMatDialog.isGiaoCaConfirmed()) {
                    // CẬP NHẬT: Gửi thêm Map chi tiết
                    giaoCaModel.setGiaoCaData(nhapTienMatDialog.getTienMatThucTeDaNhap(),
                            nhapTienMatDialog.getGhiChuDaNhap(), nhapTienMatDialog.getChiTietTienMatDaNhap() // <-- MỚI
                    );
                    updateSummaryPanel();
                }
            }
        });

        nhapTienMatDialog.setVisible(true);
    }

    private void updateTable(List<Object[]> list) {
        if (reportTableModel == null) {
            return;
        }
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

    public void updateData(String tenNV, String caLV, String ngayLV, double cashSystem, double transferSystem,
                           double totalSystem, List<Object[]> hoaDonList) {

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

    private static class ThongKeResult {
        double tongTienChuyenKhoan;
        double tongTienMat;
        double tongThuDuoc;
        String caLamViecText;
        LocalDate ngayLamViecDate;
        List<Object[]> danhSachHoaDonChiTiet;
    }
}