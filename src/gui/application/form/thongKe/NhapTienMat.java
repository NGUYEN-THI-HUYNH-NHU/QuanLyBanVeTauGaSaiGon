package gui.application.form.thongKe;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;

public class NhapTienMat extends JDialog {

    private final DecimalFormat df = new DecimalFormat("#,##0 VNĐ");
    private final double heThong_TienMatDoanhThu;

    private double tienMatThucTeDaLuu = 0.0;
    private String ghiChuDaLuu = "";
    private boolean isGiaoCaConfirmed = false;

    // Components dùng để nhập và tính toán
    private Hashtable<Integer, JSpinner> spinners = new Hashtable<>();
    private Hashtable<Integer, JLabel> labelsThanhTien = new Hashtable<>();
    private final int[] menhGiaArr = {500000, 200000, 100000, 50000, 20000, 10000, 5000, 2000, 1000};
    private JSpinner spnTienLeKhac;
    private JLabel lblTongTienMatThucTe;
    private JLabel lblTienHeThong_SoSanh;
    private JLabel lblTienThucTe_SoSanh;
    private JLabel lblChenhLech;
    private JLabel lblTrangThai;
    private JTextArea txtGhiChu;
    private JButton btnXacNhanGiaoCa;

    // Components hiển thị thông tin nhân viên
    private JLabel lblTenNhanVien, lblCaLamViec, lblNgayLamViec;

    // ====== CONSTRUCTOR ======
    public NhapTienMat(Frame owner, String tenNV, String ca, String ngay, double tienMatHeThong) {
        super(owner, "Lập Báo Cáo Giao Ca", true);
        this.heThong_TienMatDoanhThu = tienMatHeThong;

        this.setSize(1000, 700);
        this.setLocationRelativeTo(owner);

        initComponents();
        addEvents();

        // Gán dữ liệu ban đầu
        lblTenNhanVien.setText(tenNV);
        lblCaLamViec.setText(ca);
        lblNgayLamViec.setText(ngay);

        updateDoiSoat(heThong_TienMatDoanhThu, 0);

        // Tập trung vào ô tiền lẻ khi mở lên (tùy chọn)
        SwingUtilities.invokeLater(() -> {
            spnTienLeKhac.requestFocusInWindow();
        });
    }

    // =======================================================
    // GETTERS & SETTERS (PUBLIC)
    // =======================================================

    public double getTienMatThucTeDaNhap() {
        return tienMatThucTeDaLuu;
    }

    public boolean isGiaoCaConfirmed() {
        return isGiaoCaConfirmed;
    }

    public String getGhiChuDaNhap() {
        return ghiChuDaLuu;
    }

    public Map<Integer, Integer> getChiTietTienMatDaNhap() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int money : menhGiaArr) {
            if (spinners.containsKey(money)) {
                JSpinner sp = spinners.get(money);
                int soLuong = (Integer) sp.getValue();
                if (soLuong > 0) {
                    map.put(money, soLuong);
                }
            }
        }
        return map;
    }

    /**
     * Nạp lại dữ liệu cũ vào form
     */
    public void setDuLieuCu(java.util.Map<Integer, Integer> mapCu, String ghiChuCu) {
        if (mapCu != null) {
            for (java.util.Map.Entry<Integer, Integer> entry : mapCu.entrySet()) {
                int menhGia = entry.getKey();
                int soLuong = entry.getValue();
                if (spinners.containsKey(menhGia)) {
                    spinners.get(menhGia).setValue(soLuong);
                }
            }
        }
        if (ghiChuCu != null) {
            txtGhiChu.setText(ghiChuCu);
        }
        updateTongTienMat();
    }

    // =======================================================
    // SETUP UI
    // =======================================================

    private void initComponents() {
        JPanel pnlMain = new JPanel(new BorderLayout(15, 15));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlMain.setBackground(Color.WHITE);

        pnlMain.add(createTitlePanel(), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.55);
        splitPane.setBorder(null);

        splitPane.setLeftComponent(createKiemKePanel());
        splitPane.setRightComponent(createDoiSoatPanel());
        pnlMain.add(splitPane, BorderLayout.CENTER);

        pnlMain.add(createButtonPanel(), BorderLayout.SOUTH);

        setContentPane(pnlMain);
    }

    private JPanel createTitlePanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel lblTitle = new JLabel("Lập Báo Cáo Giao Ca");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 26));

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        pnl.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1; gbc.gridx = 0;
        pnl.add(new JLabel("Tên nhân viên:"), gbc);
        gbc.gridx = 1;
        lblTenNhanVien = new JLabel("...");
        lblTenNhanVien.setFont(new Font("Arial", Font.BOLD, 14));
        pnl.add(lblTenNhanVien, gbc);

        gbc.gridx = 2;
        pnl.add(new JLabel("Ca làm việc:"), gbc);
        gbc.gridx = 3;
        lblCaLamViec = new JLabel("...");
        lblCaLamViec.setFont(new Font("Arial", Font.BOLD, 14));
        pnl.add(lblCaLamViec, gbc);

        gbc.gridy = 2; gbc.gridx = 0;
        pnl.add(new JLabel("Ngày làm việc:"), gbc);
        gbc.gridx = 1;
        lblNgayLamViec = new JLabel("...");
        lblNgayLamViec.setFont(new Font("Arial", Font.BOLD, 14));
        pnl.add(lblNgayLamViec, gbc);

        return pnl;
    }

    private Component createKiemKePanel() {
        JPanel pnl = new JPanel(new BorderLayout(10, 10));
        pnl.setOpaque(false);
        pnl.setBorder(new TitledBorder("Kiểm kê tiền mặt trong két"));

        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlInputs.add(new JLabel("Mệnh giá"), gbc);
        gbc.gridx = 1;
        pnlInputs.add(new JLabel("Số lượng"), gbc);
        gbc.gridx = 2;
        pnlInputs.add(new JLabel("Thành tiền"), gbc);

        int row = 1;
        for (int money : menhGiaArr) {
            gbc.gridy = row;
            gbc.gridx = 0;
            pnlInputs.add(new JLabel(String.format("%,d", money)), gbc);

            gbc.gridx = 1;
            JSpinner sp = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
            spinners.put(money, sp);
            pnlInputs.add(sp, gbc);

            gbc.gridx = 2;
            JLabel lblThanhTien = new JLabel(df.format(0));
            labelsThanhTien.put(money, lblThanhTien);
            pnlInputs.add(lblThanhTien, gbc);

            row++;
        }

        gbc.gridy = row; gbc.gridx = 0;
        pnlInputs.add(new JLabel("Tiền lẻ/khác"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        spnTienLeKhac = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000000.0, 1000.0));
        pnlInputs.add(spnTienLeKhac, gbc);

        JPanel pnlTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlTotal.setOpaque(false);
        pnlTotal.add(new JLabel("Tổng tiền mặt thực tế (B):"));
        lblTongTienMatThucTe = new JLabel(df.format(0));
        lblTongTienMatThucTe.setForeground(new Color(0, 102, 0));
        lblTongTienMatThucTe.setFont(new Font("Arial", Font.BOLD, 18));
        pnlTotal.add(lblTongTienMatThucTe);

        pnl.add(new JScrollPane(pnlInputs), BorderLayout.CENTER);
        pnl.add(pnlTotal, BorderLayout.SOUTH);
        return pnl;
    }

    private JPanel createDoiSoatPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.setBorder(new TitledBorder("Kết quả đối soát & ghi chú"));

        JPanel pnlInfo = new JPanel(new GridBagLayout());
        pnlInfo.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        pnlInfo.add(new JLabel("Doanh thu hệ thống (A):"), gbc);
        gbc.gridx = 1;
        lblTienHeThong_SoSanh = new JLabel("0 VNĐ");
        lblTienHeThong_SoSanh.setFont(new Font("Arial", Font.BOLD, 16));
        lblTienHeThong_SoSanh.setForeground(Color.BLUE);
        pnlInfo.add(lblTienHeThong_SoSanh, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlInfo.add(new JLabel("Tiền mặt thực tế (B):"), gbc);
        gbc.gridx = 1;
        lblTienThucTe_SoSanh = new JLabel("0 VNĐ");
        lblTienThucTe_SoSanh.setFont(new Font("Arial", Font.BOLD, 16));
        lblTienThucTe_SoSanh.setForeground(new Color(0, 102, 0));
        pnlInfo.add(lblTienThucTe_SoSanh, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlInfo.add(new JLabel("CHÊNH LỆCH (B - A):"), gbc);
        gbc.gridx = 1;
        lblChenhLech = new JLabel("0 VNĐ");
        lblChenhLech.setFont(new Font("Arial", Font.BOLD, 20));
        pnlInfo.add(lblChenhLech, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        lblTrangThai = new JLabel("(Chưa kiểm kê)");
        lblTrangThai.setForeground(Color.GRAY);
        pnlInfo.add(lblTrangThai, gbc);

        pnl.add(pnlInfo, BorderLayout.NORTH);

        txtGhiChu = new JTextArea();
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        JPanel pnlGhiChu = new JPanel(new BorderLayout());
        pnlGhiChu.setOpaque(false);
        pnlGhiChu.setBorder(BorderFactory.createTitledBorder("Ghi chú"));
        pnlGhiChu.add(scrollGhiChu, BorderLayout.CENTER);

        pnl.add(pnlGhiChu, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createButtonPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnl.setOpaque(false);
        btnXacNhanGiaoCa = new JButton("Xác Nhận");
        btnXacNhanGiaoCa.setBackground(new Color(0, 102, 51));
        btnXacNhanGiaoCa.setForeground(Color.WHITE);
        btnXacNhanGiaoCa.setFont(new Font("Arial", Font.BOLD, 15));
        pnl.add(btnXacNhanGiaoCa);
        return pnl;
    }

    // =======================================================
    // LOGIC & EVENTS
    // =======================================================

    private void addEvents() {
        ChangeListener listener = e -> updateTongTienMat();

        for (JSpinner s : spinners.values()) {
            s.addChangeListener(listener);
            // THÊM: Tính năng tự bôi đen cho từng spinner
            addAutoSelectOnFocus(s);
        }

        spnTienLeKhac.addChangeListener(listener);
        // THÊM: Tính năng tự bôi đen cho spinner tiền lẻ
        addAutoSelectOnFocus(spnTienLeKhac);

        btnXacNhanGiaoCa.addActionListener(e -> xuLyXacNhanGiaoCa());
    }

    /**
     * Hàm hỗ trợ: Tự động bôi đen (Select All) khi JSpinner nhận Focus
     */
    private void addAutoSelectOnFocus(JSpinner spinner) {
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    // Dùng invokeLater để đảm bảo việc bôi đen xảy ra SAU KHI component nhận focus xong
                    SwingUtilities.invokeLater(() -> {
                        textField.selectAll();
                    });
                }
            });
        }
    }

    private void updateTongTienMat() {
        double total = 0;
        for (int money : menhGiaArr) {
            int qty = (Integer) spinners.get(money).getValue();
            double value = qty * money;
            labelsThanhTien.get(money).setText(df.format(value));
            total += value;
        }
        total += (Double) spnTienLeKhac.getValue();
        lblTongTienMatThucTe.setText(df.format(total));
        updateDoiSoat(heThong_TienMatDoanhThu, total);
    }

    private void updateDoiSoat(double A, double B) {
        lblTienHeThong_SoSanh.setText(df.format(A));
        lblTienThucTe_SoSanh.setText(df.format(B));

        double diff = B - A;
        lblChenhLech.setText(df.format(diff));

        if (B == 0 && A != 0) {
            lblChenhLech.setForeground(Color.RED);
            lblTrangThai.setText("(Thiếu tiền)");
            lblTrangThai.setForeground(Color.RED);
        } else if (diff < 0) {
            lblChenhLech.setForeground(Color.RED);
            lblTrangThai.setText("(Thiếu tiền)");
            lblTrangThai.setForeground(Color.RED);
        } else if (diff > 0) {
            lblChenhLech.setForeground(new Color(0, 102, 0));
            lblTrangThai.setText("(Thừa tiền)");
            lblTrangThai.setForeground(new Color(0, 102, 0));
        } else {
            lblChenhLech.setForeground(Color.BLACK);
            lblTrangThai.setText("(Khớp)");
            lblTrangThai.setForeground(Color.BLACK);
        }
    }

    private void xuLyXacNhanGiaoCa() {
        // 1. TÍNH LẠI TỔNG TIỀN TRỰC TIẾP (An toàn tuyệt đối)
        double tongTienThucTe = 0;
        for (int money : menhGiaArr) {
            if (spinners.containsKey(money)) {
                Number value = (Number) spinners.get(money).getValue();
                tongTienThucTe += value.doubleValue() * money;
            }
        }
        Number tienLe = (Number) spnTienLeKhac.getValue();
        tongTienThucTe += tienLe.doubleValue();

        double A = heThong_TienMatDoanhThu;
        double B = tongTienThucTe;
        double diff = B - A;

        String ghiChu = txtGhiChu.getText().trim();

        if (Math.abs(diff) > 0 && ghiChu.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Có chênh lệch (" + df.format(diff) + "), vui lòng nhập lý do vào ô Ghi chú!",
                    "Thiếu ghi chú", JOptionPane.WARNING_MESSAGE);
            txtGhiChu.requestFocus();
            return;
        }

        // 2. LƯU DỮ LIỆU
        this.tienMatThucTeDaLuu = B;
        this.ghiChuDaLuu = ghiChu;
        this.isGiaoCaConfirmed = true;

        JOptionPane.showMessageDialog(this,
                "Đã xác nhận kiểm kê!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);

        this.dispose();
    }
}