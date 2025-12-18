//package gui.application.form.NhatKyAudit;
//
//
//import com.toedter.calendar.JDateChooser;
//import controller.NhanVien_CTRL;
//import controller.NhatKyAudit_CTRL;
//import dao.NhatKyAudit_DAO;
//import entity.NhanVien;
//import entity.NhatKyAudit;
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.table.*;
//import java.awt.*;
//import java.awt.event.*;
//import java.time.*;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//
//public class PanelNhatKyAudit extends JPanel implements ActionListener, MouseListener, KeyListener {
//    private final NhatKyAudit_CTRL nhatKyAudit_ctrl;
//    private final NhanVien nhanVien;
//
//    public PanelNhatKyAudit(NhanVien nhanVien) {
//        this.nhanVien = nhanVien;
//        this.nhatKyAudit_ctrl = new NhatKyAudit_CTRL();
//
//        setLayout(new BorderLayout(10, 10));
//        setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        add(createFilterPanel(), BorderLayout.NORTH);
//        add(createTablePanel(), BorderLayout.CENTER);
//        add(createDetailPanel(), BorderLayout.SOUTH);
//
//        initCombos();
//        initTableStyle();
//        wireEvents();
//
//        // Default: 7 ngày gần nhất
//        setRangeLast7Days();
//        reloadData();
//    }
//
//    private final JDateChooser dcTuNgay = new JDateChooser();
//    private final JDateChooser dcDenNgay = new JDateChooser();
//
//    private final JComboBox<String> cbLoaiThaoTac = new JComboBox<>();
//    private final JComboBox<String> cbNhanVien = new JComboBox<>();
//
//    private final JTextField txtKeyword = new JTextField(22);
//
//    private final JButton btnHomNay = new JButton("Hôm nay");
//    private final JButton btn7Ngay = new JButton("7 ngày");
//    private final JButton btnLoc = new JButton("Lọc");
//    private final JButton btnLamMoi = new JButton("Làm mới");
//
//    private JTable tblNhatKy;
//    private DefaultTableModel model;
//
//    // ===== Detail =====
//    private final JLabel lblNhatKyID = new JLabel("-");
//    private final JLabel lblThoiDiem = new JLabel("-");
//    private final JLabel lblLoai = new JLabel("-");
//    private final JLabel lblNhanVien = new JLabel("-");
//    private final JLabel lblDoiTuong = new JLabel("-");
//    private final JTextArea txtChiTietFull = new JTextArea(6, 10);
//
//    // Data hiện tại đang hiển thị
//    private List<NhatKyAudit> current = new ArrayList<>();
//
//    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//
//    // ===================== UI =====================
//
//    private JPanel createFilterPanel() {
//        JPanel p = new JPanel(new GridBagLayout());
//        p.setBorder(BorderFactory.createTitledBorder("Bộ lọc nhật ký Audit"));
//
//        dcTuNgay.setDateFormatString("dd/MM/yyyy");
//        dcDenNgay.setDateFormatString("dd/MM/yyyy");
//
//        GridBagConstraints g = new GridBagConstraints();
//        g.insets = new Insets(6, 6, 6, 6);
//        g.fill = GridBagConstraints.HORIZONTAL;
//
//        // Row 0
//        g.gridy = 0;
//
//        g.gridx = 0;
//        p.add(new JLabel("Từ ngày:"), g);
//        g.gridx = 1;
//        p.add(dcTuNgay, g);
//
//        g.gridx = 2;
//        p.add(new JLabel("Đến ngày:"), g);
//        g.gridx = 3;
//        p.add(dcDenNgay, g);
//
//        g.gridx = 4;
//        p.add(btnHomNay, g);
//        g.gridx = 5;
//        p.add(btn7Ngay, g);
//
//        // Row 1
//        g.gridy = 1;
//
//        g.gridx = 0;
//        p.add(new JLabel("Loại thao tác:"), g);
//        g.gridx = 1;
//        p.add(cbLoaiThaoTac, g);
//
//        g.gridx = 2;
//        p.add(new JLabel("Nhân viên:"), g);
//        g.gridx = 3;
//        p.add(cbNhanVien, g);
//
//        g.gridx = 4;
//        p.add(new JLabel("Từ khóa:"), g);
//        g.gridx = 5;
//        p.add(txtKeyword, g);
//
//        // Row 2 buttons
//        g.gridy = 2;
//        g.gridx = 0;
//        g.gridwidth = 6;
//
//        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
//        btnPanel.add(btnLamMoi);
//        btnPanel.add(btnLoc);
//
//        p.add(btnPanel, g);
//
//        return p;
//    }
//
//    private JComponent createTablePanel() {
//        model = new DefaultTableModel(
//                new Object[]{"Thời điểm", "Loại", "Nhân viên", "Đối tượng", "Mã đối tượng", "Chi tiết (rút gọn)"},
//                0
//        ) {
//            @Override
//            public boolean isCellEditable(int row, int col) {
//                return false;
//            }
//        };
//
//        tblNhatKy = new JTable(model);
//        tblNhatKy.setRowHeight(28);
//        tblNhatKy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        tblNhatKy.getTableHeader().setReorderingAllowed(false);
//
//        // Sort
//        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
//        tblNhatKy.setRowSorter(sorter);
//
//        JScrollPane sp = new JScrollPane(tblNhatKy);
//        sp.setBorder(BorderFactory.createTitledBorder("Danh sách nhật ký"));
//        return sp;
//    }
//
//    private JPanel createDetailPanel() {
//        JPanel p = new JPanel(new BorderLayout(10, 10));
//        p.setBorder(BorderFactory.createTitledBorder("Chi tiết nhật ký"));
//
//        JPanel info = new JPanel(new GridLayout(2, 5, 10, 4));
//        info.add(new JLabel("Nhật ký ID:"));
//        info.add(new JLabel("Thời điểm:"));
//        info.add(new JLabel("Loại:"));
//        info.add(new JLabel("Nhân viên:"));
//        info.add(new JLabel("Đối tượng:"));
//
//        info.add(lblNhatKyID);
//        info.add(lblThoiDiem);
//        info.add(lblLoai);
//        info.add(lblNhanVien);
//        info.add(lblDoiTuong);
//
//        txtChiTietFull.setLineWrap(true);
//        txtChiTietFull.setWrapStyleWord(true);
//        txtChiTietFull.setEditable(false);
//
//        JPanel bottom = new JPanel(new BorderLayout());
//        bottom.add(new JScrollPane(txtChiTietFull), BorderLayout.CENTER);
//
//        JButton btnXemDialog = new JButton("Xem dialog");
////        btnXemDialog.addActionListener(e -> openSelectedDialog());
//
//        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
//        right.add(btnXemDialog);
//        bottom.add(right, BorderLayout.SOUTH);
//
//        p.add(info, BorderLayout.NORTH);
//        p.add(bottom, BorderLayout.CENTER);
//
//        return p;
//    }
//
//    private void initTableStyle() {
//        // Center vài cột
//        centerCol(0);
//        centerCol(1);
//        centerCol(2);
//        centerCol(3);
//        centerCol(4);
//
//        // Cột chi tiết rộng hơn
//        tblNhatKy.getColumnModel().getColumn(5).setPreferredWidth(450);
//    }
//
//    private void centerCol(int idx) {
//        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
//        r.setHorizontalAlignment(SwingConstants.CENTER);
//        tblNhatKy.getColumnModel().getColumn(idx).setCellRenderer(r);
//    }
//
//    // ===================== INIT DATA =====================
//
//    private void initCombos() {
//        // Loại thao tác: bạn có enum thì load enum, ở đây demo:
//        cbLoaiThaoTac.removeAllItems();
//        cbLoaiThaoTac.addItem("TẤT CẢ");
//        cbLoaiThaoTac.addItem("VE_BAN");
//        cbLoaiThaoTac.addItem("VE_HUY");
//        cbLoaiThaoTac.addItem("NHANVIEN_THEM");
//        cbLoaiThaoTac.addItem("NHANVIEN_SUA");
//        cbLoaiThaoTac.addItem("KHUYENMAI_THEM");
//        cbLoaiThaoTac.addItem("AUTH_LOGIN");
//        cbLoaiThaoTac.addItem("AUTH_LOGIN_FAIL");
//        cbLoaiThaoTac.addItem("AUTH_LOGOUT");
//
//        // Nhân viên: nên load từ DB (nhanVien_ctrl). Demo:
//        cbNhanVien.removeAllItems();
//        cbNhanVien.addItem("TẤT CẢ");
//        cbNhanVien.addItem("NV001");
//        cbNhanVien.addItem("NV002");
//        cbNhanVien.addItem("NV003");
//    }
//
//    private void wireEvents() {
//        btnLoc.addActionListener(e -> reloadData());
//
//        btnLamMoi.addActionListener(e -> {
//            txtKeyword.setText("");
//            cbLoaiThaoTac.setSelectedIndex(0);
//            cbNhanVien.setSelectedIndex(0);
//            setRangeLast7Days();
//            reloadData();
//        });
//
//        btnHomNay.addActionListener(e -> {
//            LocalDate today = LocalDate.now();
//            setRange(today, today);
//            reloadData();
//        });
//
//        btn7Ngay.addActionListener(e -> {
//            setRangeLast7Days();
//            reloadData();
//        });
//
//        txtKeyword.addActionListener(e -> reloadData());
//
//        tblNhatKy.getSelectionModel().addListSelectionListener(e -> {
////            if (!e.getValueIsAdjusting()) showSelectedDetail();
//        });
//
//        tblNhatKy.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
////                    openSelectedDialog();
//                }
//            }
//        });
//    }
//
//    // ===================== LOAD / FILTER =====================
//
//    private void reloadData() {
//        LocalDate tu = getLocalDate(dcTuNgay);
//        LocalDate den = getLocalDate(dcDenNgay);
//
//        if (tu == null || den == null) {
//            JOptionPane.showMessageDialog(this, "Vui lòng chọn đủ Từ ngày và Đến ngày.");
//            return;
//        }
//        if (den.isBefore(tu)) {
//            JOptionPane.showMessageDialog(this, "Đến ngày phải >= Từ ngày.");
//            return;
//        }
//
//        String loai = Objects.toString(cbLoaiThaoTac.getSelectedItem(), "TẤT CẢ");
//        String nv = Objects.toString(cbNhanVien.getSelectedItem(), "TẤT CẢ");
//        String keyword = txtKeyword.getText() == null ? "" : txtKeyword.getText().trim();
//
//        // 1) Lấy theo thời gian (DAO bạn đã có)
//        List<NhatKyAudit> ds = nhatKyAudit_ctrl.locNhatKyTheoKhoangThoiGian(tu, den);
//
//        // 2) Filter tiếp theo loại/nv/keyword (tại client cho dễ ghép)
//        current = filterClient(ds, loai, nv, keyword);
//
//        // 3) fill table
////        fillTable(current);
//
//        if (!current.isEmpty()) {
//            tblNhatKy.setRowSelectionInterval(0, 0);
//        } else {
//            clearDetail();
//        }
//    }
//
//    private List<NhatKyAudit> filterClient(List<NhatKyAudit> raw, String loai, String nv, String keyword) {
//        List<NhatKyAudit> out = new ArrayList<>();
//        for (NhatKyAudit x : raw) {
//            boolean ok = true;
//
//            if (!"TẤT CẢ".equalsIgnoreCase(loai)) {
////                ok &= loai.equalsIgnoreCase(x.getLoaiThaoTac());
//            }
//
//            if (!"TẤT CẢ".equalsIgnoreCase(nv)) {
//                ok &= nv.equalsIgnoreCase(x.getNhanVienID());
//            }
//
//            if (!keyword.isBlank()) {
//                ok &= (x.getChiTiet() != null && x.getChiTiet().toLowerCase().contains(keyword.toLowerCase()));
//            }
//
//            if (ok) out.add(x);
//        }
//        return out;
//    }
//
////    private void fillTable(List<NhatKyAudit> list) {
////        model.setRowCount(0);
////        for (NhatKyAudit x : list) {
////            String time = x.getThoiDiemThaoTac() == null ? "-" : x.getThoiDiemThaoTac().format(FMT);
////
////            // Map "đối tượng" + "mã đối tượng"
////            // Nếu bạn chưa có doiTuongLoai/doiTuongID thì có thể set đối tượng = "VE" và mã = getVeID()
////            String doiTuongLoai = nullToDash(getDoiTuongLoai(x));
////            String doiTuongId = nullToDash(getDoiTuongID(x));
////
////            model.addRow(new Object[]{
////                    time,
////                    nullToDash(x.getLoaiThaoTac()),
////                    nullToDash(x.getNhanVienID()),
////                    doiTuongLoai,
////                    doiTuongId,
////                    shorten(x.getChiTiet(), 90)
////            });
////        }
////    }
//
////    private void showSelectedDetail() {
////        int viewRow = tblNhatKy.getSelectedRow();
////        if (viewRow < 0) return;
////
////        int modelRow = tblNhatKy.convertRowIndexToModel(viewRow);
////        if (modelRow < 0 || modelRow >= current.size()) return;
////
////        NhatKyAudit x = current.get(modelRow);
////
////        lblNhatKyID.setText(nullToDash(x.getNhatKyID()));
////        lblThoiDiem.setText(x.getThoiDiemThaoTac() == null ? "-" : x.getThoiDiemThaoTac().format(FMT));
////        lblLoai.setText(nullToDash(x.getLoaiThaoTac()));
////        lblNhanVien.setText(nullToDash(x.getNhanVienID()));
////
////        lblDoiTuong.setText(nullToDash(getDoiTuongLoai(x)) + " - " + nullToDash(getDoiTuongID(x)));
////
////        txtChiTietFull.setText(x.getChiTiet() == null ? "" : x.getChiTiet());
////        txtChiTietFull.setCaretPosition(0);
////    }
//
////    private void openSelectedDialog() {
////        int viewRow = tblNhatKy.getSelectedRow();
////        if (viewRow < 0) return;
////
////        int modelRow = tblNhatKy.convertRowIndexToModel(viewRow);
////        if (modelRow < 0 || modelRow >= current.size()) return;
////
////        NhatKyAudit x = current.get(modelRow);
////
////        JTextArea area = new JTextArea(12, 60);
////        area.setLineWrap(true);
////        area.setWrapStyleWord(true);
////        area.setEditable(false);
////
////        area.setText("""
////                Nhật ký ID: %s
////                Thời điểm : %s
////                Loại      : %s
////                Nhân viên : %s
////                Đối tượng : %s - %s
////
////                Chi tiết:
////                %s
////                """.formatted(
////                nullToDash(x.getNhatKyID()),
////                x.getThoiDiemThaoTac() == null ? "-" : x.getThoiDiemThaoTac().format(FMT),
////                nullToDash(x.getLoaiThaoTac()),
////                nullToDash(x.getNhanVienID()),
////                nullToDash(getDoiTuongLoai(x)),
////                nullToDash(getDoiTuongID(x)),
////                x.getChiTiet() == null ? "" : x.getChiTiet()
////        ));
////        area.setCaretPosition(0);
////
////        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Chi tiết nhật ký", JOptionPane.INFORMATION_MESSAGE);
////    }
//
//    private void clearDetail() {
//        lblNhatKyID.setText("-");
//        lblThoiDiem.setText("-");
//        lblLoai.setText("-");
//        lblNhanVien.setText("-");
//        lblDoiTuong.setText("-");
//        txtChiTietFull.setText("");
//    }
//
//    // ===================== MAP FIELD =====================
//    // Chỗ này để bạn chỉnh theo entity thật của bạn (veID hay doiTuongID...)
//
////    private String getDoiTuongLoai(NhatKyAudit x) {
////        // Nếu entity bạn có getDoiTuongThaoTac() hoặc getDoiTuongLoai() thì đổi lại:
////        // return x.getDoiTuongThaoTac();
////        // Tạm suy luận theo veID:
////        if (x.getVeID() != null && !x.getVeID().isBlank()) return "VE";
////        return "-";
////    }
////
////    private String getDoiTuongID(NhatKyAudit x) {
////        // Nếu entity bạn có getDoiTuongID() thì đổi lại:
////        // return x.getDoiTuongID();
////        return x.getVeID(); // hoặc null
////    }
//
//    // ===================== DATE HELPERS =====================
//
//    private void setRangeLast7Days() {
//        LocalDate den = LocalDate.now();
//        LocalDate tu = den.minusDays(6);
//        setRange(tu, den);
//    }
//
//    private void setRange(LocalDate tu, LocalDate den) {
//        dcTuNgay.setDate(java.sql.Date.valueOf(tu));
//        dcDenNgay.setDate(java.sql.Date.valueOf(den));
//    }
//
//    private LocalDate getLocalDate(JDateChooser dc) {
//        if (dc.getDate() == null) return null;
//        return dc.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//    }
//
//    private String shorten(String s, int max) {
//        if (s == null) return "-";
//        s = s.replaceAll("\\s+", " ").trim();
//        if (s.length() <= max) return s;
//        return s.substring(0, max - 3) + "...";
//    }
//
//    private String nullToDash(String s) {
//        return (s == null || s.isBlank()) ? "-" : s;
//    }
//
//    @Override public void actionPerformed(ActionEvent e) {}
//    @Override public void keyTyped(KeyEvent e) {}
//    @Override public void keyPressed(KeyEvent e) {
//    }
//    @Override public void keyReleased(KeyEvent e) {}
//    @Override public void mouseClicked(MouseEvent e) {
//
//    }
//    @Override public void mousePressed(MouseEvent e) {
//
//    }
//    @Override public void mouseReleased(MouseEvent e) {
//
//    }
//    @Override public void mouseEntered(MouseEvent e) {
//
//    }
//    @Override public void mouseExited(MouseEvent e) {
//
//    }
//}
