package controller;
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

import bus.DonDatCho_BUS;
import bus.TicketBUS;
import dao.Ghe_DAO;
import dao.Toa_DAO;
import dao.Chuyen_DAO;
import entity.*;
import entity.type.TrangThaiGhe;
import gui.application.form.banVe.PanelChieuLabel;
import gui.application.form.banVe.PanelChuyenTau;
import gui.application.form.banVe.PanelDoanTau;
import gui.application.form.banVe.PanelGioVe;
import gui.application.form.banVe.PanelSoDoCho;

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

/**
 * Controller for PanelBuoc2.
 * Coordinates view panels and calls BUS/DAO.
 */
public class PanelBuoc2Controller {
    private final PanelChieuLabel panelChieuLabel;
    private final PanelChuyenTau panelChuyenTau;
    private final PanelDoanTau panelDoanTau;
    private final PanelSoDoCho panelSoDoCho;
    private final PanelGioVe panelGioVe;

    // BUS/DAO (you can replace with your existing class instances)
    private final Chuyen_DAO chuyenDAO = new Chuyen_DAO();
    private final Toa_DAO toaDAO = new Toa_DAO();
    private final Ghe_DAO gheDAO = new Ghe_DAO();
    private final DonDatCho_BUS donDatChoBUS = new DonDatCho_BUS();
    private final TicketBUS ticketBUS = TicketBUS.getInstance();

    // for countdowns: map ticketId -> Swing Timer
    private final Map<String, Timer> countdownTimers = new ConcurrentHashMap<>();

    // to update JLabel per ticket each second
    private final Map<String, JLabel> countdownLabels = new ConcurrentHashMap<>();

    // current list of chuyens shown
    private List<Chuyen> chuyenList;

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

    /**
     * Caller (PanelBuoc1) will pass the results.
     */
    public void setChuyenList(List<Chuyen> chuyens, String gaDiName, String gaDenName) {
        this.chuyenList = chuyens;
        panelChieuLabel.setInfo("Hành trình: " + gaDiName + " → " + gaDenName);
        panelChuyenTau.showChuyenList(chuyens);
        // Optionally select first chuyens
        if (chuyens != null && !chuyens.isEmpty()) {
            onChuyenSelected(chuyens.get(0));
        }
        // refresh gio ve
        panelGioVe.refresh(ticketBUS.getAllTickets());
    }

    // user clicked a chuyen in panelChuyenTau
    public void onChuyenSelected(Chuyen c) {
        // load toa for this chuyen in background
        new SwingWorker<List<Toa>, Void>() {
            protected List<Toa> doInBackground() throws Exception {
                return toaDAO.getToaByChuyenID(c.getChuyenID());
            }
            protected void done() {
                try {
                    List<Toa> list = get();
                    panelDoanTau.showToaList(list, toa -> {
                        onToaSelected(toa);
                    });
                    // set toa list for soDo to allow prev/next
                    panelSoDoCho.setToaListAndSelect(list, 0);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        }.execute();
    }

    public void onToaSelected(Toa toa) {
        // set current toa soDoCho will ask controller to load seats
        panelSoDoCho.setCurrentToa(toa);
    }

    // called by PanelSoDoCho to load seats for a Toa
    public void loadSeatsForToa(Toa toa, Consumer<List<Ghe>> callback) {
        new SwingWorker<List<Ghe>, Void>() {
            protected List<Ghe> doInBackground() throws Exception {
                // get seats from DAO
                return gheDAO.getGheByGaDiGaDenChuyenIDToaID("gaDiID", "gaDenID", "chuyenID", toa.getToaID());
            }
            protected void done() {
                try {
                    List<Ghe> seats = get();
                    callback.accept(seats);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    callback.accept(Collections.emptyList());
                }
            }
        }.execute();
    }

    // user clicked a seat button
    public void onSeatClicked(Toa toa, Ghe ghe) {
        // if seat available -> create hold (don dat cho) via BUS -> add ticket
        if (ghe.getTrangThai() == TrangThaiGhe.OCCUPIED) {
            // cannot select
            JOptionPane.showMessageDialog(null, "Ghế không thể chọn (đã bán/đang giữ).");
            return;
        }
        new SwingWorker<entity.Ve, Void>() {
            protected entity.Ve doInBackground() throws Exception {
                // create hold in DB via DonDatCho_BUS
                // returns Ve object with hold expiry timestamp and id
                return donDatChoBUS.createHold(toa, ghe);
            }
            protected void done() {
                try {
                    entity.Ve v = get();
                    if (v != null) {
                        // add to TicketBUS
                        ticketBUS.addTicket(v);
                        // update UI: refresh right panel
                        panelGioVe.refresh(ticketBUS.getAllTickets());
                        // start countdown for this ticket
                        startCountdownForTicket(v);
                        // optionally refresh seat grid to mark as selected (controller could reload seats)
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

//        long secondsLeft = v.getHoldSecondsLeft(); // Ve must supply seconds left
        long secondsLeft = 100; // Ve must supply seconds left
        JLabel lbl = countdownLabels.get(id);

        Timer timer = new Timer(1000, e -> {
            long s = 100; // mutate Ve hold seconds (implement in Ve)
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
                    if (t != null) t.stop();
                    countdownLabels.remove(v.getVeID());
                    panelGioVe.refresh(ticketBUS.getAllTickets());
                    // reload seats for current toa to reflect freed seat
                    // best effort: if current shown toa equals v's toa, refresh
                    // (controller could track currentToa; skipping explicit check here)
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
        // you may show notification
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Giữ chỗ cho vé " + v.getVeID() + " đã hết hạn."));
    }

}