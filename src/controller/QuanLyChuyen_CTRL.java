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
import gui.application.form.quanLyChuyen.PanelQuanLyChuyen;
import gui.application.form.quanLyChuyen.PanelThemChuyen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.Time;
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



        panelQuanLyChuyen.getLblMaChuyenValue().setText("...");
        panelQuanLyChuyen.getLblTenChuyenValue().setText("...");
        panelQuanLyChuyen.getLblTenTuyenValue().setText("...");
        panelQuanLyChuyen.getLblGaDiValue().setText("...");
        panelQuanLyChuyen.getLblGaDenValue().setText("...");
        panelQuanLyChuyen.getLblTauValue().setText("...");
        panelQuanLyChuyen.getModelLichTrinh().setRowCount(0);

        String homNay = LocalDate.now().format(dateTimeFormatter);
        panelQuanLyChuyen.getTxtNgayDi().setText(homNay);

        timKiemChuyen();
    }

    private void timKiemChuyen(){
        // Lấy dữ liệu từ UI
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

        panelQuanLyChuyen.getLblMaChuyenValue().setText("...");
        panelQuanLyChuyen.getLblTenChuyenValue().setText("...");
        panelQuanLyChuyen.getLblTenTuyenValue().setText("...");
        panelQuanLyChuyen.getLblGaDiValue().setText("...");
        panelQuanLyChuyen.getLblGaDenValue().setText("...");
        panelQuanLyChuyen.getLblTauValue().setText("...");
        panelQuanLyChuyen.getModelLichTrinh().setRowCount(0);
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
                    c.getGaDiHienThi(),
                    c.getGaDenHienThi(),
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
            panelQuanLyChuyen.getLblMaChuyenValue().setText(chuyen.getChuyenID());
            panelQuanLyChuyen.getLblTenChuyenValue().setText(chuyen.getTenChuyenHienThi());

            String tenTuyen = chuyen.getGaDiHienThi() + " - " + chuyen.getGaDenHienThi();
            panelQuanLyChuyen.getLblTenTuyenValue().setText(tenTuyen);

            panelQuanLyChuyen.getLblGaDiValue().setText(chuyen.getGaDiHienThi());
            panelQuanLyChuyen.getLblGaDenValue().setText(chuyen.getGaDenHienThi());
            panelQuanLyChuyen.getLblTauValue().setText(chuyen.getTau().getTenTau() != null ? chuyen.getTau().getTenTau() : "N/A");
        } else {
            panelQuanLyChuyen.getLblMaChuyenValue().setText("N/A");
            panelQuanLyChuyen.getLblTenChuyenValue().setText("N/A");
            panelQuanLyChuyen.getLblTenTuyenValue().setText("N/A");
            panelQuanLyChuyen.getLblGaDiValue().setText("N/A");
            panelQuanLyChuyen.getLblGaDenValue().setText("N/A");
            panelQuanLyChuyen.getLblTauValue().setText("N/A");
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
        return source.stream()
                .filter(s -> s.toLowerCase().contains(input.toLowerCase()))
                .limit(10) // Giới hạn 10 kết quả
                .collect(Collectors.toList());
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp,
                             Function<String, List<String>> timKiem) {
        String input = txt.getText().trim();
        if (input.length() < 1) {
            pp.setVisible(false);
            return;
        }

        List<String> ds = timKiem.apply(input);
        if (ds == null || ds.isEmpty()) {
            pp.setVisible(false);
            return;
        }

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 8));

        pp.show(txt, 0, txt.getHeight());
        txt.requestFocus();
    }

    private void taoPopGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem) {
        pp.setFocusable(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        txt.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;

            @Override
            public void insertUpdate(DocumentEvent e) { itemStateChanged(); }
            @Override
            public void removeUpdate(DocumentEvent e) { itemStateChanged(); }
            @Override
            public void changedUpdate(DocumentEvent e) { itemStateChanged(); }

            private void itemStateChanged(){
                if(timer != null && timer.isRunning()){
                    timer.stop();
                }

                timer = new Timer(300, e -> {
                    SwingUtilities.invokeLater(() -> {
                        hienThiGoiY(txt, lst, pp, timKiem);
                    });
                    timer.setRepeats(false);
                    timer.start();
                });
                lst.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1){
                            txt.setText(lst.getSelectedValue());
                            pp.setVisible(false);
                            timKiemChuyen();;
                        }
                    }
                });
                txt.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                            // Mũi tên XUỐNG
                            if (pp.isVisible()) {
                                int index = lst.getSelectedIndex();
                                if (index < lst.getModel().getSize() - 1) {
                                    lst.setSelectedIndex(index + 1); // Chọn dòng dưới
                                    lst.ensureIndexIsVisible(index + 1); // Tự cuộn xuống
                                }
                            }
                            // Chặn sự kiện để không ảnh hưởng focus
                            e.consume();
                        }
                        else if (e.getKeyCode() == KeyEvent.VK_UP) {
                            // Mũi tên LÊN
                            if (pp.isVisible()) {
                                int index = lst.getSelectedIndex();
                                if (index > 0) {
                                    lst.setSelectedIndex(index - 1); // Chọn dòng trên
                                    lst.ensureIndexIsVisible(index - 1); // Tự cuộn lên
                                }
                            }
                            e.consume();
                        }
                        else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            // Phím ENTER
                            if (pp.isVisible() && lst.getSelectedValue() != null) {
                                // Trường hợp 1: Đang chọn trong gợi ý -> Điền vào text
                                txt.setText(lst.getSelectedValue());
                                pp.setVisible(false);
                                timKiemChuyen(); // Tìm kiếm ngay
                            } else {
                                // Trường hợp 2: Không chọn gợi ý -> Tìm kiếm theo text hiện tại
                                pp.setVisible(false);
                                timKiemChuyen();
                            }
                            e.consume();
                        }
                    }
                });

                txt.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent e) {
                        SwingUtilities.invokeLater(() -> pp.setVisible(false));
                    }
                });
            }

        });

        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    int index = lst.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        txt.setText(lst.getModel().getElementAt(index));
                        pp.setVisible(false);
                        timKiemChuyen();
                    }
                }
            }
        });

        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
            }
        });
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
        }
        mapGaToID = chuyenBus.getMapTenGaToID();

        loadDataCombobox();

        panelThemChuyen.getTxtMaChuyen().setText("");
        panelThemChuyen.getTxtNgayDi().setText("");
        panelThemChuyen.getModelLichTrinh().setRowCount(0);
        dialogThem.setVisible(true);
    }

    private void loadDataCombobox(){
        List<String> dsGa = chuyenBus.getListTenGa();
        List<String> dsTau = chuyenBus.getAllTauID();
        List<String> dsTuyen = chuyenBus.getAllTuyenID();

        setupCombo(panelThemChuyen.getComboTuyen(), dsTuyen);
        setupCombo(panelThemChuyen.getComboTau(), dsTau);
        setupCombo(panelThemChuyen.getComboGaXuatPhat(), dsGa);
        setupCombo(panelThemChuyen.getComboGaDich(), dsGa);

        setupCombo(panelThemChuyen.getComboGaDiMoi(),dsGa);
        setupCombo(panelThemChuyen.getComboGaDenMoi(),dsGa);
    }

    private void setupCombo(JComboBox<String> comboBox, List<String> data){
        comboBox.removeAllItems();
        for(String s : data) comboBox.addItem(s);
        comboBox.setSelectedIndex(-1);
        ComboBoxSuggestion.addSuggestion(comboBox, data);
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
            LocalTime gioDi = LocalTime.parse(panelThemChuyen.getTxtGioDi().getText(), timeFormatter);

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


            c.setGaDiHienThi(tenGaDau);
            c.setGaDenHienThi(tenGaCuoi);

            List<ChuyenGa> listStops = new ArrayList<>();

            ChuyenGa startNode = new ChuyenGa();
            startNode.setChuyen(c);
            startNode.setGa(new Ga(idGaDau, tenGaDau));
            startNode.setThuTu(1);

            String startNgayDi = model.getValueAt(0, 2).toString();
            String startGioDi = model.getValueAt(0, 3).toString();
            startNode.setNgayDi(LocalDate.parse(startNgayDi, dateTimeFormatter));
            startNode.setGioDi(LocalTime.parse(startGioDi, timeFormatter));
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
