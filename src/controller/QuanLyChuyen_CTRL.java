package controller;/*
 * @ (#) QuanLyChuyen_CTRL.java   1.0     09/12/2025
package controller;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 09/12/2025
 */

import bus.Chuyen_BUS;
import bus.PhanQuyen_BUS;
import bus.Tuyen_BUS;
import entity.*;
import entity.type.TrangThaiTau;
import entity.type.VaiTroNhanVien;
import gui.application.form.quanLyChuyen.PanelCapNhatChuyen;
import gui.application.form.quanLyChuyen.PanelQuanLyChuyen;
import gui.application.form.quanLyChuyen.PanelThemChuyen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuanLyChuyen_CTRL {
    private Map<String, String> mapGaToID;
    private final PanelQuanLyChuyen panelQuanLyChuyen;
    private  PanelThemChuyen panelThemChuyen;
    private PanelCapNhatChuyen panelCapNhatChuyen;
    private JDialog dialogCapNhat;
    private JDialog dialogThem;
    private final Chuyen_BUS chuyenBus;
    private final Tuyen_BUS tuyenBus;
    private boolean isAdjusting = false;
    private List<String> lastSuggestionData = new ArrayList<>();
    private VaiTroNhanVien vaiTroNhanVien;

    private PhanQuyen_BUS phanQuyenBus;

    private String currentSearchGaDi = "";
    private String currentSearchGaDen = "";

    private int highlightFrom = -1;
    private int highlightTo = -1;

    private String selectedGaDi = "";
    private String selectedGaDen = "";


    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuanLyChuyen_CTRL(PanelQuanLyChuyen panelQuanLyChuyen){
        this.panelQuanLyChuyen = panelQuanLyChuyen;
        this.chuyenBus = new Chuyen_BUS();
        this.tuyenBus = new Tuyen_BUS();
        this.vaiTroNhanVien = panelQuanLyChuyen.getNhanVienThucHien().getVaiTroNhanVien();
        this.phanQuyenBus = new PhanQuyen_BUS();
        initEvents();
        taiDuLieuBanDauNgam();
    }
    private void taiDuLieuBanDauNgam() {
        panelQuanLyChuyen.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        LocalDate homNay = LocalDate.now();
        panelQuanLyChuyen.getTxtNgayDi().setText(homNay.format(dateTimeFormatter));

        SwingWorker<List<Chuyen>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Chuyen> doInBackground() throws Exception {

                return chuyenBus.layDanhSachChuyenTheoNgay(homNay);
            }

            @Override
            protected void done() {
                try {
                    List<Chuyen> dsChuyen = get();
                    loadDataToTable(dsChuyen);

                    Timer timer = new Timer(100, e -> thietLapAutoComplete());
                    timer.setRepeats(false);
                    timer.start();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    panelQuanLyChuyen.setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void tinhKhoangToMau() {
        highlightFrom = -1;
        highlightTo = -1;

        if (currentSearchGaDi.isEmpty() || currentSearchGaDen.isEmpty()) return;

        JTable table = panelQuanLyChuyen.getTableLichTrinh();
        int rowCount = table.getRowCount();

        String gaDiTim = currentSearchGaDi.trim();
        String gaDenTim = currentSearchGaDen.trim();

        for (int i = 0; i < rowCount; i++) {
            String gaDi = table.getValueAt(i, 1).toString().trim();
            String gaDen = table.getValueAt(i, 4).toString().trim();

            if (highlightFrom == -1 && gaDi.equalsIgnoreCase(gaDiTim)) {
                highlightFrom = i;
            }

            if (highlightFrom != -1 && gaDen.equalsIgnoreCase(gaDenTim)) {
                highlightTo = i;
                break;
            }
        }

        if (highlightFrom != -1 && highlightTo == -1) {
            highlightTo = highlightFrom;
        }
    }


    private void setupDetailTableRenderer() {
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                    c.setFont(panelQuanLyChuyen.getBASE_FONT());
                }

                if (highlightFrom != -1 && highlightTo != -1) {
                    if (row >= highlightFrom && row <= highlightTo) {
                        if (!isSelected) {
                            c.setBackground(new Color(255, 255, 204));
                            c.setForeground(new Color(0, 0, 150));
                        }
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        };

        // 3. Áp dụng Renderer cho TẤT CẢ các cột của bảng
        JTable table = panelQuanLyChuyen.getTableLichTrinh();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }


    private void initEvents(){
        panelQuanLyChuyen.getTableChuyen().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = panelQuanLyChuyen.getTableChuyen().getSelectedRow();
                if (row >= 0) {
                    String maChuyen = panelQuanLyChuyen.getTableChuyen().getValueAt(row, 0).toString();
                    hienThiChiTiet(maChuyen);
                }
            }
        });

        panelQuanLyChuyen.getBtnLamMoi().addActionListener(e -> lamMoi());

        panelQuanLyChuyen.getBtnThemChuyen().addActionListener(e -> hienThiFormChuyen());

        panelQuanLyChuyen.getBtnCapNhatChuen().addActionListener(e -> hienThiFormCapNhat());

        KeyAdapter searchKeyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                timKiemChuyen();
            }
        };

        panelQuanLyChuyen.getTxtMaChuyen().addKeyListener(searchKeyAdapter);
        panelQuanLyChuyen.getTxtGaXuatPhat().addKeyListener(searchKeyAdapter);
        panelQuanLyChuyen.getTxtGaDich().addKeyListener(searchKeyAdapter);
        panelQuanLyChuyen.getTxtTau().addKeyListener(searchKeyAdapter);
        panelQuanLyChuyen.getTxtNgayDi().addKeyListener(searchKeyAdapter);

        panelQuanLyChuyen.getDateChooser().addEventDateChooser((action, date)-> {
                if (action.getAction() == com.raven.datechooser.SelectedAction.DAY_SELECTED) {
                    timKiemChuyen();
                }

        });
    }


    private void lamMoi(){
        panelQuanLyChuyen.getTxtMaChuyen().setText("");
        panelQuanLyChuyen.getTxtGaXuatPhat().setText("");
        panelQuanLyChuyen.getTxtGaDich().setText("");
        panelQuanLyChuyen.getTxtTau().setText("");



        panelQuanLyChuyen.getTxtMaChuyen().setText("");
        panelQuanLyChuyen.getTxtChiTietMaTuyen().setText("");
        panelQuanLyChuyen.getTxtChiTietTau().setText("");
        panelQuanLyChuyen.getTxtChiTietTenChuyen().setText("");
        panelQuanLyChuyen.getTxtChiTietGaDi().setText("");
        panelQuanLyChuyen.getTxtChiTietGaDen().setText("");


        panelQuanLyChuyen.getModelLichTrinh().setRowCount(0);

        String homNay = LocalDate.now().format(dateTimeFormatter);
        panelQuanLyChuyen.getTxtNgayDi().setText(homNay);

        highlightFrom = -1;
        highlightTo = -1;
        selectedGaDi = "";
        selectedGaDen = "";
        panelQuanLyChuyen.getTableLichTrinh().repaint();



        timKiemChuyen();
    }

    private void hienThiFormCapNhat(){
        int row = panelQuanLyChuyen.getTableChuyen().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn chuyến cần cập nhật!");
            return;
        }
        String maChuyen = panelQuanLyChuyen.getTableChuyen().getValueAt(row, 0).toString();

        if (panelCapNhatChuyen == null) {
            panelCapNhatChuyen = new PanelCapNhatChuyen();
            panelCapNhatChuyen.setFocusable(true);
            dialogCapNhat = new JDialog();
            dialogCapNhat.setTitle("Cập Nhật Chuyến Tàu");
            dialogCapNhat.setContentPane(panelCapNhatChuyen);
            dialogCapNhat.setSize(1100, 750);
            dialogCapNhat.setLocationRelativeTo(null);
            dialogCapNhat.setModal(true);

            dialogCapNhat.addWindowListener(new WindowAdapter() {
                @Override
                public void windowOpened(WindowEvent e) {
                    panelCapNhatChuyen.requestFocusInWindow();
                }
            });

            initCapNhatEvents();
        }
        if (maChuyen.contains("_CK")) {
            panelCapNhatChuyen.getPnlChuKy().setVisible(true);
            panelCapNhatChuyen.getComboChuKy().setEnabled(false); // Khóa không cho sửa loại chu kỳ

            if (maChuyen.endsWith("_CKHN")) panelCapNhatChuyen.getComboChuKy().setSelectedItem("Hàng ngày");
            else if (maChuyen.endsWith("_CKHT")) panelCapNhatChuyen.getComboChuKy().setSelectedItem("Hàng tuần");
            else if (maChuyen.endsWith("_CKTN")) panelCapNhatChuyen.getComboChuKy().setSelectedItem("Hàng tháng");
            else if (maChuyen.endsWith("_CKHN")) panelCapNhatChuyen.getComboChuKy().setSelectedItem("Hàng năm");

            panelCapNhatChuyen.getChkKetThuc().setSelected(true);
            panelCapNhatChuyen.getTxtNgayKetThuc().setEnabled(true);
        } else {
            panelCapNhatChuyen.getPnlChuKy().setVisible(false);
        }
        mapGaToID = chuyenBus.getMapTenGaToID();
        loadDataToComboCapNhat();
        panelCapNhatChuyen.getComboTuyen().setEnabled(false);
        panelCapNhatChuyen.getComboTau().setEnabled(false);

        fillDataToUpdateForm(maChuyen);

        dialogCapNhat.setVisible(true);
    }


    private void fillDataToUpdateForm(String maChuyen) {
        Chuyen c = chuyenBus.layChuyenTheoMa(maChuyen);
        List<ChuyenGa> lichTrinh = chuyenBus.layChiTietHanhTrinh(maChuyen);

        if (c == null) return;

        panelCapNhatChuyen.getTxtMaChuyen().setText(c.getChuyenID());
        setComboText(panelCapNhatChuyen.getComboTuyen(), c.getTuyen().getTuyenID());

        String dbTauID = c.getTau().getTauID();
        String displayTau = dbTauID;

        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) panelCapNhatChuyen.getComboTau().getModel();
        for (int i = 0; i < model.getSize(); i++) {
            String item = model.getElementAt(i);

            if (layMaTuChuoiHienThi(item).equals(dbTauID)) {
                displayTau = item;
                break;
            }
        }
        setComboText(panelCapNhatChuyen.getComboTau(), displayTau);
        if (c.getNgayDi() != null)
            panelCapNhatChuyen.getTxtNgayDi().setText(c.getNgayDi().format(dateTimeFormatter));
        if (c.getGioDi() != null)
            panelCapNhatChuyen.getTxtGioDi().setText(c.getGioDi().format(timeFormatter));

        DefaultTableModel modelTable = panelCapNhatChuyen.getModelLichTrinh();
        modelTable.setRowCount(0);

        if (lichTrinh != null && lichTrinh.size() > 1) {
            for (int i = 0; i < lichTrinh.size() - 1; i++) {
                ChuyenGa gaDi = lichTrinh.get(i);
                ChuyenGa gaDen = lichTrinh.get(i + 1);

                String ngayDiStr = (gaDi.getNgayDi() != null) ? gaDi.getNgayDi().format(dateTimeFormatter) : "";
                String gioDiStr = (gaDi.getGioDi() != null) ? gaDi.getGioDi().format(timeFormatter) : "";

                String ngayDenStr = (gaDen.getNgayDen() != null) ? gaDen.getNgayDen().format(dateTimeFormatter) : "";
                String gioDenStr = (gaDen.getGioDen() != null) ? gaDen.getGioDen().format(timeFormatter) : "";

                modelTable.addRow(new Object[]{
                        (i + 1),
                        gaDi.getGa().getTenGa(),
                        ngayDiStr,
                        gioDiStr,
                        gaDen.getGa().getTenGa(),
                        ngayDenStr,
                        gioDenStr
                });
            }
        }
    }

    private void timKiemChuyen(){

        String maChuyen = panelQuanLyChuyen.getTxtMaChuyen().getText().trim();
        String gaDi = panelQuanLyChuyen.getTxtGaXuatPhat().getText().trim();
        String gaDen = panelQuanLyChuyen.getTxtGaDich().getText().trim();
        String tenTau = panelQuanLyChuyen.getTxtTau().getText().trim();
        String ngayDiStr = panelQuanLyChuyen.getTxtNgayDi().getText().trim();

        this.currentSearchGaDi = gaDi;
        this.currentSearchGaDen = gaDen;

        LocalDate ngayDi = null;
        if(!ngayDiStr.isEmpty() && !ngayDiStr.equals("Chọn ngày...")){
                ngayDi = LocalDate.parse(ngayDiStr, dateTimeFormatter);

        }

        List<Chuyen> resultList = chuyenBus.timKiemChuyen(maChuyen, gaDi, gaDen, tenTau, ngayDi);

        loadDataToTable(resultList);
        panelQuanLyChuyen.getModelLichTrinh().setRowCount(0);
    }

    private void loadDataToComboCapNhat() {
        List<String> dsTuyen = chuyenBus.getAllTuyenID();
        setupCombo(panelCapNhatChuyen.getComboTuyen(), dsTuyen);

        List<String> dsTauFormatted = chuyenBus.getListTauHoatDongFormatted();

        setupCombo(panelCapNhatChuyen.getComboTau(), dsTauFormatted);
    }

    private void initCapNhatEvents() {
        DocumentListener triggerCalcUpdate = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { khaoSatVaTinhToanCapNhat(); }
            @Override public void removeUpdate(DocumentEvent e) { khaoSatVaTinhToanCapNhat(); }
            @Override public void changedUpdate(DocumentEvent e) { khaoSatVaTinhToanCapNhat(); }
        };
        panelCapNhatChuyen.getTimePicker().addActionListener(e -> khaoSatVaTinhToanCapNhat());
        panelCapNhatChuyen.getTxtNgayDi().getDocument().addDocumentListener(triggerCalcUpdate);
        panelCapNhatChuyen.getTxtGioDi().getDocument().addDocumentListener(triggerCalcUpdate);


        panelCapNhatChuyen.getBtnCapNhatChuyen().addActionListener(e -> xuLyLuuCapNhat());
    }

    private void khaoSatVaTinhToanCapNhat() {
        if (panelCapNhatChuyen == null) return;

        String ngayDiStr = panelCapNhatChuyen.getTxtNgayDi().getText().trim();
        String gioDiStr = panelCapNhatChuyen.getTxtGioDi().getText().trim();

        // Lấy giá trị từ Editor của ComboBox (giống bên Thêm chuyến)
        Object item = panelCapNhatChuyen.getComboTau().getEditor().getItem();
        String rawTau = (item != null) ? item.toString().trim() : "";

        DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();

        if (!ngayDiStr.isEmpty() && !gioDiStr.isEmpty() && !rawTau.isEmpty() && model.getRowCount() > 0) {

            // Chuẩn hóa giờ (VD: 8:0 -> 08:00) để parse không lỗi
            if (gioDiStr.contains(":") && gioDiStr.indexOf(":") == 1) {
                gioDiStr = "0" + gioDiStr;
            }

            // Kiểm tra định dạng trước khi tính
            if (!ngayDiStr.matches("\\d{2}/\\d{2}/\\d{4}") || !gioDiStr.matches("\\d{2}:\\d{2}")) {
                return;
            }

            // Gán vào dòng đầu tiên của bảng lịch trình
            model.setValueAt(ngayDiStr, 0, 2);
            model.setValueAt(gioDiStr, 0, 3);

            // Bắt đầu chuỗi tính toán đệ quy
            tinhTuDongThoiGianDen_Update(0, ngayDiStr, gioDiStr);
        }
    }

    private void tinhTuDongThoiGianDen_Update(int currentRow, String ngayDiStr, String gioDiStr) {
        try {
            if (panelCapNhatChuyen == null) return;

            // Lấy mã tàu sạch (đã loại bỏ phần trạng thái trong ngoặc)
            Object item = panelCapNhatChuyen.getComboTau().getEditor().getItem();
            if (item == null) return;

            String tauID = layMaTuChuoiHienThi(item.toString());
            int vanToc = chuyenBus.layTocDoTau(tauID);
            if (vanToc <= 0) vanToc = 40; // Mặc định nếu không có vận tốc

            DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();

            // Lấy Ga đi và Ga đến của chặng hiện tại
            Object gaDiObj = model.getValueAt(currentRow, 1);
            Object gaDenObj = model.getValueAt(currentRow, 4);
            if (gaDiObj == null || gaDenObj == null) return;

            String idGaDi = mapGaToID.get(gaDiObj.toString());
            String idGaDen = mapGaToID.get(gaDenObj.toString());
            if (idGaDi == null || idGaDen == null) return;

            // Tính khoảng cách và thời gian di chuyển
            int khoangCachKm = tuyenBus.getKhoangCachGiuaHaiGa(idGaDi, idGaDen);
            double phutDiChuyen = (khoangCachKm > 0) ? ((double) khoangCachKm / vanToc) * 40 : 0;

            // Parse thời gian đi để tính thời gian đến
            DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dtDi = LocalDateTime.parse(ngayDiStr + " " + gioDiStr, parseFormat);
            LocalDateTime dtDen = dtDi.plusMinutes((long) Math.ceil(phutDiChuyen));

            // Cập nhật kết quả vào bảng
            model.setValueAt(dtDen.format(dateTimeFormatter), currentRow, 5);
            model.setValueAt(dtDen.format(timeFormatter), currentRow, 6);

            // Tính toán cho chặng kế tiếp (nếu có)
            if (currentRow < model.getRowCount() - 1) {
                int thoiGianNghi = 15;
                LocalDateTime dtDiTiep = dtDen.plusMinutes(thoiGianNghi);

                String ngayNext = dtDiTiep.format(dateTimeFormatter);
                String gioNext = dtDiTiep.format(timeFormatter);

                // Gán thông tin đi cho dòng tiếp theo
                model.setValueAt(ngayNext, currentRow + 1, 2);
                model.setValueAt(gioNext, currentRow + 1, 3);

                // Đệ quy
                tinhTuDongThoiGianDen_Update(currentRow + 1, ngayNext, gioNext);
            }
        } catch (Exception e) {
            // Bắt lỗi im lặng để không treo giao diện khi user đang gõ dở
        }
    }

    private void xuLyLuuCapNhat() {
        try {
            DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();
            if (model.getRowCount() < 1) {
                JOptionPane.showMessageDialog(dialogCapNhat, "Lịch trình phải có ít nhất 1 chặng!");
                return;
            }

            String maChuyen = panelCapNhatChuyen.getTxtMaChuyen().getText();

            Object itemTuyen = panelCapNhatChuyen.getComboTuyen().getEditor().getItem();
            String tuyenID = layMaTuChuoiHienThi(itemTuyen != null ? itemTuyen.toString() : "");

            // Lấy Tàu ID an toàn (kể cả khi bị disabled)
            Object itemTau = panelCapNhatChuyen.getComboTau().getEditor().getItem();
            String tauID = layMaTuChuoiHienThi(itemTau != null ? itemTau.toString() : "");

            // Kiểm tra dự phòng nếu lấy từ Editor không được (tùy thuộc vào Look and Feel)
            if (tuyenID.isEmpty()) {
                Chuyen cGoc = chuyenBus.layChuyenTheoMa(maChuyen);
                tuyenID = cGoc.getTuyen().getTuyenID();
                tauID = cGoc.getTau().getTauID();
            }
            if (maChuyen.endsWith("_PS")) {
                // Trường hợp 1: Chuyến phát sinh -> Chỉ cập nhật 1 chuyến
                thucThiCapNhatDonLe();
            }
            else if (maChuyen.endsWith("_CK")) {
                // Trường hợp 2: Chuyến chu kỳ -> Cập nhật hàng loạt các chuyến tương lai
                int confirm = JOptionPane.showConfirmDialog(dialogCapNhat,
                        "Đây là chuyến theo chu kỳ. Hệ thống sẽ cập nhật lịch trình cho tất cả các chuyến từ ngày này trở đi. Bạn có chắc chắn?",
                        "Xác nhận cập nhật chu kỳ", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    thucThiCapNhatChuKyBatch();
                }
            }
            // 2. Thu thập dữ liệu đối tượng
            LocalDate ngayDi = LocalDate.parse(panelCapNhatChuyen.getTxtNgayDi().getText(), dateTimeFormatter);
            LocalTime gioDi = LocalTime.parse(panelCapNhatChuyen.getTxtGioDi().getText(), timeFormatter);

            Chuyen c = new Chuyen(maChuyen);
            c.setTuyen(new Tuyen(tuyenID));
            c.setTau(new Tau(tauID, ""));
            c.setNgayDi(ngayDi);
            c.setGioDi(gioDi);

            String tenGaDau = model.getValueAt(0, 1).toString();
            String tenGaCuoi = model.getValueAt(model.getRowCount() - 1, 4).toString();
            String idGaDau = mapGaToID.get(tenGaDau);
            String idGaCuoi = mapGaToID.get(tenGaCuoi);

            if(idGaDau == null || idGaCuoi == null) {
                JOptionPane.showMessageDialog(dialogCapNhat, "Lỗi: Ga " + (idGaDau == null ? tenGaDau : tenGaCuoi) + " không tồn tại!");
                return;
            }
            c.setGaDi(new Ga(idGaDau, tenGaDau));
            c.setGaDen(new Ga(idGaCuoi, tenGaCuoi));

            List<ChuyenGa> listStops = new ArrayList<>();
            // ... (Logic khởi tạo startNode và vòng lặp listStops giữ nguyên của bạn)
            ChuyenGa startNode = new ChuyenGa();
            startNode.setChuyen(c);
            startNode.setGa(new Ga(idGaDau, tenGaDau));
            startNode.setThuTu(1);
            startNode.setNgayDi(LocalDate.parse(model.getValueAt(0, 2).toString(), dateTimeFormatter));
            startNode.setGioDi(LocalTime.parse(model.getValueAt(0, 3).toString(), timeFormatter));
            listStops.add(startNode);

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenGaDen = model.getValueAt(i, 4).toString();
                String idGaDen = mapGaToID.get(tenGaDen);
                if(idGaDen == null) continue;

                ChuyenGa stopNode = new ChuyenGa();
                stopNode.setChuyen(c);
                stopNode.setGa(new Ga(idGaDen, tenGaDen));
                stopNode.setThuTu(i + 2);
                stopNode.setGioDen(LocalTime.parse(model.getValueAt(i, 6).toString(), timeFormatter));
                stopNode.setNgayDen(LocalDate.parse(model.getValueAt(i, 5).toString(), dateTimeFormatter));

                if (i < model.getRowCount() - 1) {
                    String nextNgay = model.getValueAt(i + 1, 2).toString();
                    String nextGio = model.getValueAt(i + 1, 3).toString();
                    if (!nextNgay.isEmpty()) stopNode.setNgayDi(LocalDate.parse(nextNgay, dateTimeFormatter));
                    if (!nextGio.isEmpty()) stopNode.setGioDi(LocalTime.parse(nextGio, timeFormatter));
                }
                listStops.add(stopNode);
            }

            // 3. Thực hiện cập nhật và thông báo lỗi đích danh
            boolean ketQua = chuyenBus.capNhatChuyen(c, listStops, panelQuanLyChuyen.getNhanVienThucHien());

            if (ketQua) {
                JOptionPane.showMessageDialog(dialogCapNhat, "Cập nhật thành công!");
                dialogCapNhat.dispose();
                timKiemChuyen();
            } else {
                JOptionPane.showMessageDialog(dialogCapNhat,
                        "Cập nhật thất bại: Tàu hoặc Tuyến không tồn tại, hoặc lỗi kết nối cơ sở dữ liệu!",
                        "Lỗi Cập Nhật",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Lỗi: Định dạng ngày (dd/MM/yyyy) hoặc giờ (HH:mm) không hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Đã xảy ra lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadDataToTable(List<Chuyen> list){

        if (panelQuanLyChuyen.getTableChuyen().isEditing()){
            panelQuanLyChuyen.getTableChuyen().getCellEditor().cancelCellEditing();
        }

        DefaultTableModel model = panelQuanLyChuyen.getTableModel();
        model.setRowCount(0);
        for(Chuyen c : list){
            String ngayDenStr = (c.getNgayDen() != null) ? c.getNgayDen().format(dateTimeFormatter) : "N/A";
            String gioDenStr = (c.getGioDen() != null) ? c.getGioDen().format(timeFormatter) : "N/A";

            String ngayDiStr = (c.getNgayDi() != null) ? c.getNgayDi().format(dateTimeFormatter) : "N/A";
            String gioDiStr = (c.getGioDi() != null) ? c.getGioDi().format(timeFormatter) : "N/A";

            String loaiTauHienThi = "N/A";
            if (c.getTau() != null && c.getTau().getLoaiTau() != null) {
                loaiTauHienThi = c.getTau().getLoaiTau().getDescription();
            }

            model.addRow(new Object[]{
                    c.getChuyenID(),
                    c.getTenChuyenHienThi(),
                    (c.getTau() != null) ? c.getTau().getTenTau() : "N/A",
                    loaiTauHienThi,
                    ngayDiStr,
                    gioDiStr,
                    ngayDenStr,
                    gioDenStr
            });
        }
    }

    private void hienThiChiTiet(String maChuyen){
        Chuyen chuyen = chuyenBus.layChuyenTheoMa(maChuyen);
        List<ChuyenGa> hanhTrinh = chuyenBus.layChiTietHanhTrinh(maChuyen);

        if (chuyen != null) {
            String gaDiTuyen = chuyen.getTenGaDiHienThi();
            String gaDenTuyen = chuyen.getTenGaDenHienThi();
            selectedGaDi = gaDiTuyen;
            selectedGaDen = gaDenTuyen;

            String maTuyen = (chuyen.getTuyen() != null) ? chuyen.getTuyen().getTuyenID() : "N/A";

            String tenChuyen = chuyen.getTenChuyenHienThi();
            if(tenChuyen == null || tenChuyen.isEmpty() || tenChuyen.equals("N/A")){
                if(hanhTrinh != null && !hanhTrinh.isEmpty()){
                    String gaXP = hanhTrinh.get(0).getGa().getTenGa();
                    String gaDich = hanhTrinh.get(hanhTrinh.size() - 1).getGa().getTenGa();
                    tenChuyen = gaXP + " - " + gaDich;
                }else{
                    tenChuyen = "N/A";
                }
            }
            panelQuanLyChuyen.getTxtChiTietMaChuyen().setText(chuyen.getChuyenID());
            panelQuanLyChuyen.getTxtChiTietTenChuyen().setText(tenChuyen);
            panelQuanLyChuyen.getTxtChiTietMaTuyen().setText(maTuyen);

            panelQuanLyChuyen.getTxtChiTietGaDi().setText(gaDiTuyen);
            panelQuanLyChuyen.getTxtChiTietGaDen().setText(gaDenTuyen);
            panelQuanLyChuyen.getTxtChiTietTau().setText(chuyen.getTau().getTenTau() != null ? chuyen.getTau().getTenTau() : "N/A");
        } else {
            panelQuanLyChuyen.getTxtChiTietMaChuyen().setText("N/A");
            panelQuanLyChuyen.getTxtChiTietTenChuyen().setText("N/A");
            panelQuanLyChuyen.getTxtChiTietMaTuyen().setText("N/A");
            panelQuanLyChuyen.getTxtChiTietGaDi().setText("N/A");
            panelQuanLyChuyen.getTxtChiTietGaDen().setText("N/A");
            panelQuanLyChuyen.getTxtChiTietTau().setText("N/A");
        }

        DefaultTableModel model = panelQuanLyChuyen.getModelLichTrinh();
        model.setRowCount(0);

        if (hanhTrinh != null && hanhTrinh.size() > 1) {
            for (int i =0; i < hanhTrinh.size() -1; i++) {
                ChuyenGa gaDi = hanhTrinh.get(i);
                ChuyenGa gaDen = hanhTrinh.get(i + 1);
                String ngayDiStr = (gaDi.getNgayDi() != null) ? gaDi.getNgayDi().format(dateTimeFormatter) : "N/A";
                String gioDiStr = (gaDi.getGioDi() != null) ? gaDi.getGioDi().format(timeFormatter) : "N/A";
                String ngayDenStr = (gaDen.getNgayDen() != null) ? gaDen.getNgayDen().format(dateTimeFormatter) : "N/A";
                String gioDenStr = (gaDen.getGioDen() != null) ? gaDen.getGioDen().format(timeFormatter) : "N/A";

                model.addRow(new Object[]{
                        (i + 1),
                        gaDi.getGa().getTenGa(),
                        ngayDiStr,
                        gioDiStr,
                        gaDen.getGa().getTenGa(),
                        ngayDenStr,
                        gioDenStr
                });
            }
        }
        setupDetailTableRenderer();
        tinhKhoangToMau();
        panelQuanLyChuyen.getTableLichTrinh().repaint();
    }


    private void thietLapAutoComplete(){
        List<String> dataMaChuyen = chuyenBus.getListMaChuyen();
        List<String> dataTenGa = chuyenBus.getListTenGa();
        List<String> dataTenTau = chuyenBus.getListTenTau();

        taoPopupGoiY(panelQuanLyChuyen.getTxtGaXuatPhat(), panelQuanLyChuyen.getPpGaDi(),
                panelQuanLyChuyen.getListGaDi(),
                input -> {
                    if(input.length() < 2) return new ArrayList<>();
                    return chuyenBus.goiYGaDi(input, 10).stream()
                            .map(Ga::getTenGa)
                            .collect(Collectors.toList());
                },
                this::timKiemChuyen);

    }


    private List<String> locDuLieu(List<String> source, String input) {
        if (source == null) return new ArrayList<>();
        return source.stream().filter(s -> s.toLowerCase().contains(input.toLowerCase())).limit(10).collect(Collectors.toList());
    }

    private void taoPopupGoiY(JTextField txt, JPopupMenu pp, JList<String> lst,
                              Function<String, List<String>> timKiem,
                              Runnable onSelected) {

        pp.setFocusable(false);
        lst.setFocusable(false);
        lst.setRequestFocusEnabled(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        txt.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;

            private void update() {
                if (isAdjusting) return;
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(200, ev -> SwingUtilities.invokeLater(() -> {
                    if (txt.isFocusOwner()) {
                        hienThiGoiY(txt, lst, pp, timKiem);
                    }
                }));
                timer.setRepeats(false);
                timer.start();
            }

            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });

        // 2. MouseListener: Xử lý Click chuột chọn item
        lst.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
                    isAdjusting = true;
                    String selectedRaw = lst.getSelectedValue();
                    String realValue = layMaTuChuoiHienThi(selectedRaw);
                    txt.setText(realValue);
                    pp.setVisible(false);
                    isAdjusting = false;

                    // Chạy hành động tùy chỉnh (nếu có)
                    if (onSelected != null) {
                        onSelected.run();
                    }
                }
            }
        });

        // 3. KeyListener: Xử lý phím Mũi tên và Enter
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (pp.isVisible()) {
                        int index = lst.getSelectedIndex();
                        if (index < lst.getModel().getSize() - 1) lst.setSelectedIndex(index + 1);
                        lst.ensureIndexIsVisible(lst.getSelectedIndex());
                        e.consume();
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (pp.isVisible()) {
                        int index = lst.getSelectedIndex();
                        if (index > 0) lst.setSelectedIndex(index - 1);
                        lst.ensureIndexIsVisible(lst.getSelectedIndex());
                        e.consume();
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (pp.isVisible() && lst.getSelectedValue() != null) {
                        isAdjusting = true;
                        String selectedRaw = lst.getSelectedValue();
                        String realValue = layMaTuChuoiHienThi(selectedRaw);
                        txt.setText(realValue);
                        pp.setVisible(false);
                        isAdjusting = false;
                    }

                    if (onSelected != null) {
                        onSelected.run();
                    }

                    e.consume();
                }
            }
        });
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txt.getText().trim().isEmpty()) {
                    hienThiGoiY(txt, lst, pp, timKiem); // Gọi hiển thị với chuỗi rỗng để lấy toàn bộ
                }
            }
            @Override public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> pp.setVisible(false));
            }
        });

        // 4. FocusListener: Ẩn popup khi mất focus
        txt.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> pp.setVisible(false));
            }
        });
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst,
                             JPopupMenu pp,
                             Function<String, List<String>> timKiem) {

        String input = txt.getText().trim();
        List<String> ds = timKiem.apply(input);

        if (ds == null || ds.isEmpty()) {
            pp.setVisible(false);
            lastSuggestionData.clear();
            return;
        }
        if (ds.size() == 1 && ds.get(0).equalsIgnoreCase(input)) {
            pp.setVisible(false);
            lastSuggestionData.clear(); // Xóa cache để đảm bảo popup hiện lại khi xóa ký tự
            return;
        }

        if (ds.equals(lastSuggestionData)) {
            return;
        }

        lastSuggestionData = new ArrayList<>(ds);

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 10));

        if (lst.getSelectedIndex() == -1) {
            lst.setSelectedIndex(0);
        }

        if (txt.isFocusOwner()) {
            int popupWidth = Math.max(txt.getWidth(), 80);
            pp.setPopupSize(popupWidth, pp.getPreferredSize().height);
            pp.show(txt, 0, txt.getHeight());
        }
    }

    private boolean isMainSearchField(JTextField txt) {
        return txt == panelQuanLyChuyen.getTxtMaChuyen() ||
                txt == panelQuanLyChuyen.getTxtGaXuatPhat() ||
                txt == panelQuanLyChuyen.getTxtGaDich() ||
                txt == panelQuanLyChuyen.getTxtTau();
    }

    private void hienThiFormChuyen(){
        if(panelThemChuyen == null){
            panelThemChuyen = new PanelThemChuyen();
            dialogThem = new JDialog();
            dialogThem.setTitle("Thêm Chuyến Tàu Mới");
            dialogThem.setContentPane(panelThemChuyen);
            dialogThem.setSize(1000,750);
            dialogThem.setLocationRelativeTo(null);
            dialogThem.setModal(true);

            initThemChuyenEvents();

            mapGaToID = chuyenBus.getMapTenGaToID();

            loadDataCombobox();
        }

        panelThemChuyen.getTxtMaChuyen().setText("");
        panelThemChuyen.getTxtNgayDi().setText("");
        panelThemChuyen.getTxtGioDi().setText("");
        panelThemChuyen.getModelLichTrinh().setRowCount(0);

        setComboText(panelThemChuyen.getComboTuyen(), "");
        setComboText(panelThemChuyen.getComboTau(), "");
        dialogThem.setVisible(true);
    }

    private void setComboText(JComboBox<String> combo, String text){
        ((JTextField)combo.getEditor().getEditorComponent()).setText(text);
    }

    private void loadDataCombobox(){
        List<String> dsGa = chuyenBus.getListTenGa();
        List<String> dsTau = chuyenBus.getAllTauID();
        List<String> dsMaTuyen = chuyenBus.getAllTuyenID();
        Map<String, String> mapIDToTenGa = mapGaToID.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (oldValue, newValue) -> oldValue));

        List<String> dsTuyenHienThi = new ArrayList<>();
        if (dsMaTuyen != null) {
            for (String ma : dsMaTuyen) {
                dsTuyenHienThi.add(taoHienThiTuyen(ma, mapIDToTenGa));
            }
        }

        setupComboTuyen(panelThemChuyen.getComboTuyen(), dsTuyenHienThi);

        setupComboTuyen(panelThemChuyen.getComboTuyen(), dsTuyenHienThi);

        List<String> dsTauFormatted = chuyenBus.getListTauHoatDongFormatted();
        setupCombo(panelThemChuyen.getComboTau(), dsTauFormatted);

    }

    private void setupCombo(JComboBox<String> cbo, List<String> data){
        cbo.setEditable(true);
        cbo.setModel(new DefaultComboBoxModel<>(data.toArray(new String[0])));
        cbo.setSelectedIndex(-1);

        JTextField txtEditor = (JTextField) cbo.getEditor().getEditorComponent();

        Runnable actionNextFocus = txtEditor::transferFocus;

        taoPopupGoiY(txtEditor, new JPopupMenu(), new JList<>(),
                input -> locDuLieu(data, input),
                actionNextFocus);
    }

    private void initThemChuyenEvents() {
        DocumentListener triggerCalc = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { khaoSatVaTinhToan(); }
            @Override public void removeUpdate(DocumentEvent e) { khaoSatVaTinhToan(); }
            @Override public void changedUpdate(DocumentEvent e) { khaoSatVaTinhToan(); }
        };

        panelThemChuyen.getTxtNgayDi().getDocument().addDocumentListener(triggerCalc);
        panelThemChuyen.getTxtGioDi().getDocument().addDocumentListener(triggerCalc);

        ActionListener comboTrigger = e -> {
            if (!isAdjusting) {
                triggerLoadTableIfReady();
            }
        };
        panelThemChuyen.getComboTuyen().addActionListener(comboTrigger);
        panelThemChuyen.getComboTau().addActionListener(e -> {

            if (isAdjusting) return;

            SwingUtilities.invokeLater(() -> {
                Object selected = panelThemChuyen.getComboTau().getSelectedItem();
                if (selected == null) return;

                String rawTau = selected.toString();

                if (isTauKhongHopLe(rawTau)) {

                    JOptionPane.showMessageDialog(
                            dialogThem,
                            "Tàu " + layMaTuChuoiHienThi(rawTau)
                                    + " đang ngừng hoạt động hoặc bảo trì!",
                            "Cảnh báo",
                            JOptionPane.ERROR_MESSAGE
                    );

                    isAdjusting = true;
                    panelThemChuyen.getComboTau().setSelectedIndex(-1);
                    panelThemChuyen.getComboTau().getEditor().setItem("");
                    panelThemChuyen.getModelLichTrinh().setRowCount(0);
                    isAdjusting = false;
                    return;
                }

                triggerLoadTableIfReady();
            });
        });

        JTextField txtEditorTuyen = (JTextField) panelThemChuyen.getComboTuyen().getEditor().getEditorComponent();
        JTextField txtEditorTau = (JTextField) panelThemChuyen.getComboTau().getEditor().getEditorComponent();

        DocumentListener editorListener = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { trigger(); }
            @Override public void removeUpdate(DocumentEvent e) { trigger(); }
            @Override public void changedUpdate(DocumentEvent e) { trigger(); }
            private void trigger() {
                if (!isAdjusting) triggerLoadTableIfReady();
            }
        };
        txtEditorTuyen.getDocument().addDocumentListener(editorListener);
        txtEditorTau.getDocument().addDocumentListener(editorListener);

        panelThemChuyen.getComboChuKy().addActionListener(e -> genCode());
        panelThemChuyen.getBtnThemChuyen().addActionListener(e -> xuLyLuuChuyen());
    }

    private void triggerLoadTableIfReady() {
        if (isAdjusting) return;

        SwingUtilities.invokeLater(() -> {
            Object selectedTau = panelThemChuyen.getComboTau().getSelectedItem();
            if (selectedTau == null) return;

            String rawTau = getRawTauText();
            if (rawTau.isEmpty()) return;

            if (!kiemTraTrangThaiTauHopLe(rawTau)) {
                isAdjusting = true;
                panelThemChuyen.getComboTau().setSelectedIndex(-1);
                panelThemChuyen.getComboTau().getEditor().setItem("");
                panelThemChuyen.getModelLichTrinh().setRowCount(0);
                isAdjusting = false;
                return;
            }

            String rawTuyen = getSafeText(panelThemChuyen.getComboTuyen());
            if (!rawTuyen.isEmpty()) {
                String maTuyen = layMaTuChuoiHienThi(rawTuyen);
                String loaiTau = rawTau.toUpperCase().contains("DU_LICH") ? "TAU_DU_LICH" : "TAU_NHANH";
                loadLichTrinhMau(maTuyen, loaiTau);
                khaoSatVaTinhToan();
            }
        });
    }


    private void khaoSatVaTinhToan() {
        genCode();

        String ngayDiStr = panelThemChuyen.getTxtNgayDi().getText().trim();
        String gioDiStr = panelThemChuyen.getTxtGioDi().getText().trim();
        String rawTau = (String) panelThemChuyen.getComboTau().getSelectedItem();

        DefaultTableModel model = panelThemChuyen.getModelLichTrinh();

        if (!ngayDiStr.isEmpty() && !gioDiStr.isEmpty()
                && rawTau != null && !rawTau.isEmpty()
                && model.getRowCount() > 0) {

            if (!ngayDiStr.matches("\\d{2}/\\d{2}/\\d{4}") || !gioDiStr.matches("\\d{2}:\\d{2}")) {
                return;
            }

            model.setValueAt(ngayDiStr, 0, 2);
            model.setValueAt(gioDiStr, 0, 3);

            tinhTuDongThoiGianDen(0, ngayDiStr, gioDiStr);
        }
    }

    private void genCode() {
        try {
            Object item = panelThemChuyen.getComboTau().getEditor().getItem();
            String rawTau = (item != null) ? item.toString().trim() : "";

            String ngayDiStr = panelThemChuyen.getTxtNgayDi().getText().trim();
            String gioDiStr = panelThemChuyen.getTxtGioDi().getText().trim();
            String chuKy = panelThemChuyen.getComboChuKy().getSelectedItem().toString();

            if (!rawTau.isEmpty() && !ngayDiStr.isEmpty() && !gioDiStr.isEmpty()) {
                String tauID = layMaTuChuoiHienThi(rawTau);

                LocalDate d = LocalDate.parse(ngayDiStr, dateTimeFormatter);
                String strNgay = d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalTime t = LocalTime.parse(gioDiStr, timeFormatter);
                String strGio = t.format(DateTimeFormatter.ofPattern("HHmm"));

                String hauTo = getHauToTheoChuKy(chuKy);

                String maChuyenMoi = tauID.toUpperCase() + "_" + strNgay + "_" + strGio + "_" + hauTo;
                panelThemChuyen.getTxtMaChuyen().setText(maChuyenMoi);
            }
        } catch (Exception ex) {
        }
    }

    private void xuLyLuuChuyen() {
        try {
            String rawTuyen = panelThemChuyen.getComboTuyen().getEditor().getItem().toString();
            if (rawTuyen.trim().isEmpty()) {
                baoLoiVaFocus(panelThemChuyen.getComboTuyen(), "Vui lòng chọn tuyến!");
                return;
            }
            String rawTau = getRawTauText();
            if (!kiemTraTrangThaiTauHopLe(rawTau)) {
                panelThemChuyen.getComboTau().requestFocusInWindow();
                return;
            }
            String sNgayDi = panelThemChuyen.getTxtNgayDi().getText().trim();
            DefaultTableModel model = panelThemChuyen.getModelLichTrinh();

            if (rawTuyen == null || rawTuyen.isEmpty()) { baoLoiVaFocus(panelThemChuyen.getComboTuyen(), "Chọn tuyến!"); return; }
            if (rawTau == null || rawTau.isEmpty()) { baoLoiVaFocus(panelThemChuyen.getComboTau(), "Chọn tàu!"); return; }
            if (!PanelQuanLyChuyen.Validator.isValidNgay(sNgayDi)) { baoLoiVaFocus(panelThemChuyen.getTxtNgayDi(), "Ngày đi sai!"); return; }
            if (model.getRowCount() < 1) { JOptionPane.showMessageDialog(dialogThem, "Lịch trình trống!"); return; }

            String sGioDi = model.getValueAt(0, 3).toString().trim();

            String chuKy = panelThemChuyen.getComboChuKy().getSelectedItem().toString();
            boolean coKetThuc = panelThemChuyen.getChkKetThuc().isSelected();
            String sNgayKetThuc = panelThemChuyen.getTxtNgayKetThuc().getText().trim();

            LocalDate ngayBatDau = LocalDate.parse(sNgayDi, dateTimeFormatter);
            LocalDate ngayKetThuc = ngayBatDau;
            LocalDate ngayHienTai = LocalDate.now();
            final LocalDate ngayKetThucWorker = coKetThuc ? LocalDate.parse(sNgayKetThuc, dateTimeFormatter) : ngayBatDau;

            if (ngayBatDau.isBefore(ngayHienTai)) {
                JOptionPane.showMessageDialog(dialogThem,
                        "Ngày đi không được trước ngày hiện tại!",
                        "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                panelThemChuyen.getTxtNgayDi().requestFocusInWindow();
                return;
            }

            if (coKetThuc && ngayKetThucWorker.isBefore(ngayBatDau)) {
                JOptionPane.showMessageDialog(dialogThem, "Ngày kết thúc không được nhỏ hơn ngày bắt đầu!");
                return;
            }

            if (coKetThuc) {
                if (!PanelQuanLyChuyen.Validator.isValidNgay(sNgayKetThuc)) {
                    baoLoiVaFocus(panelThemChuyen.getTxtNgayKetThuc(), "Ngày kết thúc không hợp lệ!");
                    return;
                }
                ngayKetThuc = LocalDate.parse(sNgayKetThuc, dateTimeFormatter);
                if (ngayKetThuc.isBefore(ngayBatDau)) {
                    JOptionPane.showMessageDialog(dialogThem, "Ngày kết thúc không được nhỏ hơn ngày bắt đầu!");
                    return;
                }
                if (ngayKetThuc.isAfter(ngayBatDau.plusYears(10))) {
                    JOptionPane.showMessageDialog(dialogThem,
                            "Độ dài chu kỳ chuyến cố định chỉ có thể trong vòng 10 năm",
                            "Cảnh báo chu kỳ", JOptionPane.WARNING_MESSAGE);
                    panelThemChuyen.getTxtNgayKetThuc().requestFocusInWindow();
                    return;
                }
            }



            String tuyenID = layMaTuChuoiHienThi(rawTuyen);
            String tauID = layMaTuChuoiHienThi(rawTau);
            NhanVien nv = panelQuanLyChuyen.getNhanVienThucHien();

            int rows = model.getRowCount();
            int cols = model.getColumnCount();
            Object[][] dataSnapshot = new Object[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    dataSnapshot[i][j] = model.getValueAt(i, j);
                }
            }

            panelThemChuyen.getBtnThemChuyen().setEnabled(false);
            panelThemChuyen.getBtnThemChuyen().setText("Đang lưu...");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                private int soLuongChuyenDuLien = 0;
                @Override
                protected String doInBackground() throws Exception {
                    List<Chuyen> dsMoi = new ArrayList<>();
                    List<List<ChuyenGa>> dsLichTrinhMoi = new ArrayList<>();
                    LocalDate ngayChay = ngayBatDau;

                    while (!ngayChay.isAfter(ngayKetThucWorker)) {

                        Chuyen c = buildChuyenObject(ngayChay, sGioDi, tuyenID, tauID, dataSnapshot,chuKy);
                        List<ChuyenGa> stops = buildLichTrinhList(c, dataSnapshot, ngayChay);

                        dsMoi.add(c);
                        dsLichTrinhMoi.add(stops);

                        if (chuKy.equals("Chuyến phát sinh") || !coKetThuc) break;

                        ngayChay = updateNgayTheoChuKy(ngayChay, chuKy);
                    }
                    soLuongChuyenDuLien = dsMoi.size();
                    return chuyenBus.themChuyenBatch(dsMoi, dsLichTrinhMoi, nv);
                }

                @Override
                protected void done() {
                    try {
                        String error = get();
                        if (error == null) {
                            String message = String.format("Đã lưu thành công tất cả các chuyến!\n(Số chuyến được tạo: %d/%d)",
                                    soLuongChuyenDuLien, soLuongChuyenDuLien);
                            JOptionPane.showMessageDialog(panelThemChuyen, message,"Thành công",JOptionPane.INFORMATION_MESSAGE);
                            dialogThem.dispose();
                            timKiemChuyen();
                        } else {
                            JOptionPane.showMessageDialog(dialogThem, "Lỗi: " + error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(dialogThem, "Lỗi hệ thống khi lưu ngầm!", "Lỗi hệ thống", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        panelThemChuyen.getBtnThemChuyen().setEnabled(true);
                        panelThemChuyen.getBtnThemChuyen().setText("Lưu Chuyến Tàu");
                    }
                }
            };
            worker.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialogThem, "Dữ liệu không hợp lệ: " + ex.getMessage());
        }
    }

    /**
     * Hàm phụ trợ: Hiển thị thông báo lỗi và Focus ngay vào Component bị lỗi
     */
    private void baoLoiVaFocus(JComponent component, String message) {
        JOptionPane.showMessageDialog(dialogThem, message, "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);

        if (component instanceof JComboBox) {
            // Đối với ComboBox, ta focus vào phần editor để người dùng gõ được ngay
            Component editor = ((JComboBox<?>) component).getEditor().getEditorComponent();
            editor.requestFocusInWindow();
        } else {
            component.requestFocusInWindow();
        }

        // Nếu là JTextField, bôi đen toàn bộ văn bản để người dùng dễ sửa
        if (component instanceof JTextField) {
            ((JTextField) component).selectAll();
        } else if (component instanceof JComboBox) {
            Component editor = ((JComboBox<?>) component).getEditor().getEditorComponent();
            if (editor instanceof JTextField) {
                ((JTextField) editor).selectAll();
            }
        }
    }

    private void capNhatSTT(){
        DefaultTableModel m = panelThemChuyen.getModelLichTrinh();
        for(int i=0; i<m.getRowCount(); i++){
            m.setValueAt(i+1, i, 0);
        }
    }


    private void setupComboTuyen(JComboBox<String> cbo, List<String> dataFormatted) {
        cbo.setEditable(true);
        cbo.setModel(new DefaultComboBoxModel<>(dataFormatted.toArray(new String[0])));
        cbo.setSelectedIndex(-1);

        JTextField txtEditor = (JTextField) cbo.getEditor().getEditorComponent();
        JPopupMenu pp = new JPopupMenu();
        JList<String> lst = new JList<>();

        Runnable actionNextFocus = txtEditor::transferFocus;

        pp.setFocusable(false);
        lst.setFocusable(false);
        lst.setRequestFocusEnabled(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        txtEditor.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;
            private void update() {
                if (isAdjusting) return;
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(200, ev -> SwingUtilities.invokeLater(() -> {
                    if (txtEditor.isFocusOwner()) {
                        hienThiGoiY(txtEditor, lst, pp, input -> locDuLieu(dataFormatted, input));
                    }
                }));
                timer.setRepeats(false);
                timer.start();
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });
        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
                    chonTuyen(lst.getSelectedValue(), txtEditor, pp, actionNextFocus);
                }
            }
        });

        txtEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (pp.isVisible() && lst.getSelectedValue() != null) {
                        chonTuyen(lst.getSelectedValue(), txtEditor, pp, actionNextFocus);
                    }
                    else {
                        chonTuyen(txtEditor.getText(), txtEditor, pp, actionNextFocus);
                    }
                    e.consume();
                }

                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (pp.isVisible()) {
                        int index = lst.getSelectedIndex();
                        if (index < lst.getModel().getSize() - 1) lst.setSelectedIndex(index + 1);
                        lst.ensureIndexIsVisible(lst.getSelectedIndex());
                        e.consume();
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (pp.isVisible()) {
                        int index = lst.getSelectedIndex();
                        if (index > 0) lst.setSelectedIndex(index - 1);
                        lst.ensureIndexIsVisible(lst.getSelectedIndex());
                        e.consume();
                    }
                }
            }
        });

        txtEditor.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> pp.setVisible(false));
            }
        });
    }

    private void chonTuyen(String value, JTextField txt, JPopupMenu pp, Runnable next) {
        if (value == null || value.isEmpty()) return;

        isAdjusting = true;

        String maTuyen = value;
        if (value.contains("(")) {
            maTuyen = value.substring(0, value.indexOf("(")).trim();
        } else if (value.contains(" ")) {
            maTuyen = value.split(" ")[0].trim();
        }
        maTuyen = maTuyen.trim();
        txt.setText(maTuyen);

        pp.setVisible(false);

        isAdjusting = false;
        if (next != null) {
            SwingUtilities.invokeLater(next);
        }
    }


    private String layMaTuChuoiHienThi(String input) {
        if (input == null) return "";
        if (input.contains("(")) {
            return input.substring(0, input.indexOf("(")).trim();
        }
        return input.trim();
    }

    private String taoHienThiTuyen(String maTuyen, Map<String, String> mapIDToTenGa) {
        if (maTuyen == null || maTuyen.isEmpty()) return "";
        if (maTuyen.contains("-")) {
            String[] parts = maTuyen.split("-");
            if (parts.length >= 2) {
                String maDi = parts[0].trim();
                String maDen = parts[1].trim();
                String tenDi = mapIDToTenGa.getOrDefault(maDi, maDi);
                String tenDen = mapIDToTenGa.getOrDefault(maDen, maDen);
                return maTuyen + " (" + tenDi + " - " + tenDen + ")";
            }
        }
        return maTuyen;
    }

    private void loadLichTrinhMau(String tuyenID, String loaiTau) {
        List<Ga> dsGa = chuyenBus.layDsGaChoLichTrinh(tuyenID, loaiTau);

        DefaultTableModel model = panelThemChuyen.getModelLichTrinh();
        model.setRowCount(0);

        if (dsGa == null || dsGa.isEmpty()) return;

        for (int i = 0; i < dsGa.size() - 1; i++) {
            Ga gaDi = dsGa.get(i);
            Ga gaDen = dsGa.get(i + 1);
            model.addRow(new Object[]{ (i + 1), gaDi.getTenGa(), "", "", gaDen.getTenGa(), "", "" });
        }
    }

    private void tinhTuDongThoiGianDen(int currentRow, String ngayDiStr, String gioDiStr) {
        try {
            String rawTau = getSafeText(panelThemChuyen.getComboTau());
            if (rawTau.isEmpty()) return;
            String tauID = layMaTuChuoiHienThi(rawTau);

            int vanToc = chuyenBus.layTocDoTau(tauID);

            DefaultTableModel model = (DefaultTableModel) panelThemChuyen.getTableLichTrinh().getModel();
            String tenGaDi = model.getValueAt(currentRow, 1).toString();
            String tenGaDen = model.getValueAt(currentRow, 4).toString();
            String idGaDi = mapGaToID.get(tenGaDi);
            String idGaDen = mapGaToID.get(tenGaDen);

            if (idGaDi == null || idGaDen == null) return;

            int khoangCachKm = tuyenBus.getKhoangCachGiuaHaiGa(idGaDi, idGaDen);
            if (khoangCachKm <= 0) return;

            double thoiGianDiChuyenPhut = ((double) khoangCachKm / vanToc) * 60;

            DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dtDi = LocalDateTime.parse(ngayDiStr + " " + gioDiStr, parseFormat);

            LocalDateTime dtDen = dtDi.plusMinutes((long) Math.ceil(thoiGianDiChuyenPhut));

            model.setValueAt(dtDen.format(dateTimeFormatter), currentRow, 5);
            model.setValueAt(dtDen.format(timeFormatter), currentRow, 6);

            if (currentRow < model.getRowCount() - 1) {
                int thoiGianDungDo = 10;
                LocalDateTime dtDiTiep = dtDen.plusMinutes(thoiGianDungDo);

                model.setValueAt(dtDiTiep.format(dateTimeFormatter), currentRow + 1, 2);
                model.setValueAt(dtDiTiep.format(timeFormatter), currentRow + 1, 3);

                tinhTuDongThoiGianDen(currentRow + 1, dtDiTiep.format(dateTimeFormatter), dtDiTiep.format(timeFormatter));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSafeText(JComboBox<String> combo) {
        if (combo == null) return "";
        // Lấy giá trị trực tiếp từ Editor để hỗ trợ cả Autocomplete và chọn từ danh sách
        Object item = combo.getEditor().getItem();
        return (item != null) ? item.toString().trim() : "";
    }

    private Chuyen buildChuyenObject(LocalDate ngayChay, String sGioDi, String tuyenID, String tauID, Object[][] dataSnapshot, String chuKy) {
        LocalTime t = LocalTime.parse(sGioDi, timeFormatter);
        String strNgay = ngayChay.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String strGio = t.format(DateTimeFormatter.ofPattern("HHmm"));

        String hauTo = getHauToTheoChuKy(chuKy);
        String maChuyen = tauID.toUpperCase() + "_" + strNgay + "_" + strGio + "_" + hauTo;

        Chuyen c = new Chuyen(maChuyen);
        c.setTuyen(new Tuyen(tuyenID));
        c.setTau(new Tau(tauID, ""));
        c.setNgayDi(ngayChay);
        c.setGioDi(t);

        // Lấy tên ga từ snapshot dữ liệu (Ga đầu và Ga cuối)
        String tenGaDau = dataSnapshot[0][1].toString();
        String tenGaCuoi = dataSnapshot[dataSnapshot.length - 1][4].toString();

        c.setGaDi(new Ga(mapGaToID.get(tenGaDau), tenGaDau));
        c.setGaDen(new Ga(mapGaToID.get(tenGaCuoi), tenGaCuoi));
        c.setTenChuyenHienThi(tenGaDau + " - " + tenGaCuoi);

        return c;
    }

    private List<ChuyenGa> buildLichTrinhList(Chuyen c, Object[][] dataSnapshot, LocalDate ngayDiChuyen) {
        List<ChuyenGa> listStops = new ArrayList<>();

        // Tính độ lệch ngày so với lịch trình gốc trên bảng giao diện
        LocalDate ngayGocTrongBang = LocalDate.parse(dataSnapshot[0][2].toString(), dateTimeFormatter);
        long dayOffset = java.time.temporal.ChronoUnit.DAYS.between(ngayGocTrongBang, ngayDiChuyen);

        // 1. Ga xuất phát (STT 1)
        ChuyenGa startNode = new ChuyenGa();
        startNode.setChuyen(c);
        startNode.setGa(c.getGaDi());
        startNode.setThuTu(1);
        startNode.setNgayDi(ngayDiChuyen);
        startNode.setGioDi(c.getGioDi());
        listStops.add(startNode);

        // 2. Các chặng tiếp theo dựa trên snapshot dữ liệu bảng
        for (int i = 0; i < dataSnapshot.length; i++) {
            String tenGaDen = dataSnapshot[i][4].toString();
            String idGaDen = mapGaToID.get(tenGaDen);
            if (idGaDen == null) continue;

            ChuyenGa stopNode = new ChuyenGa();
            stopNode.setChuyen(c);
            stopNode.setGa(new Ga(idGaDen, tenGaDen));
            stopNode.setThuTu(i + 2);

            // Cộng dồn độ lệch ngày cho thời gian Đến và Đi
            stopNode.setNgayDen(LocalDate.parse(dataSnapshot[i][5].toString(), dateTimeFormatter).plusDays(dayOffset));
            stopNode.setGioDen(LocalTime.parse(dataSnapshot[i][6].toString(), timeFormatter));

            if (i < dataSnapshot.length - 1) {
                stopNode.setNgayDi(LocalDate.parse(dataSnapshot[i + 1][2].toString(), dateTimeFormatter).plusDays(dayOffset));
                stopNode.setGioDi(LocalTime.parse(dataSnapshot[i + 1][3].toString(), timeFormatter));
            }
            listStops.add(stopNode);
        }
        return listStops;
    }

    private LocalDate updateNgayTheoChuKy(LocalDate current, String chuKy) {
        return switch (chuKy) {
            case "Hàng ngày" -> current.plusDays(1);
            case "Hàng tuần" -> current.plusWeeks(1);
            case "Hàng tháng" -> current.plusMonths(1);
            case "Hàng năm" -> current.plusYears(1);
            default -> current.plusDays(1);
        };
    }

    private boolean isTauKhongHopLe(String rawTau) {
        if (rawTau == null || rawTau.isEmpty()) return true;

        String descNgung = TrangThaiTau.KHONG_HOAT_DONG.getDescription();
        String descBaoTri = TrangThaiTau.BAO_TRI.getDescription();

        return rawTau.contains(descNgung) || rawTau.contains(descBaoTri);
    }

    private String getRawTauText() {
        Object editorItem = panelThemChuyen.getComboTau()
                .getEditor()
                .getItem();
        return editorItem == null ? "" : editorItem.toString().trim();
    }


    private boolean kiemTraTrangThaiTauHopLe(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) return false;

        String tauID = layMaTuChuoiHienThi(rawText);

        TrangThaiTau status = chuyenBus.layTrangThaiTauTheoID(tauID);

        if (status == null) {
            JOptionPane.showMessageDialog(dialogThem,
                    "Mã tàu [" + tauID + "] không tồn tại trong hệ thống!",
                    "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (status != TrangThaiTau.HOAT_DONG) {
            String moTa = (status == TrangThaiTau.BAO_TRI) ? "đang bảo trì" : "ngừng hoạt động";
            JOptionPane.showMessageDialog(dialogThem,
                    "Tàu " + tauID + " hiện " + moTa + ", không thể thêm chuyến!",
                    "Ràng buộc tàu", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void thucThiCapNhatChuKyBatch() {
        // 1. Thu thập dữ liệu từ giao diện
        String chuKy = panelCapNhatChuyen.getComboChuKy().getSelectedItem().toString();
        String sNgayKT = panelCapNhatChuyen.getTxtNgayKetThuc().getText().trim();
        String sNgayDi = panelCapNhatChuyen.getTxtNgayDi().getText().trim();
        String sGioDi = panelCapNhatChuyen.getTxtGioDi().getText().trim();

        // Validate ngày kết thúc
        if (sNgayKT.isEmpty()) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Vui lòng chọn ngày kết thúc chu kỳ!", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate ngayBatDau = LocalDate.parse(sNgayDi, dateTimeFormatter);
        LocalDate ngayKetThuc = LocalDate.parse(sNgayKT, dateTimeFormatter);
        LocalDate ngayHienTai = LocalDate.now();

        if (ngayBatDau.isBefore(ngayHienTai)) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Ngày đi không được trước ngày hiện tại!");
            return;
        }

        if (ngayKetThuc.isAfter(ngayBatDau.plusYears(10))) {
            JOptionPane.showMessageDialog(dialogCapNhat,
                    "Độ dài chu kỳ chuyến cố định chỉ có thể trong vòng 10 năm",
                    "Lỗi chu kỳ", JOptionPane.ERROR_MESSAGE);
            panelCapNhatChuyen.getTxtNgayKetThuc().requestFocusInWindow();
            return;
        }

        if (ngayKetThuc.isBefore(ngayBatDau)) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Ngày kết thúc không được nhỏ hơn ngày bắt đầu!", "Lỗi logic", JOptionPane.ERROR_MESSAGE);
            return;
        }


        // Lấy thông tin ID
        String tuyenID = layMaTuChuoiHienThi(panelCapNhatChuyen.getComboTuyen().getEditor().getItem().toString());
        String tauID = layMaTuChuoiHienThi(panelCapNhatChuyen.getComboTau().getEditor().getItem().toString());
        NhanVien nv = panelQuanLyChuyen.getNhanVienThucHien();

        // 2. Chụp Snapshot dữ liệu bảng lịch trình (Bản ghi ga trung gian)
        DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();
        int rows = model.getRowCount();
        int cols = model.getColumnCount();
        Object[][] dataSnapshot = new Object[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                dataSnapshot[i][j] = model.getValueAt(i, j);
            }
        }

        // 3. Thực thi luồng ngầm
        panelCapNhatChuyen.getBtnCapNhatChuyen().setEnabled(false);
        panelCapNhatChuyen.getBtnCapNhatChuyen().setText("Đang cập nhật...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            private int soLuongThanhCong = 0;

            @Override
            protected String doInBackground() throws Exception {
                List<Chuyen> dsMoi = new ArrayList<>();
                List<List<ChuyenGa>> dsLichTrinhMoi = new ArrayList<>();
                LocalDate ngayChay = ngayBatDau;

                // Lặp để tạo danh sách chuyến theo chu kỳ
                while (!ngayChay.isAfter(ngayKetThuc)) {
                    // Tạo đối tượng Chuyến (Sử dụng hàm buildChuyenObject 6 tham số đã sửa trước đó)
                    Chuyen c = buildChuyenObject(ngayChay, sGioDi, tuyenID, tauID, dataSnapshot, chuKy);

                    // Tạo danh sách lịch trình ga (ChuyenGa) tương ứng
                    List<ChuyenGa> stops = buildLichTrinhList(c, dataSnapshot, ngayChay);

                    dsMoi.add(c);
                    dsLichTrinhMoi.add(stops);

                    // Tăng ngày theo chu kỳ (Hàng ngày, Hàng tuần...)
                    ngayChay = updateNgayTheoChuKy(ngayChay, chuKy);
                }

                soLuongThanhCong = dsMoi.size();
                // Gọi BUS xử lý: Xóa các chuyến cũ trong khoảng thời gian này và Insert lại
                return chuyenBus.capNhatChuyenBatch(dsMoi, dsLichTrinhMoi, nv);
            }

            @Override
            protected void done() {
                try {
                    String error = get();
                    if (error == null) {
                        String msg = String.format("Cập nhật chu kỳ thành công!\n(Đã đồng bộ %d chuyến trong hệ thống)", soLuongThanhCong);
                        JOptionPane.showMessageDialog(panelCapNhatChuyen, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dialogCapNhat.dispose();
                        timKiemChuyen();
                    } else {
                        JOptionPane.showMessageDialog(dialogCapNhat, "Lỗi cập nhật: " + error, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(dialogCapNhat, "Lỗi hệ thống: " + e.getMessage());
                } finally {
                    panelCapNhatChuyen.getBtnCapNhatChuyen().setEnabled(true);
                    panelCapNhatChuyen.getBtnCapNhatChuyen().setText("Lưu Chuyến Tàu");
                }
            }
        };
        worker.execute();
    }

    private void thucThiCapNhatDonLe() {
        // 1. Thu thập dữ liệu cơ bản
        String maChuyen = panelCapNhatChuyen.getTxtMaChuyen().getText().trim();
        String sNgayDi = panelCapNhatChuyen.getTxtNgayDi().getText().trim();
        String sGioDi = panelCapNhatChuyen.getTxtGioDi().getText().trim();
        String tuyenID = layMaTuChuoiHienThi(panelCapNhatChuyen.getComboTuyen().getEditor().getItem().toString());
        String tauID = layMaTuChuoiHienThi(panelCapNhatChuyen.getComboTau().getEditor().getItem().toString());
        NhanVien nv = panelQuanLyChuyen.getNhanVienThucHien();

        // 2. Chụp Snapshot bảng lịch trình hiện tại trên Form
        DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();
        Object[][] dataSnapshot = new Object[model.getRowCount()][model.getColumnCount()];
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                dataSnapshot[i][j] = model.getValueAt(i, j);
            }
        }

        // 3. Thực thi cập nhật ngầm
        panelCapNhatChuyen.getBtnCapNhatChuyen().setEnabled(false);
        panelCapNhatChuyen.getBtnCapNhatChuyen().setText("Đang lưu...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            private int soLuongThanhCong = 0;
            @Override
            protected String doInBackground() throws Exception {
                LocalDate ngayDi = LocalDate.parse(sNgayDi, dateTimeFormatter);
                Chuyen c = buildChuyenObject(ngayDi, sGioDi, tuyenID, tauID, dataSnapshot, "Chuyến phát sinh");

                List<ChuyenGa> stops = buildLichTrinhList(c, dataSnapshot, ngayDi);

                List<Chuyen> dsMoi = Collections.singletonList(c);
                List<List<ChuyenGa>> dsLT = Collections.singletonList(stops);

                String res = chuyenBus.capNhatChuyenBatch(dsMoi, dsLT, nv);
                if (res == null) soLuongThanhCong = 1;
                return res;
            }

            @Override
            protected void done() {
                try {
                    String error = get();
                    if (error == null) {
                        String msg = String.format("Cập nhật chuyến phát sinh thành công!\n(Số lượng: %d chuyến)", soLuongThanhCong);
                        JOptionPane.showMessageDialog(panelCapNhatChuyen, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dialogCapNhat.dispose();
                        timKiemChuyen();
                    } else {
                        JOptionPane.showMessageDialog(dialogCapNhat, "Lỗi: " + error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    panelCapNhatChuyen.getBtnCapNhatChuyen().setEnabled(true);
                    panelCapNhatChuyen.getBtnCapNhatChuyen().setText("Lưu Chuyến Tàu");
                }
            }
        };
        worker.execute();
    }

    private String getHauToTheoChuKy(String chuKy) {
        if (chuKy == null) return "PS";
        return switch (chuKy) {
            case "Hàng ngày" -> "CKHN";
            case "Hàng tuần" -> "CKHT";
            case "Hàng tháng" -> "CKTH";
            case "Hàng năm" -> "CKHN";
            default -> "PS"; // Bao gồm cả "Chuyến phát sinh"
        };
    }
}


