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
import bus.Tuyen_BUS;
import entity.*;
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

        fillDataToUpdateForm(maChuyen);

        dialogCapNhat.setVisible(true);
    }


    private void fillDataToUpdateForm(String maChuyen) {
        Chuyen c = chuyenBus.layChuyenTheoMa(maChuyen);
        List<ChuyenGa> lichTrinh = chuyenBus.layChiTietHanhTrinh(maChuyen);

        if (c == null) return;

        panelCapNhatChuyen.getTxtMaChuyen().setText(c.getChuyenID());
        setComboText(panelCapNhatChuyen.getComboTuyen(), c.getTuyen().getTuyenID());
        setComboText(panelCapNhatChuyen.getComboTau(), c.getTau().getTauID());

        if (c.getNgayDi() != null)
            panelCapNhatChuyen.getTxtNgayDi().setText(c.getNgayDi().format(dateTimeFormatter));
        if (c.getGioDi() != null)
            panelCapNhatChuyen.getTxtGioDi().setText(c.getGioDi().format(timeFormatter));

        DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();
        model.setRowCount(0);

        if (lichTrinh != null && lichTrinh.size() > 1) {
            for (int i = 0; i < lichTrinh.size() - 1; i++) {
                ChuyenGa gaDi = lichTrinh.get(i);
                ChuyenGa gaDen = lichTrinh.get(i + 1);

                String ngayDiStr = (gaDi.getNgayDi() != null) ? gaDi.getNgayDi().format(dateTimeFormatter) : "";
                String gioDiStr = (gaDi.getGioDi() != null) ? gaDi.getGioDi().format(timeFormatter) : "";

                String ngayDenStr = (gaDen.getNgayDen() != null) ? gaDen.getNgayDen().format(dateTimeFormatter) : "";
                String gioDenStr = (gaDen.getGioDen() != null) ? gaDen.getGioDen().format(timeFormatter) : "";

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
        List<String> dsGa = chuyenBus.getListTenGa();
        List<String> dsTau = chuyenBus.getAllTauID();
        List<String> dsTuyen = chuyenBus.getAllTuyenID();

        setupCombo(panelCapNhatChuyen.getComboTuyen(), dsTuyen);
        setupCombo(panelCapNhatChuyen.getComboTau(), dsTau);
    }

    private void initCapNhatEvents() {
        panelCapNhatChuyen.getTableLichTrinh().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = panelCapNhatChuyen.getTableLichTrinh().getSelectedRow();
                if (row >= 0) {
                    fillDataFromTableToInput_CapNhat(row);
                    panelCapNhatChuyen.getBtnCapNhatChang().setEnabled(true);
                }
            }
        });


        panelCapNhatChuyen.getBtnCapNhatChang().setEnabled(true);
        panelCapNhatChuyen.getBtnCapNhatChang().setText("Cập Nhật Giờ");

        for(ActionListener al : panelCapNhatChuyen.getBtnCapNhatChang().getActionListeners())
            panelCapNhatChuyen.getBtnCapNhatChang().removeActionListener(al);
        panelCapNhatChuyen.getBtnCapNhatChang().addActionListener(e -> capNhatChangTrongBang());

        panelCapNhatChuyen.getTxtGaDiMoi().setEditable(false);
        panelCapNhatChuyen.getTxtGaDenMoi().setEditable(false);

        panelCapNhatChuyen.getBtnCapNhatChuyen().addActionListener(e -> xuLyLuuCapNhat());
    }


    private void capNhatChangTrongBang() {
        String headerNgayDi = panelCapNhatChuyen.getTxtNgayDi().getText().trim();
        String headerGioDi = panelCapNhatChuyen.getTxtGioDi().getText().trim();

        xuLyLuuGioVaoBang(
                panelCapNhatChuyen.getTableLichTrinh(),
                panelCapNhatChuyen.getTxtNgayDiMoi(),
                panelCapNhatChuyen.getTxtGioDiMoi(),
                panelCapNhatChuyen.getTxtNgayDenMoi(),
                panelCapNhatChuyen.getTxtGioDenMoi(),
                headerNgayDi,
                headerGioDi,
                dialogCapNhat
        );


        int row = panelCapNhatChuyen.getTableLichTrinh().getSelectedRow();
        if (row >= 0) {
            fillDataFromTableToInput_CapNhat(row);
        }
    }

    private void fillDataFromTableToInput_CapNhat(int row) {
        DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();

        if (row > 0) {
            String prevNgayDen = model.getValueAt(row - 1, 5).toString();
            String prevGioDen = model.getValueAt(row - 1, 6).toString();

            if (prevNgayDen.isEmpty() || prevGioDen.isEmpty()) {
                JOptionPane.showMessageDialog(dialogCapNhat,
                        "Vui lòng nhập hoàn thiện thông tin cho chặng trước (" +
                                model.getValueAt(row-1, 1) + " - " + model.getValueAt(row-1, 4) + ")!",
                        "Cảnh báo thứ tự", JOptionPane.WARNING_MESSAGE);

                panelCapNhatChuyen.getTableLichTrinh().setRowSelectionInterval(row - 1, row - 1);
                fillDataFromTableToInput_CapNhat(row - 1);
                return;
            }
        }
        panelCapNhatChuyen.getTxtGaDiMoi().setText(model.getValueAt(row, 1).toString());
        panelCapNhatChuyen.getTxtNgayDiMoi().setText(model.getValueAt(row, 2).toString());
        panelCapNhatChuyen.getTxtGioDiMoi().setText(model.getValueAt(row, 3).toString());
        panelCapNhatChuyen.getTxtGaDenMoi().setText(model.getValueAt(row, 4).toString());
        panelCapNhatChuyen.getTxtNgayDenMoi().setText(model.getValueAt(row, 5).toString());
        panelCapNhatChuyen.getTxtGioDenMoi().setText(model.getValueAt(row, 6).toString());
    }

    private void clearInputLichTrinh_CapNhat() {
        panelCapNhatChuyen.getTxtGaDiMoi().setText("");
        panelCapNhatChuyen.getTxtGaDenMoi().setText("");
        panelCapNhatChuyen.getTxtNgayDiMoi().setText("");
        panelCapNhatChuyen.getTxtGioDiMoi().setText("");
        panelCapNhatChuyen.getTxtNgayDenMoi().setText("");
        panelCapNhatChuyen.getTxtGioDenMoi().setText("");
    }

    private void reIndexTable(DefaultTableModel model) {
        for(int i=0; i<model.getRowCount(); i++) {
            model.setValueAt(i+1, i, 0);
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
            String tuyenID = ((JTextField)panelCapNhatChuyen.getComboTuyen().getEditor().getEditorComponent()).getText();
            String tauID = ((JTextField)panelCapNhatChuyen.getComboTau().getEditor().getEditorComponent()).getText();

            LocalDate ngayDi = LocalDate.parse(panelCapNhatChuyen.getTxtNgayDi().getText(), dateTimeFormatter);

            String gioDiStr = panelCapNhatChuyen.getTxtGioDi().getText();
            if(gioDiStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialogCapNhat, "Vui lòng nhập giờ đi!");
                return;
            }
            LocalTime gioDi = LocalTime.parse(gioDiStr, timeFormatter);

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
                JOptionPane.showMessageDialog(dialogCapNhat, "Tên ga không tồn tại!");
                return;
            }
            c.setGaDi(new Ga(idGaDau, tenGaDau));
            c.setGaDen(new Ga(idGaCuoi, tenGaCuoi));

            List<ChuyenGa> listStops = new ArrayList<>();

            ChuyenGa startNode = new ChuyenGa();
            startNode.setChuyen(c);
            startNode.setGa(new Ga(idGaDau, tenGaDau));
            startNode.setThuTu(1);
            String startNgayDiStr = model.getValueAt(0, 2).toString();
            String startGioDiStr = model.getValueAt(0, 3).toString();
            startNode.setNgayDi(LocalDate.parse(startNgayDiStr, dateTimeFormatter));
            startNode.setGioDi(LocalTime.parse(startGioDiStr, timeFormatter));
            startNode.setGioDen(null); startNode.setNgayDen(null);
            listStops.add(startNode);

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenGaDen = model.getValueAt(i, 4).toString();
                String idGaDen = mapGaToID.get(tenGaDen);
                if(idGaDen == null) continue;

                String ngayDenStr = model.getValueAt(i, 5).toString();
                String gioDenStr = model.getValueAt(i, 6).toString();

                ChuyenGa stopNode = new ChuyenGa();
                stopNode.setChuyen(c);
                stopNode.setGa(new Ga(idGaDen, tenGaDen));
                stopNode.setThuTu(i + 2);
                stopNode.setGioDen(LocalTime.parse(gioDenStr, timeFormatter));
                stopNode.setNgayDen(LocalDate.parse(ngayDenStr, dateTimeFormatter));

                if (i < model.getRowCount() - 1) {
                    String nextNgayDi = model.getValueAt(i + 1, 2).toString();
                    String nextGioDi = model.getValueAt(i + 1, 3).toString();
                    stopNode.setNgayDi(LocalDate.parse(nextNgayDi, dateTimeFormatter));
                    stopNode.setGioDi(LocalTime.parse(nextGioDi, timeFormatter));
                } else {
                    stopNode.setNgayDi(null); stopNode.setGioDi(null);
                }
                listStops.add(stopNode);
            }

            if (chuyenBus.capNhatChuyen(c, listStops)) {
                JOptionPane.showMessageDialog(dialogCapNhat, "Cập nhật thành công!");
                dialogCapNhat.dispose();
                timKiemChuyen();
            } else {
                JOptionPane.showMessageDialog(dialogCapNhat, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Lỗi dữ liệu: " + ex.getMessage());
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

            model.addRow(new Object[]{
                    c.getChuyenID(),
                    c.getTenChuyenHienThi(),
                    c.getTenGaDiHienThi(),
                    c.getTenGaDenHienThi(),
                    c.getTau().getTenTau(),
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
                        SwingUtilities.invokeLater(onSelected);
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
                        SwingUtilities.invokeLater(onSelected);
                    }

                    e.consume();
                }
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
        panelThemChuyen.getModelLichTrinh().setRowCount(0);

        setComboText(panelThemChuyen.getComboTuyen(), "");
        setComboText(panelThemChuyen.getComboTau(), "");
        panelThemChuyen.getTxtGaDiMoi().setText("");
        panelThemChuyen.getTxtGaDenMoi().setText("");
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

        setupCombo(panelThemChuyen.getComboTau(), dsTau);

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
        DocumentListener autoCode = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                genCode();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                genCode();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                genCode();
            }
        };

        DocumentListener headerTimeListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                syncHeaderToTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                syncHeaderToTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                syncHeaderToTable();
            }

            private void syncHeaderToTable() {
                // Nếu bảng đã có dữ liệu, cập nhật dòng đầu tiên (row 0)
                if (panelThemChuyen.getModelLichTrinh().getRowCount() > 0) {
                    panelThemChuyen.getModelLichTrinh().setValueAt(panelThemChuyen.getTxtNgayDi().getText(), 0, 2);
                    panelThemChuyen.getModelLichTrinh().setValueAt(panelThemChuyen.getTxtGioDi().getText(), 0, 3);
                }
                genCode(); // Gọi lại genCode để cập nhật mã chuyến
            }
        };

        if (panelThemChuyen.getComboTau().getEditor().getEditorComponent() instanceof JTextField txt) {
            txt.getDocument().addDocumentListener(autoCode);
        }
        panelThemChuyen.getTxtNgayDi().getDocument().addDocumentListener(headerTimeListener);
        panelThemChuyen.getTxtGioDi().getDocument().addDocumentListener(headerTimeListener);

        if (panelThemChuyen.getComboTuyen().getEditor().getEditorComponent() instanceof JTextField) {
            panelThemChuyen.getComboTuyen().addActionListener(e -> {
                String tuyenID = (String) panelThemChuyen.getComboTuyen().getSelectedItem();
                if (tuyenID != null && !tuyenID.isEmpty()) {
                    loadLichTrinhMau(tuyenID);
                }
            });
        }
        panelThemChuyen.getBtnThemGa().setText("Lưu Giờ");
        for (ActionListener al : panelThemChuyen.getBtnThemGa().getActionListeners())
            panelThemChuyen.getBtnThemGa().removeActionListener(al);
        panelThemChuyen.getBtnThemGa().addActionListener(e -> updateTimeIntoTable_Them());
        panelThemChuyen.getTableLichTrinh().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = panelThemChuyen.getTableLichTrinh().getSelectedRow();
                if (row >= 0) fillDataFromTableToInput_Them(row);
            }
        });

        panelThemChuyen.getBtnThemChuyen().addActionListener(e -> xuLyLuuChuyen());
    }

    private void genCode(){
        try{
            String tau = panelThemChuyen.getComboTau().getEditor().getItem().toString().trim();
            String ngayDiStr = panelThemChuyen.getTxtNgayDi().getText().trim();
            if (!tau.isEmpty() && !ngayDiStr.isEmpty()) {
                LocalDate d = LocalDate.parse(ngayDiStr, dateTimeFormatter);
                panelThemChuyen.getTxtMaChuyen().setText(tau.toUpperCase() + "_" + d.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            }
        }catch (Exception ex){}
    }

    private void xuLyLuuChuyen() {
        try {
            DefaultTableModel model = panelThemChuyen.getModelLichTrinh();
            if (model.getRowCount() < 1) {
                JOptionPane.showMessageDialog(dialogThem, "Vui lòng chọn Tuyến!"); return;
            }
            for(int i=0; i<model.getRowCount(); i++){
                if(model.getValueAt(i, 3).toString().isEmpty() || model.getValueAt(i, 6).toString().isEmpty()){
                    JOptionPane.showMessageDialog(dialogThem, "Chưa nhập giờ cho chặng " + (i+1));
                    return;
                }
            }
            String maChuyen = panelThemChuyen.getTxtMaChuyen().getText();
            String tuyenID = (String) panelThemChuyen.getComboTuyen().getSelectedItem();
            String tauID = (String) panelThemChuyen.getComboTau().getSelectedItem();

            LocalDate ngayDi = LocalDate.parse(panelThemChuyen.getTxtNgayDi().getText(), dateTimeFormatter);
            String gioDiStr = model.getValueAt(0,3).toString();
            LocalTime gioDi = LocalTime.parse(gioDiStr, timeFormatter);

            if (!PanelQuanLyChuyen.Validator.isValidGio(gioDiStr)) {
                JOptionPane.showMessageDialog(dialogThem, "Giờ đi không hợp lệ (HH:mm)!");
                panelThemChuyen.getTxtGioDi().requestFocus();
                return;
            }

            Chuyen c = new Chuyen(maChuyen);
            c.setTuyen(new Tuyen(tuyenID));
            c.setTau(new Tau(tauID, ""));
            c.setNgayDi(ngayDi);
            c.setGioDi(gioDi);

            // Lấy ID Ga
            String tenGaDau = model.getValueAt(0, 1).toString();
            String tenGaCuoi = model.getValueAt(model.getRowCount() - 1, 4).toString();
            String idGaDau = mapGaToID.get(tenGaDau);
            String idGaCuoi = mapGaToID.get(tenGaCuoi);

            c.setGaDi(new Ga(idGaDau, tenGaDau));
            c.setGaDen(new Ga(idGaCuoi, tenGaCuoi));
            c.setTenChuyenHienThi(tenGaDau + " - " + tenGaCuoi);
            c.setTenGaDiHienThi(tenGaDau);
            c.setTenGaDenHienThi(tenGaCuoi);

            List<ChuyenGa> listStops = new ArrayList<>();

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
                    stopNode.setNgayDi(LocalDate.parse(model.getValueAt(i+1, 2).toString(), dateTimeFormatter));
                    stopNode.setGioDi(LocalTime.parse(model.getValueAt(i+1, 3).toString(), timeFormatter));
                }
                listStops.add(stopNode);
            }

            if (chuyenBus.themChuyen(c, listStops)) {
                JOptionPane.showMessageDialog(panelThemChuyen, "Thêm thành công!");
                dialogThem.dispose();
                timKiemChuyen();
            } else {
                JOptionPane.showMessageDialog(dialogThem, "Thêm thất bại!");
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }

    private void capNhatSTT(){
        DefaultTableModel m = panelThemChuyen.getModelLichTrinh();
        for(int i=0; i<m.getRowCount(); i++){
            m.setValueAt(i+1, i, 0);
        }
    }


    private void setupComboTuyen(JComboBox<String> cbo, List<String> dataFormatted) {
        cbo.setEditable(true);
        // Model chứa full chuỗi "Mã (Tên)" để hiển thị đẹp khi xổ xuống
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

        // 1. Sự kiện gõ phím để lọc
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

        // 2. Sự kiện Click chuột: Gọi chonTuyen ngay
        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
                    chonTuyen(lst.getSelectedValue(), txtEditor, pp, actionNextFocus);
                }
            }
        });

        // 3. Sự kiện Phím Enter: Gọi chonTuyen ngay
        txtEditor.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Nếu popup đang hiện -> chọn từ list
                    if (pp.isVisible() && lst.getSelectedValue() != null) {
                        chonTuyen(lst.getSelectedValue(), txtEditor, pp, actionNextFocus);
                    }
                    // Nếu popup không hiện (đã gõ xong) -> chọn text hiện tại
                    else {
                        chonTuyen(txtEditor.getText(), txtEditor, pp, actionNextFocus);
                    }
                    e.consume();
                }
                // Xử lý phím lên xuống
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

        loadLichTrinhMau(maTuyen);

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

    private void loadLichTrinhMau(String tuyenID) {
        List<Ga> dsGa = chuyenBus.layDsGaCuaTuyen(tuyenID);
        DefaultTableModel model = panelThemChuyen.getModelLichTrinh();
        model.setRowCount(0);

        if (dsGa == null || dsGa.isEmpty()) return;

        String ngayKhoiHanh = panelThemChuyen.getTxtNgayDi().getText().trim();
        String gioKhoiHanh = panelThemChuyen.getTxtGioDi().getText().trim();

        for (int i = 0; i < dsGa.size() - 1; i++) {
            Ga gaDi = dsGa.get(i);
            Ga gaDen = dsGa.get(i+1);

            String valNgayDi = "";
            String valGioDi = "";

            if (i == 0) {
                valNgayDi = ngayKhoiHanh;
                valGioDi = gioKhoiHanh;
            }

            model.addRow(new Object[]{ (i + 1), gaDi.getTenGa(), valNgayDi, valGioDi, gaDen.getTenGa(), "", "" });
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

        panelThemChuyen.getTxtGaDiMoi().setText(model.getValueAt(row, 1).toString());
        panelThemChuyen.getTxtNgayDiMoi().setText(model.getValueAt(row, 2).toString());
        panelThemChuyen.getTxtGioDiMoi().setText(model.getValueAt(row, 3).toString());
        panelThemChuyen.getTxtGaDenMoi().setText(model.getValueAt(row, 4).toString());
        panelThemChuyen.getTxtNgayDenMoi().setText(model.getValueAt(row, 5).toString());
        panelThemChuyen.getTxtGioDenMoi().setText(model.getValueAt(row, 6).toString());

        panelThemChuyen.getTxtGaDiMoi().setEditable(false);
        panelThemChuyen.getTxtGaDenMoi().setEditable(false);

        panelThemChuyen.getTxtNgayDiMoi().requestFocus();
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
            model.setValueAt(sNgayDen, row, 5);
            model.setValueAt(sGioDen, row, 6);

            if (row < model.getRowCount() - 1) {

                model.setValueAt(sNgayDen, row + 1, 2);
                table.setRowSelectionInterval(row + 1, row + 1);

            } else {
                JOptionPane.showMessageDialog(parentComponent, "Đã hoàn thành nhập liệu lịch trình!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTimeIntoTable_Them() {
        xuLyLuuGioVaoBang(
                panelThemChuyen.getTableLichTrinh(),
                panelThemChuyen.getTxtNgayDiMoi(),
                panelThemChuyen.getTxtGioDiMoi(),
                panelThemChuyen.getTxtNgayDenMoi(),
                panelThemChuyen.getTxtGioDenMoi(),
                panelThemChuyen.getTxtNgayDi().getText().trim(), // Header Ngày
                panelThemChuyen.getTxtGioDi().getText().trim(), // Header Giờ
                dialogThem
        );

        int row = panelThemChuyen.getTableLichTrinh().getSelectedRow();
        if (row >= 0) {
            fillDataFromTableToInput_Them(row);
        }
    }
}


