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
        phanQuyenBus.phanQuyenQuanLyChuyen(panelQuanLyChuyen, vaiTroNhanVien);

        loadDataToTable(chuyenBus.layDanhSachChuyen());
        initEvents();
        thietLapAutoComplete();

        String homNay = LocalDate.now().format(dateTimeFormatter);
        panelQuanLyChuyen.getTxtNgayDi().setText(homNay);

        timKiemChuyen();
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
        panelQuanLyChuyen.getTableChuyen().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = panelQuanLyChuyen.getTableChuyen().getSelectedRow();
                if(row>=0){
                    String maChuyen = panelQuanLyChuyen.getTableChuyen().getValueAt(row,0).toString();
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

        Runnable actionTimkiem = this::timKiemChuyen;
        taoPopupGoiY(panelQuanLyChuyen.getTxtMaChuyen(), panelQuanLyChuyen.getPpMaChuyen(),
                panelQuanLyChuyen.getListMaChuyen(),
                input -> locDuLieu(dataMaChuyen, input),
                actionTimkiem);

        taoPopupGoiY(panelQuanLyChuyen.getTxtGaXuatPhat(), panelQuanLyChuyen.getPpGaDi(),
                panelQuanLyChuyen.getListGaDi(),
                input -> locDuLieu(dataTenGa, input),
                actionTimkiem);

        taoPopupGoiY(panelQuanLyChuyen.getTxtGaDich(), panelQuanLyChuyen.getPpGaDen(),
                panelQuanLyChuyen.getListGaDen(),
                input -> locDuLieu(dataTenGa, input),
                actionTimkiem);

        taoPopupGoiY(panelQuanLyChuyen.getTxtTau(), panelQuanLyChuyen.getPpTau(),
                panelQuanLyChuyen.getListTau(),
                input -> locDuLieu(dataTenTau, input),
                actionTimkiem);

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

        if (panelThemChuyen.getComboTuyen().getEditor().getEditorComponent() instanceof JTextField) {
            panelThemChuyen.getComboTuyen().addActionListener(e -> {
                Object selected = panelThemChuyen.getComboTuyen().getSelectedItem();
                if (selected == null) return;
                String rawValue = selected.toString().trim();
                if (rawValue.isEmpty()) return;

                String maTuyen = layMaTuChuoiHienThi(rawValue);
                setComboText(panelThemChuyen.getComboTuyen(), maTuyen);

                panelThemChuyen.getModelLichTrinh().setRowCount(0);

                triggerLoadTableIfReady();
            });
        }

        panelThemChuyen.getComboTau().addActionListener(e -> {
            triggerLoadTableIfReady();
        });

        Component editorTau = panelThemChuyen.getComboTau().getEditor().getEditorComponent();
        if (editorTau instanceof JTextField) {
            ((JTextField) editorTau).getDocument().addDocumentListener(triggerCalc);

            editorTau.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    triggerLoadTableIfReady();
                }
            });
        }

        panelThemChuyen.getBtnThemChuyen().addActionListener(e -> xuLyLuuChuyen());
    }

    private void triggerLoadTableIfReady() {
        String rawTuyen = (String) panelThemChuyen.getComboTuyen().getSelectedItem();
        String rawTau = (String) panelThemChuyen.getComboTau().getSelectedItem();

        if (rawTuyen != null && !rawTuyen.isEmpty() && rawTau != null && !rawTau.isEmpty()) {
            String maTuyen = layMaTuChuoiHienThi(rawTuyen);

            String loaiTau = "TAU_DU_LICH";
            if (rawTau.contains("(") && rawTau.contains(")")) {
                loaiTau = rawTau.substring(rawTau.indexOf("(") + 1, rawTau.indexOf(")"));
            } else {
                loaiTau = "TAU_NHANH";
            }

            loadLichTrinhMau(maTuyen, loaiTau);
            khaoSatVaTinhToan();
        }
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

            if (!rawTau.isEmpty() && !ngayDiStr.isEmpty() && !gioDiStr.isEmpty()) {
                String tauID = layMaTuChuoiHienThi(rawTau);

                LocalDate d = LocalDate.parse(ngayDiStr, dateTimeFormatter);
                String strNgay = d.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                LocalTime t = LocalTime.parse(gioDiStr, timeFormatter);
                String strGio = t.format(DateTimeFormatter.ofPattern("HHmm"));

                String maChuyenMoi = tauID.toUpperCase() + "_" + strNgay + "_" + strGio;
                panelThemChuyen.getTxtMaChuyen().setText(maChuyenMoi);
            }
        } catch (Exception ex) {
        }
    }

    private void xuLyLuuChuyen() {
        try {
            String rawTuyen = (String) panelThemChuyen.getComboTuyen().getSelectedItem();
            String rawTau = (String) panelThemChuyen.getComboTau().getSelectedItem();
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
            }

            String tuyenID = layMaTuChuoiHienThi(rawTuyen);
            String tauID = layMaTuChuoiHienThi(rawTau);
            NhanVien nv = panelQuanLyChuyen.getNhanVienThucHien();

            if (chuKy.equals("Chuyến phát sinh") || !coKetThuc) {
                if (thucThiLuuMotChuyen(ngayBatDau, sGioDi, tuyenID, tauID, model, nv)) {
                    JOptionPane.showMessageDialog(panelThemChuyen, "Thêm chuyến phát sinh thành công!");
                    dialogThem.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialogThem, "Thêm chuyến thất bại (vui lòng kiểm tra mã chuyến)!");
                }
            } else {
                LocalDate ngayChayHienTai = ngayBatDau;
                int countSuccess = 0;
                int countTotal = 0;

                while (!ngayChayHienTai.isAfter(ngayKetThuc)) {
                    countTotal++;
                    if (thucThiLuuMotChuyen(ngayChayHienTai, sGioDi, tuyenID, tauID, model, nv)) {
                        countSuccess++;
                    }

                    ngayChayHienTai = switch (chuKy) {
                        case "Hàng ngày" -> ngayChayHienTai.plusDays(1);
                        case "Hàng tuần" -> ngayChayHienTai.plusWeeks(1);
                        case "Hàng tháng" -> ngayChayHienTai.plusMonths(1);
                        case "Hàng năm" -> ngayChayHienTai.plusYears(1);
                        default -> ngayKetThuc.plusDays(1); // Để thoát vòng lặp
                    };
                }

                String thongBao = String.format("Hoàn tất chu kỳ! Thành công: %d/%d chuyến.", countSuccess, countTotal);
                JOptionPane.showMessageDialog(panelThemChuyen, thongBao);
                dialogThem.dispose();
            }

            timKiemChuyen();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialogThem, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private boolean thucThiLuuMotChuyen(LocalDate ngayDiChuyen, String sGioDi, String tuyenID, String tauID, DefaultTableModel model, NhanVien nv) {
        try {

            LocalTime t = LocalTime.parse(sGioDi, timeFormatter);
            String maChuyen = tauID.toUpperCase() + "_" + ngayDiChuyen.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "_" + t.format(DateTimeFormatter.ofPattern("HHmm"));

            Chuyen c = new Chuyen(maChuyen);
            c.setTuyen(new Tuyen(tuyenID));
            c.setTau(new Tau(tauID, ""));
            c.setNgayDi(ngayDiChuyen);
            c.setGioDi(t);

            String tenGaDau = model.getValueAt(0, 1).toString();
            String tenGaCuoi = model.getValueAt(model.getRowCount() - 1, 4).toString();
            String idGaDau = mapGaToID.get(tenGaDau);
            String idGaCuoi = mapGaToID.get(tenGaCuoi);

            c.setGaDi(new Ga(idGaDau, tenGaDau));
            c.setGaDen(new Ga(idGaCuoi, tenGaCuoi));
            c.setTenChuyenHienThi(tenGaDau + " - " + tenGaCuoi);


            LocalDate ngayGocTrongBang = LocalDate.parse(model.getValueAt(0, 2).toString(), dateTimeFormatter);
            long dayOffset = java.time.temporal.ChronoUnit.DAYS.between(ngayGocTrongBang, ngayDiChuyen);

            List<ChuyenGa> listStops = new ArrayList<>();

            ChuyenGa startNode = new ChuyenGa();
            startNode.setChuyen(c);
            startNode.setGa(new Ga(idGaDau, tenGaDau));
            startNode.setThuTu(1);
            startNode.setNgayDi(ngayDiChuyen);
            startNode.setGioDi(t);
            listStops.add(startNode);

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenGaDen = model.getValueAt(i, 4).toString();
                String idGaDen = mapGaToID.get(tenGaDen);
                if (idGaDen == null) continue;

                ChuyenGa stopNode = new ChuyenGa();
                stopNode.setChuyen(c);
                stopNode.setGa(new Ga(idGaDen, tenGaDen));
                stopNode.setThuTu(i + 2);
                stopNode.setNgayDen(LocalDate.parse(model.getValueAt(i, 5).toString(), dateTimeFormatter).plusDays(dayOffset));
                stopNode.setGioDen(LocalTime.parse(model.getValueAt(i, 6).toString(), timeFormatter));

                if (i < model.getRowCount() - 1) {
                    stopNode.setNgayDi(LocalDate.parse(model.getValueAt(i + 1, 2).toString(), dateTimeFormatter).plusDays(dayOffset));
                    stopNode.setGioDi(LocalTime.parse(model.getValueAt(i + 1, 3).toString(), timeFormatter));
                }
                listStops.add(stopNode);
            }

            String error = chuyenBus.themChuyen(c, listStops, nv);
            return error == null;
        } catch (Exception e) {
            return false;
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

    private void fillDataFromTableToInput_Them(int row) {
        DefaultTableModel model = panelThemChuyen.getModelLichTrinh();

        if (row > 0) {

            String prevNgayDen = model.getValueAt(row - 1, 5).toString();
            String prevGioDen = model.getValueAt(row - 1, 6).toString();

            if (prevNgayDen.isEmpty() || prevGioDen.isEmpty()) {
                JOptionPane.showMessageDialog(dialogThem,
                        "Vui lòng nhập hoàn thiện thông tin cho chặng trước đó (" +
                                model.getValueAt(row-1, 1) + " - " + model.getValueAt(row-1, 4) + ")!",
                        "Cảnh báo thứ tự", JOptionPane.WARNING_MESSAGE);

                panelThemChuyen.getTableLichTrinh().setRowSelectionInterval(row - 1, row - 1);
                fillDataFromTableToInput_Them(row - 1);
                return;
            }
        }

    }


    private void xuLyLuuGioVaoBang(JTable table, JTextField txtNgayDi, JTextField txtGioDi,
                                   JTextField txtNgayDen, JTextField txtGioDen,
                                   String headerNgayDi, String headerGioDi, Component parentComponent) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(parentComponent, "Vui lòng chọn chặng cần nhập giờ!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String sNgayDi = txtNgayDi.getText().trim();
        String sGioDi = txtGioDi.getText().trim();
        String sNgayDen = txtNgayDen.getText().trim();
        String sGioDen = txtGioDen.getText().trim();

        if (sNgayDi.isEmpty() || sGioDi.isEmpty() || sNgayDen.isEmpty() || sGioDen.isEmpty()) {
            JOptionPane.showMessageDialog(parentComponent, "Vui lòng nhập đầy đủ ngày và giờ!");
            return;
        }

        if (!PanelQuanLyChuyen.Validator.isValidNgay(sNgayDi) || !PanelQuanLyChuyen.Validator.isValidGio(sGioDi) ||
                !PanelQuanLyChuyen.Validator.isValidNgay(sNgayDen) || !PanelQuanLyChuyen.Validator.isValidGio(sGioDen)) {
            JOptionPane.showMessageDialog(parentComponent, "Định dạng ngày (dd/MM/yyyy) hoặc giờ (HH:mm) không hợp lệ!");
            return;
        }

        if (row == 0) {
            if (!sNgayDi.equals(headerNgayDi) || !sGioDi.equals(headerGioDi)) {
                JOptionPane.showMessageDialog(parentComponent,
                        "Ngày đi và Giờ đi của chặng đầu tiên phải trùng khớp với thông tin chung của chuyến!\n" +
                                "Thông tin chung: " + headerNgayDi + " " + headerGioDi,
                        "Lỗi Logic", JOptionPane.ERROR_MESSAGE);

                txtNgayDi.setText(headerNgayDi);
                txtGioDi.setText(headerGioDi);
                return;
            }
        }

        try {
            java.time.LocalDateTime dtDi = java.time.LocalDateTime.parse(
                    sNgayDi + " " + sGioDi, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            java.time.LocalDateTime dtDen = java.time.LocalDateTime.parse(
                    sNgayDen + " " + sGioDen, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            if (!dtDen.isAfter(dtDi)) {
                JOptionPane.showMessageDialog(parentComponent, "Thời gian Đến phải sau thời gian Đi!", "Lỗi Logic", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (row > 0) {
                String sPrevNgayDen = model.getValueAt(row - 1, 5).toString();
                String sPrevGioDen = model.getValueAt(row - 1, 6).toString();

                if (!sPrevNgayDen.isEmpty() && !sPrevGioDen.isEmpty()) {
                    java.time.LocalDateTime dtPrevDen = java.time.LocalDateTime.parse(
                            sPrevNgayDen + " " + sPrevGioDen, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                    if (dtDi.isBefore(dtPrevDen)) {
                        JOptionPane.showMessageDialog(parentComponent,
                                "Tàu chưa tới ga này! Thời gian Đi phải sau khi tàu Đến ga trước (" + sPrevGioDen + " " + sPrevNgayDen + ")",
                                "Lỗi Logic", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            model.setValueAt(sNgayDi, row, 2);
            model.setValueAt(sGioDi, row, 3);
            if (sNgayDen.isEmpty() || sGioDen.isEmpty()) {
                tinhTuDongThoiGianDen(row, sNgayDi, sGioDi);

                sNgayDen = model.getValueAt(row, 5).toString();
                sGioDen = model.getValueAt(row, 6).toString();
            } else {
                model.setValueAt(sNgayDen, row, 5);
                model.setValueAt(sGioDen, row, 6);


                if (row < model.getRowCount() - 1) {
                    int thoiGianDungDo = 15; // 15 phút
                    java.time.LocalDateTime dtDenn = java.time.LocalDateTime.parse(
                            sNgayDen + " " + sGioDen, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    java.time.LocalDateTime dtDiTiep = dtDenn.plusMinutes(thoiGianDungDo);

                    String nextNgay = dtDiTiep.format(dateTimeFormatter);
                    String nextGio = dtDiTiep.format(timeFormatter);

                    model.setValueAt(nextNgay, row + 1, 2);
                    model.setValueAt(nextGio, row + 1, 3);

                    tinhTuDongThoiGianDen(row + 1, nextNgay, nextGio);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void tinhTuDongThoiGianDen(int currentRow, String ngayDiStr, String gioDiStr) {
        try {
            String rawTau = (String) panelThemChuyen.getComboTau().getSelectedItem();
            if (rawTau == null || rawTau.isEmpty()) return;
            String tauID = layMaTuChuoiHienThi(rawTau);
            int vanToc = chuyenBus.layTocDoTau(tauID);

            DefaultTableModel model = panelThemChuyen.getModelLichTrinh();
            String tenGaDi = model.getValueAt(currentRow, 1).toString();
            String tenGaDen = model.getValueAt(currentRow, 4).toString();
            String idGaDi = mapGaToID.get(tenGaDi);
            String idGaDen = mapGaToID.get(tenGaDen);

            if (idGaDi == null || idGaDen == null) return;

            int khoangCachKm = tuyenBus.getKhoangCachGiuaHaiGa(idGaDi, idGaDen);
            if (khoangCachKm <= 0) return;

            double thoiGianDiChuyenPhut = ((double) khoangCachKm / vanToc) * 40;

            DateTimeFormatter parseFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String chuoiThoiGian = ngayDiStr + " " + gioDiStr;
            LocalDateTime dtDi = LocalDateTime.parse(chuoiThoiGian,parseFormat);

            LocalDateTime dtDen = dtDi.plusMinutes((long) Math.ceil(thoiGianDiChuyenPhut));

            String ngayDenMoi = dtDen.format(dateTimeFormatter);
            String gioDenMoi = dtDen.format(timeFormatter);

            model.setValueAt(ngayDenMoi, currentRow, 5);
            model.setValueAt(gioDenMoi, currentRow, 6);

            if (currentRow < model.getRowCount() - 1) {
                int thoiGianDungDo = 15;
                java.time.LocalDateTime dtDiTiep = dtDen.plusMinutes(thoiGianDungDo);

                String ngayDiTiep = dtDiTiep.format(dateTimeFormatter);
                String gioDiTiep = dtDiTiep.format(timeFormatter);

                model.setValueAt(ngayDiTiep, currentRow + 1, 2);
                model.setValueAt(gioDiTiep, currentRow + 1, 3);

                tinhTuDongThoiGianDen(currentRow + 1, ngayDiTiep, gioDiTiep);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


