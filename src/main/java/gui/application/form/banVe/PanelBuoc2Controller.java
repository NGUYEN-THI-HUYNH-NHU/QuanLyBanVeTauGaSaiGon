package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2Controller.java  1.0  [12:53:22 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import bus.Chuyen_BUS;
import bus.DatCho_BUS;
import bus.Ve_BUS;
import entity.*;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PanelBuoc2Controller {
    private final PanelChieuLabel panelChieuLabel;
    private final PanelChuyenTau panelChuyenTau;
    private final PanelDoanTau panelDoanTau;
    private final PanelSoDoCho panelSoDoCho;

    private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
    private final Ve_BUS veBUS = new Ve_BUS();
    private final DatCho_BUS datChoBUS = new DatCho_BUS();

    private final List<SeatSelectedListener> seatSelectedListeners = new ArrayList<>();

    private BookingSession bookingSession;
    private int currentTripIndex = 0;
    private List<Chuyen> chuyenList;
    private Chuyen selectedChuyen;
    private Toa selectedToa;

    public PanelBuoc2Controller(PanelChieuLabel chieuLabel, PanelChuyenTau chuyenTau, PanelDoanTau doanTau,
                                PanelSoDoCho soDoCho) {
        this.panelChieuLabel = chieuLabel;
        this.panelChuyenTau = chuyenTau;
        this.panelDoanTau = doanTau;
        this.panelSoDoCho = soDoCho;

        panelChuyenTau.setController(this);
        panelDoanTau.setController(this);
        panelSoDoCho.setController(this);
    }

    public void addSeatSelectedListener(SeatSelectedListener listener) {
        if (listener != null) {
            this.seatSelectedListeners.add(listener);
        }
    }

    public int getCurrentTripIndex() {
        return this.currentTripIndex;
    }

    public void setCurrentTripIndex(int idx) {
        this.currentTripIndex = idx;
    }

    public SearchCriteria getCurrentTripCriteria() {
        if (getBookingSession() == null) {
            return null;
        }
        return (currentTripIndex == 0) ? getBookingSession().getOutboundCriteria()
                : getBookingSession().getReturnCriteria();
    }

    public int getGiaForTooltip(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID) {
        return chuyenBUS.layGiaGheTheoPhanDoan(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);
    }

    public void displayChuyenList(SearchCriteria criteria, List<Chuyen> chuyens, int tripIndex) {
        if (criteria == null || chuyens == null || chuyens.isEmpty()) {
            return;
        }

        // 1. Set tripIndex để controller biết đang xử lý chiều đi hay về
        setCurrentTripIndex(tripIndex);

        String gaDiName = criteria.getGaDiName();
        String gaDenName = criteria.getGaDenName();

        this.chuyenList = chuyens;
        String chieu = (tripIndex == 0) ? "[Chiều đi] " : "[Chiều về] ";
        panelChieuLabel.setText(chieu + gaDiName + " - " + gaDenName + ": "
                + chuyens.get(0).getNgayDi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        panelChuyenTau.showChuyenList(chuyens);

        // Load thống kê ghế cho từng chuyến
        loadSeatStatsForChuyens(chuyens, criteria);

        if (chuyens != null && !chuyens.isEmpty()) {
            panelChuyenTau.selectChuyenById(chuyens.get(0).getId());
            onChuyenSelected(chuyens.get(0));
        }
    }

    /**
     * @param chuyens
     * @param criteria
     */
    private void loadSeatStatsForChuyens(List<Chuyen> chuyens, SearchCriteria criteria) {
        if (chuyens == null) {
            return;
        }

        String gaDiID = criteria.getGaDiId();
        String gaDenID = criteria.getGaDenId();

        new SwingWorker<Map<String, int[]>, Void>() {
            @Override
            protected Map<String, int[]> doInBackground() throws Exception {
                Map<String, int[]> stats = new HashMap<>();
                for (Chuyen c : chuyens) {
                    int[] stat = getChuyenBUS().layThongKeCho(c.getId(), gaDiID, gaDenID);
                    stats.put(c.getId(), stat);
                }
                return stats;
            }

            @Override
            protected void done() {
                try {
                    Map<String, int[]> result = get();
                    for (Map.Entry<String, int[]> entry : result.entrySet()) {
                        // Update UI
                        panelChuyenTau.updateSeatCount(entry.getKey(), entry.getValue()[0], entry.getValue()[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    public void onChuyenSelected(Chuyen c) {
        if (c == null) {
            return;
        }
        this.setSelectedChuyen(c);

        new SwingWorker<List<Toa>, Void>() {
            @Override
            protected List<Toa> doInBackground() throws Exception {
                return getChuyenBUS().layCacToaTheoChuyen(c.getId());
            }

            @Override
            protected void done() {
                try {
                    List<Toa> list = get();
                    panelDoanTau.showToaList(list, toa -> onToaSelected(toa));
                    if (list != null && !list.isEmpty()) {
                        panelDoanTau.selectToaById(list.get(0).getId());
                        panelSoDoCho.setToaList(list);
                        panelSoDoCho.setCurrentToa(list.get(0));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    public void onToaSelected(Toa toa) {
        this.selectedToa = toa;
        panelSoDoCho.setCurrentToa(toa);
    }

    private boolean isMissingId(String s) {
        return s == null || s.trim().isEmpty() || "null".equalsIgnoreCase(s.trim());
    }

    public void loadSeatsForToa(Toa toa, Consumer<List<Ghe>> callback) {
        if (toa == null) {
            callback.accept(Collections.emptyList());
            return;
        }

        // 1) fetch current chuyen/toa
        String chuyenID = (getSelectedChuyen() != null) ? getSelectedChuyen().getId() : null;
        String toaID = toa.getId();

        // 2) try to get gaDi/gaDen from bookingSession (based on currentTripIndex)
        String gaDiID = null;
        String gaDenID = null;
        if (getBookingSession() != null) {
            SearchCriteria sc = (currentTripIndex == 0) ? getBookingSession().getOutboundCriteria()
                    : getBookingSession().getReturnCriteria();
            if (sc != null) {
                gaDiID = sc.getGaDiId(); // ensure method name matches your class
                gaDenID = sc.getGaDenId();
            }
        }

        // 3) Normalize: treat literal "null" or empty as missing
        if (isMissingId(gaDiID)) {
            gaDiID = null;
        }
        if (isMissingId(gaDenID)) {
            gaDenID = null;
        }

        // 4) Fallback: try get from selectedChuyen's tuyen (if entity supports it)
        if ((gaDiID == null || gaDenID == null) && getSelectedChuyen() != null) {
            try {
                Tuyen tuyen = getSelectedChuyen().getTuyen(); // adjust to your entity API
                if (tuyen != null) {
//                    if (gaDiID == null && tuyen.getGaDi() != null) gaDiID = tuyen.getGaDi().getGaID();
//                    if (gaDenID == null && tuyen.getGaDen() != null) gaDenID = tuyen.getGaDen().getGaID();
                }
            } catch (Throwable ignored) {
                // nếu entity khác, xử lý tương ứng
            }
        }

        // 5) Fallback 2: try resolve by name via BUS (use SearchCriteria names)
        if ((gaDiID == null || gaDenID == null) && getBookingSession() != null) {
            SearchCriteria sc = getBookingSession().getOutboundCriteria();
            if (sc == null && currentTripIndex == 1) {
                sc = getBookingSession().getReturnCriteria();
            }
            if (sc != null) {
                try {
                    if (gaDiID == null && sc.getGaDiName() != null && !sc.getGaDiName().trim().isEmpty()) {
                        Ga g = getChuyenBUS().timGaTheoTenGa(sc.getGaDiName().trim());
                        if (g != null) {
                            gaDiID = g.getGaID();
                        }
                    }
                    if (gaDenID == null && sc.getGaDenName() != null && !sc.getGaDenName().trim().isEmpty()) {
                        Ga g2 = getChuyenBUS().timGaTheoTenGa(sc.getGaDenName().trim());
                        if (g2 != null) {
                            gaDenID = g2.getGaID();
                        }
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }

        // 6) final check
        if (isMissingId(gaDiID) || isMissingId(gaDenID) || isMissingId(chuyenID) || isMissingId(toaID)) {
            System.err.println(String.format("loadSeatsForToa: missing param gaDi=%s gaDen=%s chuyen=%s toa=%s", gaDiID,
                    gaDenID, chuyenID, toaID));
            callback.accept(Collections.emptyList());
            return;
        }

        // 7) call core loader (thứ tự tham số đúng theo Chuyen_BUS)
        loadSeatsForToa(gaDiID, gaDenID, chuyenID, toaID, callback);
    }

    private void loadSeatsForToa(String gaDiID, String gaDenID, String chuyenID, String toaID,
                                 Consumer<List<Ghe>> callback) {
        new SwingWorker<List<Ghe>, Void>() {
            @Override
            protected List<Ghe> doInBackground() throws Exception {
                if (gaDiID == null || gaDenID == null || chuyenID == null || toaID == null) {
                    return Collections.emptyList();
                }
                return getChuyenBUS().layCacGheTrongToaTrenChuyen(gaDiID, gaDenID, chuyenID, toaID);
            }

            @Override
            protected void done() {
                try {
                    callback.accept(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    callback.accept(Collections.emptyList());
                }
            }
        }.execute();
    }

    public void highlightToa(Toa toa) {
        if (toa == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                if (panelDoanTau != null) {
                    panelDoanTau.selectToaById(toa.getId());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // user clicked a seat button
    public void onSeatClicked(Toa toa, Ghe ghe) {
        if (ghe == null || toa == null) {
            return;
        }

        final int tripIndex = getCurrentTripIndex();

        new SwingWorker<VeSession, Void>() {
            @Override
            protected VeSession doInBackground() throws Exception {
                SearchCriteria criteria = (tripIndex == 0) ? bookingSession.getOutboundCriteria()
                        : bookingSession.getReturnCriteria();

                if (criteria == null) {
                    System.err.println(
                            "createVeSessionForSeat: Không tìm thấy SearchCriteria cho tripIndex " + tripIndex);
                    return null;
                }

                VeSession v = veBUS.createVeSessionForSeat(selectedChuyen, toa, ghe, criteria);

                if (v == null) {
                    return null;
                }

                if (tripIndex == 0) {
                    bookingSession.addOutboundTicket(v);
                } else {
                    bookingSession.addReturnTicket(v);
                }

                return v;
            }

            @Override
            protected void done() {
                try {
                    VeSession v = get();
                    if (v != null) {
                        for (SeatSelectedListener listener : seatSelectedListeners) {
                            listener.onSeatSelected(v);
                        }
                        // Cập nhật số lượng trên card chuyến tàu
                        updateSeatCountLocal(selectedChuyen.getId(), 1); // +1 đặt
                        panelSoDoCho.setCurrentToa(toa);
                    } else {
                        JOptionPane.showMessageDialog(null, "Không thể giữ ghế (lỗi tạo vé).");
                        panelSoDoCho.setCurrentToa(toa);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    /**
     * @param chuyenID
     * @param i
     */
    private void updateSeatCountLocal(String chuyenID, int deltaDat) {
        // Lấy số hiện tại từ Panel (hoặc từ cache nếu có biến lưu trữ trong
        // Controller)
        int[] current = panelChuyenTau.getCurrentSeatCount(chuyenID);
        int newDat = current[0] + deltaDat;
        int newTrong = current[1] - deltaDat;

        if (newDat < 0) {
            newDat = 0;
        }
        if (newTrong < 0) {
            newTrong = 0;
        }

        panelChuyenTau.updateSeatCount(chuyenID, newDat, newTrong);
    }

    /**
     * Xử lý khi người dùng bấm vào một ghế ĐÃ ĐƯỢC CHỌN (để bỏ chọn).
     */
    public void handleSeatDeselection(Toa toa, Ghe ghe) {
        if (toa == null || ghe == null || getBookingSession() == null) {
            return;
        }

        // 1. Lấy thông tin định danh của ghế
        String currentChuyenID = getSelectedChuyen().getId();
        String currentToaID = toa.getId();
        int currentSoGhe = ghe.getSoGhe();

        // 2. Lấy danh sách vé của CHUYẾN HIỆN TẠI
        List<VeSession> currentTripTickets = bookingSession.getSelectedTicketsForTrip(getCurrentTripIndex());

        // 3. Tìm VeSession THỰC SỰ đang có trong danh sách
        VeSession veToRemove = currentTripTickets.stream()
                .filter(v -> v.getVe().getChuyen().getId().equals(currentChuyenID)
                        && v.getVe().getGhe().getToa().getId().equals(currentToaID)
                        && v.getVe().getGhe().getSoGhe() == currentSoGhe)
                .findFirst().orElse(null);

        if (veToRemove != null) {
            // Báo cho BanVe1Controller
            for (SeatSelectedListener listener : seatSelectedListeners) {
                listener.onSeatDeselected(veToRemove);
            }
            // Cập nhật số lượng trên card chuyến tàu
            updateSeatCountLocal(currentChuyenID, -1); // -1 đặt
        } else {
            // Lỗi: Không tìm thấy vé để xóa
            if (selectedToa != null) {
                panelSoDoCho.setCurrentToa(selectedToa);
            }
        }
    }

    // user clicked trash icon or timer expired -> remove ticket
    public void onRemoveVe(VeSession v) {
        if (v == null || bookingSession == null) {
            return;
        }

        // Xác định đúng danh sách cần xóa (chiều đi hay về)
        boolean removed = false;
        if (currentTripIndex == 0) {
            removed = bookingSession.removeOutboundTicket(v);
        } else {
            removed = bookingSession.removeReturnTicket(v);
        }

        // Luôn refresh sơ đồ ghế để cập nhật màu sắc
        if (selectedToa != null) {
            refreshSeatOnDelete(v);
        }
    }

    /**
     * Hàm này được gọi khi một vé bị xóa TỪ BẤT CỨ ĐÂU. Nó kiểm tra và cập nhật lại
     * PanelSoDoCho NẾU cần thiết. * @param veSessionBiXoa Vé vừa bị xóa khỏi
     * BookingSession
     */
    public void refreshSeatOnDelete(VeSession veSessionBiXoa) {
        if (veSessionBiXoa == null) {
            return;
        }
        panelSoDoCho.updateSeatVisual(veSessionBiXoa.getVe().getGhe().getSoGhe(), false);
    }

    public void releaseHoldAndRemoveVe(VeSession v) {
        if (v == null || bookingSession == null) {
            return;
        }

        boolean removedOutbound = bookingSession.removeOutboundTicket(v);
        boolean removedReturn = bookingSession.removeReturnTicket(v);

        bookingSession.removeVeSession(v);

        datChoBUS.xoaPhieuGiuChoChiTietByPgcctID(v.getPhieuGiuChoChiTiet().getId());
        if (bookingSession.getOutboundSelectedTickets().size() == 0
                && bookingSession.getReturnSelectedTickets().size() == 0) {
            datChoBUS.xoaPhieuGiuCho(bookingSession.getPhieuGiuCho().getId());
        }

        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(null, "Giữ chỗ cho vé " + v.prettyString() + " đã hết hạn."));

        // Refresh sơ đồ ghế nếu vé hết hạn thuộc toa đang xem
        if (selectedToa != null && v.getVe().getGhe().getToa().getId().equals(selectedToa.getId())
                && (removedOutbound || removedReturn)) {
            // Kiểm tra xem vé có thuộc CHUYẾN ĐANG XEM không
            if (selectedChuyen != null && v.getVe().getChuyen().getId().equals(selectedChuyen.getId())) {
                refreshSeatOnDelete(v);
            }
        }
    }

    public Set<Integer> getSelectedSoGhe(Toa currentToa) {
        if (currentToa == null || getSelectedChuyen() == null || getBookingSession() == null) {
            return Collections.emptySet();
        }

        String currentChuyenID = getSelectedChuyen().getId();
        String currentToaID = currentToa.getId();

        Set<Integer> selectedSoGheSet = getBookingSession().getSelectedTicketsForTrip(getCurrentTripIndex()).stream()
                .filter(v -> currentChuyenID.equals(v.getVe().getChuyen().getId())
                        && currentToaID.equals(v.getVe().getGhe().getToa().getId()))
                .map(VeSession::getSoGhe).collect(Collectors.toSet());

        return selectedSoGheSet;
    }

    public BookingSession getBookingSession() {
        return bookingSession;
    }

    public void setBookingSession(BookingSession s) {
        this.bookingSession = s;
    }

    public Chuyen getSelectedChuyen() {
        return selectedChuyen;
    }

    public void setSelectedChuyen(Chuyen selectedChuyen) {
        this.selectedChuyen = selectedChuyen;
    }

    public Chuyen_BUS getChuyenBUS() {
        return chuyenBUS;
    }

    protected interface SeatSelectedListener {
        void onSeatSelected(VeSession v);

        void onSeatDeselected(VeSession v);
    }
}
