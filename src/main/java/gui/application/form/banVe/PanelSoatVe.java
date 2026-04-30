package gui.application.form.banVe;

/*
 * @(#) PanelSoatVe.java  1.0  [8:20:14 PM] Dec 9, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 9, 2025
 * @version: 1.0
 */

import dao.impl.VeDAO;
import entity.Ve;
import entity.type.TrangThaiVe;

import javax.swing.*;
import java.awt.*;

public class PanelSoatVe extends JPanel {
    private JTextField txtBarcode;
    private JLabel lblStatusIcon;
    private JLabel lblMessage;
    private JTextArea txtInfo;
    private JButton btnConfirmUsed;

    private VeDAO veDAO = new VeDAO();
    private Ve currentVe = null; // Lưu vé đang quét để xử lý

    public PanelSoatVe() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- PHẦN TRÊN: Ô QUÉT ---
        JPanel pnlInput = new JPanel(new BorderLayout(10, 10));
        pnlInput.setBorder(BorderFactory.createTitledBorder("Khu vuc quet ̣(dat con tro chuot vao day)"));

        JLabel lblScan = new JLabel("Ma ve:");
        lblScan.setFont(new Font("Arial", Font.BOLD, 16));

        txtBarcode = new JTextField();
        txtBarcode.setFont(new Font("Monospaced", Font.BOLD, 24));
        txtBarcode.setHorizontalAlignment(JTextField.CENTER);
        // Mẹo: Máy quét thường thêm ký tự xuống dòng ở cuối -> Dùng ActionListener là
        // bắt được ngay
        txtBarcode.addActionListener(e -> xuLyQuetVe(txtBarcode.getText()));

        pnlInput.add(lblScan, BorderLayout.WEST);
        pnlInput.add(txtBarcode, BorderLayout.CENTER);

        // --- PHẦN GIỮA: HIỂN THỊ KẾT QUẢ ---
        JPanel pnlResult = new JPanel();
        pnlResult.setLayout(new BoxLayout(pnlResult, BoxLayout.Y_AXIS));
        pnlResult.setBackground(Color.WHITE);
        pnlResult.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        lblStatusIcon = new JLabel("SAN SANG QUET");
        lblStatusIcon.setFont(new Font("Arial", Font.BOLD, 32));
        lblStatusIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStatusIcon.setForeground(Color.GRAY);

        lblMessage = new JLabel("Vui long quet ma QR tren ve");
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 18));
        lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtInfo = new JTextArea(5, 30);
        txtInfo.setFont(new Font("Monospaced", Font.PLAIN, 14));
        txtInfo.setEditable(false);
        txtInfo.setBorder(BorderFactory.createTitledBorder("Chi tiet ve"));

        pnlResult.add(lblStatusIcon);
        pnlResult.add(Box.createVerticalStrut(10));
        pnlResult.add(lblMessage);
        pnlResult.add(Box.createVerticalStrut(20));
        pnlResult.add(new JScrollPane(txtInfo));

        // --- PHẦN DƯỚI: NÚT XÁC NHẬN ---
        JPanel pnlAction = new JPanel();
        btnConfirmUsed = new JButton("XAC NHAN CHO KHACH LEN TAU (DA SU DUNG)");
        btnConfirmUsed.setFont(new Font("Arial", Font.BOLD, 16));
        btnConfirmUsed.setBackground(new Color(0, 153, 51));
        btnConfirmUsed.setForeground(Color.WHITE);
        btnConfirmUsed.setEnabled(false); // Chỉ bật khi vé hợp lệ

        btnConfirmUsed.addActionListener(e -> markAsUsed());

        pnlAction.add(btnConfirmUsed);

        add(pnlInput, BorderLayout.NORTH);
        add(pnlResult, BorderLayout.CENTER);
        add(pnlAction, BorderLayout.SOUTH);
    }

    private void xuLyQuetVe(String qrRawData) {
        // Reset giao diện
        txtBarcode.setText("");
        txtBarcode.requestFocus(); // Giữ focus để quét vé tiếp theo ngay lập tức
        currentVe = null;
        btnConfirmUsed.setEnabled(false);
        String veID = null;

        try {
            // 1. Phân tích chuỗi QR
            // Cách xử lý nhanh không cần thư viện (Gson/Jackson):
            // B1: Xóa các ký tự ngoặc nhọn {} và ngoặc kép "
            String cleanData = qrRawData.replace("{", "").replace("}", "").replace("\"", "");
            // Lúc này chuỗi thành: id:VE-SGOBHOSE6...,trangThai:TODO,tau:SE6...

            // B2: Tách theo dấu phẩy
            String[] pairs = cleanData.split(",");

            for (String pair : pairs) {
                // B3: Tách key và value theo dấu hai chấm đầu tiên
                // Dùng limit = 2 để đề phòng trong value có dấu : (ví dụ giờ phút)
                String[] keyValue = pair.split(":", 2);

                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    if (key.equals("id")) {
                        veID = value;
                        break; // Tìm thấy ID rồi thì dừng
                    }
                }
            }
        } catch (

                Exception e) {
            hienThiLoi("Lỗi phân tích mã QR!");
            e.printStackTrace();
            return;
        }

        if (veID == null) {
            hienThiLoi("Không tìm thấy mã vé trong QR!");
            return;
        }

        // 2. Tra cứu Database
        currentVe = veDAO.getVeByVeID(veID);

        if (currentVe == null) {
            hienThiLoi("Vé không tồn tại trong hệ thống!");
            return;
        }

        // 3. Kiểm tra trạng thái
        // Giả sử: 0=Hợp lệ, 1=Đã dùng, 2=Đã hủy
        TrangThaiVe trangThai = currentVe.getTrangThai();

        StringBuilder info = new StringBuilder();
        info.append("Mã vé: ").append(currentVe.getVeID()).append("\n");
        info.append("Khách hàng: ").append(currentVe.getKhachHang().getHoTen()).append("\n");
        info.append("Tàu/Toa/Ghế: ").append(currentVe.getGhe().getToa().getTau().getTauID()).append(" / ")
                .append(currentVe.getGhe().getToa().getSoToa()).append(" / ").append(currentVe.getGhe().getSoGhe())
                .append("\n");
        info.append("Ngày đi: ").append(currentVe.getNgayGioDi());

        txtInfo.setText(info.toString());

        if (trangThai == TrangThaiVe.DA_DUNG) {
            hienThiCanhBao("VE DA SU DUNG (USED)", "Ve nay da duoc quet truoc do!");
        } else if (trangThai == TrangThaiVe.DA_HOAN || trangThai == TrangThaiVe.DA_DOI) {
            hienThiLoi("VE DA BI HUY (CANCELLED)");
        } else {
            // HỢP LỆ
            lblStatusIcon.setText("VE HOP LE");
            lblStatusIcon.setForeground(new Color(0, 153, 51)); // Màu xanh
            lblMessage.setText("Cho phep hanh khach len tau.");
            btnConfirmUsed.setEnabled(true); // Bật nút xác nhận
            btnConfirmUsed.requestFocus(); // Focus vào nút này để bấm Enter là xong luôn
        }
    }

    private void markAsUsed() {
        if (currentVe != null) {
            boolean result = veDAO.updateTrangThaiVe(currentVe.getVeID(), TrangThaiVe.DA_DUNG);
            if (result) {
                JOptionPane.showMessageDialog(this, "Da cap nhat ve thanh cong!");
                // Reset về trạng thái chờ
                lblStatusIcon.setText("SAN SANG QUET");
                lblStatusIcon.setForeground(Color.GRAY);
                lblMessage.setText("Vui long quet ve tiep theo");
                txtInfo.setText("");
                btnConfirmUsed.setEnabled(false);
                txtBarcode.requestFocus(); // Trả focus về ô nhập
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienThiLoi(String msg) {
        hienThiLoi(msg, msg);
    }

    private void hienThiLoi(String title, String msg) {
        lblStatusIcon.setText("✖ " + title);
        lblStatusIcon.setForeground(Color.RED);
        lblMessage.setText(msg);
        // Phát tiếng kêu beep (tùy chọn)
        Toolkit.getDefaultToolkit().beep();
    }

    private void hienThiCanhBao(String title, String msg) {
        lblStatusIcon.setText("⚠ " + title);
        lblStatusIcon.setForeground(Color.ORANGE);
        lblMessage.setText(msg);
        Toolkit.getDefaultToolkit().beep();
    }
}