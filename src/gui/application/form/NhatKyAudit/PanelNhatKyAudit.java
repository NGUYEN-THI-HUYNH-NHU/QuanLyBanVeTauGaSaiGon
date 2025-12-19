package gui.application.form.NhatKyAudit;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import controller.NhanVien_CTRL;
import controller.NhatKyAudit_CTRL;
import entity.NhanVien;
import entity.NhatKyAudit;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanelNhatKyAudit extends JPanel implements ActionListener, MouseListener {

    private JTable table;
    private DefaultTableModel tableModel;

    private JDateChooser dcTuNgay, dcDenNgay;
    private JComboBox<entity.type.NhatKyAudit> cboLoai;
    private JComboBox<String> cboNhanVien;
    private JButton btnLamMoi, btnLoc, btnHomNay, btn7Ngay;

    private final NhatKyAudit_CTRL nhatKyAudit_ctrl;
    private final NhanVien_CTRL nhanVien_ctrl;
    private final NhanVien nhanVienHienTai;

    private final Color base_color = new Color(36, 104, 155);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private List<NhatKyAudit> current = new ArrayList<>();

    public PanelNhatKyAudit(NhanVien nhanVien) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        this.nhanVienHienTai = nhanVien;

        this.nhatKyAudit_ctrl = new NhatKyAudit_CTRL();
        this.nhanVien_ctrl = new NhanVien_CTRL(nhanVienHienTai); // ✅ FIX: truyền người đăng nhập

        initUI();

        // default UI hiển thị 7 ngày gần nhất nhưng KHÔNG lọc tự động
        loadDefault7Days();
        loadDataToTable(nhatKyAudit_ctrl.layDanhSachNhatKy());
    }

    private void initUI() {
        // ===== TITLE =====
        JLabel lblTitle = new JLabel("NHẬT KÝ AUDIT");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(base_color);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        // ===== 1) PANEL LỌC =====
        JPanel pnlLoc = new JPanel(new BorderLayout());
        pnlLoc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(base_color),
                "Bộ lọc", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                base_color
        ));

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
        btnLamMoi.setIcon(new FlatSVGIcon("gui/icon/svg/refresh-1.svg", 0.8f));
        btnLamMoi.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLamMoi.setBackground(base_color);
        btnLamMoi.setForeground(Color.WHITE);

        btnLoc = new JButton("Lọc");
        btnLoc.setIcon(new FlatSVGIcon("gui/icon/svg/search.svg", 0.8f));
        btnLoc.setFont(new Font("Segoe UI", Font.BOLD, 14));
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
        String[] columns = {
                "Mã nhật ký",
                "Đối tượng thao tác",
                "Nhân viên",
                "Loại thao tác",
                "Đối tượng ID",
                "Thời điểm",
                "Chi tiết"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(table.getFont().deriveFont(14f));
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
            if (i != 6) table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // header style (giữ màu)
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(base_color);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.WHITE));
                return lbl;
            }
        });

        // sorter
        table.setRowSorter(new TableRowSorter<>(tableModel));

        add(new JScrollPane(table), BorderLayout.CENTER);

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
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) setText("TẤT CẢ");
                return this;
            }
        });
        cboLoai.setSelectedItem(null);

        cboNhanVien.removeAllItems();
        cboNhanVien.addItem("TẤT CẢ");

        // thêm nv hiện tại lên đầu cho dễ chọn
        if (nhanVienHienTai != null && nhanVienHienTai.getNhanVienID() != null) {
            cboNhanVien.addItem(nhanVienHienTai.getNhanVienID());
        }

        for (NhanVien nv : nhanVien_ctrl.layDanhSachNhanVien()) {
            if (nv != null && nv.getNhanVienID() != null) {
                cboNhanVien.addItem(nv.getNhanVienID());
            }
        }
    }

    private void loadDataToTable(List<NhatKyAudit> list) {
        current = (list == null) ? new ArrayList<>() : list;
        tableModel.setRowCount(0);

        for (NhatKyAudit a : current) {
            String loai = (a.getLoaiThaoTac() == null) ? "" : a.getLoaiThaoTac().name();
            String time = (a.getThoiDiemThaoTac() == null) ? "" : a.getThoiDiemThaoTac().format(FMT);

            tableModel.addRow(new Object[]{
                    a.getNhatKyAuditID(),
                    a.getDoiTuongThaoTac(),
                    a.getNhanVienID(),
                    loai,
                    a.getDoiTuongID(),
                    time,
                    a.getChiTiet()
            });
        }
    }

    // ================= FILTER =================

    private void locTheoKhoangThoiGian() {
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
        if ("TẤT CẢ".equals(nhanVien)) nhanVien = null;

        entity.type.NhatKyAudit loaiEnum = (entity.type.NhatKyAudit) cboLoai.getSelectedItem();
        String loai = (loaiEnum == null) ? null : loaiEnum.name();

        String doiTuongID = null;

        List<NhatKyAudit> list = nhatKyAudit_ctrl.locNhatKy(tu, den, nhanVien, loai, doiTuongID);
        loadDataToTable(list);
    }

    private void onLoc() { locTheoKhoangThoiGian(); }

    private void onLamMoi() {
        cboNhanVien.setSelectedItem("TẤT CẢ");
        cboLoai.setSelectedItem(null);

        loadDefault7Days();

        // làm mới = load full (không lọc)
        List<NhatKyAudit> list = nhatKyAudit_ctrl.layDanhSachNhatKy();
        loadDataToTable(list);
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
        if (viewRow < 0) return;

        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= current.size()) return;

        NhatKyAudit audit = current.get(modelRow);

        String tenNV = nhatKyAudit_ctrl.layTenNhanVienTheoMaNV(audit.getNhanVienID());
        if (tenNV == null || tenNV.isBlank()) tenNV = "-";

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
        if (dc.getDate() == null) return null;
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
        if (e.getSource() == table &&
                e.getClickCount() == 2 &&
                SwingUtilities.isLeftMouseButton(e)) {
            int viewRow = table.rowAtPoint(e.getPoint());
            if (viewRow >= 0) {
                table.setRowSelectionInterval(viewRow, viewRow);
                openDialogSelectedRow();
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
