package controller.doiVe;
/*
 * @(#) DoiVe2Controller.java  1.0  [5:41:27 PM] Nov 17, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 17, 2025
 * @version: 1.0
 */

import bus.DatCho_BUS;
import controller.doiVe.DoiVeBuoc4Controller.SearchNewTicketListener;
import controller.doiVe.DoiVeBuoc5Controller.SeatSelectedListener;
import dto.ChuyenDTO;
import dto.PhieuGiuChoDTO;
import gui.application.form.banVe.SearchCriteria;
import gui.application.form.banVe.VeSession;
import gui.application.form.doiVe.*;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DoiVe2Controller {
    private final PanelDoiVe2 view;
    private final PanelDoiVeBuoc4 p4;
    private final PanelDoiVeBuoc5 p5;
    private final PanelDoiVeBuoc6 p6;

    private final Map<String, Timer> countdownTimers = new ConcurrentHashMap<>();
    private final Map<String, JLabel> countdownLabels = new ConcurrentHashMap<>();

    private final ExchangeSession exchangeSession;

    // Các sub-controller
    private final DoiVeBuoc4Controller buoc4Controller;
    private final DoiVeBuoc5Controller buoc5Controller;
    private final DoiVeBuoc6Controller buoc6Controller;

    private final DatCho_BUS datChoBUS;

    private Runnable onPanel2CompleteListener;
    private Runnable onPanel2ReturnListener;

    public DoiVe2Controller(PanelDoiVe2 view, ExchangeSession session) {
        this.view = view;
        this.exchangeSession = session;
        this.datChoBUS = new DatCho_BUS();

        // Khởi tạo các panel con
        this.p4 = view.getPanelBuoc4();
        this.p5 = view.getPanelBuoc5();
        this.p6 = view.getPanelBuoc6();

        this.buoc4Controller = new DoiVeBuoc4Controller(p4);
        PanelChuyenDoiVe panelChuyen = p5.getPanelChuyen();
        PanelGioVeDoiVe panelGioVe = p5.getPanelGioVe();

        this.buoc5Controller = new DoiVeBuoc5Controller(panelChuyen.getPanelChieuLabel(),
                panelChuyen.getPanelChuyenTau(), panelChuyen.getPanelDoanTau(), panelChuyen.getPanelSoDoCho());

        this.buoc6Controller = new DoiVeBuoc6Controller(this.p6, session);

        panelGioVe.setMediator(this);

        view.setBuoc5Enabled(false);
        view.setBuoc6Enabled(false);

        initMediatorLogic();
    }

    public void addPanel2CompleteListener(Runnable listener) {
        this.onPanel2CompleteListener = listener;
    }

    public void addPanel2ReturnListener(Runnable listener) {
        this.onPanel2ReturnListener = listener;
    }

    private void initMediatorLogic() {

        // Lắng nghe sự kiện từ Buoc4 (Tìm chuyến)
        this.buoc4Controller.addSearchListener(new SearchNewTicketListener() {
            @Override
            // 1. Cập nhật chữ ký (signature) của hàm để nhận danh sách
            public void onSearchSuccess(List<ChuyenDTO> result, SearchCriteria criteria) {

                // 1. Lưu criteria và kết quả vào session
                exchangeSession.setCriteriaTimKiem(criteria);
                exchangeSession.setListChuyenTauTimDuoc(result);

                // 2. Kích hoạt Bước 5
                view.setBuoc5Enabled(true);
                view.setBuoc6Enabled(false);

                // Bơm data vào controller
                buoc5Controller.displayChuyenList(criteria, result);

                refreshGioVe();
            }

            @Override
            public void onSearchFailure() {
                // Xóa cả hai danh sách kết quả khi tìm thất bại
                exchangeSession.setListChuyenTauTimDuoc(null);
                view.setBuoc5Enabled(false);
                view.setBuoc6Enabled(false);
                refreshGioVe();
            }

        });

        // === Lắng nghe sự kiện từ controller ===
        // 1. Gắn MỘT listener duy nhất cho nút Mua Vé
        p5.getPanelGioVe().addBuyButtonListener(e -> {
            handleMuaVe();
        });

        // 2. Lắng nghe sự kiện chọn ghế
        SeatSelectedListener seatSelectListener = new SeatSelectedListener() {
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
                // 1. Cập nhật Model và PanelSoDoCho
                if (buoc5Controller != null) {
                    buoc5Controller.onRemoveVe(veSession);
                }

                // 3. Dừng timer
                stopCountdownForVe(veSession);

                // 4. Refresh Giỏ vé (View)
                refreshGioVe();
            }
        };
        this.buoc5Controller.addSeatSelectedListener(seatSelectListener);

        // Lắng nghe sự kiện từ Buoc6 (Bấm nút xóa hàng vé)
        this.buoc6Controller.setOnDeleteListener(veSession -> {
            // 1. Gọi BUS để xóa phiếu giữ chỗ chi tiết TRONG DB
            new SwingWorker<Boolean, Void>() {
                private String errorMessage = "Lỗi không xác định khi xóa phiếu.";

                @Override
                protected Boolean doInBackground() throws Exception {
                    try {
                        return datChoBUS.xoaPhieuGiuChoChiTietByPgcctID(
                                veSession.getPhieuGiuChoChiTiet().getId());

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
                            if (buoc5Controller != null) {
                                buoc5Controller.onRemoveVe(veSession);
                            }
                            // 2. Dừng timer (logic đã chuyển về đây)
                            stopCountdownForVe(veSession);
                            // 3. Refresh giỏ vé (Mediator tự làm)
                            refreshGioVe();
                            // 4. Xóa PGC nếu giỏ rỗng (Giữ nguyên)
                            if (exchangeSession.getListVeMoiDangChon().isEmpty()) {
                                if (exchangeSession.getPhieuGiuCho() != null) {
                                    datChoBUS.xoaPhieuGiuCho(exchangeSession.getPhieuGiuCho().getPhieuGiuChoID());
                                }
                            }

                            // 5. Tải lại bảng Buoc6
                            if (p6 != null) {
                                p6.initFromExchangeSession(exchangeSession);
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
        });

        this.buoc6Controller.setOnConfirmListener(() -> {
            if (onPanel2CompleteListener != null) {
                onPanel2CompleteListener.run();
            }
        });

        // Lắng nghe sự kiện "Hủy" từ Buoc 6
        this.buoc6Controller.setOnCancelListener(() -> {
            view.setBuoc6Enabled(false);
            if (onPanel2ReturnListener != null) {
                onPanel2ReturnListener.run();
            }
        });
    }

    /**
     * Hàm này thực hiện gọi BUS trong luồng nền
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
                        exchangeSession.setPhieuGiuCho(pgc);

                        view.setBuoc6Enabled(true);
                        p6.initFromExchangeSession(exchangeSession);

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
     * Hàm mới: Xử lý khi bấm nút "Tiếp tục"
     */
    private void handleMuaVe() {
        view.setBuoc4Enabled(false);
        view.setBuoc5Enabled(false);

        // Lấy TẤT CẢ vé
        List<VeSession> listVeMoi = exchangeSession.getListVeMoiDangChon();
        int soLuongVeDoi = exchangeSession.getListVeCuCanDoi().size();

        // Kiểm tra giỏ vé không tương ứng với số lượng vé cần đổi
        if (listVeMoi.size() != soLuongVeDoi) {
            JOptionPane.showMessageDialog(view,
                    String.format("Vui lòng chọn số lượng vé tương ứng với số vé cần đổi ~ %d vé", soLuongVeDoi), "Lỗi",
                    JOptionPane.WARNING_MESSAGE);
            view.setBuoc5Enabled(true);
            return;
        }

        goiBusGiuCho(listVeMoi);
    }

    /**
     * Hàm mới: Xử lý khi bấm nút "Xóa" (trash) TỪ GIỎ VÉ
     */
    public void handleGioVeRemove(VeSession v) {
        if (v == null) {
            return;
        }

        // 2. Cập nhật Model (xóa khỏi session)
        // (onRemoveVe của controller sẽ làm việc này)
        if (buoc5Controller != null) {
            buoc5Controller.onRemoveVe(v); // Sẽ refresh SoDoCho
        }

        // 3. Dừng timer
        stopCountdownForVe(v);

        // 4. Refresh Giỏ vé (View)
        refreshGioVe();
    }

    /**
     * Refresh giỏ vé
     */
    public void refreshGioVe() {
        if (p5 != null && p5.getPanelGioVe() != null && exchangeSession != null) {
            p5.getPanelGioVe().refresh(exchangeSession.getListVeMoiDangChon());
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

                if (buoc5Controller != null) {
                    buoc5Controller.releaseHoldAndRemoveVe(v);
                    refreshGioVe();
                }

                // Xóa PGC nếu giỏ rỗng
                if (exchangeSession.getListVeMoiDangChon().isEmpty()) {
                    if (exchangeSession.getPhieuGiuCho() != null) {
                        datChoBUS.xoaPhieuGiuCho(exchangeSession.getPhieuGiuCho().getPhieuGiuChoID());
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    if (p6 != null) {
                        p6.initFromExchangeSession(exchangeSession);
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

    /**
     * Được gọi bởi PanelDoiVe (Mediator cha) khi kết thúc Giai đoạn 1. Nhiệm vụ:
     * Chuẩn bị dữ liệu và giao diện cho Giai đoạn 2.
     */
    public void loadDataForChoosingNewTickets() {
        // 1. Reset dữ liệu Session liên quan đến vé MỚI (để tránh rác từ lần thao tác
        // trước)
        exchangeSession.setCriteriaTimKiem(null);
        exchangeSession.setListChuyenTauTimDuoc(null);
        exchangeSession.getListVeMoiDangChon().clear();
        exchangeSession.getMapVeCuVoiVeMoi().clear();

        // 2. Cập nhật lại giao diện Giỏ vé (lúc này sẽ trống)
        refreshGioVe();

        // 3. Yêu cầu Controller Bước 4 nạp dữ liệu từ Session
        // (Lấy Ga Đi/Ga Đến của vé cũ điền vào ô text, set ngày hiện tại...)
        buoc4Controller.initDataFromSession();

        // 4. Cấu hình trạng thái hiển thị: Chỉ mở Bước 4 (Tìm chuyến)
        view.setBuoc4Enabled(true);
        view.setBuoc5Enabled(false); // Chưa tìm chuyến thì chưa chọn ghế
        view.setBuoc6Enabled(false); // Chưa chọn ghế thì chưa xác nhận
    }
}