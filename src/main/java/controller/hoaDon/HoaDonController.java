package controller.hoaDon;

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
import dto.HoaDonChiTietDTO;
import dto.HoaDonDTO;
import dto.KhachHangDTO;
import gui.application.form.hoaDon.HoaDonTableButtonRenderer;
import gui.application.form.hoaDon.HoaDonTableModel;
import gui.application.form.hoaDon.ModalHoaDon;
import gui.application.form.hoaDon.PanelQuanLyHoaDon;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HoaDonController {
    private final HoaDon_BUS hoaDonBUS = new HoaDon_BUS();
    private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();
    private final JPopupMenu traCuuSuggestionPopup = new JPopupMenu();
    private final JPopupMenu khachHangSuggestionPopup = new JPopupMenu();
    private PanelQuanLyHoaDon view;
    private KhachHangDTO selectedKhachHang = null;
    private int rowsPerPage = 20;
    private int totalRecords = 0;
    private int currentPage = 1;
    private int totalPages = 1;
    private SearchState currentState = SearchState.ALL;

    public HoaDonController(PanelQuanLyHoaDon view) {
        this.view = view;

        // Tắt focusable của popup để focus vẫn nằm ở TextField khi gõ
        this.traCuuSuggestionPopup.setFocusable(false);
        this.khachHangSuggestionPopup.setFocusable(false);

        init();
    }

    private void loadAllHoaDon() {
        currentState = SearchState.ALL;
        currentPage = 1;

        // 1. Đếm tổng số để tính trang
        totalRecords = hoaDonBUS.countAllHoaDon();
        calculateTotalPages();

        // 2. Fetch trang 1
        fetchAndDisplayData();
    }

    private void init() {
        loadAllHoaDon();
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
                HoaDonDTO selectedHoaDon = view.getTableModel().getRow(row);

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
    }

    private void handleNgayLoc() {
        boolean isSelected = view.getCheckBoxTatCaNgay().isSelected();
        view.getDateChooserTuNgay().setEnabled(!isSelected);
        view.getDateChooserDenNgay().setEnabled(!isSelected);
    }

    // Xử lý khi bấm nút Lọc
    private void handleLoc() {
        currentState = SearchState.FILTER;
        currentPage = 1;

        boolean isTatCaNgay = view.getCheckBoxTatCaNgay().isSelected();
        Date tuNgay = view.getDateChooserTuNgay().getDate();
        Date denNgay = view.getDateChooserDenNgay().getDate();

        if (isTatCaNgay) {
            tuNgay = null;
            denNgay = null;
        } else if (tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(view, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi bộ lọc",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String loaiHD = (String) view.getCboLoaiHoaDon().getSelectedItem();
        String tuKhoaInput = view.getTxtKhachHangSuggest().getText().trim();
        String hinhThucTT = (String) view.getCboHinhThucTT().getSelectedItem();
        String searchKeyword = null; // Dùng tìm theo tên/sđt/cccd (LIKE)
        String searchID = null; // Dùng tìm chính xác theo ID (=)

        if (selectedKhachHang != null && tuKhoaInput.equals(selectedKhachHang.getHoTen())) {
            searchID = selectedKhachHang.getId();
        } else {
            searchKeyword = tuKhoaInput.isEmpty() ? null : tuKhoaInput;
        }

        String tuKhoaTraCuu = view.getTxtTuKhoa().getText().trim();
        String loaiTraCuu = (String) view.getCboLoaiTimKiem().getSelectedItem();

        // 3. Đếm tổng số record thỏa mãn để chia trang
        totalRecords = hoaDonBUS.countHoaDonByFilter(tuKhoaTraCuu, loaiTraCuu,
                loaiHD, searchKeyword, searchID, tuNgay, denNgay, hinhThucTT);

        if (totalRecords == 0) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy kết quả nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 4. Lấy dữ liệu trang 1 và hiển thị
        calculateTotalPages();
        fetchAndDisplayData();
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
        currentState = SearchState.SEARCH;
        currentPage = 1;

        // 1. Lấy dữ liệu
        String keyword = view.getTxtTuKhoa().getText().trim();
        String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

        // 2. Đếm tổng số record
        totalRecords = hoaDonBUS.countHoaDonByKeyword(keyword, type);

        if (totalRecords == 0) {
            JOptionPane.showMessageDialog(view, "Không tìm thấy kết quả nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 3. Lấy dữ liệu trang 1 và hiển thị
        calculateTotalPages();
        fetchAndDisplayData();
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
        if ("Mã hóa đơn".equals(type)) suggestions = hoaDonBUS.layTop10HoaDonID(keyword);
        else if ("Số điện thoại khách hàng".equals(type)) suggestions = khachHangBUS.layTop10SoDienThoai(keyword);
        else if ("Số giấy tờ khách hàng".equals(type)) suggestions = khachHangBUS.layTop10SoGiayTo(keyword);

        // 5. Hiển thị Popup
        if (!suggestions.isEmpty()) {
            for (String s : suggestions) {
                JMenuItem item = new JMenuItem(s);
                // Highlight icon khác nhau cho đẹp
                if ("Mã hóa đơn".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/order.svg", 0.6f));
                } else if ("Số điện thoại khách hàng".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/phone.svg", 0.6f));
                } else if ("Số giấy tờ khách hàng".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/idcard.svg", 0.6f));
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

    private void handleXemChiTiet(HoaDonDTO hoaDon) {
        // 1. Lấy danh sách chi tiết
        List<HoaDonChiTietDTO> listCT = hoaDonBUS.layCacHoaDonChiTietTheoHoaDonID(hoaDon.getId());

        // 2. Lấy JFrame chứa nó để làm parent cho Modal
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(view);

        ModalHoaDon modal = new ModalHoaDon(parentFrame, hoaDon, listCT);
        modal.setVisible(true);
    }

    private void handleInHoaDon(HoaDonDTO hoaDon) {
        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có muốn in hóa đơn " + hoaDon.getId() + " không?",
                "Xác nhận in", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Đang in hóa đơn: " + hoaDon.getId());

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

    private void calculateTotalPages() {
        totalPages = (int) Math.ceil((double) totalRecords / rowsPerPage);
        if (totalPages == 0) totalPages = 1;
    }

    // Gọi truy vấn đúng với trạng thái hiện tại kèm theo OFFSET (page)
    private void fetchAndDisplayData() {
        List<HoaDonDTO> dtos = new ArrayList<>();

        if (currentState == SearchState.ALL) {
            dtos = hoaDonBUS.getHoaDonByPage(currentPage, rowsPerPage);
        } else if (currentState == SearchState.FILTER) {
            String loaiHD = (String) view.getCboLoaiHoaDon().getSelectedItem();
            String tuKhoaInput = view.getTxtKhachHangSuggest().getText().trim();
            boolean isTatCaNgay = view.getCheckBoxTatCaNgay().isSelected();
            Date tuNgay = isTatCaNgay ? null : view.getDateChooserTuNgay().getDate();
            Date denNgay = isTatCaNgay ? null : view.getDateChooserDenNgay().getDate();
            String hinhThucTT = (String) view.getCboHinhThucTT().getSelectedItem();

            String searchKeyword = null;
            String searchID = null;
            if (selectedKhachHang != null && tuKhoaInput.equals(selectedKhachHang.getHoTen()))
                searchID = selectedKhachHang.getId();
            else searchKeyword = tuKhoaInput.isEmpty() ? null : tuKhoaInput;

            String tuKhoaTraCuu = view.getTxtTuKhoa().getText().trim();
            String loaiTraCuu = (String) view.getCboLoaiTimKiem().getSelectedItem();

            dtos = hoaDonBUS.locHoaDonTheoCacTieuChi(tuKhoaTraCuu, loaiTraCuu, loaiHD, searchKeyword, searchID, tuNgay, denNgay, hinhThucTT, currentPage, rowsPerPage);
        } else if (currentState == HoaDonController.SearchState.SEARCH) {
            String keyword = view.getTxtTuKhoa().getText().trim();
            String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

            dtos = hoaDonBUS.layHoaDonTheoKeyWord(keyword, type, currentPage, rowsPerPage);
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