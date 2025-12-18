package controller;/*
 * @ (#) ThemTuyen_CTRL.java   1.0     28/10/2025
package controller;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 28/10/2025
 */

import bus.Ga_BUS;
import bus.Tuyen_BUS;
import dao.KhoangCachChuan_DAO;
import entity.Ga;
import entity.Tuyen;
import entity.TuyenChiTiet;
import gui.application.form.quanLyTuyen.PanelThemTuyen;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.text.Normalizer;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ThemTuyen_CTRL {
    private final PanelThemTuyen panelThemTuyen;
    private final Tuyen_BUS tuyenBus;
    private final JDialog dialog;
    private final Ga_BUS gaBus;
    private final KhoangCachChuan_DAO khoangCachChuanDao;

    private final Map<String, Ga> dsGaCoSan;
    private final List<Ga> dsGaDaChon;
    private List<String> listTenGaGoc;

    private JPopupMenu ppGaXuatPhat = new JPopupMenu();
    private JList<String> lstGaXuatPhat = new JList<>();

    private JPopupMenu ppGaDich = new JPopupMenu();
    private JList<String> lstGaDich = new JList<>();

    private JPopupMenu ppGaTrungGian = new JPopupMenu();
    private JList<String> lstGaTrungGian = new JList<>();

    public ThemTuyen_CTRL(PanelThemTuyen panelThemTuyen, JDialog dialog){
        this.panelThemTuyen = panelThemTuyen;
        this.dialog = dialog;
        tuyenBus = new Tuyen_BUS();
        gaBus = new Ga_BUS();
        khoangCachChuanDao = new KhoangCachChuan_DAO();

        dsGaCoSan = new LinkedHashMap<>();
        dsGaDaChon = new ArrayList<>();
        listTenGaGoc = new ArrayList<>();

        khoiTaoDuLieuBanDau();
        thietLapListener();

        this.dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                lamMoi();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                lamMoi();
            }
        });
    }

    private void khoiTaoDuLieuBanDau(){
        List<Ga> listGaFull = gaBus.getAllGa();

        listTenGaGoc = new ArrayList<>();
        dsGaCoSan.clear();

        for(Ga ga : listGaFull){
            dsGaCoSan.put(ga.getTenGa(), ga);
            listTenGaGoc.add(ga.getTenGa());
        }
    }

    private void setModelToComboBox(JComboBox<String> cbo){
        cbo.setEditable(true);
        cbo.setSelectedIndex(-1);
    }

    private void thietLapListener(){
        JTextField txtXP = panelThemTuyen.getTxtGaXuatPhat();
        JTextField txtDich = panelThemTuyen.getTxtGaDich();
        JTextField txtTG = panelThemTuyen.getTxtGaTrungGian();

        taoPopupGoiY(txtXP, ppGaXuatPhat, lstGaXuatPhat,
                input -> locDuLieu(listTenGaGoc, input),
                txtDich);

        taoPopupGoiY(txtDich, ppGaDich, lstGaDich,
                input -> locDuLieu(listTenGaGoc, input),
                txtTG);

        taoPopupGoiY(txtTG, ppGaTrungGian, lstGaTrungGian,
                input -> locDuLieu(listTenGaGoc, input),
                null);

        setupUpdateMatuyenEvent(txtXP);
        setupUpdateMatuyenEvent(txtDich);

        txtTG.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(!ppGaTrungGian.isVisible()){
                        xuLyChonGaTrungGian();
                    }
                }
            }
        });

        panelThemTuyen.getBtnXacNhanTinhKC().addActionListener(e -> capNhatDanhSachVaTinhKC());
        panelThemTuyen.getBtnLuu().addActionListener(e -> xuLyLuuTuyen());
        panelThemTuyen.getBtnHuy().addActionListener(e -> xuLyHuyBo());
    }

    private String unAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s.toLowerCase(), java.text.Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private List<String> locDuLieu(List<String> src, String input){
        if(src == null) return new ArrayList<>();

        if (input.trim().isEmpty()) {
            return new ArrayList<>(src);
        }

        String inputNorm = unAccent(input);
        return src.stream()
                .filter(s -> unAccent(s).contains(inputNorm))
                .limit(20)
                .collect(Collectors.toList());
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem){
        String input = txt.getText().trim();

        List<String> ds = timKiem.apply(input);
        if(ds == null || ds.isEmpty()){
            pp.setVisible(false);
            return;
        }

        if (ds.size() == 1 && ds.get(0).equalsIgnoreCase(input)) {
            pp.setVisible(false);
            return;
        }

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 10));

        if(txt.isShowing()){
            pp.setPopupSize(txt.getWidth(), pp.getPreferredSize().height);
            pp.show(txt, 0, txt.getHeight());
            txt.requestFocus();
        }
    }

    private void taoPopupGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem, JComponent nextFocus){
        pp.setFocusable(false);
        lst.setFocusable(false);
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        txt.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;

            private void update() {
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(300, e -> SwingUtilities.invokeLater(() -> {
                    if (txt.isFocusOwner()) {
                        hienThiGoiY(txt, lst, pp, timKiem);
                    }
                }));
                timer.setRepeats(false);
                timer.start();
            }

            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });
        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiGoiY(txt, lst, pp, timKiem);
            }
        });
        lst.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
                    txt.setText(lst.getSelectedValue());
                    pp.setVisible(false);
                    capNhatMaTuyen();
                    if(nextFocus != null) {
                        nextFocus.requestFocusInWindow();
                    }
                }
            }
        });
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (pp.isVisible()) {
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        int index = lst.getSelectedIndex();
                        if (index < lst.getModel().getSize() - 1) {
                            lst.setSelectedIndex(index + 1);
                            lst.ensureIndexIsVisible(index + 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        int index = lst.getSelectedIndex();
                        if (index > 0) {
                            lst.setSelectedIndex(index - 1);
                            lst.ensureIndexIsVisible(index - 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (pp.isVisible()) {
                            if (lst.getSelectedIndex() == -1 && lst.getModel().getSize() > 0) {
                                lst.setSelectedIndex(0);
                            }
                            if (lst.getSelectedValue() != null) {
                                txt.setText(lst.getSelectedValue());
                                pp.setVisible(false);
                                capNhatMaTuyen();

                                if (nextFocus != null) {
                                    nextFocus.requestFocusInWindow();
                                }
                            }
                            e.consume();
                        }
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    hienThiGoiY(txt, lst, pp, timKiem);
                }
            }
        });
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
//                SwingUtilities.invokeLater(() -> hienThiGoiY(txt, lst, pp, timKiem));
            }

            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!pp.isFocusOwner() && !lst.isFocusOwner()) {
                        pp.setVisible(false);
                    }
                });
            }
        });
    }

    private void setupUpdateMatuyenEvent(JTextField txt){
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                capNhatMaTuyen();
            }
        });
    }

    private void capNhatMaTuyen() {
        String tenGaDi = panelThemTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelThemTuyen.getTxtGaDich().getText().trim();
        if(!tenGaDi.isEmpty() && !tenGaDen.isEmpty()){
            String baseMa = tuyenBus.taoMaTuyenCoSo(tenGaDi,tenGaDen);
            if(baseMa.isEmpty()){
                panelThemTuyen.getTxtMaTuyen().setText("");
                return;
            }
            if(tuyenBus.kiemTraMaTuyuenDaTonTai(baseMa)) {
                panelThemTuyen.getTxtMaTuyen().setText("LỖI: Tuyến " + baseMa + " đã tồn tại!");
                panelThemTuyen.getTxtMaTuyen().setForeground(Color.RED);
            }else{
                panelThemTuyen.getTxtMaTuyen().setText(baseMa);
                panelThemTuyen.getTxtMaTuyen().setForeground(Color.BLACK);
            }
        }else{
            panelThemTuyen.getTxtMaTuyen().setText("");
            panelThemTuyen.getTxtMaTuyen().setForeground(Color.BLACK);
        }
    }

    private void xuLyChonGaTrungGian(){
        String tenGaMoi = panelThemTuyen.getTxtGaTrungGian().getText().trim();
        if(tenGaMoi.isEmpty()) return;

        // 1. Kiểm tra Ga Xuất Phát và Ga Đích
        String tenGaXP = panelThemTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelThemTuyen.getTxtGaDich().getText().trim();

        if (tenGaXP.isEmpty() || tenGaDen.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen,
                    "Vui lòng chọn Ga Xuất Phát và Ga Đích trước!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Kiểm tra Ga hợp lệ
        if(!dsGaCoSan.containsKey(tenGaMoi) || !dsGaCoSan.containsKey(tenGaXP) || !dsGaCoSan.containsKey(tenGaDen)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Tên ga không tồn tại trong hệ thống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Ga gaMoi = dsGaCoSan.get(tenGaMoi);
        Ga gaXP = dsGaCoSan.get(tenGaXP);
        Ga gaDen = dsGaCoSan.get(tenGaDen);

        // 3. Kiểm tra trùng
        if(tenGaMoi.equals(tenGaXP) || tenGaMoi.equals(tenGaDen)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga trung gian không được trùng với Ga XP hoặc Ga Đích!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(dsGaDaChon.stream().anyMatch(g -> g.getTenGa().equals(tenGaMoi))){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga này đã được thêm rồi!", "Trùng lặp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 4. KIỂM TRA LOGIC HƯỚNG VÀ THỨ TỰ (QUAN TRỌNG) ---
        try {
            // A. Tính tổng quãng đường (XP -> Đích)
            int kcTong = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getGaID(), gaDen.getGaID());
            // B. Tính quãng đường từ XP -> Ga Mới
            int kcTuDauDenMoi = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getGaID(), gaMoi.getGaID());
            // C. Tính quãng đường từ Ga Mới -> Đích
            int kcTuMoiDenDich = tuyenBus.tinhKhoangCachTongDijsktra(gaMoi.getGaID(), gaDen.getGaID());

            // Check lỗi DB
            if (kcTong == -1 || kcTuDauDenMoi == -1 || kcTuMoiDenDich == -1) {
                JOptionPane.showMessageDialog(panelThemTuyen, "Lỗi tính toán khoảng cách. Kiểm tra lại dữ liệu Ga.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check 4.1: Ga Mới có nằm TRÊN tuyến đường không?
            // Nếu (XP->Mới) + (Mới->Đích) > (XP->Đích) nghĩa là ga này nằm lệch đường hoặc ngược chiều hẳn
            // Cho phép sai số nhỏ (ví dụ 5km) do dữ liệu đường sắt có thể không tuyệt đối thẳng
            if (Math.abs((kcTuDauDenMoi + kcTuMoiDenDich) - kcTong) > 20) {
                JOptionPane.showMessageDialog(panelThemTuyen,
                        "Ga " + tenGaMoi + " không nằm trên cung đường từ " + tenGaXP + " đến " + tenGaDen + ".\n" +
                                "Hoặc ga này nằm ngược chiều di chuyển.",
                        "Sai lộ trình", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!dsGaDaChon.isEmpty()) {
                Ga gaCuoiCung = dsGaDaChon.get(dsGaDaChon.size() - 1);
                int kcTuDauDenCuoiList = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getGaID(), gaCuoiCung.getGaID());

                if (kcTuDauDenMoi <= kcTuDauDenCuoiList) {
                    JOptionPane.showMessageDialog(panelThemTuyen,
                            "Sai thứ tự hành trình!\n" +
                                    "- Ga " + gaCuoiCung.getTenGa() + " cách " + tenGaXP + ": " + kcTuDauDenCuoiList + " km\n" +
                                    "- Ga " + tenGaMoi + " cách " + tenGaXP + ": " + kcTuDauDenMoi + " km\n\n" +
                                    "-> Bạn phải nhập ga " + tenGaMoi + " TRƯỚC ga " + gaCuoiCung.getTenGa() + ".",
                            "Sai thứ tự nhập liệu", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            dsGaDaChon.add(gaMoi);
            taovaThemTagGa(gaMoi);
            panelThemTuyen.getTxtGaTrungGian().setText("");
            ppGaTrungGian.setVisible(false);

            capNhatDanhSachVaTinhKC();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void taovaThemTagGa(Ga ga){
        JButton btnGaTag = new JButton(ga.getTenGa() + " \u2715");
        btnGaTag.setMargin(new Insets(3,5,3,5));
        btnGaTag.addActionListener(e -> {
            panelThemTuyen.getPnlGaTrungGianDaChon().remove(btnGaTag);
            dsGaDaChon.remove(ga);
            panelThemTuyen.getPnlGaTrungGianDaChon().revalidate();
            panelThemTuyen.getPnlGaTrungGianDaChon().repaint();
        });

        panelThemTuyen.getPnlGaTrungGianDaChon().add(btnGaTag);
        panelThemTuyen.getPnlGaTrungGianDaChon().revalidate();
        panelThemTuyen.getPnlGaTrungGianDaChon().repaint();
    }

    private void capNhatDanhSachVaTinhKC() {
        DefaultTableModel model = panelThemTuyen.getModelGaChiTiet();
        model.setRowCount(0);

        String tenGaDi = panelThemTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelThemTuyen.getTxtGaDich().getText().trim();
        if ( tenGaDi.isEmpty() || tenGaDen.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Vui lòng chọn Ga Xuất Phát và Ga Đích trước khi tính khoảng cách.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            panelThemTuyen.getTxtDoDaiQuangDuong().setText("0");
            return;
        }
        if(!dsGaCoSan.containsKey(tenGaDi) || !dsGaCoSan.containsKey(tenGaDen)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga Xuất Phát hoặc Ga Đích không tồn tại trong hệ thống.", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            panelThemTuyen.getTxtDoDaiQuangDuong().setText("Lỗi dữ liệu");
            return;
        }

        Ga gaXP = dsGaCoSan.get(tenGaDi);
        Ga gaDich = dsGaCoSan.get(tenGaDen);

        List<Ga> toanBoTuyen = new ArrayList<>();
        toanBoTuyen.add(gaXP);
        toanBoTuyen.addAll(dsGaDaChon);
        toanBoTuyen.add(gaDich);

        int accumulatedDistance = 0;
        Ga gaTruoc = null;
        Ga gaHienTai = null;
        boolean isLoiKhoangCacg = false;

        for (int i = 0; i < toanBoTuyen.size(); i++) {
            gaHienTai = toanBoTuyen.get(i);
            String loaiGa;
            int kcTuGaXP = 0;
            int kcDoan = 0;

            if (gaTruoc != null) {
                kcDoan = tuyenBus.tinhKhoangCachTongDijsktra(gaTruoc.getGaID(), gaHienTai.getGaID());
                if(kcDoan == -1){
                    isLoiKhoangCacg = true;
                    break;
                }
                accumulatedDistance += kcDoan;
            }

            kcTuGaXP = accumulatedDistance;

            if (i == 0) loaiGa = "Xuất Phát";
            else if (i == toanBoTuyen.size() - 1) loaiGa = "Đích";
            else loaiGa = "Trung Gian";

            model.addRow(new Object[]{
                    gaHienTai.getTenGa(),
                    loaiGa,
                    kcTuGaXP
            });

            gaTruoc = gaHienTai;
        }
        if (isLoiKhoangCacg) {
            JOptionPane.showMessageDialog(panelThemTuyen,
                    "Lỗi: Không tìm thấy đường đi hoặc khoảng cách chuẩn giữa " +
                            (gaTruoc != null ? gaTruoc.getTenGa() : "N/A") + " và " +
                            (gaHienTai != null ? gaHienTai.getTenGa() : "N/A") + ".\nVui lòng kiểm tra lại dữ liệu.",
                    "Lỗi Dữ Liệu Tuyến", JOptionPane.ERROR_MESSAGE);
            model.setRowCount(0);

            panelThemTuyen.getTxtDoDaiQuangDuong().setText("Lỗi dữ liệu");

    }
        else {
            panelThemTuyen.getTxtDoDaiQuangDuong().setText(String.valueOf(accumulatedDistance));
        }
    }

    private void xuLyLuuTuyen() {
        String maTuyen = panelThemTuyen.getTxtMaTuyen().getText().trim();
        String moTa = panelThemTuyen.getTxtMoTa().getText().trim();
        String doDaiKCStr = panelThemTuyen.getTxtDoDaiQuangDuong().getText().trim();

        if (maTuyen.startsWith("LỖI") || maTuyen.isEmpty() || tuyenBus.kiemTraMaTuyuenDaTonTai(maTuyen)) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Mã tuyến không hợp lệ hoặc đã tồn tại.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (doDaiKCStr.equals("Lỗi dữ liệu") || doDaiKCStr.equals("0") || moTa.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Dữ liệu không hợp lệ (Khoảng cách hoặc Mô tả).", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (panelThemTuyen.getModelGaChiTiet().getRowCount() < 2) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Tuyến phải có ít nhất ga xuất phát và ga đích.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String luaChonTrangThai = panelThemTuyen.getCboTrangThai().getSelectedItem().toString();
        boolean trangThai = luaChonTrangThai.equals("Hoạt Động");
        try {
            Integer.parseInt(doDaiKCStr);
            Tuyen tuyenMoi = new Tuyen(maTuyen, moTa, trangThai);

            List<TuyenChiTiet> dsTuyenChiTiet = new ArrayList<>();
            DefaultTableModel modelChiTiet = panelThemTuyen.getModelGaChiTiet();

            for (int i = 0; i < modelChiTiet.getRowCount(); i++) {
                String tenGa = (String) modelChiTiet.getValueAt(i, 0);
                Object kcXPValue = modelChiTiet.getValueAt(i, 2);
                int kcXP;
                if (kcXPValue instanceof Integer) {
                    kcXP = (Integer) kcXPValue;
                } else if (kcXPValue instanceof String) {
                    kcXP = Integer.parseInt((String) kcXPValue);
                } else {
                    throw new IllegalArgumentException("Khoảng cách Ga (" + tenGa + ") không hợp lệ.");
                }
                Ga ga = dsGaCoSan.get(tenGa);
                TuyenChiTiet tct = new TuyenChiTiet(tuyenMoi, ga, i + 1, kcXP);
                dsTuyenChiTiet.add(tct);
            }
            boolean luuTuyenThanhCong = tuyenBus.themTuyen(tuyenMoi, dsTuyenChiTiet);
            if (luuTuyenThanhCong) {
                JOptionPane.showMessageDialog(panelThemTuyen, "Đã lưu tuyến mới " + maTuyen + " thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(panelThemTuyen, "Lưu tuyến thất bại! Vui lòng kiểm tra lại dữ liệu.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(panelThemTuyen, e.getMessage(), "Lỗi Nghiệp Vụ", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panelThemTuyen, "Đã xảy ra lỗi hệ thống: " + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xuLyHuyBo() {
        int choice = JOptionPane.showConfirmDialog(
                panelThemTuyen,
                "Bạn có chắc chắn muốn hủy bỏ thao tác thêm tuyến?\nMọi thay đổi chưa lưu sẽ bị mất.",
                "Xác nhận Hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            dialog.dispose();
        }
    }

    public void lamMoi() {
        panelThemTuyen.getTxtGaXuatPhat().setText("");
        panelThemTuyen.getTxtGaDich().setText("");
        panelThemTuyen.getTxtGaTrungGian().setText("");
        panelThemTuyen.getTxtMaTuyen().setText("");
        panelThemTuyen.getTxtDoDaiQuangDuong().setText("");
        panelThemTuyen.getTxtMoTa().setText("");

        panelThemTuyen.getTxtMaTuyen().setForeground(Color.BLACK);

        dsGaDaChon.clear();

        panelThemTuyen.getPnlGaTrungGianDaChon().removeAll();
        panelThemTuyen.getPnlGaTrungGianDaChon().revalidate();
        panelThemTuyen.getPnlGaTrungGianDaChon().repaint();

        panelThemTuyen.getModelGaChiTiet().setRowCount(0);

        ppGaXuatPhat.setVisible(false);
        ppGaDich.setVisible(false);
        ppGaTrungGian.setVisible(false);
    }
}
