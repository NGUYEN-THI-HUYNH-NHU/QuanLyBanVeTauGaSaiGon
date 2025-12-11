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
import entity.*;
import gui.application.form.quanLyChuyen.PanelCapNhatChuyen;
import gui.application.form.quanLyChuyen.PanelQuanLyChuyen;
import gui.application.form.quanLyChuyen.PanelThemChuyen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
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

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public QuanLyChuyen_CTRL(PanelQuanLyChuyen panelQuanLyChuyen){
        this.panelQuanLyChuyen = panelQuanLyChuyen;
        this.chuyenBus = new Chuyen_BUS();

        loadDataToTable(chuyenBus.layDanhSachChuyen());
        initEvents();
        thietLapAutoComplete();

        String homNay = LocalDate.now().format(dateTimeFormatter);
        panelQuanLyChuyen.getTxtNgayDi().setText(homNay);

        timKiemChuyen();
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
                    // Ép focus vào Panel nền để không ô text nào bị chọn
                    panelCapNhatChuyen.requestFocusInWindow();
                }
            });

            initCapNhatEvents();
        }
        mapGaToID = chuyenBus.getMapTenGaToID();
        loadDataToComboCapNhat();

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

        if (c.getNgayDi() != null) panelCapNhatChuyen.getTxtNgayDi().setText(c.getNgayDi().format(dateTimeFormatter));
        if (c.getGioDi() != null) panelCapNhatChuyen.getTxtGioDi().setText(c.getGioDi().format(timeFormatter));

        setComboText(panelCapNhatChuyen.getComboGaXuatPhat(), c.getTenGaDiHienThi());
        setComboText(panelCapNhatChuyen.getComboGaDich(), c.getTenGaDenHienThi());

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

        LocalDate ngayDi = null;
        if(!ngayDiStr.isEmpty() && !ngayDiStr.equals("Chọn ngày...")){
                ngayDi = LocalDate.parse(ngayDiStr, dateTimeFormatter);

        }

        List<Chuyen> resultList = chuyenBus.timKiemChuyen(maChuyen, gaDi, gaDen, tenTau, ngayDi);

        loadDataToTable(resultList);

        panelQuanLyChuyen.getTxtMaChuyen().setText("");
        panelQuanLyChuyen.getTxtChiTietMaTuyen().setText("");
        panelQuanLyChuyen.getTxtChiTietTau().setText("");
        panelQuanLyChuyen.getTxtChiTietTenChuyen().setText("");
        panelQuanLyChuyen.getTxtChiTietGaDi().setText("");
        panelQuanLyChuyen.getTxtChiTietGaDen().setText("");
        panelQuanLyChuyen.getModelLichTrinh().setRowCount(0);
    }

    private void loadDataToComboCapNhat() {
        List<String> dsGa = chuyenBus.getListTenGa();
        List<String> dsTau = chuyenBus.getAllTauID();
        List<String> dsTuyen = chuyenBus.getAllTuyenID();

        setupCombo(panelCapNhatChuyen.getComboTuyen(), dsTuyen);
        setupCombo(panelCapNhatChuyen.getComboTau(), dsTau);
        setupCombo(panelCapNhatChuyen.getComboGaXuatPhat(), dsGa);
        setupCombo(panelCapNhatChuyen.getComboGaDich(), dsGa);
        setupCombo(panelCapNhatChuyen.getComboGaDiMoi(), dsGa);
        setupCombo(panelCapNhatChuyen.getComboGaDenMoi(), dsGa);
    }

    private void initCapNhatEvents() {
        // Sự kiện click vào bảng lịch trình -> Đổ dữ liệu lên form nhập
        panelCapNhatChuyen.getTableLichTrinh().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = panelCapNhatChuyen.getTableLichTrinh().getSelectedRow();
                if (row >= 0) {
                    fillDataFromTableToInput(row);
                    panelCapNhatChuyen.getBtnCapNhatChang().setEnabled(true); // Bật nút sửa
                    panelCapNhatChuyen.getBtnCapNhatGa().setEnabled(false); // Tắt nút thêm để tránh nhầm
                }
            }
        });

        // Click ra ngoài bảng (để reset form nhập về trạng thái thêm mới)
        panelCapNhatChuyen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                panelCapNhatChuyen.getTableLichTrinh().clearSelection();
                clearInputLichTrinh();
                panelCapNhatChuyen.getBtnCapNhatChang().setEnabled(false);
                panelCapNhatChuyen.getBtnCapNhatGa().setEnabled(true);
            }
        });

        panelCapNhatChuyen.getBtnCapNhatGa().addActionListener(e -> themChangVaoBangCapNhat());

        panelCapNhatChuyen.getBtnCapNhatChang().addActionListener(e -> capNhatChangTrongBang());

        panelCapNhatChuyen.getBtnXoaGa().addActionListener(e -> {
            int row = panelCapNhatChuyen.getTableLichTrinh().getSelectedRow();
            if(row >= 0) {
                panelCapNhatChuyen.getModelLichTrinh().removeRow(row);
                reIndexTable(panelCapNhatChuyen.getModelLichTrinh());
                clearInputLichTrinh();
            }
        });

        panelCapNhatChuyen.getBtnCapNhatChuyen().addActionListener(e -> xuLyLuuCapNhat());
    }

    private void themChangVaoBangCapNhat() {
        String gaDi = ((JTextField)panelCapNhatChuyen.getComboGaDiMoi().getEditor().getEditorComponent()).getText();
        String gaDen = ((JTextField)panelCapNhatChuyen.getComboGaDenMoi().getEditor().getEditorComponent()).getText();
        String ngayDi = panelCapNhatChuyen.getTxtNgayDiMoi().getText();
        String gioDi = panelCapNhatChuyen.getTxtGioDiMoi().getText();
        String ngayDen = panelCapNhatChuyen.getTxtNgayDenMoi().getText();
        String gioDen = panelCapNhatChuyen.getTxtGioDenMoi().getText();

        if (gaDi.isEmpty() || gaDen.isEmpty() || gaDi.equals(gaDen)) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Ga đi/đến không hợp lệ!");
            return;
        }
        int stt = panelCapNhatChuyen.getModelLichTrinh().getRowCount() + 1;
        panelCapNhatChuyen.getModelLichTrinh().addRow(new Object[]{stt, gaDi, ngayDi, gioDi, gaDen, ngayDen, gioDen});

        setComboText(panelCapNhatChuyen.getComboGaDiMoi(), gaDen);
        setComboText(panelCapNhatChuyen.getComboGaDenMoi(), "");
        panelCapNhatChuyen.getTxtNgayDiMoi().setText(ngayDen);
        panelCapNhatChuyen.getTxtNgayDenMoi().setText(ngayDen);
        panelCapNhatChuyen.getTxtGioDiMoi().setText("");
        panelCapNhatChuyen.getTxtGioDenMoi().setText("");
    }

    private void fillDataFromTableToInput(int row) {
        DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();

        String gaDi = model.getValueAt(row, 1).toString();
        String ngayDi = model.getValueAt(row, 2).toString();
        String gioDi = model.getValueAt(row, 3).toString();
        String gaDen = model.getValueAt(row, 4).toString();
        String ngayDen = model.getValueAt(row, 5).toString();
        String gioDen = model.getValueAt(row, 6).toString();

        setComboText(panelCapNhatChuyen.getComboGaDiMoi(), gaDi);
        setComboText(panelCapNhatChuyen.getComboGaDenMoi(), gaDen);

        panelCapNhatChuyen.getTxtNgayDiMoi().setText(ngayDi);
        panelCapNhatChuyen.getTxtGioDiMoi().setText(gioDi);
        panelCapNhatChuyen.getTxtNgayDenMoi().setText(ngayDen);
        panelCapNhatChuyen.getTxtGioDenMoi().setText(gioDen);
    }

    private void capNhatChangTrongBang() {
        int row = panelCapNhatChuyen.getTableLichTrinh().getSelectedRow();
        if (row < 0) return;

        String gaDi = ((JTextField)panelCapNhatChuyen.getComboGaDiMoi().getEditor().getEditorComponent()).getText();
        String gaDen = ((JTextField)panelCapNhatChuyen.getComboGaDenMoi().getEditor().getEditorComponent()).getText();
        String ngayDi = panelCapNhatChuyen.getTxtNgayDiMoi().getText();
        String gioDi = panelCapNhatChuyen.getTxtGioDiMoi().getText();
        String ngayDen = panelCapNhatChuyen.getTxtNgayDenMoi().getText();
        String gioDen = panelCapNhatChuyen.getTxtGioDenMoi().getText();

        if (gaDi.isEmpty() || gaDen.isEmpty() || gaDi.equals(gaDen)) {
            JOptionPane.showMessageDialog(dialogCapNhat, "Thông tin chặng không hợp lệ!");
            return;
        }
        DefaultTableModel model = panelCapNhatChuyen.getModelLichTrinh();
        model.setValueAt(gaDi, row, 1);
        model.setValueAt(ngayDi, row, 2);
        model.setValueAt(gioDi, row, 3);
        model.setValueAt(gaDen, row, 4);
        model.setValueAt(ngayDen, row, 5);
        model.setValueAt(gioDen, row, 6);

        panelCapNhatChuyen.getTableLichTrinh().clearSelection();
        clearInputLichTrinh();
        panelCapNhatChuyen.getBtnCapNhatChang().setEnabled(false);
        panelCapNhatChuyen.getBtnCapNhatGa().setEnabled(true);
    }

    private void clearInputLichTrinh() {
        setComboText(panelCapNhatChuyen.getComboGaDiMoi(), "");
        setComboText(panelCapNhatChuyen.getComboGaDenMoi(), "");
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
    }

    private void thietLapAutoComplete(){
        List<String> dataMaChuyen = chuyenBus.getListMaChuyen();
        List<String> dataTenGa = chuyenBus.getListTenGa();
        List<String> dataTenTau = chuyenBus.getListTenTau();

        taoPopGoiY(panelQuanLyChuyen.getTxtMaChuyen(),panelQuanLyChuyen.getPpMaChuyen(),
                panelQuanLyChuyen.getListMaChuyen(), input -> locDuLieu(dataMaChuyen, input));
        taoPopGoiY(panelQuanLyChuyen.getTxtGaXuatPhat(), panelQuanLyChuyen.getPpGaDi(),
                panelQuanLyChuyen.getListGaDi(),
                input -> locDuLieu(dataTenGa, input));

        taoPopGoiY(panelQuanLyChuyen.getTxtGaDich(), panelQuanLyChuyen.getPpGaDen(),
                panelQuanLyChuyen.getListGaDen(),
                input -> locDuLieu(dataTenGa, input));

        taoPopGoiY(panelQuanLyChuyen.getTxtTau(), panelQuanLyChuyen.getPpTau(),
                panelQuanLyChuyen.getListTau(),
                input -> locDuLieu(dataTenTau, input));
    }

    private List<String> locDuLieu(List<String> source, String input) {
        if (source == null) return new ArrayList<>();
        return source.stream().filter(s -> s.toLowerCase().contains(input.toLowerCase())).limit(10).collect(Collectors.toList());
    }

    private void taoPopGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem) {
        pp.setFocusable(false);
        lst.setFocusable(false);
        lst.setRequestFocusEnabled(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        txt.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
            private void update() {
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(300, e -> SwingUtilities.invokeLater(() -> {
                    if (txt.isFocusOwner() ) {
                        hienThiGoiY(txt, lst, pp, timKiem);
                    }
                }));
                timer.setRepeats(false);
                timer.start();
            }
        });

        lst.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
                    txt.setText(lst.getSelectedValue());
                    pp.setVisible(false);
                    if(isMainSearchField(txt)) timKiemChuyen();
                }
                txt.transferFocus();
            }
        });

        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (pp.isVisible()) {
                        int index = lst.getSelectedIndex();
                        if (index < lst.getModel().getSize() - 1) {
                            lst.setSelectedIndex(index + 1);
                            lst.ensureIndexIsVisible(index + 1);
                        }
                    }
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (pp.isVisible()) {
                        int index = lst.getSelectedIndex();
                        if (index > 0) {
                            lst.setSelectedIndex(index - 1);
                            lst.ensureIndexIsVisible(index - 1);
                        }
                    }
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    boolean dataSelected = false;

                    if (pp.isVisible() && lst.getSelectedValue() != null) {
                        txt.setText(lst.getSelectedValue());
                        dataSelected = true;
                    }
                    pp.setVisible(false);

                    if (isMainSearchField(txt)) {
                        timKiemChuyen();
                    } else if (dataSelected) {
                        txt.transferFocus();
                    } else {
                        txt.transferFocus();
                    }

                    e.consume();
                }
            }
        });

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!lst.isFocusOwner()) {
                        pp.setVisible(false);
                    }
                });
            }
        });
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem) {
        String input = txt.getText().trim();
        if (input.isEmpty()) { pp.setVisible(false); return; }

        List<String> ds = timKiem.apply(input);
        if (ds == null || ds.isEmpty()) { pp.setVisible(false); return; }

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 8));

        if (txt.isFocusOwner()) {
            pp.show(txt, 0, txt.getHeight());
            txt.requestFocus();
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
        setComboText(panelThemChuyen.getComboGaXuatPhat(), "");
        setComboText(panelThemChuyen.getComboGaDich(), "");
        setComboText(panelThemChuyen.getComboGaDiMoi(), "");
        setComboText(panelThemChuyen.getComboGaDenMoi(), "");
        dialogThem.setVisible(true);
    }

    private void setComboText(JComboBox<String> combo, String text){
        ((JTextField)combo.getEditor().getEditorComponent()).setText(text);
    }

    private void loadDataCombobox(){
        List<String> dsGa = chuyenBus.getListTenGa();
        List<String> dsTau = chuyenBus.getAllTauID();
        List<String> dsTuyen = chuyenBus.getAllTuyenID();

        setupCombo(panelThemChuyen.getComboTuyen(), dsTuyen);
        setupCombo(panelThemChuyen.getComboTau(), dsTau);
        setupCombo(panelThemChuyen.getComboGaXuatPhat(), dsGa);
        setupCombo(panelThemChuyen.getComboGaDich(), dsGa);
        setupCombo(panelThemChuyen.getComboGaDiMoi(), dsGa);
        setupCombo(panelThemChuyen.getComboGaDenMoi(), dsGa);
    }

    private void setupCombo(JComboBox<String> cbo, List<String> data){
        cbo.setEditable(true);
        cbo.setModel(new DefaultComboBoxModel<>(data.toArray(new String[0])));
        cbo.setSelectedIndex(-1);

        JTextField txtEditor = (JTextField) cbo.getEditor().getEditorComponent();

        taoPopGoiY(txtEditor, new JPopupMenu(), new JList<>(), input -> locDuLieu(data, input));
    }

    private void initThemChuyenEvents(){
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
        if(panelThemChuyen.getComboTau().getEditor().getEditorComponent() instanceof JTextField txt) {
            txt.getDocument().addDocumentListener(autoCode);
        }
        panelThemChuyen.getTxtNgayDi().getDocument().addDocumentListener(autoCode);

        panelThemChuyen.getBtnThemGa().addActionListener(e -> themChangVaoBang());

        panelThemChuyen.getBtnXoaGa().addActionListener(e -> {
            int row = panelThemChuyen.getTableLichTrinh().getSelectedRow();
            if(row >= 0){
                panelThemChuyen.getModelLichTrinh().removeRow(row);
                capNhatSTT();
            }
        });
        panelThemChuyen.getBtnThemChuyen().addActionListener(e -> xuLyLuuChuyen());
    }

    private void themChangVaoBang(){
        String gaDi = (String) panelThemChuyen.getComboGaDiMoi().getSelectedItem();
        String gaDen = (String) panelThemChuyen.getComboGaDenMoi().getSelectedItem();
        String ngayDi = panelThemChuyen.getTxtNgayDiMoi().getText();
        String gioDi = panelThemChuyen.getTxtGioDiMoi().getText();
        String ngayDen = panelThemChuyen.getTxtNgayDenMoi().getText();
        String gioDen = panelThemChuyen.getTxtGioDenMoi().getText();

        if(gaDi == null || gaDen == null || gaDi.equals(gaDen)){
            JOptionPane.showMessageDialog(dialogThem, "Ga đi và ga đến không hợp lệ!");
            return;
        }

        int stt = panelThemChuyen.getModelLichTrinh().getRowCount() + 1;
        panelThemChuyen.getModelLichTrinh().addRow(new Object[]{stt, gaDi, ngayDi, gioDi, gaDen, ngayDen, gioDen});
        panelThemChuyen.getComboGaDiMoi().setSelectedItem(gaDen);
        panelThemChuyen.getComboGaDenMoi().setSelectedIndex(-1);
        panelThemChuyen.getTxtNgayDiMoi().setText(ngayDen);
        panelThemChuyen.getTxtNgayDenMoi().setText(ngayDen);
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
                JOptionPane.showMessageDialog(dialogThem, "Lịch trình phải có ít nhất 1 chặng!");
                return;
            }
            String maChuyen = panelThemChuyen.getTxtMaChuyen().getText();
            String tuyenID = (String) panelThemChuyen.getComboTuyen().getSelectedItem();
            String tauID = (String) panelThemChuyen.getComboTau().getSelectedItem();

            LocalDate ngayDi = LocalDate.parse(panelThemChuyen.getTxtNgayDi().getText(), dateTimeFormatter);
            String gioDiStr = model.getValueAt(0,3).toString();
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

            if(idGaDau == null || idGaCuoi == null){
                JOptionPane.showMessageDialog(dialogThem, "Lỗi: Không tìm thấy ID của ga trong CSDL!");
                return;
            }


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

            String startNgayDi = model.getValueAt(0, 2).toString();
            startNode.setNgayDi(LocalDate.parse(startNgayDi, dateTimeFormatter));
            startNode.setGioDi(LocalTime.parse(gioDiStr, timeFormatter));
            startNode.setGioDen(null);
            startNode.setNgayDen(null);
            listStops.add(startNode);

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenGaDen = model.getValueAt(i, 4).toString();
                String idGaDen = mapGaToID.get(tenGaDen);


                if(idGaDen == null){
                    JOptionPane.showMessageDialog(dialogThem, "Lỗi: Không tìm thấy ID của ga " + tenGaDen + " trong CSDL!");
                    return;
                }

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
                    stopNode.setNgayDi(null);
                    stopNode.setGioDi(null);
                }
                listStops.add(stopNode);
            }

            if (chuyenBus.themChuyen(c, listStops)) {

                JOptionPane.showMessageDialog(panelThemChuyen, "Thêm chuyến thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialogThem.dispose();
                timKiemChuyen();
            }else{
                    JOptionPane.showMessageDialog(dialogThem, "Thêm thất bại. Kiểm tra lại dữ liệu!");
                }
        }catch(Exception ex){
                JOptionPane.showMessageDialog(dialogThem, "Lỗi khi thêm chuyến: " + ex.getMessage());
                ex.printStackTrace();
        }
    }

    private void capNhatSTT(){
        DefaultTableModel m = panelThemChuyen.getModelLichTrinh();
        for(int i=0; i<m.getRowCount(); i++){
            m.setValueAt(i+1, i, 0);
        }
    }


}
