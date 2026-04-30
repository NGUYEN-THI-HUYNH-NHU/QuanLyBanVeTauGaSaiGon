package gui.application.form.donDatCho;

/*
 * @(#) DonDatChoController.java  1.0  [11:45:09 AM] Dec 12, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 12, 2025
 * @version: 1.0
 */

import bus.DonDatCho_BUS;
import bus.Ve_BUS;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import dto.DonDatChoDTO;
import dto.VeDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DonDatChoController {
    private final PanelQuanLyDonDatCho view;
    private final DonDatCho_BUS donDatChoBUS;
    private final Ve_BUS veBUS;
    private JPopupMenu traCuuSuggestionPopup;

    public DonDatChoController(PanelQuanLyDonDatCho view) {
        this.view = view;

        this.traCuuSuggestionPopup = new JPopupMenu();
        this.traCuuSuggestionPopup.setFocusable(false);

        this.donDatChoBUS = new DonDatCho_BUS();
        this.veBUS = new Ve_BUS();

        init();
    }

    private void init() {
        loadData();

        // Sự kiện nút
        view.getBtnTraCuu().addActionListener(e -> handleTraCuu());
        view.getBtnRefresh().addActionListener(e -> handleRefresh());
        view.getBtnLoc().addActionListener(e -> handleLoc());
        view.getBtnReset().addActionListener(e -> handleReset());

        // Sự kiện Checkbox ngày
        view.getCheckBoxTatCaNgay().addActionListener(e -> {
            boolean isAll = view.getCheckBoxTatCaNgay().isSelected();
            view.getDateChooserTuNgay().setEnabled(!isAll);
            view.getDateChooserDenNgay().setEnabled(!isAll);
        });

        // Sự kiện click vào bảng (Cột xem)
        view.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = view.getTable().getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / view.getTable().getRowHeight();

                if (row >= 0 && column == DonDatChoTableModel.COL_XEM) {
                    DonDatChoDTO ddc = view.getTableModel().getRow(row);
                    showModalChiTiet(ddc);
                }
            }
        });

        // Hiệu ứng con trỏ chuột
        view.getTable().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int column = view.getTable().columnAtPoint(e.getPoint());
                if (column == DonDatChoTableModel.COL_XEM) {
                    view.getTable().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    view.getTable().setCursor(Cursor.getDefaultCursor());
                }
            }
        });

        setupTraCuuSuggestion();
    }

    private void setupTraCuuSuggestion() {
        JTextField txtSearch = view.getTxtTuKhoa();
        JComboBox<String> cboType = view.getCboLoaiTimKiem();

        // 1. Reset text khi đổi loại tìm kiếm (để tránh user tìm ID đơn đặt chỗ bằng mã
        // KH)
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

        // "Mã đặt chỗ", "Số giấy tờ", "Số điện thoại", "Tên khách hàng"
        // 4. Lấy danh sách gợi ý dựa trên loại đang chọn
        if ("Mã đặt chỗ".equals(type)) {
            suggestions = donDatChoBUS.layTop10DonDatChoID(keyword);
        } else if ("Số giấy tờ".equals(type)) {
            suggestions = donDatChoBUS.layTop10SoGiayTo(keyword);
        } else if ("Số điện thoại".equals(type)) {
            suggestions = donDatChoBUS.layTop10SoDienThoai(keyword);
        } else if ("Tên khách hàng".equals(type)) {
            suggestions = donDatChoBUS.layTop10TenKhachHang(keyword);
        }

        // 5. Hiển thị Popup
        if (!suggestions.isEmpty()) {
            for (String s : suggestions) {
                JMenuItem item = new JMenuItem(s);
                // Highlight icon khác nhau cho đẹp
                if ("Mã đặt chỗ".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/order.svg", 0.6f));
                } else if ("Số giấy tờ".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/idcard.svg", 0.6f));
                } else if ("Số điện thoại".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/phone.svg", 0.6f));
                } else if ("Tên khách hàng".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/person.svg", 0.6f));
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

    private void loadData() {
        List<DonDatChoDTO> list = donDatChoBUS.layDanhSachDonDatCho();
        view.getTableModel().setRows(list);
    }

    private void handleTraCuu() {
        String keyword = view.getTxtTuKhoa().getText().trim();
        String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

        List<DonDatChoDTO> results;

        if (keyword.isEmpty()) {
            loadData();
            return;
        }

        results = donDatChoBUS.layDonDatChoTheoKeyword(keyword, type);

        view.getTableModel().setRows(results);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy đơn đặt chỗ nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("Tìm thấy " + results.size() + " kết quả cho: " + keyword);
        }
    }

    private void handleLoc() {
        // 1. Lấy dữ liệu từ View
        Date tuNgay = view.getCheckBoxTatCaNgay().isSelected() ? null : view.getDateChooserTuNgay().getDate();
        Date denNgay = view.getCheckBoxTatCaNgay().isSelected() ? null : view.getDateChooserDenNgay().getDate();

        // 3. Validate Ngày tháng
        if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(view, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi bộ lọc",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println(String.format("Filter: Ngay=%s-%s", tuNgay, denNgay));

        // 4. Lọc hóa đơn theo tiêu chí
        List<DonDatChoDTO> results = donDatChoBUS.locHoaDonTheoCacTieuChi(tuNgay, denNgay);

        // 5. Cập nhật UI và thông báo kết quả
        view.getTableModel().setRows(results);

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy hóa đơn nào phù hợp!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            view.getTable().scrollRectToVisible(view.getTable().getCellRect(0, 0, true));
        }
    }

    private void handleRefresh() {
        view.getTxtTuKhoa().setText("");
        view.getCheckBoxTatCaNgay().setSelected(true);
        loadData();
    }

    private void handleReset() {
        view.getTxtTuKhoa().setText("");
        view.getCboLoaiTimKiem().setSelectedIndex(0);
        view.getCheckBoxTatCaNgay().setSelected(true);
        view.getDateChooserTuNgay().setEnabled(false);
        view.getDateChooserDenNgay().setEnabled(false);
    }

    private void showModalChiTiet(dto.DonDatChoDTO ddc) {
        // Lấy danh sách vé chi tiết của đơn này
        List<VeDTO> listVe = veBUS.timCacVeTheoDonDatChoID(ddc.getId());

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        ModalChiTietDonDatCho modal = new ModalChiTietDonDatCho(parent, ddc, listVe);
        modal.setVisible(true);
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