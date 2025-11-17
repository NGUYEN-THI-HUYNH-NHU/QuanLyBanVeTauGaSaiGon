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
import gui.application.UngDung;
import gui.application.form.quanLyTuyen.PanelQuanLyTuyen;
import gui.application.form.quanLyTuyen.PanelThemTuyen;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class ThemTuyen_CTRL {
    private final PanelThemTuyen panelThemTuyen;
    private final Tuyen_BUS tuyenBus;
    private final Ga_BUS gaBus;
    private final KhoangCachChuan_DAO khoangCachChuanDao;

    private final Map<String, Ga> dsGaCoSan;
    private final List<Ga> dsGaDaChon;

    public ThemTuyen_CTRL(PanelThemTuyen panelThemTuyen){
        this.panelThemTuyen = panelThemTuyen;
        tuyenBus = new Tuyen_BUS();
        gaBus = new Ga_BUS();
        khoangCachChuanDao = new KhoangCachChuan_DAO();

        dsGaCoSan = new LinkedHashMap<>();
        dsGaDaChon = new ArrayList<>();

        khoiTaoDuLieuBanDau();
        thietLapListener();
    }

    private void khoiTaoDuLieuBanDau(){
       List<String> dsGa = gaBus.getDanhSachTenGa();
       for(String tenGa : dsGa){
              Ga ga = gaBus.getGaByTenGa(tenGa);
              if(ga != null){
                dsGaCoSan.put(tenGa, ga);
              }
       }
    }

    private void thietLapListener(){
       taoPopGoiY(
               panelThemTuyen.getTxtGaXuatPhat(),
                panelThemTuyen.getPpGaXuatPhat(),
                panelThemTuyen.getListGaXuatPhat(),
                gaBus::timTenGaChoGoiY,
               this::capNhatMaTuyen
       );

       taoPopGoiY(
                panelThemTuyen.getTxtGaDich(),
                panelThemTuyen.getPpGaDich(),
                panelThemTuyen.getListGaDich(),
                gaBus::timTenGaChoGoiY,
                this::capNhatMaTuyen
       );

       taoPopGoiY(
                panelThemTuyen.getTxtGaTrungGian(),
                panelThemTuyen.getPpGaTrungGian(),
                panelThemTuyen.getListGaTrungGian(),
                gaBus::timTenGaChoGoiY,
               () -> xuLyChonGaTrungGian()
       );

       panelThemTuyen.getBtnXacNhanTinhKC().addActionListener(e -> capNhatDanhSachVaTinhKC());
       panelThemTuyen.getBtnHuy().addActionListener(e -> xuLyHuyBo());
       panelThemTuyen.getBtnLuu().addActionListener(e -> xuLyLuuTuyen());

    }

    private void capNhatMaTuyen() {
        String tenGaDi = panelThemTuyen.getTxtGaXuatPhat().getText().trim(); // <-- SỬA
        String tenGaDen = panelThemTuyen.getTxtGaDich().getText().trim(); // <-- SỬA

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

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem) {
        String input = txt.getText().trim();
        if(input.isEmpty()){
            pp.setVisible(false);
            return;
        }

        List<String> ds = timKiem.apply(input);
        if(ds == null || ds.isEmpty()){
            pp.setVisible(false);
            return;
        }

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 8));

        pp.show(txt, 0, txt.getHeight());
        txt.requestFocusInWindow();
    }

    private void taoPopGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem, Runnable actionOnSelect) {

        pp.setFocusable(false);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        // Hiển thị gợi ý khi input thay đổi
        txt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                hienThiGoiY(txt, lst, pp, timKiem);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                hienThiGoiY(txt, lst, pp, timKiem);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        // Mouse click chọn item -> CHỈ CẬP NHẬT COMBOBOX VÀ GỌI HÀM XỬ LÝ LIÊN QUAN
        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = lst.locationToIndex(e.getPoint());
                if (index >= 0) {
                    txt.setText(lst.getModel().getElementAt(index));
                    pp.setVisible(false);
                    actionOnSelect.run();
                }
            }
        });

        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                int selectedIndex = lst.getSelectedIndex();
                if (pp.isVisible()) {
                    switch (key) {
                        case KeyEvent.VK_DOWN:
                            if (selectedIndex < lst.getModel().getSize() - 1) {
                                lst.setSelectedIndex(selectedIndex + 1);
                                lst.ensureIndexIsVisible(selectedIndex + 1);
                            }
                            e.consume();
                            break;
                        case KeyEvent.VK_UP:
                            if (selectedIndex > 0) {
                                lst.setSelectedIndex(selectedIndex - 1);
                                lst.ensureIndexIsVisible(selectedIndex - 1);
                            }
                            e.consume();
                            break;
                        case KeyEvent.VK_ENTER:
                            String selected = lst.getSelectedValue();
                            if (selected != null) {
                                txt.setText(selected);
                            }
                            pp.setVisible(false);
                            actionOnSelect.run();
                            e.consume();
                            break;
                        case KeyEvent.VK_ESCAPE:
                            pp.setVisible(false);
                            e.consume();
                            break;
                    }
                }
            }
        });

        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // Tạm ẩn popup
                Timer timer = new Timer(200, (ae) -> {
                    if (!pp.isFocusOwner() && !(lst.isFocusOwner())) {
                        pp.setVisible(false);
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }


    private void xuLyChonGaTrungGian(){
        JTextField txtGaMoi = panelThemTuyen.getTxtGaTrungGian();
        String tenGaMoi = txtGaMoi.getText().trim();
        if(tenGaMoi == null || tenGaMoi.isEmpty()){
            return;
        }

       String gaDi = panelThemTuyen.getTxtGaXuatPhat().getText().trim();
        String gaDen = panelThemTuyen.getTxtGaDich().getText().trim();

        if(!dsGaCoSan.containsKey(tenGaMoi)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga trung gian không tồn tại trong hệ thống!", "Lỗi chọn ga trung gian", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Ga gaMoi = dsGaCoSan.get(tenGaMoi);
        if(tenGaMoi.equals(gaDi) || tenGaMoi.equals(gaDen)){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga trung gian không được trùng với ga xuất phát hoặc ga đích!", "Lỗi chọn ga trung gian", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(dsGaDaChon.stream().anyMatch(g -> g.getTenGa().equals(tenGaMoi))){
            JOptionPane.showMessageDialog(panelThemTuyen, "Ga trung gian đã được chọn!", "Lỗi chọn ga trung gian", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dsGaDaChon.add(gaMoi);
        taovaThemTagGa(gaMoi);
        txtGaMoi.setText("");
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

        String tenGaDi = (String) panelThemTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = (String) panelThemTuyen.getTxtGaDich().getText().trim();

        if ( tenGaDi.isEmpty() || tenGaDen.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Vui lòng chọn Ga Xuất Phát và Ga Đích trước khi tính khoảng cách.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            panelThemTuyen.getTxtDoDaiQuangDuong().setText("0");
            return;
        }

        Ga gaXP = dsGaCoSan.get(tenGaDi);
        Ga gaDich = dsGaCoSan.get(tenGaDen);

        if (gaXP == null || gaDich == null) return;

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
        if(maTuyen.startsWith("LỖI") || maTuyen.isEmpty()){
            JOptionPane.showMessageDialog(panelThemTuyen, "Mã tuyến không hợp lệ do đã tồn tại tuyến tương tự.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tuyenBus.kiemTraMaTuyuenDaTonTai(maTuyen)) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Mã tuyến " + maTuyen + " đã tồn tại. Vui lòng chọn lại.", "Lỗi Trùng Lặp", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (doDaiKCStr.equals("Lỗi Dữ Liệu") || doDaiKCStr.equals("0") || maTuyen.isEmpty() || moTa.isEmpty()) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Không thể lưu tuyến do có lỗi về khoảng cách giữa các ga.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (panelThemTuyen.getModelGaChiTiet().getRowCount() < 2) {
            JOptionPane.showMessageDialog(panelThemTuyen, "Tuyến phải có ít nhất ga xuất phát và ga đích.", "Lỗi Lưu Tuyến", JOptionPane.ERROR_MESSAGE);
        }
        try {
            Integer.parseInt(doDaiKCStr);
            Tuyen tuyenMoi = new Tuyen(maTuyen, moTa);

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
                PanelQuanLyTuyen panelQuanLy = new PanelQuanLyTuyen(panelThemTuyen.getNhanVienThucHien());
                UngDung.showGiaoDienChinh(panelQuanLy);
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
            PanelQuanLyTuyen panelQuanLy = new PanelQuanLyTuyen(panelThemTuyen.getNhanVienThucHien());
            UngDung.showGiaoDienChinh(panelQuanLy);
        }
    }
}
