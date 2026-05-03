package controller.xemInVe;
/*
 * @(#) XemInVeController.java  1.0  [7:11:00 PM] Dec 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import bus.KhachHang_BUS;
import bus.Ve_BUS;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import dto.KhachHangDTO;
import dto.VeDTO;
import entity.type.TrangThaiVe;
import gui.application.form.xemInVe.PanelXemInVe;
import gui.application.form.xemInVe.VeTableButtonRenderer;
import gui.application.form.xemInVe.VeTableModel;
import gui.application.paymentHelper.PdfTicketExporter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Dec 17, 2025
 * @version: 1.0
 */

public class XemInVeController {
    private final Ve_BUS veBUS = new Ve_BUS();
    private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();
    private final int rowsPerPage = 20;
    private final JPopupMenu traCuuSuggestionPopup = new JPopupMenu();
    private final JPopupMenu khachHangSuggestionPopup = new JPopupMenu();
    private PanelXemInVe view;
    private KhachHangDTO selectedKhachHang = null;

    private int currentPage = 1;
    private int totalPages = 1;
    private SearchState currentState = SearchState.ALL;

    public XemInVeController(PanelXemInVe view) {
        this.view = view;

        // Tắt focusable của popup để focus vẫn nằm ở TextField khi gõ
        this.traCuuSuggestionPopup.setFocusable(false);
        this.khachHangSuggestionPopup.setFocusable(false);

        loadAllVe();
        init();
    }

    private void loadAllVe() {
        currentState = SearchState.ALL;
        currentPage = 1;

        // 1. Đếm tổng số để tính trang
        int totalRecords = veBUS.countAllVe();
        calculateTotalPages(totalRecords);

        // 2. Fetch trang 1
        fetchAndDisplayData();
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
        view.getTable().getColumnModel().getColumn(VeTableModel.COL_IN).setCellRenderer(new VeTableButtonRenderer());

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

                // Lấy đối tượng vé tại dòng click
                VeDTO selectedVe = view.getTableModel().getRow(row);

                if (column == VeTableModel.COL_IN) {
                    handleInVe(selectedVe);
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
                if (row >= 0 && column == VeTableModel.COL_IN) {
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
    }

    private void handleNgayLoc() {
        boolean isSelected = view.getCheckBoxTatCaNgay().isSelected();
        view.getDateChooserTuNgay().setEnabled(!isSelected);
        view.getDateChooserDenNgay().setEnabled(!isSelected);
    }

    private void handleLoc() {
        currentState = SearchState.FILTER;
        currentPage = 1;

        // 1. Validate Ngày tháng trước khi làm các việc khác
        boolean isTatCaNgay = view.getCheckBoxTatCaNgay().isSelected();
        Date tuNgay = view.getDateChooserTuNgay().getDate();
        Date denNgay = view.getDateChooserDenNgay().getDate();

        if (!isTatCaNgay && tuNgay != null && denNgay != null && tuNgay.after(denNgay)) {
            JOptionPane.showMessageDialog(view, "Ngày bắt đầu không được lớn hơn ngày kết thúc!", "Lỗi bộ lọc",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String loaiVe = (String) view.getCboLoaiVe().getSelectedItem();
        String tuKhoaInput = view.getTxtKhachHangSuggest().getText().trim();
        String searchKeyword = null;
        String searchID = null;

        if (selectedKhachHang != null && tuKhoaInput.equals(selectedKhachHang.getHoTen())) {
            searchID = selectedKhachHang.getId();
        } else {
            searchKeyword = tuKhoaInput.isEmpty() ? null : tuKhoaInput;
        }

        String tuKhoaTraCuu = view.getTxtTuKhoa().getText().trim();
        String loaiTraCuu = (String) view.getCboLoaiTimKiem().getSelectedItem();

        // 3. Đếm tổng số record thỏa mãn để chia trang
        int totalRecords = veBUS.countVeByFilter(tuKhoaTraCuu, loaiTraCuu, loaiVe, searchKeyword, searchID, isTatCaNgay ? null : tuNgay, isTatCaNgay ? null : denNgay);
        calculateTotalPages(totalRecords);

        if (totalRecords == 0) JOptionPane.showMessageDialog(view, "Không tìm thấy vé nào phù hợp!");
        else view.getTable().scrollRectToVisible(view.getTable().getCellRect(0, 0, true));


        // 4. Lấy dữ liệu trang 1 và hiển thị
        fetchAndDisplayData();
    }

    private void handleReset() {
        view.getCboLoaiVe().setSelectedIndex(0);
        view.getTxtKhachHangSuggest().setText("");
        view.getCheckBoxTatCaNgay().setSelected(true);
        view.getDateChooserTuNgay().setDate(new Date());
        view.getDateChooserDenNgay().setDate(new Date());
        view.getDateChooserTuNgay().setEnabled(false);
        view.getDateChooserDenNgay().setEnabled(false);

        loadAllVe();
    }

    private void handleTraCuu() {
        currentState = SearchState.SEARCH;
        currentPage = 1;

        // 1. Lấy dữ liệu
        String keyword = view.getTxtTuKhoa().getText().trim();
        String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

        // 2. Đếm tổng số record
        int totalRecords = veBUS.countVeByKeyword(keyword, type);
        calculateTotalPages(totalRecords);

        if (totalRecords == 0) JOptionPane.showMessageDialog(view, "Không tìm thấy kết quả nào!", "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);

        // 3. Lấy dữ liệu trang 1 và hiển thị
        fetchAndDisplayData();
    }

    private void handleRefresh() {
        view.getTxtTuKhoa().setText("");
        view.getCboLoaiTimKiem().setSelectedIndex(0);

        handleReset();
        loadAllVe();
    }

    private void handleInVe(VeDTO ve) {
        if (!ve.getTrangThai().equals(TrangThaiVe.DA_BAN.name())) {
            JOptionPane.showMessageDialog(view, "Vé " + TrangThaiVe.valueOf(ve.getTrangThai()).getDescription() + ". Không thể in");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view, "Bạn có muốn in vé " + ve.getVeID() + " không?",
                "Xác nhận in", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            PdfTicketExporter exporter = new PdfTicketExporter();
            exporter.exportTicketsToPdf(ve);
        }
    }

    private void setupTraCuuSuggestion() {
        JTextField txtSearch = view.getTxtTuKhoa();
        JComboBox<String> cboType = view.getCboLoaiTimKiem();

        // 1. Reset text khi đổi loại tìm kiếm (để tránh user tìm ID vé bằng mã KH)
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
        if ("Mã vé".equals(type)) {
            suggestions = veBUS.layTop10VeID(keyword);
        } else if ("Mã đặt chỗ".equals(type)) {
            suggestions = veBUS.layTop10DonDatChoID(keyword);
        } else if ("Số giấy tờ khách hàng".equals(type)) {
            suggestions = veBUS.layTop10SoGiayToKhachHang(keyword);
        }

        // 5. Hiển thị Popup
        if (!suggestions.isEmpty()) {
            for (String s : suggestions) {
                JMenuItem item = new JMenuItem(s);
                // Highlight icon khác nhau cho đẹp
                if ("Mã vé".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/ticket.svg", 0.6f));
                } else if ("Mã đặt chỗ".equals(type)) {
                    item.setIcon(new FlatSVGIcon("icon/svg/booking.svg", 0.6f));
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

    private void calculateTotalPages(int totalRecords) {
        totalPages = (int) Math.ceil((double) totalRecords / rowsPerPage);
        if (totalPages == 0) totalPages = 1;
    }

    // Gọi truy vấn đúng với trạng thái hiện tại kèm theo OFFSET (page)
    private void fetchAndDisplayData() {
        List<VeDTO> dtos = new ArrayList<>();

        if (currentState == SearchState.ALL) {
            dtos = veBUS.getVeByPage(currentPage, rowsPerPage);
        } else if (currentState == SearchState.FILTER) {
            String loaiVe = (String) view.getCboLoaiVe().getSelectedItem();
            String tuKhoaInput = view.getTxtKhachHangSuggest().getText().trim();
            boolean isTatCaNgay = view.getCheckBoxTatCaNgay().isSelected();
            Date tuNgay = isTatCaNgay ? null : view.getDateChooserTuNgay().getDate();
            Date denNgay = isTatCaNgay ? null : view.getDateChooserDenNgay().getDate();

            String searchKeyword = null;
            String searchID = null;
            if (selectedKhachHang != null && tuKhoaInput.equals(selectedKhachHang.getHoTen()))
                searchID = selectedKhachHang.getId();
            else searchKeyword = tuKhoaInput.isEmpty() ? null : tuKhoaInput;

            String tuKhoaTraCuu = view.getTxtTuKhoa().getText().trim();
            String loaiTraCuu = (String) view.getCboLoaiTimKiem().getSelectedItem();

            dtos = veBUS.locVeTheoCacTieuChi(tuKhoaTraCuu, loaiTraCuu, loaiVe, searchKeyword, searchID, tuNgay, denNgay, currentPage, rowsPerPage);
        } else if (currentState == SearchState.SEARCH) {
            String keyword = view.getTxtTuKhoa().getText().trim();
            String type = (String) view.getCboLoaiTimKiem().getSelectedItem();

            dtos = veBUS.layVeTheoKeyword(keyword, type, currentPage, rowsPerPage);
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