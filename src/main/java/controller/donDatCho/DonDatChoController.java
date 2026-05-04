package controller.donDatCho;

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
import bus.KhachHang_BUS;
import bus.Ve_BUS;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import dto.DonDatChoDTO;
import dto.VeDTO;
import gui.application.form.donDatCho.DonDatChoTableModel;
import gui.application.form.donDatCho.ModalChiTietDonDatCho;
import gui.application.form.donDatCho.PanelQuanLyDonDatCho;

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
    private final DonDatCho_BUS donDatChoBUS = new DonDatCho_BUS();
    private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();
    private final Ve_BUS veBUS = new Ve_BUS();
    private final JPopupMenu traCuuSuggestionPopup = new JPopupMenu();
    private int rowsPerPage = 20;
    private int totalRecords = 0;
    private int currentPage = 1;
    private int totalPages = 1;
    private SearchState currentState = SearchState.ALL;

    public DonDatChoController(PanelQuanLyDonDatCho view) {
        this.view = view;

        this.traCuuSuggestionPopup.setFocusable(false);

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

        // Sự kiện chuyển trang
        view.getBtnPrevPage().addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                fetchAndDisplayData();
            }
        });

        view.getBtnNextPage().addActionListener(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                fetchAndDisplayData();
            }
        });

        view.getCboRowsPerPage().addActionListener(e -> {
            int selectedRows = (Integer) view.getCboRowsPerPage().getSelectedItem();
            if (this.rowsPerPage != selectedRows) {
                this.rowsPerPage = selectedRows;
                this.currentPage = 1; // Quay về trang 1

                // Tính lại tổng số trang và load dữ liệu
                calculateTotalPages();
                fetchAndDisplayData();
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
            suggestions = khachHangBUS.layTop10SoGiayTo(keyword);
        } else if ("Số điện thoại".equals(type)) {
            suggestions = khachHangBUS.layTop10SoDienThoai(keyword);
        } else if ("Tên khách hàng".equals(type)) {
            suggestions = khachHangBUS.layTop10HoTen(keyword);
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
        currentState = SearchState.ALL;
        currentPage = 1;

        // 1. Đếm tổng số để tính trang
        totalRecords = donDatChoBUS.countAllDonDatCho();
        calculateTotalPages();

        // 2. Fetch trang 1
        fetchAndDisplayData();
    }

    private void handleTraCuu() {
        currentState = SearchState.SEARCH;
        currentPage = 1;

        String keyword = view.getTxtTuKhoa().getText().trim();
        String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

        if (keyword.isEmpty()) return;

        // 2. Đếm tổng số record
        totalRecords = donDatChoBUS.countDonDatChoByKeyword(keyword, type);

        if (totalRecords == 0) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy kết quả nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Lấy dữ liệu trang 1 và hiển thị
        calculateTotalPages();
        fetchAndDisplayData();
    }

    private void handleLoc() {
        currentState = SearchState.FILTER;
        currentPage = 1;

        // 1. Lấy dữ liệu từ View
        boolean isTatCaNgay = view.getCheckBoxTatCaNgay().isSelected();
        Date tuNgay = isTatCaNgay ? null : view.getDateChooserTuNgay().getDate();
        Date denNgay = isTatCaNgay ? null : view.getDateChooserDenNgay().getDate();

        // 2. Validate Ngày tháng
        if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(view, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi bộ lọc",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tuKhoaTraCuu = view.getTxtTuKhoa().getText().trim();
        String loaiTraCuu = (String) view.getCboLoaiTimKiem().getSelectedItem();

        // 3. Đếm tổng số record thỏa mãn để chia trang
        totalRecords = donDatChoBUS.countDonDatChoByFilter(tuKhoaTraCuu, loaiTraCuu, tuNgay, denNgay);
        calculateTotalPages();

        if (totalRecords == 0) JOptionPane.showMessageDialog(view, "Không tìm thấy vé nào phù hợp!");

        // 4. Lấy dữ liệu trang 1 và hiển thị
        fetchAndDisplayData();
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

    private void calculateTotalPages() {
        totalPages = (int) Math.ceil((double) totalRecords / rowsPerPage);
        if (totalPages == 0) totalPages = 1;
    }

    // Gọi truy vấn đúng với trạng thái hiện tại kèm theo OFFSET (page)
    private void fetchAndDisplayData() {
        List<DonDatChoDTO> dtos = new ArrayList<>();

        if (currentState == SearchState.ALL) {
            dtos = donDatChoBUS.getDonDatChoByPage(currentPage, rowsPerPage);
        } else if (currentState == SearchState.FILTER) {
            boolean isTatCaNgay = view.getCheckBoxTatCaNgay().isSelected();
            Date tuNgay = isTatCaNgay ? null : view.getDateChooserTuNgay().getDate();
            Date denNgay = isTatCaNgay ? null : view.getDateChooserDenNgay().getDate();
            String tuKhoaTraCuu = view.getTxtTuKhoa().getText().trim();
            String loaiTraCuu = (String) view.getCboLoaiTimKiem().getSelectedItem();

            dtos = donDatChoBUS.locDonDatChoTheoCacTieuChi(tuKhoaTraCuu, loaiTraCuu, tuNgay, denNgay, currentPage, rowsPerPage);
        } else if (currentState == SearchState.SEARCH) {
            String keyword = view.getTxtTuKhoa().getText().trim();
            String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

            dtos = donDatChoBUS.layDonDatChoTheoKeyword(keyword, type, currentPage, rowsPerPage);
        }

        view.getTableModel().setRows(dtos);
        renderPageNumbers();
    }

    // Vẽ lại các nút số trang
    private void renderPageNumbers() {
        JPanel pnlPages = view.getPnlPageNumbers();
        pnlPages.removeAll();

        int maxPagesToShow = 5;
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

        if (endPage - startPage + 1 < maxPagesToShow) {
            startPage = Math.max(1, endPage - maxPagesToShow + 1);
        }

        for (int i = startPage; i <= endPage; i++) {
            int pageNum = i;
            JButton btnPage = new JButton(String.valueOf(pageNum));
            btnPage.setMargin(new Insets(2, 6, 2, 6));
            btnPage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            if (pageNum == currentPage) {
                btnPage.setBackground(new Color(38, 117, 191));
                btnPage.setForeground(Color.WHITE);
                btnPage.setFont(btnPage.getFont().deriveFont(Font.BOLD));
            } else {
                btnPage.setBackground(Color.WHITE);
                btnPage.setForeground(Color.BLACK);
            }

            btnPage.addActionListener(e -> {
                currentPage = pageNum;
                fetchAndDisplayData(); // Bấm sang số nào thì chạy lại query cho số đó
            });

            pnlPages.add(btnPage);
        }

        view.getBtnPrevPage().setEnabled(currentPage > 1);
        view.getBtnNextPage().setEnabled(currentPage < totalPages);

        pnlPages.revalidate();
        pnlPages.repaint();
    }

    private enum SearchState {ALL, FILTER, SEARCH}
}