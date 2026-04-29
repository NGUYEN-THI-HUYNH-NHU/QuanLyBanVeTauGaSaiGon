package gui.application.form.doiVe;
/*
 * @(#) DoiVeBuoc5Controller.java  1.0  [12:53:22 AM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

import bus.Chuyen_BUS;
import bus.DatCho_BUS;
import bus.Ve_BUS;
import entity.Chuyen;
import entity.Ghe;
import entity.Toa;
import gui.application.form.banVe.PanelChieuLabel;
import gui.application.form.banVe.SearchCriteria;
import gui.application.form.banVe.VeSession;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DoiVeBuoc5Controller {
    private final PanelChieuLabel panelChieuLabel;
    private final PanelChuyenTauDoiVe panelChuyenTau;
    private final PanelDoanTauDoiVe panelDoanTau;
    private final PanelSoDoChoDoiVe panelSoDoCho;

    private final Chuyen_BUS chuyenBUS = new Chuyen_BUS();
    private final Ve_BUS veBUS = new Ve_BUS();
    private final DatCho_BUS datChoBUS = new DatCho_BUS();

    private final List<SeatSelectedListener> seatSelectedListeners = new ArrayList<>();

    private ExchangeSession exchangeSession;
    private List<Chuyen> chuyenList;
    private Chuyen selectedChuyen;
    private Toa selectedToa;

    public DoiVeBuoc5Controller(PanelChieuLabel chieuLabel, PanelChuyenTauDoiVe chuyenTau, PanelDoanTauDoiVe doanTau,
                                PanelSoDoChoDoiVe soDoCho) {
        this.panelChieuLabel = chieuLabel;
        this.panelChuyenTau = chuyenTau;
        this.panelDoanTau = doanTau;
        this.panelSoDoCho = soDoCho;

        panelChuyenTau.setController(this);
        panelDoanTau.setController(this);
        panelSoDoCho.setController(this);

        this.exchangeSession = ExchangeSession.getInstance();
    }

    public void addSeatSelectedListener(SeatSelectedListener listener) {
        if (listener != null) {
            this.seatSelectedListeners.add(listener);
        }
    }

    public SearchCriteria getCurrentTripCriteria() {
        return exchangeSession.getCriteriaTimKiem();
    }

    public int getGiaForTooltip(String chuyenID, String gaDiID, String gaDenID, String loaiTauID, String hangToaID) {
        return chuyenBUS.layGiaGheTheoPhanDoan(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);
    }

    public void displayChuyenList(SearchCriteria criteria, List<Chuyen> chuyens) {
        if (criteria == null || chuyens == null || chuyens.isEmpty()) {
            return;
        }

        String gaDiName = criteria.getGaDiName();
        String gaDenName = criteria.getGaDenName();

        this.chuyenList = chuyens;
        panelChieuLabel.setText(gaDiName + " - " + gaDenName + ": "
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
        SearchCriteria sc = exchangeSession.getCriteriaTimKiem();
        if (sc == null || selectedChuyen == null) {
            callback.accept(Collections.emptyList());
            return;
        }

        loadSeatsForToa(sc.getGaDiId(), sc.getGaDenId(), selectedChuyen.getId(), toa.getId(), callback);
    }

    public void loadSeatsForToa(String gaDiID, String gaDenID, String chuyenID, String toaID,
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

        new SwingWorker<VeSession, Void>() {
            @Override
            protected VeSession doInBackground() throws Exception {
                SearchCriteria criteria = exchangeSession.getCriteriaTimKiem();
                if (criteria == null) {
                    System.err.println("createVeSessionForSeat: Không tìm thấy SearchCriteria");
                    return null;
                }

                VeSession v = veBUS.createVeSessionForSeat(selectedChuyen, toa, ghe, criteria);
                if (v == null) {
                    return null;
                }
                exchangeSession.addVeMoi(v);
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
     * @param deltaDat
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
        if (toa == null || ghe == null || getExchangeSession() == null) {
            return;
        }

        // 1. Lấy thông tin định danh của ghế
        String currentChuyenID = getSelectedChuyen().getId();
        String currentToaID = toa.getId();
        int currentSoGhe = ghe.getSoGhe();

        // 2. Lấy danh sách vé của CHUYẾN HIỆN TẠI
        List<VeSession> currentTripTickets = exchangeSession.getListVeMoiDangChon();

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
        exchangeSession.removeVeMoi(v);
        // Refresh UI
        if (selectedToa != null) {
            panelSoDoCho.updateSeatVisual(v.getSoGhe(), false);
        }
    }

    public void refreshSeatOnDelete(VeSession veSessionBiXoa) {
        if (veSessionBiXoa == null) {
            return;
        }
        panelSoDoCho.updateSeatVisual(veSessionBiXoa.getSoGhe(), false);
    }

    public void releaseHoldAndRemoveVe(VeSession v) {
        if (v == null || exchangeSession == null) {
            return;
        }

        exchangeSession.removeVeMoi(v);

        datChoBUS.xoaPhieuGiuChoChiTietByPgcctID(v.getPhieuGiuChoChiTiet().getId());
        if (exchangeSession.getListVeMoiDangChon().size() == 0) {
            datChoBUS.xoaPhieuGiuCho(exchangeSession.getPhieuGiuCho().getId());
        }

        SwingUtilities.invokeLater(
                () -> JOptionPane.showMessageDialog(null, "Giữ chỗ cho vé " + v.prettyString() + " đã hết hạn."));

        // Refresh sơ đồ ghế nếu vé hết hạn thuộc toa đang xem
        if (selectedToa != null && v.getVe().getGhe().getToa().getId().equals(selectedToa.getId())) {
            // Kiểm tra xem vé có thuộc CHUYẾN ĐANG XEM không
            if (selectedChuyen != null && v.getVe().getChuyen().getId().equals(selectedChuyen.getId())) {
                panelSoDoCho.updateSeatVisual(v.getSoGhe(), false);
            }
        }
    }

    public Set<Integer> getSelectedSoGhe(Toa currentToa) {
        if (currentToa == null || selectedChuyen == null) {
            return Collections.emptySet();
        }

        String currentChuyenID = selectedChuyen.getId();
        String currentToaID = currentToa.getId();

        // Lọc từ list vé mới trong ExchangeSession
        return exchangeSession.getListVeMoiDangChon().stream()
                .filter(v -> currentChuyenID.equals(v.getVe().getChuyen().getId())
                        && currentToaID.equals(v.getVe().getGhe().getToa().getId()))
                .map(VeSession::getSoGhe).collect(Collectors.toSet());
    }

    public ExchangeSession getExchangeSession() {
        return exchangeSession;
    }

    public void setExchangeSession(ExchangeSession exchangeSession) {
        this.exchangeSession = exchangeSession;
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
