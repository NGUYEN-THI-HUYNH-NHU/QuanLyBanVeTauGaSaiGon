package gui.application.form.nhatKyAudit;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import controller.NhanVien_CTRL;
import controller.NhatKyAudit_CTRL;
import dto.NhanVienDTO;
import entity.NhanVien;
import entity.NhatKyAudit;
import gui.tuyChinh.LeftTopRenderer;
import mapper.NhanVienMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanelNhatKyAudit extends JPanel implements ActionListener, MouseListener {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final NhatKyAudit_CTRL nhatKyAudit_ctrl;
    private final NhanVien_CTRL nhanVien_ctrl;
    private final NhanVienDTO nhanVienHienTai;
    private final Color base_color = new Color(36, 104, 155);
    private JTable table;
    private DefaultTableModel tableModel;
    private JDateChooser dcTuNgay, dcDenNgay;
    private JComboBox<entity.type.NhatKyAudit> cboLoai;
    private JComboBox<String> cboNhanVien;
    private JButton btnLamMoi, btnLoc, btnHomNay, btn7Ngay;
    private List<NhatKyAudit> current = new ArrayList<>();

    // Thành phần phân trang
    private int currentPage = 1;
    private int pageSize = 15;
    private long totalRows = 0;
    private JPanel paginationNumberPanel;
    private JComboBox<Integer> cboPageSize;
    private JButton btnPrevPage, btnNextPage;

    public PanelNhatKyAudit(NhanVienDTO nhanVien) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        this.nhanVienHienTai = nhanVien;

        this.nhatKyAudit_ctrl = new NhatKyAudit_CTRL();
        this.nhanVien_ctrl = new NhanVien_CTRL(NhanVienMapper.INSTANCE.toEntity(nhanVien));

        initUI();

        loadDefault7Days();
        loadDataToTable();
    }

    private void initUI() {
        // ===== TITLE =====
        JLabel lblTitle = new JLabel("NHẬT KÝ AUDIT");
        lblTitle.setFont(new Font(getFont().getFontName(), Font.BOLD, 24));
        lblTitle.setForeground(base_color);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== 1) PANEL LỌC =====
        JPanel pnlLoc = new JPanel(new BorderLayout());
        pnlLoc.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(base_color), "Bộ lọc",
                TitledBorder.LEFT, TitledBorder.TOP, new Font(getFont().getFontName(), Font.BOLD, 16), base_color));

        JPanel panelTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        dcTuNgay = new JDateChooser();
        dcDenNgay = new JDateChooser();
        dcTuNgay.setDateFormatString("dd/MM/yyyy");
        dcDenNgay.setDateFormatString("dd/MM/yyyy");

        cboLoai = new JComboBox<>();
        cboNhanVien = new JComboBox<>();

        btnHomNay = new JButton("Hôm nay");
        btn7Ngay = new JButton("7 ngày");

        btnLamMoi = new JButton("(F5) Làm mới");
        btnLamMoi.setIcon(new FlatSVGIcon("icon/svg/refresh-1.svg", 0.8f));
        btnLamMoi.setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
        btnLamMoi.setBackground(base_color);
        btnLamMoi.setForeground(Color.WHITE);

        btnLoc = new JButton("Lọc");
        btnLoc.setIcon(new FlatSVGIcon("icon/svg/search.svg", 0.8f));
        btnLoc.setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
        btnLoc.setBackground(base_color);
        btnLoc.setForeground(Color.WHITE);

        panelTimKiem.add(new JLabel("Từ ngày:"));
        panelTimKiem.add(dcTuNgay);
        panelTimKiem.add(new JLabel("Đến ngày:"));
        panelTimKiem.add(dcDenNgay);

        panelTimKiem.add(new JLabel("Loại:"));
        panelTimKiem.add(cboLoai);

        panelTimKiem.add(new JLabel("Nhân viên:"));
        panelTimKiem.add(cboNhanVien);

        panelTimKiem.add(btnHomNay);
        panelTimKiem.add(btn7Ngay);
        panelTimKiem.add(btnLamMoi);
        panelTimKiem.add(btnLoc);

        pnlLoc.add(panelTimKiem, BorderLayout.CENTER);

        // ===== NORTH CONTAINER =====
        JPanel pnlNorthContainer = new JPanel();
        pnlNorthContainer.setLayout(new BoxLayout(pnlNorthContainer, BoxLayout.Y_AXIS));

        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlLoc.setMaximumSize(new Dimension(Integer.MAX_VALUE, pnlLoc.getPreferredSize().height));

        pnlNorthContainer.add(lblTitle);
        pnlNorthContainer.add(pnlLoc);

        add(pnlNorthContainer, BorderLayout.NORTH);

        // ===== 2) TABLE =====
        String[] columns = {"Mã nhật ký", "Đối tượng thao tác", "Nhân viên", "Loại thao tác", "Đối tượng ID",
                "Thời điểm", "Chi tiết"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));

        // set độ rộng cột (đúng 7 cột)
        table.getColumnModel().getColumn(0).setPreferredWidth(100); // Mã nhật ký
        table.getColumnModel().getColumn(1).setPreferredWidth(140); // Đối tượng thao tác
        table.getColumnModel().getColumn(2).setPreferredWidth(110); // Nhân viên
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Loại thao tác
        table.getColumnModel().getColumn(4).setPreferredWidth(110); // Đối tượng ID
        table.getColumnModel().getColumn(5).setPreferredWidth(160); // Thời điểm
        table.getColumnModel().getColumn(6).setPreferredWidth(520); // Chi tiết

        // căn giữa 1 số cột (trừ Chi tiết)
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 6) {
                table.getColumnModel().getColumn(i).setCellRenderer(center);
            }
        }

        // header style (giữ màu)
        JTableHeader header = table.getTableHeader();
        header.setEnabled(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                lbl.setBackground(base_color);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font(getFont().getFontName(), Font.BOLD, 14));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.WHITE));
                return lbl;
            }
        });

        table.getColumnModel().getColumn(0).setCellRenderer(new LeftTopRenderer());
        table.getColumnModel().getColumn(1).setCellRenderer(new LeftTopRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new LeftTopRenderer());
        table.getColumnModel().getColumn(3).setCellRenderer(new LeftTopRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new LeftTopRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new LeftTopRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new LeftTopRenderer());

        // sorter
        table.setRowSorter(new TableRowSorter<>(tableModel));

        // Thành phần phân trang
        JPanel pnlTableContainer = new JPanel(new BorderLayout());
        pnlTableContainer.add(new JScrollPane(table), BorderLayout.CENTER);
        pnlTableContainer.add(createPaginationPanel(), BorderLayout.SOUTH);

        add(pnlTableContainer, BorderLayout.CENTER);

        // events
        table.addMouseListener(this);
        btnLoc.addActionListener(this);
        btn7Ngay.addActionListener(this);
        btnHomNay.addActionListener(this);
        btnLamMoi.addActionListener(this);

        initCombos();
    }

    private void initCombos() {
        cboLoai.removeAllItems();
        cboLoai.addItem(null);
        for (entity.type.NhatKyAudit loai : entity.type.NhatKyAudit.values()) {
            cboLoai.addItem(loai);
        }
        cboLoai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("TẤT CẢ");
                }
                return this;
            }
        });
        cboLoai.setSelectedItem(null);

        cboNhanVien.removeAllItems();
        cboNhanVien.addItem("TẤT CẢ");

        // thêm nv hiện tại lên đầu cho dễ chọn
        if (nhanVienHienTai != null && nhanVienHienTai.getId() != null) {
            cboNhanVien.addItem(nhanVienHienTai.getId());
        }

        for (NhanVien nv : nhanVien_ctrl.layDanhSachNhanVien()) {
            if (nv != null && nv.getNhanVienID() != null) {
                cboNhanVien.addItem(nv.getNhanVienID());
            }
        }
    }

    private void loadDataToTable() {
        // 1. Lấy thông tin từ bộ lọc trên giao diện (Ngày, Nhân viên, Loại thao tác)
        LocalDate tu = getLocalDate(dcTuNgay);
        LocalDate den = getLocalDate(dcDenNgay);
        String nv = (String) cboNhanVien.getSelectedItem();
        if ("TẤT CẢ".equals(nv)) nv = null;

        entity.type.NhatKyAudit loaiEnum = (entity.type.NhatKyAudit) cboLoai.getSelectedItem();
        String loai = (loaiEnum == null) ? null : loaiEnum.name();

        // 2. GỌI XUỐNG TẦNG DƯỚI: Lấy đúng "khúc" dữ liệu của trang hiện tại
        // current bây giờ chỉ chứa 15-20 dòng tùy pageSize, chứ không phải toàn bộ DB
        current = nhatKyAudit_ctrl.layDanhSachPhanTrang(currentPage, pageSize, tu, den, nv, loai);
        totalRows = nhatKyAudit_ctrl.demTongSoDong(tu, den, nv, loai);

        // 3. ĐỔ DỮ LIỆU LÊN BẢNG (Đây chính là logic hàm cũ của bạn)
        tableModel.setRowCount(0);
        for (NhatKyAudit a : current) {
            String loaiThaoTac = (a.getLoaiThaoTac() == null) ? "" : a.getLoaiThaoTac().name();
            String time = (a.getThoiDiemThaoTac() == null) ? "" : a.getThoiDiemThaoTac().format(FMT);

            tableModel.addRow(new Object[]{
                    a.getNhatKyAuditID(),
                    a.getDoiTuongThaoTac(),
                    a.getNhanVienID(),
                    loaiThaoTac,
                    a.getDoiTuongID(),
                    time,
                    String.format("<html>%s</html>", a.getChiTiet())
            });
        }

        renderPageNumbers();
    }

    // ================ PHÂN TRANG ================
    private JPanel createPaginationPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(5, 10, 5, 10));

        // Nhóm nút số trang ở giữa
        JPanel pnlCenter = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlCenter.setBackground(Color.WHITE);
        btnPrevPage = createPageBtn("<", false);
        btnNextPage = createPageBtn(">", false);
        paginationNumberPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        paginationNumberPanel.setBackground(Color.WHITE);

        pnlCenter.add(btnPrevPage);
        pnlCenter.add(paginationNumberPanel);
        pnlCenter.add(btnNextPage);

        // Nhóm chọn số dòng bên phải
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setBackground(Color.WHITE);
        pnlRight.add(new JLabel("Số dòng:"));
        cboPageSize = new JComboBox<>(new Integer[]{15, 30, 50});
        cboPageSize.setSelectedItem(pageSize);
        pnlRight.add(cboPageSize);

        p.add(pnlCenter, BorderLayout.CENTER);
        p.add(pnlRight, BorderLayout.EAST);

        // Sự kiện khi thay đổi số dòng
        cboPageSize.addActionListener(e -> {
            pageSize = (int) cboPageSize.getSelectedItem();
            currentPage = 1;
            loadDataToTable();
        });

        // Sự kiện nút Tiến/Lùi
        btnPrevPage.addActionListener(e -> { if (currentPage > 1) { currentPage--; loadDataToTable(); } });
        btnNextPage.addActionListener(e -> {
            int totalPages = (int) Math.ceil((double) totalRows / pageSize);
            if (currentPage < totalPages) { currentPage++; loadDataToTable(); }
        });

        return p;
    }

    private JButton createPageBtn(String t, boolean sel) {
        JButton b = new JButton(t);
        b.setPreferredSize(new Dimension(40, 35));
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (sel) {
            b.setBackground(base_color);
            b.setForeground(Color.WHITE);
        } else {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.BLACK);
        }
        return b;
    }

    private void renderPageNumbers() {
        paginationNumberPanel.removeAll();
        int totalPages = (int) Math.ceil((double) totalRows / pageSize);
        if (totalPages <= 0) totalPages = 1;

        // Thuật toán hiển thị 5 nút số quanh trang hiện tại
        int start = Math.max(1, currentPage - 2);
        int end = Math.min(totalPages, start + 4);
        if (end - start < 4) start = Math.max(1, end - 4);
        start = Math.max(1, start);

        for (int i = start; i <= end; i++) {
            final int p = i;
            JButton b = createPageBtn(String.valueOf(i), i == currentPage);
            b.addActionListener(e -> { currentPage = p; loadDataToTable(); });
            paginationNumberPanel.add(b);
        }

        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);

        paginationNumberPanel.revalidate();
        paginationNumberPanel.repaint();
    }

    // ================= FILTER =================

    private void locTheoKhoangThoiGian() {
        currentPage = 1;
        LocalDate tu = getLocalDate(dcTuNgay);
        LocalDate den = getLocalDate(dcDenNgay);

        if (tu == null || den == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ Từ ngày và Đến ngày.");
            return;
        }
        if (den.isBefore(tu)) {
            JOptionPane.showMessageDialog(this, "Đến ngày phải >= Từ ngày.");
            return;
        }

        String nhanVien = (String) cboNhanVien.getSelectedItem();
        if ("TẤT CẢ".equals(nhanVien)) {
            nhanVien = null;
        }

        entity.type.NhatKyAudit loaiEnum = (entity.type.NhatKyAudit) cboLoai.getSelectedItem();
        String loai = (loaiEnum == null) ? null : loaiEnum.name();

        String doiTuongID = null;

        List<NhatKyAudit> list = nhatKyAudit_ctrl.locNhatKy(tu, den, nhanVien, loai, doiTuongID);
        loadDataToTable();
    }

    private void onLoc() {
        locTheoKhoangThoiGian();
    }

    private void onLamMoi() {
        cboNhanVien.setSelectedItem("TẤT CẢ");
        cboLoai.setSelectedItem(null);

        loadDefault7Days();

        // làm mới = load full (không lọc)
        List<NhatKyAudit> list = nhatKyAudit_ctrl.layDanhSachNhatKy();
        loadDataToTable();
    }

    private void onHomNay() {
        LocalDate today = LocalDate.now();
        dcTuNgay.setDate(java.sql.Date.valueOf(today));
        dcDenNgay.setDate(java.sql.Date.valueOf(today));
        locTheoKhoangThoiGian();
    }

    private void on7Ngay() {
        loadDefault7Days();
        locTheoKhoangThoiGian();
    }

    // ================= DIALOG =================

    private void openDialogSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= current.size()) {
            return;
        }

        NhatKyAudit audit = current.get(modelRow);

        String tenNV = nhatKyAudit_ctrl.layTenNhanVienTheoMaNV(audit.getNhanVienID());
        if (tenNV == null || tenNV.isBlank()) {
            tenNV = "-";
        }

        Window w = SwingUtilities.getWindowAncestor(this);
        DialogChiTietAudit dlg = new DialogChiTietAudit(w, audit, tenNV);
        dlg.setVisible(true);
    }

    // ================= HELPERS =================

    private void loadDefault7Days() {
        LocalDate den = LocalDate.now();
        LocalDate tu = den.minusDays(6);
        setRange(tu, den);
    }

    private void setRange(LocalDate tu, LocalDate den) {
        dcTuNgay.setDate(java.sql.Date.valueOf(tu));
        dcDenNgay.setDate(java.sql.Date.valueOf(den));
    }

    private LocalDate getLocalDate(JDateChooser dc) {
        if (dc.getDate() == null) {
            return null;
        }
        return dc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // ================= EVENTS =================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLoc) {
            onLoc();
        } else if (e.getSource() == btnLamMoi) {
            onLamMoi();
        } else if (e.getSource() == btnHomNay) {
            onHomNay();
        } else if (e.getSource() == btn7Ngay) {
            on7Ngay();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() == table && e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
            int viewRow = table.rowAtPoint(e.getPoint());
            if (viewRow >= 0) {
                table.setRowSelectionInterval(viewRow, viewRow);
                openDialogSelectedRow();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
