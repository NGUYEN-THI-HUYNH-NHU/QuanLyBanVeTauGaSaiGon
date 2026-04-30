package gui.application.form.banVe;
/*
 * @(#) PanelSoDoCho.java  1.0  [12:51:43 PM] Sep 29, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 29, 2025
 * @version: 1.0
 */

import entity.Ghe;
import entity.Toa;
import entity.type.TrangThaiGhe;
import gui.tuyChinh.ColorIcon;
import gui.tuyChinh.RoundedBorder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PanelSoDoCho extends JPanel {
    private static final JButton btnChiem = new JButton();
    private static final JButton btnBan = new JButton();
    private static final int SEAT_WIDTH = 24;
    private static final int SEAT_HEIGHT = 24;
    private static final String SEAT_SIZE_CONSTRAINTS = String.format("w %d!, h %d!", SEAT_WIDTH, SEAT_HEIGHT);
    // Kích thước viewport (có thể điều chỉnh)
    private static final int VIEWPORT_HEIGHT = 180;
    private static final int VIEWPORT_WIDTH = 500;
    private final JPanel pnlGridChoNgoi;
    private final JScrollPane scroll;
    private final JButton btnPrev, btnNext;
    private final JLabel lblToaInfo;
    private final JPanel pnlNorth;
    // Lưu trữ các nút ghế để dễ truy cập (tùy chọn)
    private final Map<Integer, JButton> seatButtonMap = new HashMap<>();
    private Toa currentToa;
    private List<Toa> toaList;
    private int currentIndex = 0;
    private int doanTauLength;
    private PanelBuoc2Controller panelBuoc2Controller;

    public PanelSoDoCho() {
        setBorder(new TitledBorder("Sơ đồ chỗ"));
        setLayout(new BorderLayout());

        lblToaInfo = new JLabel("Chưa chọn toa", SwingConstants.CENTER);
        lblToaInfo.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 8));

        btnPrev = new JButton("<");
        btnNext = new JButton(">");
        btnPrev.setPreferredSize(new Dimension(35, 25));
        btnNext.setPreferredSize(new Dimension(35, 25));

        pnlNorth = new JPanel(new BorderLayout());
        pnlNorth.add(lblToaInfo, BorderLayout.CENTER);

        pnlGridChoNgoi = new JPanel();
        pnlGridChoNgoi.setBorder(new RoundedBorder(20, new Color(220, 220, 220), 1));

        scroll = new JScrollPane(pnlGridChoNgoi);
        scroll.setPreferredSize(new Dimension(VIEWPORT_WIDTH, VIEWPORT_HEIGHT));
        scroll.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        // Tăng tốc độ cuộn
        scroll.getHorizontalScrollBar().setUnitIncrement(16);

        btnChiem.setBackground(new Color(207, 207, 207, 220));
        btnChiem.setForeground(new Color(60, 60, 60));
        btnBan.setBackground(new Color(220, 53, 53, 220));
        btnBan.setForeground(Color.WHITE);

        add(pnlNorth, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(btnPrev, BorderLayout.WEST);
        add(btnNext, BorderLayout.EAST);

        btnPrev.addActionListener(e -> showPrevToa());
        btnNext.addActionListener(e -> showNextToa());
    }

    public void setController(PanelBuoc2Controller c) {
        this.panelBuoc2Controller = c;
    }

    public void setToaList(List<Toa> list) {
        this.toaList = list;
        this.doanTauLength = (list == null) ? 0 : list.size();
        this.currentIndex = 0;
        if (list != null && !list.isEmpty()) {
            setCurrentToa(list.get(0));
        } else {
            setCurrentToa(null);
        }
    }

    public void setCurrentToa(Toa t) {
        this.currentToa = t;
        if (t == null) {
            lblToaInfo.setText("Chưa chọn toa");
            showMessage("Không có toa được chọn");
            return;
        }

        lblToaInfo.setText("Toa số " + t.getSoToa() + ": "
                + (t.getHangToa() != null ? t.getHangToa().getHangToaID() : "Chưa xác định"));

        if (panelBuoc2Controller != null) {
            new LoadSeatWorker(t).execute();
        }
    }

    public void showMessage(String text) {
        pnlGridChoNgoi.removeAll();
        pnlGridChoNgoi.repaint();
    }

    /**
     * Cập nhật trạng thái trực quan cho MỘT nút ghế
     *
     * @param soGhe      Số ghế cần cập nhật
     * @param isSelected Trạng thái mới (true = chọn, false = bỏ chọn)
     */
    public void updateSeatVisual(int soGhe, boolean isSelected) {
        JButton b = seatButtonMap.get(soGhe);
        if (b == null) {
            return;
        }

        if (isSelected) {
            b.setBackground(new Color(40, 167, 69));
            b.setForeground(Color.WHITE);
            b.setEnabled(true);
            b.setOpaque(true);
        } else {
            b.setBackground(UIManager.getColor("Button.background"));
            b.setForeground(UIManager.getColor("Button.foreground"));
            b.setOpaque(true);
            b.setEnabled(true);
        }
        // Yêu cầu Swing vẽ lại chỉ nút này
        b.repaint();
    }

    public void renderSeats(List<Ghe> gheListFull) {
        pnlGridChoNgoi.removeAll();
        seatButtonMap.clear();

        if (gheListFull == null || gheListFull.isEmpty() || currentToa == null || currentToa.getHangToa() == null) {
            showMessage("Không có ghế hoặc thông tin toa không hợp lệ");
            return;
        }

        List<Ghe> gheListFiltered = filterSeatsForDemo(gheListFull, currentToa.getHangToa().getHangToaID());

        if (gheListFiltered.isEmpty()) {
            showMessage("Không có ghế phù hợp để hiển thị");
            return;
        }

        // Sắp xếp ghế theo số ghế
        gheListFiltered.sort(Comparator.comparingInt(Ghe::getSoGhe));

        Set<Integer> selectedSoGheSet = (panelBuoc2Controller != null)
                ? panelBuoc2Controller.getSelectedSoGhe(this.currentToa)
                : Collections.emptySet();

        String giaFormatted = "(Không rõ giá)";
        boolean priceCalculated = false;
        try {
            if (panelBuoc2Controller != null && panelBuoc2Controller.getSelectedChuyen() != null) {

                String chuyenID = panelBuoc2Controller.getSelectedChuyen().getChuyenID();
                String loaiTauID = panelBuoc2Controller.getSelectedChuyen().getTau().getLoaiTau().toString();

                SearchCriteria criteria = panelBuoc2Controller.getCurrentTripCriteria();
                String gaDiID = (criteria != null) ? criteria.getGaDiId() : null;
                String gaDenID = (criteria != null) ? criteria.getGaDenId() : null;

                String hangToaID = (currentToa.getHangToa() != null) ? currentToa.getHangToa().getHangToaID() : null;

                if (chuyenID != null && gaDiID != null && gaDenID != null && loaiTauID != null && hangToaID != null) {

                    // Gọi Controller để lấy giá
                    int gia = panelBuoc2Controller.getGiaForTooltip(chuyenID, gaDiID, gaDenID, loaiTauID, hangToaID);

                    // Định dạng tiền tệ VNĐ
                    DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
                    giaFormatted = formatter.format(gia);
                    priceCalculated = true;
                }
            }
        } catch (Exception e) {
            System.out.println("PanelSoDoCho: renderSeats() Lỗi tính giá");
        }

        String hangToaID = currentToa.getHangToa().getHangToaID();
        switch (hangToaID) {
            case "GN_K4":
                layout_GN_K4(gheListFiltered, selectedSoGheSet, giaFormatted, priceCalculated);
                break;
            case "GN_K6":
                layout_GN_K6(gheListFiltered, selectedSoGheSet, giaFormatted, priceCalculated);
                break;
            case "NM_CLC":
                layout_NM_CLC(gheListFiltered, selectedSoGheSet, giaFormatted, priceCalculated);
                break;
            default:
                layout_Default(gheListFiltered, selectedSoGheSet, giaFormatted, priceCalculated);
                break;
        }

        pnlGridChoNgoi.revalidate();
        pnlGridChoNgoi.repaint();
    }

    private List<Ghe> filterSeatsForDemo(List<Ghe> originalList, String hangToaID) {
        int maxSeats;
        switch (hangToaID) {
            case "GN_K4":
                maxSeats = 28;
                break;
            case "GN_K6":
                maxSeats = 42;
                break;
            case "NM_CLC":
                maxSeats = 56;
                break;
            default:
                return new ArrayList<>(originalList);
        }
        return originalList.stream().filter(g -> g.getSoGhe() <= maxSeats).collect(Collectors.toList());
    }

    // --- HÀM TẠO NÚT GHẾ CHUNG ---
    private JButton createSeatButton(Ghe g, boolean isSelectedInSession, String tooltipPrice, boolean priceCalculated) {
        JButton b = new JButton(String.valueOf(g.getSoGhe()));
        b.setOpaque(true);
        b.setMargin(new Insets(0, 0, 0, 0));
        b.setFocusPainted(false);
        b.setFont(new Font(getFont().getFamily(), Font.PLAIN, 10));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        TrangThaiGhe status = g.getTrangThai();
        boolean isAvailable = (status == TrangThaiGhe.TRONG);

        if (isAvailable) {
            b.setToolTipText(String.format("<html><span style='color:green'>Chỗ %s trống</span><br>Giá: %s</html>",
                    g.getSoGhe(), tooltipPrice));
        } else if (status == TrangThaiGhe.DA_BAN) {
            b.setToolTipText(String.format("<html>Chỗ %s đã bán<br>Không thể đặt</html>", g.getSoGhe()));
        } else if (status == TrangThaiGhe.BI_CHIEM) {
            b.setToolTipText(String.format("<html>Chỗ %s đang bị giữ<br>Không thể đặt</html>", g.getSoGhe()));
        }

        // --- Xác định trạng thái và màu ban đầu ---
        b.setEnabled(isAvailable);

        if (isSelectedInSession && isAvailable) {
            b.setBackground(new Color(40, 167, 69));
            b.setForeground(Color.WHITE);
        } else if (status == TrangThaiGhe.DA_BAN) {
            b.setIcon(new ColorIcon(btnBan.getBackground(), SEAT_WIDTH, SEAT_HEIGHT));
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setVerticalTextPosition(SwingConstants.CENTER);
            b.setForeground(btnBan.getForeground());
        } else if (status == TrangThaiGhe.BI_CHIEM) {
            b.setIcon(new ColorIcon(btnChiem.getBackground(), SEAT_WIDTH, SEAT_HEIGHT));
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setVerticalTextPosition(SwingConstants.CENTER);
            b.setForeground(btnChiem.getForeground());
        }

        b.addActionListener(e -> {
            if (!b.isEnabled()) {
                return;
            }
            boolean wasSelectedNow = panelBuoc2Controller.getSelectedSoGhe(currentToa).contains(g.getSoGhe());

            if (panelBuoc2Controller != null) {
                if (wasSelectedNow) {
                    b.setBackground(UIManager.getColor("Button.background"));
                    b.setForeground(UIManager.getColor("Button.foreground"));
                    b.setOpaque(false);

                    panelBuoc2Controller.handleSeatDeselection(currentToa, g);
                } else {
                    b.setBackground(new Color(40, 167, 69));
                    b.setForeground(Color.WHITE);
                    b.setOpaque(true);

                    panelBuoc2Controller.onSeatClicked(currentToa, g);
                }
            }
        });

        seatButtonMap.put(g.getSoGhe(), b); // Lưu nút vào map
        return b;
    }

    // Layout cho GN_K4 (Giường nằm khoang 4 - 28 ghế)
    private void layout_GN_K4(List<Ghe> gheList, Set<Integer> selectedSoGheSet, String giaFormatted,
                              boolean priceCalculated) {
        // Layout: 2 hàng (T1, T2), 7 cột khoang, mỗi khoang 2 ghế
        // Định nghĩa cột: 14 cột (7 cặp), có gap lớn hơn giữa các cặp
        String colConstraints = String.format("[%d!]%d", SEAT_WIDTH, 5); // Ghế + gap nhỏ
        String pairConstraints = String.format("[%d!]%d", SEAT_WIDTH, 15); // Ghế + gap lớn (sau cặp)
        StringBuilder finalColConstraints = new StringBuilder();
        for (int i = 0; i < 6; i++) { // 6 cặp đầu
            finalColConstraints.append(colConstraints).append(pairConstraints);
        }
        finalColConstraints.append(colConstraints).append("[").append(SEAT_WIDTH).append("!]"); // Cặp cuối cùng

        pnlGridChoNgoi.setLayout(new MigLayout("insets 10, gapy 10, alignx center, aligny center",
                finalColConstraints.toString(), String.format("[%d!]%d[%d!]", SEAT_HEIGHT, 15, SEAT_HEIGHT)));

        Map<Integer, Ghe> gheMap = gheList.stream().collect(Collectors.toMap(Ghe::getSoGhe, g -> g));

        // Thứ tự ghế
        int[] row2Order = {3, 4, 7, 8, 11, 12, 15, 16, 19, 20, 23, 24, 27, 28};
        int[] row1Order = {1, 2, 5, 6, 9, 10, 13, 14, 17, 18, 21, 22, 25, 26};

        // Vẽ hàng T2 (Hàng 0 trong MigLayout)
        for (int i = 0; i < row2Order.length; i++) {
            int soGhe = row2Order[i];
            Ghe g = gheMap.get(soGhe);
            String cellConstraint = "cell " + i + " 0"; // Xác định ô: cột i, hàng 0
            if (g != null) {
                boolean isSelected = selectedSoGheSet.contains(soGhe);
                JButton btn = createSeatButton(g, isSelected, giaFormatted, priceCalculated);
                pnlGridChoNgoi.add(btn, cellConstraint + ", " + SEAT_SIZE_CONSTRAINTS);
            } else {
                pnlGridChoNgoi.add(new JLabel(""), cellConstraint); // Placeholder
            }
        }

        // Vẽ hàng T1 (Hàng 1 trong MigLayout)
        for (int i = 0; i < row1Order.length; i++) {
            int soGhe = row1Order[i];
            Ghe g = gheMap.get(soGhe);
            String cellConstraint = "cell " + i + " 1"; // Xác định ô: cột i, hàng 1
            if (g != null) {
                boolean isSelected = selectedSoGheSet.contains(soGhe);
                JButton btn = createSeatButton(g, isSelected, giaFormatted, priceCalculated);
                pnlGridChoNgoi.add(btn, cellConstraint + ", " + SEAT_SIZE_CONSTRAINTS);
            } else {
                pnlGridChoNgoi.add(new JLabel(""), cellConstraint);
            }
        }
    }

    // Layout cho GN_K6 (Giường nằm khoang 6 - 42 ghế)
    private void layout_GN_K6(List<Ghe> gheList, Set<Integer> selectedSoGheSet, String giaFormatted,
                              boolean priceCalculated) {
        // Layout: 3 hàng (T1, T2, T3), 7 cột khoang, mỗi khoang 2 ghế
        // Định nghĩa cột tương tự GN_K4
        String colConstraints = String.format("[%d!]%d", SEAT_WIDTH, 5);
        String pairConstraints = String.format("[%d!]%d", SEAT_WIDTH, 15);
        StringBuilder finalColConstraints = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            finalColConstraints.append(colConstraints).append(pairConstraints);
        }
        finalColConstraints.append(colConstraints).append("[").append(SEAT_WIDTH).append("!]");

        pnlGridChoNgoi.setLayout(
                new MigLayout("insets 10, gapy 10, alignx center, aligny center", finalColConstraints.toString(),
                        String.format("[%d!]%d[%d!]%d[%d!]", SEAT_HEIGHT, 10, SEAT_HEIGHT, 10, SEAT_HEIGHT)));

        Map<Integer, Ghe> gheMap = gheList.stream().collect(Collectors.toMap(Ghe::getSoGhe, g -> g));

        // Thứ tự ghế
        int[] row3Order = {5, 6, 11, 12, 17, 18, 23, 24, 29, 30, 35, 36, 41, 42};
        int[] row2Order = {3, 4, 9, 10, 15, 16, 21, 22, 27, 28, 33, 34, 39, 40};
        int[] row1Order = {1, 2, 7, 8, 13, 14, 19, 20, 25, 26, 31, 32, 37, 38};

        // Vẽ hàng T3 (Hàng 0)
        for (int i = 0; i < row3Order.length; i++) {
            int soGhe = row3Order[i];
            Ghe g = gheMap.get(soGhe);
            String cellConstraint = "cell " + i + " 0";
            if (g != null) {
                boolean isSelected = selectedSoGheSet.contains(soGhe);
                JButton btn = createSeatButton(g, isSelected, giaFormatted, priceCalculated);
                pnlGridChoNgoi.add(btn, cellConstraint + ", " + SEAT_SIZE_CONSTRAINTS);
            } else {
                pnlGridChoNgoi.add(new JLabel(""), cellConstraint);
            }
        }

        // Vẽ hàng T2 (Hàng 1)
        for (int i = 0; i < row2Order.length; i++) {
            int soGhe = row2Order[i];
            Ghe g = gheMap.get(soGhe);
            String cellConstraint = "cell " + i + " 1";
            if (g != null) {
                boolean isSelected = selectedSoGheSet.contains(soGhe);
                JButton btn = createSeatButton(g, isSelected, giaFormatted, priceCalculated);
                pnlGridChoNgoi.add(btn, cellConstraint + ", " + SEAT_SIZE_CONSTRAINTS);
            } else {
                pnlGridChoNgoi.add(new JLabel(""), cellConstraint);
            }
        }

        // Vẽ hàng T1 (Hàng 2)
        for (int i = 0; i < row1Order.length; i++) {
            int soGhe = row1Order[i];
            Ghe g = gheMap.get(soGhe);
            String cellConstraint = "cell " + i + " 2";
            if (g != null) {
                boolean isSelected = selectedSoGheSet.contains(soGhe);
                JButton btn = createSeatButton(g, isSelected, giaFormatted, priceCalculated);
                pnlGridChoNgoi.add(btn, cellConstraint + ", " + SEAT_SIZE_CONSTRAINTS);
            } else {
                pnlGridChoNgoi.add(new JLabel(""), cellConstraint);
            }
        }
    }

    // Layout cho NM_CLC (Ngồi mềm CLC - 56 ghế)
    private void layout_NM_CLC(List<Ghe> gheList, Set<Integer> selectedSoGheSet, String giaFormatted,
                               boolean priceCalculated) {
        StringBuilder colFormat = new StringBuilder();
        int seatGap = 5;
        int aisleGap = 10;
        int aisleMinWidth = 20;
        // 7 ghế trái
        for (int i = 0; i < 7; i++) {
            colFormat.append("[").append(SEAT_WIDTH).append("!]");
            if (i < 6) {
                colFormat.append(seatGap);
            }
        }
        // Lối đi (cột logic 7)
        colFormat.append("[").append("min:").append(aisleMinWidth).append("]").append(aisleGap);
        // 7 ghế phải
        for (int i = 0; i < 7; i++) {
            colFormat.append("[").append(SEAT_WIDTH).append("!]");
            if (i < 6) {
                colFormat.append(seatGap);
            }
        }
        String finalColConstraints = colFormat.toString();

        StringBuilder rowFormat = new StringBuilder();
        int rowGap = 5;
        int aisleRowGap = 14;

        rowFormat.append("[").append(SEAT_HEIGHT).append("!]"); // Hàng 0
        rowFormat.append(rowGap); // Gap nhỏ
        rowFormat.append("[").append(SEAT_HEIGHT).append("!]"); // Hàng 1
        rowFormat.append(aisleRowGap); // *** GAP LỚN ***
        rowFormat.append("[").append(SEAT_HEIGHT).append("!]"); // Hàng 2
        rowFormat.append(rowGap); // Gap nhỏ
        rowFormat.append("[").append(SEAT_HEIGHT).append("!]"); // Hàng 3

        String finalRowConstraints = rowFormat.toString();

        // --- Sử dụng ràng buộc đã xây dựng ---
        pnlGridChoNgoi.setLayout(new MigLayout("insets 4, gapy 4, alignx center, aligny center", finalColConstraints,
                finalRowConstraints));

        Map<Integer, Ghe> gheMap = gheList.stream().collect(Collectors.toMap(Ghe::getSoGhe, g -> g));

        // --- Hardcode vị trí từng ghế (giữ nguyên logic đặt ghế vào cell) ---
        // Hàng 1 (MigLayout row 0)
        addSeatButtonToGridNM_Cell(gheMap, 1, selectedSoGheSet, 0, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 8, selectedSoGheSet, 1, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 9, selectedSoGheSet, 2, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 16, selectedSoGheSet, 3, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 17, selectedSoGheSet, 4, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 24, selectedSoGheSet, 5, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 25, selectedSoGheSet, 6, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 32, selectedSoGheSet, 8, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 33, selectedSoGheSet, 9, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 40, selectedSoGheSet, 10, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 41, selectedSoGheSet, 11, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 48, selectedSoGheSet, 12, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 49, selectedSoGheSet, 13, 0, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 56, selectedSoGheSet, 14, 0, giaFormatted, priceCalculated);

        // Hàng 2 (MigLayout row 1)
        addSeatButtonToGridNM_Cell(gheMap, 2, selectedSoGheSet, 0, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 7, selectedSoGheSet, 1, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 10, selectedSoGheSet, 2, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 15, selectedSoGheSet, 3, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 18, selectedSoGheSet, 4, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 23, selectedSoGheSet, 5, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 26, selectedSoGheSet, 6, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 31, selectedSoGheSet, 8, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 34, selectedSoGheSet, 9, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 39, selectedSoGheSet, 10, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 42, selectedSoGheSet, 11, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 47, selectedSoGheSet, 12, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 50, selectedSoGheSet, 13, 1, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 55, selectedSoGheSet, 14, 1, giaFormatted, priceCalculated);

        // Hàng 3 (MigLayout row 2)
        addSeatButtonToGridNM_Cell(gheMap, 3, selectedSoGheSet, 0, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 6, selectedSoGheSet, 1, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 11, selectedSoGheSet, 2, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 14, selectedSoGheSet, 3, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 19, selectedSoGheSet, 4, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 22, selectedSoGheSet, 5, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 27, selectedSoGheSet, 6, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 30, selectedSoGheSet, 8, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 35, selectedSoGheSet, 9, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 38, selectedSoGheSet, 10, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 43, selectedSoGheSet, 11, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 46, selectedSoGheSet, 12, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 51, selectedSoGheSet, 13, 2, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 54, selectedSoGheSet, 14, 2, giaFormatted, priceCalculated);

        // Hàng 4 (MigLayout row 3)
        addSeatButtonToGridNM_Cell(gheMap, 4, selectedSoGheSet, 0, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 5, selectedSoGheSet, 1, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 12, selectedSoGheSet, 2, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 13, selectedSoGheSet, 3, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 20, selectedSoGheSet, 4, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 21, selectedSoGheSet, 5, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 28, selectedSoGheSet, 6, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 29, selectedSoGheSet, 8, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 36, selectedSoGheSet, 9, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 37, selectedSoGheSet, 10, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 44, selectedSoGheSet, 11, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 45, selectedSoGheSet, 12, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 52, selectedSoGheSet, 13, 3, giaFormatted, priceCalculated);
        addSeatButtonToGridNM_Cell(gheMap, 53, selectedSoGheSet, 14, 3, giaFormatted, priceCalculated);
    }

    // (Hàm addSeatButtonToGridNM_Cell giữ nguyên)
    private void addSeatButtonToGridNM_Cell(Map<Integer, Ghe> gheMap, int soGhe, Set<Integer> selectedSoGheSet, int col,
                                            int row, String giaFormatted, boolean priceCalculated) {
        Ghe g = gheMap.get(soGhe);
        String constraints = "cell " + col + " " + row; // Tạo ràng buộc cell
        if (g != null && soGhe <= 56) {
            boolean isSelected = selectedSoGheSet.contains(soGhe);
            JButton btn = createSeatButton(g, isSelected, giaFormatted, priceCalculated);
            pnlGridChoNgoi.add(btn, constraints + ", " + SEAT_SIZE_CONSTRAINTS);
        } else {
            pnlGridChoNgoi.add(new JLabel(""), constraints);
        }
    }

    // Layout mặc định (GridLayout cũ)
    private void layout_Default(List<Ghe> gheList, Set<Integer> selectedSoGheSet, String giaFormatted,
                                boolean priceCalculated) {
        int cols = Math.min(gheList.size(), 8); // Tăng số cột mặc định
        int rows = (int) Math.ceil(gheList.size() / (double) cols);
        pnlGridChoNgoi.setLayout(new MigLayout(String.format("wrap %d, insets 10, gap 5 5", cols),
                "[" + SEAT_WIDTH + "!]", "[" + SEAT_HEIGHT + "!]"));

        for (Ghe g : gheList) {
            boolean isSelected = selectedSoGheSet.contains(g.getSoGhe());
            JButton b = createSeatButton(g, isSelected, giaFormatted, priceCalculated);
            pnlGridChoNgoi.add(b, SEAT_SIZE_CONSTRAINTS);
        }
    }

    // ==== Navigation ====
    public void showPrevToa() {
        if (toaList == null || toaList.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex == 0) ? doanTauLength - 1 : currentIndex - 1;
        Toa t = toaList.get(currentIndex);
        setCurrentToa(t);
        if (panelBuoc2Controller != null) {
            panelBuoc2Controller.highlightToa(t);
        }
    }

    public void showNextToa() {
        if (toaList == null || toaList.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex == doanTauLength - 1) ? 0 : currentIndex + 1;
        Toa t = toaList.get(currentIndex);
        setCurrentToa(t);
        if (panelBuoc2Controller != null) {
            panelBuoc2Controller.highlightToa(t);
        }
    }

    public Map<Integer, JButton> getSeatButtonMap() {
        return seatButtonMap;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        scroll.setEnabled(enabled);
        btnPrev.setEnabled(enabled);
        btnNext.setEnabled(enabled);
        lblToaInfo.setEnabled(enabled);
    }

    // ==== Background Worker ====
    private class LoadSeatWorker extends SwingWorker<List<Ghe>, Void> {
        private final Toa toa;

        LoadSeatWorker(Toa t) {
            this.toa = t;
        }

        @Override
        protected List<Ghe> doInBackground() throws Exception {
            // --- SỬA LỖI: Không nên dùng callback lồng nhau kiểu này với SwingWorker ---
            // Cách tiếp cận này có thể gây deadlock hoặc lỗi thread.
            // Nên gọi trực tiếp BUS trong doInBackground.
            if (panelBuoc2Controller == null) {
                return Collections.emptyList();
            }
            // Lấy gaDiID, gaDenID từ controller (logic này đã có trong controller)
            String gaDiID = null;
            String gaDenID = null;
            String chuyenID = null;

            if (panelBuoc2Controller.getBookingSession() != null) {
                SearchCriteria sc = (panelBuoc2Controller.getCurrentTripIndex() == 0)
                        ? panelBuoc2Controller.getBookingSession().getOutboundCriteria()
                        : panelBuoc2Controller.getBookingSession().getReturnCriteria();
                if (sc != null) {
                    gaDiID = sc.getGaDiId();
                    gaDenID = sc.getGaDenId();
                }
            }
            if (panelBuoc2Controller.getSelectedChuyen() != null) {
                chuyenID = panelBuoc2Controller.getSelectedChuyen().getChuyenID();
            }

            // Gọi BUS trực tiếp
            if (gaDiID != null && gaDenID != null && chuyenID != null && toa != null) {
                return panelBuoc2Controller.getChuyenBUS().layCacGheTrongToaTrenChuyen(gaDiID, gaDenID, chuyenID,
                        toa.getToaID());
            } else {
                System.err.println("LoadSeatWorker: Thiếu thông tin để tải ghế.");
                return Collections.emptyList();
            }
        }

        @Override
        protected void done() {
            try {
                List<Ghe> seats = get();
                // Gọi renderSeats trên EDT
                SwingUtilities.invokeLater(() -> renderSeats(seats));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                // Hiển thị lỗi trên EDT
                SwingUtilities.invokeLater(() -> showMessage("Lỗi tải dữ liệu chỗ ngồi"));
            }
        }
    }
}