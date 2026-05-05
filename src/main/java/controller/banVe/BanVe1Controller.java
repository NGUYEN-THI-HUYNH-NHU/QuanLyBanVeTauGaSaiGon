package controller.banVe;

/*
 * @(#) PanelBanVe1Controller.java  1.0  [10:42:48 AM] Oct 22, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 22, 2025
 * @version: 1.0
 */

import bus.DatCho_BUS;
import bus.PhieuGiuCho_BUS;
import controller.banVe.PanelBuoc1Controller.SearchListener;
import controller.banVe.PanelBuoc2Controller.SeatSelectedListener;
import dto.ChuyenDTO;
import dto.PhieuGiuChoDTO;
import gui.application.form.banVe.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BanVe1Controller {
    private final PanelBanVe1 view;
    private final PanelBuoc1 p1;
    private final PanelBuoc2 p2;
    private final PanelBuoc3 p3;

    private final Map<String, Timer> countdownTimers = new ConcurrentHashMap<>();
    private final Map<String, JLabel> countdownLabels = new ConcurrentHashMap<>();

    private final BookingSession bookingSession;

    // Các sub-controller
    private final PanelBuoc1Controller buoc1Controller;
    private final PanelBuoc2Controller buoc2ControllerDi;
    private final PanelBuoc2Controller buoc2ControllerVe;
    private final PanelBuoc3Controller buoc3Controller;

    private final DatCho_BUS datChoBUS = new DatCho_BUS();
    private final PhieuGiuCho_BUS phieuGiuChoBUS = new PhieuGiuCho_BUS();

    private Runnable onRefreshListener;
    private Runnable onPanel1CompleteListener;

    public BanVe1Controller(PanelBanVe1 view, BookingSession session) {
        this.view = view;
        this.bookingSession = session;

        // Khởi tạo các panel con
        this.p1 = view.getPanelBuoc1();
        this.p2 = view.getPanelBuoc2();
        this.p3 = view.getPanelBuoc3();

        this.buoc1Controller = new PanelBuoc1Controller(this.p1);

        // Lấy các panel con từ PanelBuoc2
        PanelChuyen panelChieuDi = p2.getPanelChieuDi();
        PanelChuyen panelChieuVe = p2.getPanelChieuVe();
        PanelGioVe panelGioVe = p2.getPanelGioVe(); // Giỏ vé dùng chung

        this.buoc2ControllerDi = new PanelBuoc2Controller(panelChieuDi.getPanelChieuLabel(),
                panelChieuDi.getPanelChuyenTau(), panelChieuDi.getPanelDoanTau(), panelChieuDi.getPanelSoDoCho());
        this.buoc2ControllerDi.setBookingSession(this.bookingSession);

        this.buoc2ControllerVe = new PanelBuoc2Controller(panelChieuVe.getPanelChieuLabel(),
                panelChieuVe.getPanelChuyenTau(), panelChieuVe.getPanelDoanTau(), panelChieuVe.getPanelSoDoCho());
        this.buoc2ControllerVe.setBookingSession(this.bookingSession);

        this.buoc3Controller = new PanelBuoc3Controller(view.getPanelBuoc3(), this.bookingSession);

        panelGioVe.setMediator(this);

        // 2. Kích hoạt Bước 2
        view.setBuoc2Enabled(false);
        view.setBuoc3Enabled(false);

        initMediatorLogic();

        phieuGiuChoBUS.donDepPhieuHetHan(10);
    }

    public void addRefreshListener(Runnable listener) {
        this.onRefreshListener = listener;
    }

    public void addPanel1CompleteListener(Runnable listener) {
        this.onPanel1CompleteListener = listener;
    }

    private void initMediatorLogic() {

        // Lắng nghe sự kiện từ Buoc1 (Tìm chuyến)
        this.buoc1Controller.addSearchListener(new SearchListener() {
            @Override
            // 1. Cập nhật chữ ký (signature) của hàm để nhận 2 danh sách
            public void onSearchSuccess(List<ChuyenDTO> outboundResults, List<ChuyenDTO> returnResults,
                                        SearchCriteria criteria) {

                // 1. Lưu criteria và kết quả vào session
                bookingSession.setOutboundCriteria(criteria);
                bookingSession.setOutboundResults(outboundResults);
                bookingSession.setReturnResults(returnResults);

                // 2. Kích hoạt Bước 2
                view.setBuoc2Enabled(true);
                view.setBuoc3Enabled(false);

                // 3. Xử lý vé khứ hồi
                if (criteria.isKhuHoi() && returnResults != null && !returnResults.isEmpty()) {
                    // CÓ VÉ VỀ
                    bookingSession.setReturnResults(returnResults);
                    p2.showReturnTab(true); // Hiển thị tab "Chiều về"

                    // Tạo criteria cho chiều về (đảo ngược ga)
                    SearchCriteria criteriaVe = new SearchCriteria.Builder().tenGaDi(criteria.getGaDenName())
                            .gaDiId(criteria.getGaDenId()).tenGaDen(criteria.getGaDiName())
                            .gaDenId(criteria.getGaDiId()).ngayDi(criteria.getNgayVe()).build();
                    bookingSession.setReturnCriteria(criteriaVe);
                    // Bơm data vào cả 2 controller
                    buoc2ControllerDi.displayChuyenList(criteria, outboundResults, 0);
                    buoc2ControllerVe.displayChuyenList(criteriaVe, returnResults, 1);

                } else {
                    // CHỈ CÓ VÉ ĐI
                    bookingSession.setReturnResults(null);
                    bookingSession.setReturnCriteria(null);
                    p2.showReturnTab(false); // Ẩn tab "Chiều về"

                    // Chỉ bơm data vào controller "Chiều đi"
                    buoc2ControllerDi.displayChuyenList(criteria, outboundResults, 0);
                }

                // 4. Mặc định chọn tab "Chiều đi" và refresh giỏ vé
                p2.getTabbedPane().setSelectedIndex(0);

                refreshGioVe();
            }

            @Override
            public void onSearchFailure() {
                // Xóa cả hai danh sách kết quả khi tìm thất bại
                bookingSession.setOutboundResults(null);
                bookingSession.setReturnResults(null);
                view.setBuoc2Enabled(false);
                view.setBuoc3Enabled(false);
                refreshGioVe();
            }
        });

        p2.getTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Khi người dùng đổi tab (đi <-> về)
                int selectedIndex = p2.getTabbedPane().getSelectedIndex();

//				// Cập nhật giỏ vé để hiển thị vé của tab tương ứng
//				p2.getPanelGioVe().refresh(bookingSession.getSelectedTicketsForTrip(selectedIndex));

                // (Quan trọng) Cập nhật index cho CẢ HAI controller
                // để chúng biết tab nào đang active
                buoc2ControllerDi.setCurrentTripIndex(selectedIndex);
                buoc2ControllerVe.setCurrentTripIndex(selectedIndex);
            }
        });

        // === Lắng nghe sự kiện từ CẢ 2 CONTROLLER ===
        // 1. Gắn MỘT listener duy nhất cho nút Mua Vé
        p2.getPanelGioVe().addBuyButtonListener(e -> {
            handleMuaVe();
        });

        // 2. Lắng nghe sự kiện chọn/bỏ chọn ghế từ cả 2 controller
        SeatSelectedListener seatSelectOnlyListener = new SeatSelectedListener() {
            @Override
            public void onSeatSelected(VeSession veSession) {
                // Khi 1 ghế được chọn (từ controllerDi hoặc Ve)
                // 1. Đăng ký timer cho nó
                startCountdownForVe(veSession);
                // 2. Refresh lại giỏ vé (View)
                refreshGioVe();
            }

            @Override
            public void onSeatDeselected(VeSession veSession) {
                // 1. Tìm controller đúng
                final PanelBuoc2Controller correctController = (bookingSession.getReturnSelectedTickets() != null
                        && bookingSession.getReturnSelectedTickets().contains(veSession)) ? buoc2ControllerVe
                        : buoc2ControllerDi;

                // 2. Cập nhật Model và PanelSoDoCho
                if (correctController != null) {
                    correctController.onRemoveVe(veSession);
                }

                // 3. Dừng timer
                stopCountdownForVe(veSession);

                // 4. Refresh Giỏ vé (View)
                refreshGioVe();
            }
        };
        this.buoc2ControllerDi.addSeatSelectedListener(seatSelectOnlyListener);
        this.buoc2ControllerVe.addSeatSelectedListener(seatSelectOnlyListener);

        // Lắng nghe sự kiện từ Buoc3
        this.buoc3Controller.setOnRefreshListener(() -> {
            view.setBuoc3Enabled(false);
            // Xoá tất cả vé trong giỏ
            for (VeSession ve : bookingSession.getAllSelectedTickets()) {
                handleXoaHangVe(ve);
            }
            if (onRefreshListener != null) {
                onRefreshListener.run();
            }
        });

        this.buoc3Controller.setOnDeleteListener(veSession -> {
            handleXoaHangVe(veSession);
        });

        this.buoc3Controller.setOnConfirmListener(() -> {
            if (onPanel1CompleteListener != null) {
                onPanel1CompleteListener.run();
            }
        });

        // Lắng nghe sự kiện "Hủy" từ Buoc 3
        this.buoc3Controller.setOnCancelListener(() -> {
            view.setBuoc3Enabled(false);
            // Xoá tất cả vé trong giỏ
            for (VeSession ve : bookingSession.getAllSelectedTickets()) {
                handleXoaHangVe(ve);
            }
            view.setBuoc1Enabled(true);
            view.setBuoc2Enabled(true);
            view.setBuoc3Enabled(false);
        });
    }

    private void handleXoaHangVe(VeSession veSession) {
        // Tìm xem vé này thuộc controller nào (đi hay về)
        final PanelBuoc2Controller correctController = (bookingSession.getReturnSelectedTickets() != null
                && bookingSession.getReturnSelectedTickets().contains(veSession)) ? buoc2ControllerVe
                : buoc2ControllerDi;
        goiBusHuyGiuCho(correctController, veSession);
    }

    /**
     * Hàm này thực hiện gọi BUS giữ chỗ cho các vé (trong giỏ) trong luồng nền
     */
    private void goiBusGiuCho(List<VeSession> veTrongGio) {
        new SwingWorker<PhieuGiuChoDTO, Void>() {
            private String errorMessage = "Lỗi không xác định";

            @Override
            protected PhieuGiuChoDTO doInBackground() throws Exception {
                try {
                    return datChoBUS.thucHienGiuCho(veTrongGio);
                } catch (Exception e) {
                    // Lấy lỗi nghiệp vụ (ví dụ: "Ghế bị chiếm)
                    errorMessage = e.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    PhieuGiuChoDTO pgc = get();
                    if (pgc != null) {
                        bookingSession.setPhieuGiuCho(pgc);

                        view.setBuoc3Enabled(true);
                        p3.initFromBookingSession(bookingSession, p2.getTabbedPane().getSelectedIndex());
                    } else {
                        JOptionPane.showMessageDialog(view, "Không thể giữ chỗ: \n" + errorMessage, "Lỗi giữ chỗ",
                                JOptionPane.ERROR_MESSAGE);
                        // (Tùy chọn: refresh lại sơ đồ ghế để thấy ghế bị trùng)
                    }
                } catch (Exception e) {
                    // Lỗi của chính SwingWorker hoặc lỗi logic
                    JOptionPane.showMessageDialog(view, "Lỗi hệ thống: " + e.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    // (Tùy chọn: refresh lại sơ đồ ghế để thấy ghế bị trùng)
                }
            }
        }.execute();
    }

    /**
     * Hàm này thực hiện gọi BUS hủy giữ chỗ cho 1 vé trong luồng nền
     */
    private void goiBusHuyGiuCho(PanelBuoc2Controller correctController, VeSession veSession) {
        // 1. Gọi BUS để xóa phiếu giữ chỗ chi tiết TRONG DB
        new SwingWorker<Boolean, Void>() {
            private String errorMessage = "Lỗi không xác định khi xóa phiếu.";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    if (bookingSession.getPhieuGiuCho() == null || bookingSession.getPhieuGiuCho().getPhieuGiuChoID() == null) {
                        return true;
                    }
                    if (veSession.getPhieuGiuChoChiTiet() != null) {
                        return datChoBUS.xoaPhieuGiuChoChiTietByPgcctID(
                                veSession.getPhieuGiuChoChiTiet().getId());
                    }
                    return true;
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void done() {
                try {
                    Boolean deleteSuccess = get();
                    if (deleteSuccess) {
                        // 1. Gọi onRemoveVe 1 LẦN (để xóa khỏi session và refresh SoDoCho)
                        if (correctController != null) {
                            correctController.onRemoveVe(veSession);
                        }
                        // 2. Dừng timer (logic đã chuyển về đây)
                        stopCountdownForVe(veSession);
                        // 3. Refresh giỏ vé (Mediator tự làm)
                        refreshGioVe();
                        // 4. Xóa PGC nếu giỏ rỗng
                        if (bookingSession.getAllSelectedTickets().isEmpty()) {
                            if (bookingSession.getPhieuGiuCho() != null) {
                                datChoBUS.xoaPhieuGiuCho(bookingSession.getPhieuGiuCho().getPhieuGiuChoID());
                            }
                        }

                        // 5. Tải lại bảng Buoc3 (Giữ nguyên)
                        if (p3 != null) {
                            p3.initFromBookingSession(bookingSession, p2.getTabbedPane().getSelectedIndex());
                        }

                    } else {
                        // Báo lỗi nếu xóa DB thất bại
                        JOptionPane.showMessageDialog(view, "Lỗi: " + errorMessage, "Lỗi xóa vé",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(view, "Lỗi hệ thống khi xóa phiếu giữ chỗ.", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Hàm mới: Xử lý khi bấm nút "Mua vé" (thay thế seatListenerChinh)
     */
    private void handleMuaVe() {
        view.setBuoc1Enabled(false);
        view.setBuoc2Enabled(false);

        // Kiểm tra logic khứ hồi
        if (bookingSession.isRoundTrip() && (bookingSession.getOutboundSelectedTickets().isEmpty()
                || bookingSession.getReturnSelectedTickets().isEmpty())) {
            int choice = JOptionPane.showConfirmDialog(view,
                    "Bạn chưa chọn vé cho cả 2 chiều. Bạn có muốn tiếp tục không?", "Xác nhận khứ hồi",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.NO_OPTION) {
                view.setBuoc2Enabled(true);
                return;
            }
        }

        // Lấy TẤT CẢ vé (logic cũ đã đúng)
        List<VeSession> allTickets = bookingSession.getAllSelectedTickets();

        // Kiểm tra giỏ vé trống
        if (allTickets.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Giỏ vé trống. Vui lòng chọn ít nhất 1 vé.", "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
            view.setBuoc2Enabled(true); // Mở lại Bước 2
            return;
        }

        goiBusGiuCho(allTickets);
    }

    /**
     * Hàm mới: Xử lý khi bấm nút "Xóa" (trash) TỪ GIỎ VÉ
     */
    public void handleGioVeRemove(VeSession v) {
        if (v == null) {
            return;
        }

        // 1. Tìm controller đúng
        final PanelBuoc2Controller correctController = (bookingSession.getReturnSelectedTickets() != null
                && bookingSession.getReturnSelectedTickets().contains(v)) ? buoc2ControllerVe : buoc2ControllerDi;

        // 2. Cập nhật Model (xóa khỏi session)
        // (onRemoveVe của controller sẽ làm việc này)
        if (correctController != null) {
            correctController.onRemoveVe(v); // Sẽ refresh SoDoCho
        }

        // 3. Dừng timer
        stopCountdownForVe(v);

        // 4. Refresh Giỏ vé (View)
        refreshGioVe();
    }

    /**
     * Hàm mới: Refresh giỏ vé (tập trung)
     */
    public void refreshGioVe() {
        if (p2 != null && p2.getPanelGioVe() != null && bookingSession != null) {
            p2.getPanelGioVe().refresh(bookingSession.getAllSelectedTickets());
        }
    }

    public void registerCountdownLabelForVe(VeSession v, JLabel lbl) {
        countdownLabels.put(v.toString(), lbl);
        if (!countdownTimers.containsKey(v.toString())) {
            startCountdownForVe(v);
        }
    }

    private void stopCountdownForVe(VeSession v) {
        if (v == null) {
            return;
        }
        Timer old = countdownTimers.remove(v.toString());
        if (old != null) {
            old.stop();
        }
        countdownLabels.remove(v.toString());
    }

    private void startCountdownForVe(VeSession v) {
        String id = v.toString();
        stopCountdownForVe(v); // Dừng timer cũ (nếu có)

        final LocalDateTime thoiDiemHetHan = v.getThoiDiemHetHan();
        Timer timer = new Timer(1000, e -> {
            long s = ChronoUnit.SECONDS.between(LocalDateTime.now(), thoiDiemHetHan);
            JLabel label = countdownLabels.get(id);
            if (label != null) {
                label.setText(formatSeconds(s));
            }
            if (s <= 0) {
                ((Timer) e.getSource()).stop();
                countdownTimers.remove(id);
                countdownLabels.remove(id);

                // (Tìm controller đúng để gọi release)
                final PanelBuoc2Controller correctController = (bookingSession.getReturnSelectedTickets() != null
                        && bookingSession.getReturnSelectedTickets().contains(v)) ? buoc2ControllerVe
                        : buoc2ControllerDi;

                if (correctController != null) {
                    correctController.releaseHoldAndRemoveVe(v);
                    refreshGioVe();
                }
                // Xóa PGC nếu giỏ rỗng
                if (bookingSession.getAllSelectedTickets().isEmpty()) {
                    if (bookingSession.getPhieuGiuCho() != null) {
                        datChoBUS.xoaPhieuGiuCho(bookingSession.getPhieuGiuCho().getPhieuGiuChoID());
                    }
                }

                // Vì vé đã bị xóa khỏi session ở bước 1, nên ta chỉ cần
                // bảo PanelBuoc3 load lại dữ liệu từ session là xong.
                SwingUtilities.invokeLater(() -> {
                    if (p3 != null) {
                        // Load lại bảng dựa trên tab đang chọn (Đi hoặc Về)
                        p3.initFromBookingSession(bookingSession, p2.getTabbedPane().getSelectedIndex());
                    }
                });
            }
        });
        timer.setInitialDelay(0);
        timer.start();
        countdownTimers.put(id, timer);
    }

    /**
     * Dừng tất cả các bộ đếm ngược (Được gọi khi thanh toán thành công)
     */
    public void stopAllTimers() {
        // Duyệt qua tất cả các timer đang chạy
        for (String key : countdownTimers.keySet()) {
            Timer t = countdownTimers.get(key);
            if (t != null) {
                t.stop();
            }
        }
        // Xóa sạch danh sách timer và label
        countdownTimers.clear();
        countdownLabels.clear();
    }

    private String formatSeconds(long s) {
        if (s <= 0) {
            return "00:00";
        }
        long m = s / 60;
        long sec = s % 60;
        return String.format("%02d:%02d", m, sec);
    }
}