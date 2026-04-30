package gui.application.form.banVe;
/*
 * @(#) PanelBuoc3Controller.java  1.0  [8:06:26 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import bus.KhachHang_BUS;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import entity.KhachHang;
import entity.LoaiKhachHang;
import entity.type.LoaiKhachHangEnums;
import gui.application.AuthService;
import gui.application.UngDung;
import mapper.KhachHangMapper;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PanelBuoc3Controller {

    private final PanelBuoc3 view;
    private final BookingSession bookingSession;
    private final KhachHang_BUS khachHangBUS = new KhachHang_BUS();
    private JPopupMenu khachHangSuggestionPopup;

    // Listeners để báo cho Controller Mediator (BanVe1Controller)
    private Runnable onRefreshListener;
    private Runnable onConfirmListener;
    private Runnable onCancelListener;

    private Consumer<VeSession> onDeleteListener;

    public PanelBuoc3Controller(PanelBuoc3 view, BookingSession bookingSession) {
        this.view = view;
        this.khachHangSuggestionPopup = new JPopupMenu();
        this.khachHangSuggestionPopup.setFocusable(false);

        this.bookingSession = bookingSession;

        this.view.setController(this);

        attachListeners();
        setupKhachHangSuggestion();
    }

    // Gợi ý kiểu google search trong phạm vi hoTen/soDienThoai/soGiayTo/khachHangID
    private void setupKhachHangSuggestion() {
        JTextField txtSuggest = view.getTxtCccdNguoiMua();

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

    private void showKhachHangSuggestions() {
        String keyword = view.getTxtCccdNguoiMua().getText().trim();
        khachHangSuggestionPopup.setVisible(false);
        khachHangSuggestionPopup.removeAll();

        if (keyword.length() < 1) {
            return;
        }

        // Lấy danh sách gợi ý
        List<KhachHang> listSuggest = khachHangBUS.layGoiYKhachHang(keyword);
        int limit = 3;
        int i = 0;

        if (!listSuggest.isEmpty()) {
            for (KhachHang kh : listSuggest) {
                if (i++ == limit) {
                    break;
                }
                // Tạo text hiển thị: "Tên - SĐT - CCCD - ID"
                String displayText = String.format(
                        "<html><b>%s</b> - %s <br><i style='color:gray; font-size:9px'>%s - %s</i></html>",
                        kh.getHoTen(), (kh.getSoDienThoai() == null ? "N/A" : kh.getSoDienThoai()), kh.getSoGiayTo(),
                        kh.getKhachHangID());

                JMenuItem item = new JMenuItem(displayText);
                item.setIcon(new FlatSVGIcon("icon/svg/person.svg", 0.6f));

                // Sự kiện khi chọn 1 dòng gợi ý
                item.addActionListener(e -> {
                    // 1. Điền tên vào TextField
                    view.getTxtCccdNguoiMua().setText(kh.getSoGiayTo());
                    // 2. Ẩn popup
                    khachHangSuggestionPopup.setVisible(false);
                });
                khachHangSuggestionPopup.add(item);
            }

            // Hiển thị Popup ngay dưới TextField
            khachHangSuggestionPopup.show(view.getTxtCccdNguoiMua(), 0, view.getTxtCccdNguoiMua().getHeight());
            // Focus lại vào textfield để user gõ
            view.getTxtCccdNguoiMua().requestFocus();
        }
    }

    // Gắn listener vào các nút của View
    private void attachListeners() {
        view.getRefreshButton().addActionListener(e -> handleRefresh());
        view.getConfirmButton().addActionListener(e -> {
            handleConfirm();
        });
        view.getCancelButton().addActionListener(e -> handleCancel());
        view.setPassengerDeleteListener(row -> {
            handleDelete(row);
        });

        // 1. Enter trên CCCD -> Tìm kiếm và focus Tên
        view.getTxtCccdNguoiMua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findNguoiMua();
                view.getTxtTenNguoiMua().requestFocusInWindow();
            }
        });

        // 2. Enter trên Tên -> focus Số điện thoại
        view.getTxtTenNguoiMua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getTxtPhoneNguoiMua().requestFocusInWindow();
            }
        });

        // 3. Enter trên SĐT -> focus Email
        view.getTxtPhoneNguoiMua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getTxtEmailNguoiMua().requestFocusInWindow();
            }
        });

        // 4. Enter trên SĐT -> focus Nút Xác nhận
        view.getTxtEmailNguoiMua().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getConfirmButton().requestFocusInWindow();
            }
        });

        addValidateListener(view.getTxtCccdNguoiMua());
        addValidateListener(view.getTxtTenNguoiMua());
        addValidateListener(view.getTxtPhoneNguoiMua());
        addValidateListener(view.getTxtEmailNguoiMua());
    }

    private void handleRefresh() {
        if (JOptionPane.showConfirmDialog(view, "Bạn xác nhận làm mới phiên bán vé?", "Lưu ý",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (onRefreshListener != null) {
                onRefreshListener.run();
            }
        }
    }

    // Kiểm tra lỗi ngay khi gõ
    private void addValidateListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validate(textField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate(textField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate(textField);
            }
        });
    }

    private boolean validate(JTextField textField) {
        // 1. Check ID (CCCD/Hộ chiếu)
        if (view.getTxtCccdNguoiMua().isFocusOwner()) {
            String cccdNguoiMua = view.getTxtCccdNguoiMua().getText();
            if (cccdNguoiMua.isEmpty()) {
                showError("Vui lòng nhập CCCD/Hộ chiếu", view.getTxtCccdNguoiMua());
                return false;
            }
            // Regex: Chỉ chấp nhận số, độ dài 9-15 (CCCD VN là 12)
            if (!cccdNguoiMua.matches("^[0-9]{12}$")) {
                showError("CCCD không đúng định dạng (12 ký số)", view.getTxtCccdNguoiMua());
                return false;
            }
        }
        // 2. Check Tên
        else if (view.getTxtTenNguoiMua().isFocusOwner()) {
            String tenNguoiMua = view.getTxtTenNguoiMua().getText();
            if (tenNguoiMua.isEmpty()) {
                showError("Vui lòng nhập họ tên", view.getTxtTenNguoiMua());
                return false;
            }
            // Regex: Chấp nhận chữ cái unicode (tiếng Việt), khoảng trắng, dấu chấm (nếu
            // cần)
            // [^0-9!@#...] -> Đơn giản là không chứa số và ký tự đặc biệt cơ bản
            if (tenNguoiMua.matches(".*\\d.*") || tenNguoiMua.matches(".*[!@#$%^&*()_+=<>?].*")) {
                showError("Tên không được chứa số hoặc ký tự đặc biệt", view.getTxtTenNguoiMua());
                return false;
            }
        }
        // 3. Check số điện thoại
        else if (view.getTxtPhoneNguoiMua().isFocusOwner()) {
            String phoneNguoiMua = view.getTxtPhoneNguoiMua().getText();
            if (phoneNguoiMua.isEmpty()) {
                showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
                return false;
            }
            // Regex: 10 số bắt đầu bằng 0
            if (!phoneNguoiMua.matches("^0[0-9]{9}$")) {
                showError("Số điện thoại gồm 10 số, bắt đầu bằng 0", view.getTxtPhoneNguoiMua());
                return false;
            }
        }
        // 4. Check email
        else if (view.getTxtEmailNguoiMua().isFocusOwner()) {
            String emailNguoiMua = view.getTxtEmailNguoiMua().getText();
            if (emailNguoiMua.isEmpty()) {
                showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
                return false;
            }
            if (!emailNguoiMua.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                showError("Email không hợp lệ", view.getTxtEmailNguoiMua());
                return false;
            }
        }

        hideError();
        return true;
    }

    /**
     * Hàm tìm kiếm người mua (KhachHang) bằng CCCD và cập nhật View nếu tìm thấy.
     */
    private void findNguoiMua() {
        String id = view.getTxtCccdNguoiMua().getText().trim();
        if (id.isEmpty()) {
            return;
        }

        // Gọi BUS để tìm
        KhachHang kh = findKhachHangByID(id);

        if (kh != null) {
            // Tìm thấy -> Cập nhật View
            view.getTxtTenNguoiMua().setText(kh.getHoTen());
            view.getTxtPhoneNguoiMua().setText(kh.getSoDienThoai());
            view.getTxtEmailNguoiMua().setText(kh.getEmail());
            // Lưu khách hàng tìm thấy vào session
            bookingSession.setKhachHang(kh);
        } else {
            // Không tìm thấy -> Xóa dữ liệu cũ (nếu có)
            view.getTxtTenNguoiMua().setText("");
            view.getTxtPhoneNguoiMua().setText("");
            view.getTxtEmailNguoiMua().setText("");
            // Đặt session về null để handleConfirm biết là khách mới
            bookingSession.setKhachHang(null);
            int choice = JOptionPane.showConfirmDialog(view, "Khách hàng chưa tồn tại. Bạn có muốn thêm mới không?",
                    "Thông báo", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                // Điều hướng sang màn hình Quản lý khách hàng
                UngDung.loadDataForCreatingNewKhachHang(AuthService.getInstance().getCurrentUser(), id);
            }
        }
    }

    public KhachHang findKhachHangByID(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        try {
            return khachHangBUS.timKiemKhachHangTheoSoGiayTo(id.trim());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addHanhKhach(KhachHang hanhKhach) {
        if (hanhKhach != null) {
            String hanhKhachID = khachHangBUS.taoMaKhachHangTuDong();
            hanhKhach.setKhachHangID(hanhKhachID);
            return khachHangBUS.themKhachHang(hanhKhach);
        }
        return false;
    }

    private void handleDelete(PassengerRow rowToDelete) {
        if (rowToDelete == null) {
            return;
        }

        VeSession veSession = rowToDelete.getVeSession();

        // Hiển thị xác nhận
        int choice = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa vé:\n" + veSession.prettyString(),
                "Xác nhận xóa vé", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            // Nếu người dùng đồng ý, báo cho BanVe1Controller
            if (onDeleteListener != null) {
                onDeleteListener.accept(veSession);
            }
        }
    }

    /**
     * Xử lý logic khi bấm "Xác nhận"
     */
    private void handleConfirm() {
        // 1. Lấy dữ liệu thô từ View
        List<PassengerRow> rows = view.getPassengerRows();
        String tenNguoiMua = view.getTxtTenNguoiMua().getText().trim();
        String cccdNguoiMua = view.getTxtCccdNguoiMua().getText().trim();
        String phoneNguoiMua = view.getTxtPhoneNguoiMua().getText().trim();
        String emailNguoiMua = view.getTxtEmailNguoiMua().getText().trim();

        // 2. Validate
        if (!validate(cccdNguoiMua, tenNguoiMua, phoneNguoiMua, emailNguoiMua)) {
            return;
        }

        // Kiểm tra toàn bộ danh sách
        for (int i = 0; i < rows.size(); i++) {
            PassengerRow row = rows.get(i);
            String cccd = row.getSoGiayTo();
            String ten = row.getHoTen(); // Nên kiểm tra cả tên nữa

            // Kiểm tra null HOẶC rỗng
            if (cccd == null || cccd.trim().isEmpty() || ten == null || ten.trim().isEmpty()) {
                JOptionPane.showMessageDialog(view,
                        "Vui lòng nhập đầy đủ Tên và CCCD cho hành khách thứ " + (i + 1) + "!");
                view.focusErrorRow(i);
                return;
            }
        }

        // --- DÙNG MAP ĐỂ TRÁNH TRÙNG LẶP TRONG PHIÊN XỬ LÝ ---
        // Key: Số giấy tờ (CCCD), Value: Đối tượng KhachHang
        Map<String, KhachHang> processedCustomers = new HashMap<>();

        // 3. Cập nhật Model (BookingSession)
        // 3a. Cập nhật thông tin Hành Khách vào từng VeSession
        for (PassengerRow row : rows) {
            VeSession ve = row.getVeSession();
            String cccdHanhKhach = row.getSoGiayTo();

            // Bước 1: Kiểm tra trong Map cục bộ (đã xử lý ở vòng lặp trước chưa?)
            KhachHang hanhKhach = processedCustomers.get(cccdHanhKhach);

            // Bước 2: Nếu chưa có trong Map, kiểm tra trong CSDL
            if (hanhKhach == null) {
                hanhKhach = khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccdHanhKhach);
            }

            // Bước 3: Nếu chưa có ở đâu cả -> Tạo mới
            if (hanhKhach == null) {
                if (khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccdHanhKhach) != null) {
                    return;
                }
                hanhKhach = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), row.getHoTen(), null, null,
                        cccdHanhKhach, null, row.getLoaiDoiTuong(), new LoaiKhachHang(LoaiKhachHangEnums.HANH_KHACH.name()));
                khachHangBUS.themKhachHang(hanhKhach);
//				System.out.println("Tạo hành khách mới: " + hanhKhach.getHoTen());
            } else {
                // Nếu đã có, cập nhật thông tin mới nhất từ UI (ví dụ tên có thể sửa)
                hanhKhach.setHoTen(row.getHoTen());
                hanhKhach.setLoaiDoiTuong(row.getLoaiDoiTuong());
                khachHangBUS.capNhatKhachHang(hanhKhach);
            }

            // Bước 4: Lưu vào Map để dùng lại (cho vé khứ hồi hoặc cho người mua)
            processedCustomers.put(cccdHanhKhach, hanhKhach);

            // Gán vào vé
            ve.getVe().setKhachHangDTO(KhachHangMapper.INSTANCE.toDTO(hanhKhach));
        }

        // 3b. Cập nhật Khách hàng (Người Mua)
        // Ưu tiên 1: Lấy từ Map (nếu người mua chính là một trong các hành khách vừa
        // nhập)
        KhachHang nguoiMua = processedCustomers.get(cccdNguoiMua);

        // Ưu tiên 2: Nếu không phải hành khách, tìm trong CSDL (khách cũ)
        if (nguoiMua == null) {
            nguoiMua = khachHangBUS.timKiemKhachHangTheoSoGiayTo(cccdNguoiMua);
        }

        if (nguoiMua != null) {
            // === TRƯỜNG HỢP: NGƯỜI MUA ĐÃ TỒN TẠI (hoặc trùng với hành khách) ===
            // Cập nhật thông tin người mua
            nguoiMua.setHoTen(tenNguoiMua);
            nguoiMua.setSoDienThoai(phoneNguoiMua);
            nguoiMua.setEmail(emailNguoiMua);

            // Logic cập nhật loại khách hàng
            if (nguoiMua.getLoaiKhachHang().getLoaiKhachHangID().equals(LoaiKhachHangEnums.HANH_KHACH)) {
                // Nếu trước đây chỉ là hành khách, giờ thành Hành khách + Người mua
                nguoiMua.setLoaiKhachHang(new LoaiKhachHang(LoaiKhachHangEnums.HANH_KHACH_KHACH_HANG.name()));
            }

            khachHangBUS.capNhatKhachHang(nguoiMua);
//			System.out.println("Cập nhật thông tin người mua: " + nguoiMua.getHoTen());

        } else {
            // === TRƯỜNG HỢP: NGƯỜI MUA MỚI TINH (Và không đi tàu) ===
            nguoiMua = new KhachHang(khachHangBUS.taoMaKhachHangTuDong(), tenNguoiMua, phoneNguoiMua, emailNguoiMua,
                    cccdNguoiMua, null, null, new LoaiKhachHang(LoaiKhachHangEnums.KHACH_HANG.name()));
            khachHangBUS.themKhachHang(nguoiMua);
//			System.out.println("Tạo người mua mới: " + nguoiMua.getHoTen());
        }

        // Lưu vào session
        bookingSession.setKhachHang(nguoiMua);

//		System.out.println("BookingSession đã được cập nhật.");

        // 4. Báo cho Controller cha
        if (onConfirmListener != null) {
            onConfirmListener.run();
        }
    }

    /**
     * @param cccdNguoiMua
     * @param tenNguoiMua
     * @param phoneNguoiMua
     * @return
     */
    private boolean validate(String cccdNguoiMua, String tenNguoiMua, String phoneNguoiMua, String emailNguoiMua) {
        // 1. Check ID (CCCD/Hộ chiếu)
        if (cccdNguoiMua.isEmpty()) {
            showError("Vui lòng nhập CCCD/Hộ chiếu", view.getTxtCccdNguoiMua());
            return false;
        }
        // Regex: Chỉ chấp nhận số, độ dài 9-15 (CCCD VN là 12)
        if (!cccdNguoiMua.matches("^[0-9]{12}$")) {
            showError("CCCD không đúng định dạng (12 ký số)", view.getTxtCccdNguoiMua());
            return false;
        }

        // 2. Check Tên
        if (tenNguoiMua.isEmpty()) {
            showError("Vui lòng nhập họ tên", view.getTxtTenNguoiMua());
            return false;
        }
        // Regex: Chấp nhận chữ cái unicode (tiếng Việt), khoảng trắng, dấu chấm (nếu
        // cần)
        // [^0-9!@#...] -> Đơn giản là không chứa số và ký tự đặc biệt cơ bản
        if (tenNguoiMua.matches(".*\\d.*") || tenNguoiMua.matches(".*[!@#$%^&*()_+=<>?].*")) {
            showError("Tên không được chứa số hoặc ký tự đặc biệt", view.getTxtTenNguoiMua());
            return false;
        }

        // 2. Check số điện thoại
        if (phoneNguoiMua.isEmpty()) {
            showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
            return false;
        }
        // Regex: 10 số bắt đầu bằng 0
        if (!phoneNguoiMua.matches("^0[0-9]{9}$")) {
            showError("Số điện thoại gồm 10 số, bắt đầu bằng 0", view.getTxtPhoneNguoiMua());
            return false;
        }

        if (emailNguoiMua.isEmpty()) {
            showError("Vui lòng nhập số điện thoại", view.getTxtPhoneNguoiMua());
            return false;
        }
        if (!emailNguoiMua.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            showError("Email không hợp lệ", view.getTxtEmailNguoiMua());
            return false;
        }

        hideError();
        return true;
    }

    private void showError(String msg, JTextField textField) {
        view.getLblError().setText(msg);
        view.getLblError().setVisible(true);
        textField.requestFocusInWindow();
    }

    private void hideError() {
        view.getLblError().setVisible(false);
        view.getLblError().setText("");
    }

    /**
     * Xử lý logic khi bấm "Hủy"
     */
    private void handleCancel() {
        if (JOptionPane.showConfirmDialog(view,
                "Bạn xác nhận hủy giữ chỗ cho " + bookingSession.getAllSelectedTickets().size() + " vé", "Lưu ý",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            view.getModel().clear();

            // Báo cho Controller cha biết
            if (onCancelListener != null) {
                onCancelListener.run();
            }
        }
    }

    // Setter cho các listener
    public void setOnRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }

    public void setOnConfirmListener(Runnable listener) {
        this.onConfirmListener = listener;
    }

    public void setOnCancelListener(Runnable listener) {
        this.onCancelListener = listener;
    }

    public void setOnDeleteListener(Consumer<VeSession> listener) {
        this.onDeleteListener = listener;
    }
}