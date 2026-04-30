package gui.application.form.hoaDon;

/*
 * @(#) HoaDonController.java  1.0  [2:58:25 PM] Nov 24, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 24, 2025
 * @version: 1.0
 */

import bus.HoaDon_BUS;
import bus.KhachHang_BUS;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import dto.KhachHangDTO;
import entity.HoaDon;
import entity.HoaDonChiTiet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDonController {
    private final HoaDon_BUS hoaDonBUS;
    private final KhachHang_BUS khachHangBUS;
    private PanelQuanLyHoaDon view;
    private JPopupMenu traCuuSuggestionPopup;
    private JPopupMenu khachHangSuggestionPopup;
    private KhachHangDTO selectedKhachHang = null;

    public HoaDonController(PanelQuanLyHoaDon view) {
        this.view = view;
        this.traCuuSuggestionPopup = new JPopupMenu();
        this.khachHangSuggestionPopup = new JPopupMenu();
        // Tắt focusable của popup để focus vẫn nằm ở TextField khi gõ
        this.traCuuSuggestionPopup.setFocusable(false);
        this.khachHangSuggestionPopup.setFocusable(false);

        this.hoaDonBUS = new HoaDon_BUS();
        this.khachHangBUS = new KhachHang_BUS();

        loadAllHoaDon();

        init();
    }

    private void loadAllHoaDon() {
        this.view.getTableModel().setRows(hoaDonBUS.layTatCaHoaDon());
    }

    private void init() {
        attachListeners();
        setupTraCuuSuggestion();
        setupKhachHangSuggestion();
    }

    private void attachListeners() {
        // 1. Sự kiện nút Lọc
        view.getBtnLoc().addActionListener(e -> handleLoc());

        // 2. Sự kiện nút Reset
        view.getBtnReset().addActionListener(e -> handleReset());

        // 3. Sự kiện nút Tra Cứu
        view.getBtnTraCuu().addActionListener(e -> handleTraCuu());

        // 4. Sự kiện nút Refresh
        view.getBtnRefresh().addActionListener(e -> handleRefresh());

        // 5. Sự kiện checkbox tất cả ngày
        view.getCheckBoxTatCaNgay().addActionListener(e -> handleNgayLoc());

        // 1. Gán Renderer cho cột Button
        view.getTable().getColumnModel().getColumn(HoaDonTableModel.COL_XEM)
                .setCellRenderer(new HoaDonTableButtonRenderer());
        view.getTable().getColumnModel().getColumn(HoaDonTableModel.COL_IN)
                .setCellRenderer(new HoaDonTableButtonRenderer());

        // 2. Xử lý sự kiện click trên Table (Thay vì dùng CellEditor)
        view.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = view.getTable().getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / view.getTable().getRowHeight();

                if (row < 0 || row >= view.getTable().getRowCount() || column < 0
                        || column >= view.getTable().getColumnCount()) {
                    return;
                }

                // Lấy đối tượng Hóa đơn tại dòng click
                HoaDon selectedHoaDon = view.getTableModel().getRow(row);

                if (column == HoaDonTableModel.COL_XEM) {
                    handleXemChiTiet(selectedHoaDon);
                } else if (column == HoaDonTableModel.COL_IN) {
                    handleInHoaDon(selectedHoaDon);
                }
            }
        });

        // Xử lý sự kiện hover trên nút xem/in
        view.getTable().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                JTable table = (JTable) e.getSource();
                int column = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0 && (column == HoaDonTableModel.COL_XEM || column == HoaDonTableModel.COL_IN)) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                }
            }
        });
    }

    private void handleNgayLoc() {
        boolean isSelected = view.getCheckBoxTatCaNgay().isSelected();
        view.getDateChooserTuNgay().setEnabled(!isSelected);
        view.getDateChooserDenNgay().setEnabled(!isSelected);
    }

    // Xử lý khi bấm nút Lọc
    private void handleLoc() {
        // 1. Lấy dữ liệu từ View
        String loaiHD = (String) view.getCboLoaiHoaDon().getSelectedItem();
        String tuKhoaInput = view.getTxtKhachHangSuggest().getText().trim();
        boolean isTatCaNgay = view.getCheckBoxTatCaNgay().isSelected();
        Date tuNgay = view.getDateChooserTuNgay().getDate();
        Date denNgay = view.getDateChooserDenNgay().getDate();
        String hinhThucTT = (String) view.getCboHinhThucTT().getSelectedItem();

        String searchKeyword = null; // Dùng tìm theo tên/sđt/cccd (LIKE)
        String searchID = null; // Dùng tìm chính xác theo ID (=)

        // 2. Logic thông minh
        if (selectedKhachHang != null && tuKhoaInput.equals(selectedKhachHang.getHoTen())) {
            // Nếu người dùng chọn từ gợi ý và không sửa tên -> Tìm chính xác theo ID
            searchID = selectedKhachHang.getId();
        } else {
            // Nếu tự gõ hoặc đã sửa tên -> Tìm tương đối theo từ khóa
            searchKeyword = tuKhoaInput.isEmpty() ? null : tuKhoaInput;
            selectedKhachHang = null; // Reset biến nhớ để tránh nhầm lẫn lần sau
        }

        // 3. Validate Ngày tháng
        if (isTatCaNgay) {
            tuNgay = null;
            denNgay = null;
        } else if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(view, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi bộ lọc",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println(String.format("Filter: Loai=%s | Keyword=%s | ID=%s | Ngay=%s-%s", loaiHD, searchKeyword,
                searchID, tuNgay, denNgay));

        // 4. Lọc hóa đơn theo tiêu chí
        List<HoaDon> results = hoaDonBUS.locHoaDonTheoCacTieuChi(loaiHD, searchKeyword, searchID, tuNgay, denNgay,
                hinhThucTT);

        // 5. Cập nhật UI và thông báo kết quả
        view.getTableModel().setRows(results);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy hóa đơn nào phù hợp!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            view.getTable().scrollRectToVisible(view.getTable().getCellRect(0, 0, true));
        }
    }

    // Xử lý khi bấm nút Xóa bộ lọc
    private void handleReset() {
        view.getCboLoaiHoaDon().setSelectedIndex(0);
        view.getTxtKhachHangSuggest().setText("");
        view.getCboHinhThucTT().setSelectedIndex(0);
        view.getCheckBoxTatCaNgay().setSelected(true);
        view.getDateChooserTuNgay().setDate(new Date());
        view.getDateChooserDenNgay().setDate(new Date());
        view.getDateChooserTuNgay().setEnabled(false);
        view.getDateChooserDenNgay().setEnabled(false);
    }

    private void handleTraCuu() {
        // 1. Lấy dữ liệu
        String keyword = view.getTxtTuKhoa().getText().trim();
        String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

        // 2. Lấy các hóa đơn theo keyword và loại tra cứu
        List<HoaDon> result = hoaDonBUS.layHoaDonTheoKeyWord(keyword, type);

        // 3. Update Table
        view.getTableModel().setRows(result);

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy kết quả nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("Tìm thấy " + result.size() + " kết quả cho: " + keyword);
        }
    }

    // Xử lý khi bấm nút Làm mới
    private void handleRefresh() {
        view.getTxtTuKhoa().setText("");
        view.getCboLoaiTimKiem().setSelectedIndex(0);

        handleReset();
        loadAllHoaDon();
    }

    private void setupTraCuuSuggestion() {
        JTextField txtSearch = view.getTxtTuKhoa();
        JComboBox<String> cboType = view.getCboLoaiTimKiem();

        // 1. Reset text khi đổi loại tìm kiếm (để tránh user tìm ID hóa đơn bằng mã KH)
        cboType.addActionListener(e -> {
            txtSearch.setText("");
            traCuuSuggestionPopup.setVisible(false);
        });

        // 2. Lắng nghe sự kiện gõ phím
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                showTraCuuSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                showTraCuuSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                showTraCuuSuggestions();
            }
        });

        // 3. Ẩn popup khi click ra ngoài
        txtSearch.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    traCuuSuggestionPopup.setVisible(false);
                }
            }
        });

        addSuggestionKeyListeners(txtSearch, traCuuSuggestionPopup, () -> view.getBtnTraCuu().doClick());
    }

    private void showTraCuuSuggestions() {
        String keyword = view.getTxtTuKhoa().getText().trim();
        String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

        traCuuSuggestionPopup.setVisible(false);
        traCuuSuggestionPopup.removeAll();

        if (keyword.length() < 1) {
            return;
        }

        List<String> suggestions = new ArrayList<>();

        // 4. Lấy danh sách gợi ý dựa trên loại đang chọn
        if ("Mã hóa đơn".equals(type)) {
            suggestions = hoaDonBUS.layTop10HoaDonID(keyword);
        } else if ("Mã khách hàng".equals(type)) {
            suggestions = hoaDonBUS.layTop10KhachHangID(keyword);
        } else if ("Mã giao dịch".equals(type)) {
            suggestions = hoaDonBUS.layTop10MaGD(keyword);
        }

        // 5. Hiển thị Popup
        if (!suggestions.isEmpty()) {
            for (String s : suggestions) {
                JMenuItem item = new JMenuItem(s);
                // Highlight icon khác nhau cho đẹp
                if ("Mã hóa đơn".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/order.svg", 0.6f));
                } else if ("Mã khách hàng".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/person.svg", 0.6f));
                } else if ("Mã giao dịch".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/payment.svg", 0.6f));
                }

                item.addActionListener(e -> {
                    view.getTxtTuKhoa().setText(s);
                    traCuuSuggestionPopup.setVisible(false);
                    // Tự động tra cứu luôn khi chọn item
                    view.getBtnTraCuu().doClick();
                });
                traCuuSuggestionPopup.add(item);
            }
            // Hiển thị ngay dưới TextField
            traCuuSuggestionPopup.show(view.getTxtTuKhoa(), 0, view.getTxtTuKhoa().getHeight());
            view.getTxtTuKhoa().requestFocus(); // Giữ focus để gõ tiếp
        }
    }

    // Gợi ý kiểu google search trong phạm vi hoTen/soDienThoai/soGiayTo/khachHangID
    private void setupKhachHangSuggestion() {
        JTextField txtSuggest = view.getTxtKhachHangSuggest();

        // 1. Lắng nghe sự kiện thay đổi text
        txtSuggest.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                showKhachHangSuggestions();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                showKhachHangSuggestions();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                showKhachHangSuggestions();
            }
        });

        // 2. Ẩn popup khi click ra ngoài hoặc click vào textfield mà không gõ
        txtSuggest.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (txtSuggest.getText().trim().isEmpty()) {
                    khachHangSuggestionPopup.setVisible(false);
                }
            }
        });

        addSuggestionKeyListeners(txtSuggest, khachHangSuggestionPopup, null);
    }

    private void showKhachHangSuggestions() {
        String keyword = view.getTxtKhachHangSuggest().getText().trim();
        khachHangSuggestionPopup.setVisible(false);
        khachHangSuggestionPopup.removeAll();

        if (keyword.length() < 1) {
            selectedKhachHang = null;
            return;
        }

        // Lấy danh sách gợi ý
        List<KhachHangDTO> listSuggest = khachHangBUS.layGoiYKhachHang(keyword);

        if (!listSuggest.isEmpty()) {
            for (KhachHangDTO kh : listSuggest) {
                // Tạo text hiển thị: "Tên - SĐT - CCCD - ID"
                String displayText = String.format(
                        "<html><b>%s</b> - %s <br><i style='color:gray; font-size:9px'>%s - %s</i></html>",
                        kh.getHoTen(), (kh.getSoDienThoai() == null ? "N/A" : kh.getSoDienThoai()), kh.getSoGiayTo(),
                        kh.getId());

                JMenuItem item = new JMenuItem(displayText);
                item.setIcon(new FlatSVGIcon("icon/svg/person.svg", 0.6f));

                // Sự kiện khi chọn 1 dòng gợi ý
                item.addActionListener(e -> {
                    // 1. Điền tên vào TextField
                    view.getTxtKhachHangSuggest().setText(kh.getHoTen());
                    // 2. Lưu đối tượng được chọn để xử lý lọc chính xác hơn
                    selectedKhachHang = kh;
                    // 3. Ẩn popup
                    khachHangSuggestionPopup.setVisible(false);
                });
                khachHangSuggestionPopup.add(item);
            }

            // Hiển thị Popup ngay dưới TextField
            khachHangSuggestionPopup.show(view.getTxtKhachHangSuggest(), 0, view.getTxtKhachHangSuggest().getHeight());
            // Focus lại vào textfield để user gõ
            view.getTxtKhachHangSuggest().requestFocus();
        }
    }

    private void handleXemChiTiet(HoaDon hoaDon) {
        // 1. Lấy danh sách chi tiết
        List<HoaDonChiTiet> listCT = hoaDonBUS.layCacHoaDonChiTietTheoHoaDonID(hoaDon.getHoaDonID());

        // 2. Lấy JFrame chứa nó để làm parent cho Modal
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(view);

        ModalHoaDon modal = new ModalHoaDon(parentFrame, hoaDon, listCT);
        modal.setVisible(true);
    }

    private void handleInHoaDon(HoaDon hd) {
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có muốn in hóa đơn " + hd.getHoaDonID() + " không?",
                "Xác nhận in", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Đang in hóa đơn: " + hd.getHoaDonID());

            // TODO: logic in hóa đơn
        }
    }

    private void addSuggestionKeyListeners(JTextField textField, JPopupMenu popup, Runnable defaultEnterAction) {
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (popup.isVisible()) {
                        navigatePopup(popup, 1); // Đi xuống
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (popup.isVisible()) {
                        navigatePopup(popup, -1); // Đi lên
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Kiểm tra xem có item nào trong popup đang được chọn không
                    MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();

                    // Nếu popup đang hiện VÀ có item đang được highlight (path > 0)
                    // VÀ item đó thuộc về popup hiện tại
                    if (popup.isVisible() && path != null && path.length > 0 && isMenuPathInPopup(path, popup)) {
                        // Lấy item cuối cùng trong đường dẫn (chính là JMenuItem đang chọn)
                        Component selectedComp = path[path.length - 1].getComponent();
                        if (selectedComp instanceof JMenuItem) {
                            ((JMenuItem) selectedComp).doClick(); // Kích hoạt sự kiện click của item
                        }
                    } else {
                        // Nếu không chọn item nào trong popup -> Thực hiện hành động mặc định (VD: Nút
                        // Tra Cứu)
                        if (defaultEnterAction != null) {
                            defaultEnterAction.run();
                            popup.setVisible(false); // Ẩn popup đi
                        }
                    }
                }
            }
        });
    }

    // Helper: Điều hướng lên xuống trong Popup
    private void navigatePopup(JPopupMenu popup, int direction) {
        MenuSelectionManager menuManager = MenuSelectionManager.defaultManager();
        MenuElement[] selection = menuManager.getSelectedPath();
        MenuElement[] items = popup.getSubElements();

        if (items.length == 0) {
            return;
        }

        int selectedIndex = -1;
        // Tìm vị trí item đang được chọn hiện tại
        if (selection != null && selection.length > 0) {
            Component current = selection[selection.length - 1].getComponent();
            for (int i = 0; i < items.length; i++) {
                if (items[i].getComponent() == current) {
                    selectedIndex = i;
                    break;
                }
            }
        }

        // Tính toán chỉ số mới
        int nextIndex;
        if (selectedIndex == -1) {
            // Chưa chọn gì -> Bấm xuống chọn cái đầu, Bấm lên chọn cái cuối
            nextIndex = (direction > 0) ? 0 : items.length - 1;
        } else {
            nextIndex = (selectedIndex + direction + items.length) % items.length; // Cộng vòng tròn
        }

        // Set highlight cho item mới
        MenuElement[] newSelection = new MenuElement[]{popup, items[nextIndex]};
        menuManager.setSelectedPath(newSelection);
    }

    // Helper: Kiểm tra xem path đang chọn có thuộc popup này không
    private boolean isMenuPathInPopup(MenuElement[] path, JPopupMenu popup) {
        if (path.length == 0) {
            return false;
        }
        // Phần tử đầu tiên của path thường là JPopupMenu cha
        return path[0].getComponent() == popup;
    }
}