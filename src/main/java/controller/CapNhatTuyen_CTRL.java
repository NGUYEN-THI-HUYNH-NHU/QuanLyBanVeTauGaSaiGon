package controller;/*
 * @ (#) CapNhatTuyen_CTRL.java   1.0     16/11/2025
package controller;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 16/11/2025
 */

import bus.Ga_BUS;
import bus.IGaBUS;
import bus.ITuyenBUS;
import bus.Tuyen_BUS;
import dto.GaDTO;
import dto.NhanVienDTO;
import dto.TuyenChiTietDTO;
import dto.TuyenDTO;
import gui.application.form.quanLyTuyen.PanelCapNhatTuyen;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CapNhatTuyen_CTRL {
    private final PanelCapNhatTuyen panelCapNhatTuyen;
    private final ITuyenBUS tuyenBus;
    private final IGaBUS gaBus;
    private final JDialog dialog;
    private final String tuyenIDCanCapNhat;
    private final Map<String, GaDTO> dsGaCoSan;
    private final List<GaDTO> dsGaDaChon;
    private boolean dangChonTuPopup = false;
    private List<String> listTenGaGoc;

    private boolean isDataLoading = false;

    public CapNhatTuyen_CTRL(PanelCapNhatTuyen panelCapNhatTuyen, JDialog dialog, String tuyenID) {
        this.panelCapNhatTuyen = panelCapNhatTuyen;
        this.dialog = dialog;
        this.tuyenIDCanCapNhat = tuyenID;
        tuyenBus = new Tuyen_BUS();
        gaBus = new Ga_BUS();

        dsGaCoSan = new LinkedHashMap<>();
        dsGaDaChon = new ArrayList<>();
        listTenGaGoc = new ArrayList<>();

        this.dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                xuLyHuyBo();
            }
        });

        khoiTaoDuLieuBanDau();
        thietLapListener();
        taiDuLieuTuyen(tuyenIDCanCapNhat);
    }

    private void khoiTaoDuLieuBanDau() {
        List<GaDTO> listAllGa = gaBus.getAllGa();
        dsGaCoSan.clear();
        listTenGaGoc.clear();
        for (GaDTO ga : listAllGa) {
            dsGaCoSan.put(ga.getTenGa(), ga);
            listTenGaGoc.add(ga.getTenGa());
        }
    }

    private void setModelToComboBox(JComboBox<String> cbo, List<String> data) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(data.toArray(new String[0]));
        cbo.setModel(model);
        cbo.setSelectedIndex(-1);
    }

    private void thietLapListener() {
        taoPopupGoiY(panelCapNhatTuyen.getTxtGaXuatPhat(),
                panelCapNhatTuyen.getPpGaXuatPhat(),
                panelCapNhatTuyen.getListGaXuatPhat(),
                input -> locDuLieu(listTenGaGoc, input),
                panelCapNhatTuyen.getTxtGaDich());

        taoPopupGoiY(panelCapNhatTuyen.getTxtGaDich(),
                panelCapNhatTuyen.getPpGaDich(),
                panelCapNhatTuyen.getListGaDich(),
                input -> locDuLieu(listTenGaGoc, input),
                panelCapNhatTuyen.getTxtGaTrungGian());

        taoPopupGoiY(panelCapNhatTuyen.getTxtGaTrungGian(),
                panelCapNhatTuyen.getPpGaTrungGian(),
                panelCapNhatTuyen.getListGaTrungGian(),
                input -> locDuLieu(listTenGaGoc, input),
                null);

        setupUpdateMaTuyenEvent(panelCapNhatTuyen.getTxtGaXuatPhat());
        setupUpdateMaTuyenEvent(panelCapNhatTuyen.getTxtGaDich());

        panelCapNhatTuyen.getTxtGaTrungGian().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Chỉ thêm khi popup đã ẩn (nghĩa là đã chọn xong ga)
                    if (!panelCapNhatTuyen.getPpGaTrungGian().isVisible()) {
                        xuLyChonGaTrungGian();
                    }
                }
            }
        });

        panelCapNhatTuyen.getBtnXacNhanTinhKC().addActionListener(e -> capNhatDanhSachVaTinhKC());
        panelCapNhatTuyen.getBtnLuu().addActionListener(e -> xuLyCapNhatTuyen());
        panelCapNhatTuyen.getBtnHuy().addActionListener(e -> xuLyHuyBo());
    }

    private void taoPopupGoiY(JTextField txt, JPopupMenu pp, JList<String> lst, Function<String, List<String>> timKiem, JComponent nextFocus) {
        pp.setFocusable(false);
        lst.setFocusable(false);
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pp.removeAll();
        pp.add(new JScrollPane(lst));

        // Lắng nghe sự kiện gõ phím
        txt.getDocument().addDocumentListener(new DocumentListener() {
            private Timer timer;

            private void update() {
                if (!txt.isEditable()) return;
                if (timer != null && timer.isRunning()) timer.stop();
                timer = new Timer(300, e -> SwingUtilities.invokeLater(() -> {
                    if (txt.isFocusOwner()) {
                        hienThiGoiY(txt, lst, pp, timKiem);
                    }
                }));
                timer.setRepeats(false);
                timer.start();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        // Click chuột cũng hiện gợi ý
        txt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!txt.isEditable()) return;
                hienThiGoiY(txt, lst, pp, timKiem);
            }
        });

        // Chọn item trong list bằng chuột
        lst.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && lst.getSelectedIndex() != -1) {
                    txt.setText(lst.getSelectedValue());
                    pp.setVisible(false);
                    capNhatMaTuyenTuDong();
                    if (nextFocus != null && nextFocus.isEnabled()) {
                        nextFocus.requestFocusInWindow();
                    }
                }
            }
        });

        // Điều hướng bằng phím mũi tên và Enter
        txt.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!txt.isEditable()) return;
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
                        if (lst.getSelectedValue() != null) {
                            txt.setText(lst.getSelectedValue());
                            pp.setVisible(false);
                            capNhatMaTuyenTuDong();
                            if (nextFocus != null) nextFocus.requestFocusInWindow();
                        }
                        e.consume();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    hienThiGoiY(txt, lst, pp, timKiem);
                }
            }
        });

        // Ẩn popup khi mất focus
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if (!pp.isFocusOwner() && !lst.isFocusOwner()) {
                        pp.setVisible(false);
                        if (txt.isEditable()) capNhatMaTuyenTuDong();
                    }
                });
            }
        });
    }

    private void hienThiGoiY(JTextField txt, JList<String> lst, JPopupMenu pp, Function<String, List<String>> timKiem) {
        if (isDataLoading) return;
        String input = txt.getText().trim();
        List<String> ds = timKiem.apply(input);

        if (ds == null || ds.isEmpty()) {
            pp.setVisible(false);
            return;
        }
        // Nếu chỉ có 1 kết quả và khớp hoàn toàn thì không hiện nữa
        if (ds.size() == 1 && ds.get(0).equalsIgnoreCase(input)) {
            pp.setVisible(false);
            return;
        }

        lst.setListData(ds.toArray(new String[0]));
        lst.setVisibleRowCount(Math.min(ds.size(), 10));

        if (txt.isShowing()) {
            pp.setPopupSize(txt.getWidth(), pp.getPreferredSize().height);
            pp.show(txt, 0, txt.getHeight());
            txt.requestFocus();
        }
    }

    private List<String> locDuLieu(List<String> src, String input) {
        if (src == null) return new ArrayList<>();
        if (input.trim().isEmpty()) return new ArrayList<>(src);
        String inputNorm = unAccent(input);
        return src.stream()
                .filter(s -> unAccent(s).contains(inputNorm))
                .limit(20)
                .collect(Collectors.toList());
    }

    private String unAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s.toLowerCase(), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private void taiDuLieuTuyen(String maTuyen) {
        if (maTuyen == null || maTuyen.isEmpty()) return;
        isDataLoading = true;
        try {
            List<TuyenChiTietDTO> dsChiTiet = tuyenBus.getDanhSachTuyenChiTiet(maTuyen);
            if (dsChiTiet == null || dsChiTiet.size() < 2) return;

            TuyenDTO tuyen = tuyenBus.getTuyenTheoMa(maTuyen);

            TuyenChiTietDTO dau = dsChiTiet.get(0);
            TuyenChiTietDTO cuoi = dsChiTiet.get(dsChiTiet.size() - 1);

            if (tuyen != null) {
                panelCapNhatTuyen.getTxtMaTuyen().setText(tuyen.getId()); // Dùng .getId() theo TuyenDTO
                panelCapNhatTuyen.getTxtMoTa().setText(tuyen.getMoTa());
                panelCapNhatTuyen.getCboTrangThai().setSelectedIndex(tuyen.isTrangThai() ? 0 : 1);
            }

            panelCapNhatTuyen.getTxtGaXuatPhat().setText(dau.getTenGa());
            panelCapNhatTuyen.getTxtGaDich().setText(cuoi.getTenGa());

            panelCapNhatTuyen.getTxtGaXuatPhat().setEditable(false);
            panelCapNhatTuyen.getTxtGaDich().setEditable(false);
            panelCapNhatTuyen.getTxtGaXuatPhat().setFocusable(false);
            panelCapNhatTuyen.getTxtGaDich().setFocusable(false);
            Color readOnlyColor = new Color(240, 240, 240);
            panelCapNhatTuyen.getTxtGaXuatPhat().setBackground(readOnlyColor);
            panelCapNhatTuyen.getTxtGaDich().setBackground(readOnlyColor);

            dsGaDaChon.clear();
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().removeAll();

            if (dsChiTiet.size() > 2) {
                for (int i = 1; i < dsChiTiet.size() - 1; i++) {
                    TuyenChiTietDTO tct = dsChiTiet.get(i);

                    GaDTO gaTG = GaDTO.builder()
                            .id(tct.getGaID())
                            .tenGa(tct.getTenGa())
                            .build();

                    dsGaDaChon.add(gaTG);
                    taovaThemTagGa(gaTG);
                }
            }

            capNhatDanhSachVaTinhKC();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isDataLoading = false;
        }
    }

    private void setupComboBoxAutocomplete(JComboBox<String> comboBox, List<String> sourceData) {
        comboBox.setEditable(true);
        final JTextField textfield = (JTextField) comboBox.getEditor().getEditorComponent();

        Timer debounceTimer = new Timer(300, e -> {
            if (!textfield.isShowing()) return;
            if (isDataLoading) return;

            String text = textfield.getText();
            List<String> filteredList;
            if (text.trim().isEmpty()) {
                filteredList = new ArrayList<>(sourceData);
            } else {
                String textNormalized = unAccent(text);
                filteredList = sourceData.stream()
                        .filter(item -> unAccent(item).contains(textNormalized))
                        .collect(Collectors.toList());
            }

            String currentText = textfield.getText();
            int caretPosition = textfield.getCaretPosition();

            DefaultComboBoxModel<String> newModel = new DefaultComboBoxModel<>(filteredList.toArray(new String[0]));
            comboBox.setModel(newModel);

            comboBox.setSelectedItem(null);
            textfield.setText(currentText);

            try {
                if (caretPosition > currentText.length()) caretPosition = currentText.length();
                textfield.setCaretPosition(caretPosition);
            } catch (Exception ex) {
            }

            if (!filteredList.isEmpty() && !text.trim().isEmpty()) {
                comboBox.showPopup();
            } else {
                comboBox.hidePopup();
            }
        });
        debounceTimer.setRepeats(false);

        textfield.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN ||
                        e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT ||
                        e.getKeyCode() == KeyEvent.VK_ENTER) return;
                debounceTimer.restart();
            }
        });
    }

    private void setupUpdateMaTuyenEvent(JTextField txt) {
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                capNhatMaTuyenTuDong();
            }
        });
    }

    private void capNhatMaTuyenTuDong() {
        if (isDataLoading) return;
        String tenGaDi = panelCapNhatTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelCapNhatTuyen.getTxtGaDich().getText().trim();

        if (!tenGaDi.isEmpty() && !tenGaDen.isEmpty()) {
            String maMoi = tuyenBus.taoMaTuyenCoSo(tenGaDi, tenGaDen);
            if (!maMoi.equals(panelCapNhatTuyen.getTxtMaTuyen().getText())) {
                if (tuyenBus.kiemTraMaTuyuenDaTonTai(maMoi) && !maMoi.equals(tuyenIDCanCapNhat)) {
                    panelCapNhatTuyen.getTxtMaTuyen().setText(maMoi);
                    panelCapNhatTuyen.getTxtMaTuyen().setForeground(Color.RED);
                } else {
                    panelCapNhatTuyen.getTxtMaTuyen().setText(maMoi);
                    panelCapNhatTuyen.getTxtMaTuyen().setForeground(Color.BLACK);
                }
            }
        }
    }


    private void xuLyChonGaTrungGian() {
        String tenGaMoi = panelCapNhatTuyen.getTxtGaTrungGian().getText().trim();
        if (tenGaMoi.isEmpty()) return;

        String tenGaXP = panelCapNhatTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelCapNhatTuyen.getTxtGaDich().getText().trim();

        if (!dsGaCoSan.containsKey(tenGaMoi)) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Tên ga '" + tenGaMoi + "' không tồn tại trong hệ thống!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (tenGaMoi.equals(tenGaXP) || tenGaMoi.equals(tenGaDen)) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga trung gian không được trùng với Ga XP hoặc Ga Đích!", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (dsGaDaChon.stream().anyMatch(g -> g.getTenGa().equals(tenGaMoi))) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga này đã có trong danh sách!", "Lỗi trùng lặp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GaDTO gaMoi = dsGaCoSan.get(tenGaMoi);
        GaDTO gaXP = dsGaCoSan.get(tenGaXP);
        GaDTO gaDen = dsGaCoSan.get(tenGaDen);

        try {
            int kcTong = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getId(), gaDen.getId());
            int kcTuDauDenMoi = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getId(), gaMoi.getId());
            int kcTuMoiDenDich = tuyenBus.tinhKhoangCachTongDijsktra(gaMoi.getId(), gaDen.getId());

            if (kcTong == -1 || kcTuDauDenMoi == -1 || kcTuMoiDenDich == -1) {
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Không thể tính toán khoảng cách.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (Math.abs((kcTuDauDenMoi + kcTuMoiDenDich) - kcTong) > 20) {
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Ga " + tenGaMoi + " không nằm trên lộ trình hợp lệ.", "Sai lộ trình", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!dsGaDaChon.isEmpty()) {
                GaDTO gaCuoiCung = dsGaDaChon.get(dsGaDaChon.size() - 1);
                int kcTuDauDenCuoiList = tuyenBus.tinhKhoangCachTongDijsktra(gaXP.getId(), gaCuoiCung.getId());
                if (kcTuDauDenMoi <= kcTuDauDenCuoiList) {
                    JOptionPane.showMessageDialog(panelCapNhatTuyen, "Sai thứ tự hành trình! Ga mới phải nằm sau ga trước đó.", "Sai thứ tự", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            dsGaDaChon.add(gaMoi);
            taovaThemTagGa(gaMoi);
            panelCapNhatTuyen.getTxtGaTrungGian().setText("");
            panelCapNhatTuyen.getPpGaTrungGian().setVisible(false);
            capNhatDanhSachVaTinhKC();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void taovaThemTagGa(GaDTO ga) {
        JButton btnGaTag = new JButton(ga.getTenGa() + " \u2715");
        btnGaTag.setMargin(new Insets(3, 5, 3, 5));
        btnGaTag.addActionListener(e -> {
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().remove(btnGaTag);
            dsGaDaChon.remove(ga);
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().revalidate();
            panelCapNhatTuyen.getPnlGaTrungGianDaChon().repaint();
        });
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().add(btnGaTag);
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().revalidate();
        panelCapNhatTuyen.getPnlGaTrungGianDaChon().repaint();
    }

    private void capNhatDanhSachVaTinhKC() {
        DefaultTableModel model = panelCapNhatTuyen.getModelGaChiTiet();
        model.setRowCount(0);

        String tenGaDi = panelCapNhatTuyen.getTxtGaXuatPhat().getText().trim();
        String tenGaDen = panelCapNhatTuyen.getTxtGaDich().getText().trim();

        if (tenGaDi.isEmpty() || tenGaDen.isEmpty()) {
            panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText("0");
            return;
        }
        if (!dsGaCoSan.containsKey(tenGaDi) || !dsGaCoSan.containsKey(tenGaDen)) return;

        GaDTO gaXP = dsGaCoSan.get(tenGaDi);
        GaDTO gaDich = dsGaCoSan.get(tenGaDen);

        List<GaDTO> toanBoTuyen = new ArrayList<>();
        toanBoTuyen.add(gaXP);
        toanBoTuyen.addAll(dsGaDaChon);
        toanBoTuyen.add(gaDich);

        int accumulatedDistance = 0;
        for (int i = 0; i < toanBoTuyen.size(); i++) {
            GaDTO gaHienTai = toanBoTuyen.get(i);
            String loaiGa = (i == 0) ? "Xuất Phát" : (i == toanBoTuyen.size() - 1 ? "Đích" : "Trung Gian");
            int kcDoan = 0;
            if (i > 0) {
                GaDTO gaTruoc = toanBoTuyen.get(i - 1);
                kcDoan = tuyenBus.tinhKhoangCachTongDijsktra(gaTruoc.getId(), gaHienTai.getId());
                if (kcDoan == -1) {
                    JOptionPane.showMessageDialog(panelCapNhatTuyen, "Không tìm thấy đường đi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                accumulatedDistance += kcDoan;
            }
            model.addRow(new Object[]{gaHienTai.getTenGa(), loaiGa, accumulatedDistance});
        }
        panelCapNhatTuyen.getTxtDoDaiQuangDuong().setText(String.valueOf(accumulatedDistance));
    }

    private void xuLyCapNhatTuyen() {
        String maTuyenMoi = panelCapNhatTuyen.getTxtMaTuyen().getText().trim();
        String moTa = panelCapNhatTuyen.getTxtMoTa().getText().trim();

        if (maTuyenMoi.isEmpty() || panelCapNhatTuyen.getTxtDoDaiQuangDuong().getText().equals("0")) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Dữ liệu không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!maTuyenMoi.equals(tuyenIDCanCapNhat) && tuyenBus.kiemTraMaTuyuenDaTonTai(maTuyenMoi)) {
            JOptionPane.showMessageDialog(panelCapNhatTuyen, "Mã tuyến mới đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean trangThai = panelCapNhatTuyen.getCboTrangThai().getSelectedIndex() == 0;
            TuyenDTO tuyenCapNhatDTO = TuyenDTO.builder()
                    .id(maTuyenMoi)
                    .moTa(moTa)
                    .trangThai(trangThai)
                    .build();
            List<TuyenChiTietDTO> dsChiTietDTO = new ArrayList<>();
            DefaultTableModel model = panelCapNhatTuyen.getModelGaChiTiet();

            for (int i = 0; i < model.getRowCount(); i++) {
                String tenGa = model.getValueAt(i, 0).toString();
                int kcTuDau = Integer.parseInt(model.getValueAt(i, 2).toString());

                GaDTO gaDTO = dsGaCoSan.get(tenGa);

                TuyenChiTietDTO tctDTO = TuyenChiTietDTO.builder()
                        .tuyenID(tuyenCapNhatDTO.getId())
                        .gaID(gaDTO.getId())
                        .tenGa(gaDTO.getTenGa())
                        .thuTu(i + 1)
                        .khoangCachTuGaXuatPhatKm(kcTuDau)
                        .build();

                dsChiTietDTO.add(tctDTO);
            }
            NhanVienDTO nvDTO = panelCapNhatTuyen.getNhanVienThucHien();

            if (tuyenBus.capNhatTuyen(tuyenCapNhatDTO, dsChiTietDTO, nvDTO)) {
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Cập nhật thành công!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(panelCapNhatTuyen, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xuLyHuyBo() {
        int choice = JOptionPane.showConfirmDialog(
                panelCapNhatTuyen,
                "Bạn có chắc chắn muốn hủy bỏ thao tác thêm tuyến?\nMọi thay đổi chưa lưu sẽ bị mất.",
                "Xác nhận Hủy",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            dialog.dispose();
        }
    }

}
