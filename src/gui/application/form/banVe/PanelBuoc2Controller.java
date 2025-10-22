package gui.application.form.banVe;
/*
 * @(#) PanelBuoc2Controller.java  1.0  [12:53:22 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import bus.Chuyen_BUS;
import bus.DonDatCho_BUS;
import bus.TicketBUS;

import entity.*;
import entity.type.TrangThaiGhe;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class PanelBuoc2Controller {
    private final PanelChieuLabel panelChieuLabel;
    private final PanelChuyenTau panelChuyenTau;
    private final PanelDoanTau panelDoanTau;
    private final PanelSoDoCho panelSoDoCho;
    private final PanelGioVe panelGioVe;

    private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
    private final DonDatCho_BUS donDatChoBUS = new DonDatCho_BUS();
    private final TicketBUS ticketBUS = TicketBUS.getInstance();

    private final Map<String, Timer> countdownTimers = new ConcurrentHashMap<>();
    private final Map<String, JLabel> countdownLabels = new ConcurrentHashMap<>();

    private BookingSession bookingSession;
    private List<Chuyen> chuyenList;
    private Chuyen selectedChuyen;
    private int currentTripIndex = 0;
    private Toa selectedToa;

    public PanelBuoc2Controller(PanelChieuLabel chieuLabel, PanelChuyenTau chuyenTau,
                                PanelDoanTau doanTau, PanelSoDoCho soDoCho, PanelGioVe gioVe) {
        this.panelChieuLabel = chieuLabel;
        this.panelChuyenTau = chuyenTau;
        this.panelDoanTau = doanTau;
        this.panelSoDoCho = soDoCho;
        this.panelGioVe = gioVe;

        panelChuyenTau.setController(this);
        panelDoanTau.setController(this);
        panelSoDoCho.setController(this);
        panelGioVe.setController(this);
    }

    public void setBookingSession(BookingSession s) {
        this.bookingSession = s;
    }
    public void setCurrentTripIndex(int idx) {
        this.currentTripIndex = idx;
    }
    public int getCurrentTripIndex() {
        return this.currentTripIndex;
    }

    public void setChuyenList(List<Chuyen> chuyens, String gaDiName, String gaDenName) {
        this.chuyenList = chuyens;
        panelChieuLabel.setText(gaDiName + " - " + gaDenName + ": " + chuyens.get(0).getNgayDi());
        panelChuyenTau.showChuyenList(chuyens);
        if (chuyens != null && !chuyens.isEmpty()) {
            panelChuyenTau.selectChuyenById(chuyens.get(0).getChuyenID());
            onChuyenSelected(chuyens.get(0));
        }
        panelGioVe.refresh(ticketBUS.getAllTickets());
    }
    
    /**
     * Đây là hàm "cổng vào" mới, thay thế cho logic của WizardController.goToStep(2) 
     * Nó sẽ được gọi bởi PanelBanVe1Controller (Mediator).
     */
    public void displayChuyenList(SearchCriteria criteria, List<Chuyen> chuyens, int tripIndex) {
        if (criteria == null || chuyens == null || chuyens.isEmpty()) {
            // Có thể ẩn hoặc xóa trắng panel
            return;
        }

        // 1. Set tripIndex để controller biết đang xử lý chiều đi hay về
        setCurrentTripIndex(tripIndex);

        String gaDiName = criteria.getGaDiName();
        String gaDenName = criteria.getGaDenName();
        
        this.chuyenList = chuyens;
        panelChieuLabel.setText(gaDiName + " - " + gaDenName + ": " + chuyens.get(0).getNgayDi());
        panelChuyenTau.showChuyenList(chuyens);
        
        if (chuyens != null && !chuyens.isEmpty()) {
            panelChuyenTau.selectChuyenById(chuyens.get(0).getChuyenID());
            onChuyenSelected(chuyens.get(0));
        }
        panelGioVe.refresh(ticketBUS.getAllTickets());
    }

    public void onChuyenSelected(Chuyen c) {
        if (c == null)
            return;
        this.selectedChuyen = c;

        new SwingWorker<List<Toa>, Void>() {
            protected List<Toa> doInBackground() throws Exception {
                return chuyenBUS.layCacToaTheoChuyen(c.getChuyenID());
            }
            protected void done() {
                try {
                    List<Toa> list = get();
                    panelDoanTau.showToaList(list, toa -> {
                        onToaSelected(toa);
                    });
                    if (list != null && !list.isEmpty()) {
                        panelDoanTau.selectToaById(list.get(0).getToaID());
                    }
                    panelSoDoCho.setToaListAndSelect(list, 0);
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
        String chuyenID = (selectedChuyen != null) ? selectedChuyen.getChuyenID() : null;
        String toaID = toa.getToaID();

        // 2) try to get gaDi/gaDen from bookingSession (based on currentTripIndex)
        String gaDiID = null;
        String gaDenID = null;
        if (bookingSession != null) {
            SearchCriteria sc = (currentTripIndex == 0) ? bookingSession.getOutboundCriteria()
                    : bookingSession.getReturnCriteria();
            if (sc != null) {
                gaDiID = sc.getGaDiId();   // ensure method name matches your class
                gaDenID = sc.getGaDenId();
            }
        }

        // 3) Normalize: treat literal "null" or empty as missing
        if (isMissingId(gaDiID)) gaDiID = null;
        if (isMissingId(gaDenID)) gaDenID = null;

        // 4) Fallback: try get from selectedChuyen's tuyen (if entity supports it)
        if ((gaDiID == null || gaDenID == null) && selectedChuyen != null) {
            try {
                Tuyen tuyen = selectedChuyen.getTuyen(); // adjust to your entity API
                if (tuyen != null) {
                    if (gaDiID == null && tuyen.getGaDi() != null) gaDiID = tuyen.getGaDi().getGaID();
                    if (gaDenID == null && tuyen.getGaDen() != null) gaDenID = tuyen.getGaDen().getGaID();
                }
            } catch (Throwable ignored) {
                // nếu entity khác, xử lý tương ứng
            }
        }

        // 5) Fallback 2: try resolve by name via BUS (use SearchCriteria names)
        if ((gaDiID == null || gaDenID == null) && bookingSession != null) {
            SearchCriteria sc = bookingSession.getOutboundCriteria();
            if (sc == null && currentTripIndex == 1) sc = bookingSession.getReturnCriteria();
            if (sc != null) {
                try {
                    if (gaDiID == null && sc.getGaDiName() != null && !sc.getGaDiName().trim().isEmpty()) {
                        Ga g = chuyenBUS.timGaTheoTenGa(sc.getGaDiName().trim());
                        if (g != null) gaDiID = g.getGaID();
                    }
                    if (gaDenID == null && sc.getGaDenName() != null && !sc.getGaDenName().trim().isEmpty()) {
                        Ga g2 = chuyenBUS.timGaTheoTenGa(sc.getGaDenName().trim());
                        if (g2 != null) gaDenID = g2.getGaID();
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }

        // 6) final check
        if (isMissingId(gaDiID) || isMissingId(gaDenID) || isMissingId(chuyenID) || isMissingId(toaID)) {
            System.err.println(String.format("loadSeatsForToa: missing param gaDi=%s gaDen=%s chuyen=%s toa=%s",
                    gaDiID, gaDenID, chuyenID, toaID));
            callback.accept(Collections.emptyList());
            return;
        }

        // 7) call core loader (thứ tự tham số đúng theo Chuyen_BUS)
        loadSeatsForToa(gaDiID, gaDenID, chuyenID, toaID, callback);
    }

    public void loadSeatsForToa(String gaDiID, String gaDenID, String chuyenID, String toaID, Consumer<List<Ghe>> callback) {
        new SwingWorker<List<Ghe>, Void>() {
            protected List<Ghe> doInBackground() throws Exception {
                if (gaDiID == null || gaDenID == null || chuyenID == null || toaID == null) {
                    return Collections.emptyList();
                }
                return chuyenBUS.layCacGheTrongToaTrenChuyen(gaDiID, gaDenID, chuyenID, toaID);
            }
            protected void done() {
                try { callback.accept(get()); }
                catch (Exception ex) { ex.printStackTrace(); callback.accept(Collections.emptyList()); }
            }
        }.execute();
    }

    public void highlightToa(Toa toa) {
        if (toa == null)
        	return;
        SwingUtilities.invokeLater(() -> {
            try {
                if (panelDoanTau != null) {
                    panelDoanTau.selectToaById(toa.getToaID());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // user clicked a seat button
    public void onSeatClicked(Toa toa, Ghe ghe) {
        if (ghe == null || toa == null) return;
        if (ghe.getTrangThai() == TrangThaiGhe.DA_BAN) {
            JOptionPane.showMessageDialog(null, "Ghế không thể chọn (đã bán/đang giữ).");
            return;
        }
        new SwingWorker<Ve, Void>() {
            protected Ve doInBackground() throws Exception {
                // create hold in DB via DonDatCho_BUS
                // returns Ve object with hold expiry timestamp and id
                return donDatChoBUS.createHold(toa, ghe);
            }
            protected void done() {
                try {
                    Ve v = get();
                    if (v != null) {
                        // add to TicketBUS
                        ticketBUS.addTicket(v);
                        // update UI: refresh right panel
                        panelGioVe.refresh(ticketBUS.getAllTickets());
                        // start countdown for this ticket
                        startCountdownForTicket(v);
                        // refresh seat grid to mark as selected
                        panelSoDoCho.setCurrentToa(toa);
                    } else {
                        JOptionPane.showMessageDialog(null, "Không thể giữ ghế (lỗi).");
                        panelSoDoCho.setCurrentToa(toa);
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    // start a swing timer updating corresponding JLabel; label may be registered by panelGioVe
    public void registerCountdownLabelForTicket(entity.Ve v, JLabel lbl) {
        countdownLabels.put(v.getVeID(), lbl);
        // if a timer already exists, reuse (otherwise create)
        if (!countdownTimers.containsKey(v.getVeID())) {
            startCountdownForTicket(v);
        }
    }

    private void startCountdownForTicket(entity.Ve v) {
        String id = v.getVeID();
        // cancel existing timer
        Timer old = countdownTimers.remove(id);
        if (old != null) old.stop();

        long secondsLeft = 100;
        JLabel lbl = countdownLabels.get(id);

        Timer timer = new Timer(1000, e -> {
            long s = 100;
            // update label
            JLabel label = countdownLabels.get(id);
            if (label != null) {
                label.setText(formatSeconds(s));
            }
            if (s <= 0) {
                ((Timer)e.getSource()).stop();
                countdownTimers.remove(id);
                countdownLabels.remove(id);
                // auto-release hold
                releaseHoldAndRemoveTicket(v);
            }
        });
        timer.setInitialDelay(0);
        timer.start();
        countdownTimers.put(id, timer);
    }

    private String formatSeconds(long s) {
        if (s <= 0) return "00:00";
        long m = s / 60;
        long sec = s % 60;
        return String.format("%02d:%02d", m, sec);
    }

    // user clicked trash icon or timer expired -> remove ticket
    public void onRemoveTicket(entity.Ve v) {
        // cancel hold in DB via BUS, remove from TicketBUS, stop timer, refresh UI
        new SwingWorker<Boolean, Void>() {
            protected Boolean doInBackground() throws Exception {
                boolean ok = donDatChoBUS.releaseHold(v);
                return ok;
            }
            protected void done() {
                try {
                    boolean ok = get();
                    ticketBUS.removeTicket(v);
                    Timer t = countdownTimers.remove(v.getVeID());
                    if (t != null)
                        t.stop();
                    countdownLabels.remove(v.getVeID());
                    panelGioVe.refresh(ticketBUS.getAllTickets());

                    if (selectedToa != null) {
                        panelSoDoCho.setCurrentToa(selectedToa);
                    }
                    JOptionPane.showMessageDialog(null, "Đã xóa giữ chỗ.");
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    private void releaseHoldAndRemoveTicket(entity.Ve v) {
        // similar to onRemoveTicket but invoked automatically
        try {
            donDatChoBUS.releaseHold(v);
        } catch (Exception ex) { ex.printStackTrace(); }
        ticketBUS.removeTicket(v);
        countdownLabels.remove(v.getVeID());
        panelGioVe.refresh(ticketBUS.getAllTickets());
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Giữ chỗ cho vé " + v.getVeID() + " đã hết hạn."));
    }

//    private Ve findTicketForSeat(Toa toa, Ghe ghe) {
//        if (toa == null || ghe == null) return null;
//        List<Ve> tickets = ticketBUS.getAllTickets();
//        if (tickets == null) return null;
//        for (Ve v : tickets) {
//            try {
//                if (v.getChuyen() != null && selectedChuyen != null
//                        && v.getChuyen().getChuyenID().equals(selectedChuyen.getChuyenID())
//                    && v.getChuyen(). != null && v.getToaID().equals(toa.getToaID())
//                    && v.getGheID() != null && v.getGheID().equals(ghe.getGheID())) {
//                    return v;
//                }
//            } catch (Throwable ignored) {}
//        }
//        return null;
//    }
//
//    public void toggleSeatSelection(Toa toa, Ghe ghe) {
//        if (toa == null || ghe == null)
//        	return;
//
//        Ve existing = findTicketForSeat(toa, ghe);
//        if (existing != null) {
//        	onRemoveTicket(existing);
//            return;
//        } else {
//            onSeatClicked(toa, ghe);
//        }
//    }
}