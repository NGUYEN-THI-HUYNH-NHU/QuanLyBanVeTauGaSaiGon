package gui.application.form.doiVe;
/*
 * @(#) PanelDoiVeBuoc7.java  1.0  [11:18:55 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import entity.KhuyenMai;
import gui.application.form.banVe.KhuyenMaiRenderer;
import gui.application.form.banVe.VeSession;
import gui.tuyChinh.CurrencyTopRenderer;
import gui.tuyChinh.LeftTopRenderer;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class PanelDoiVeBuoc7 extends JPanel {
    private final MappingVeTableModel model;
    private final JTable table;
    private KhuyenMaiProvider khuyenMaiProvider;
    private JComboBox cbKhuyenMai;
    private TableModelListener tableUpdateListener;


    public PanelDoiVeBuoc7() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createTitledBorder("Xác nhận thông tin vé"));
        setPreferredSize(new Dimension(getWidth(), 350));

        model = new MappingVeTableModel() {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnIndex == COL_KHUYEN_MAI;
            }
        };
        table = new JTable(model);

        setUpTable();

        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);
    }

    private void setUpTable() {
        table.setRowHeight(90);

        table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setMaxWidth(30);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setMinWidth(130);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setMinWidth(160);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO).setMinWidth(160);

        // Cấu hình Cột Khuyến Mãi
        // 2. Cấu hình Cột Khuyến Mãi (Tách editor ra)
        TableColumn khuyenMaiCol = table.getColumnModel().getColumn(MappingVeTableModel.COL_KHUYEN_MAI);
        khuyenMaiCol.setMinWidth(120);

        cbKhuyenMai = new JComboBox<>();
        KhuyenMaiRenderer renderer = new KhuyenMaiRenderer();
        khuyenMaiCol.setCellRenderer(renderer);

        CurrencyTopRenderer currencyRenderer = new CurrencyTopRenderer();
        LeftTopRenderer leftTopRenderer = new LeftTopRenderer();

        // Cột Tiền
        table.getColumnModel().getColumn(MappingVeTableModel.COL_PHIEU_VIP_GIA).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_LE_PHI).setCellRenderer(currencyRenderer);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_CHENH_LECH).setCellRenderer(currencyRenderer);

        // Cột Text thường (Tên, Thông tin vé)
        table.getColumnModel().getColumn(MappingVeTableModel.COL_STT).setCellRenderer(leftTopRenderer);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_HANH_KHACH).setCellRenderer(leftTopRenderer);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_CU_INFO).setCellRenderer(leftTopRenderer);
        table.getColumnModel().getColumn(MappingVeTableModel.COL_VE_MOI_INFO).setCellRenderer(leftTopRenderer);
    }

    public void setKhuyenMaiProvider(KhuyenMaiProvider provider) {
        this.khuyenMaiProvider = provider;

        // Khởi tạo Editor SAU KHI đã có provider (và model)
        TableColumn khuyenMaiCol = table.getColumnModel().getColumn(MappingVeTableModel.COL_KHUYEN_MAI);
        // Dùng class Editor mới tách
        KhuyenMaiCellEditor editor = new KhuyenMaiCellEditor(cbKhuyenMai, provider, model);
        khuyenMaiCol.setCellEditor(editor);
    }

    // Thêm hàm để Controller đăng ký lắng nghe thay đổi
    public void addTableUpdateListener(TableModelListener l) {
        this.tableUpdateListener = l;
        model.addTableModelListener(l);
    }

    /**
     * Được gọi bởi DoiVe3Controller để đổ dữ liệu từ session vào bảng.
     */
    public void hienThiThongTin(ExchangeSession session) {
        if (session == null) {
            model.setData(null, null);
            return;
        }

        List<VeDoiRow> listVeDoi = session.getListVeCuCanDoi();
        List<VeSession> listVeMoi = session.getListVeMoiDangChon();

        model.setData(listVeDoi, listVeMoi);
    }

    /**
     * Được gọi bởi controller để bật/tắt toàn bộ panel.
     */
    public void setComponentsEnabled(boolean enabled) {
        super.setEnabled(enabled);
        table.setEnabled(enabled);
        for (Component comp : getComponents()) {
            comp.setEnabled(enabled);
        }
    }

    public MappingVeTableModel getModel() {
        return model;
    }

    public JTable getTable() {
        return table;
    }

    public interface KhuyenMaiProvider {
        List<KhuyenMai> getKhuyenMaiFor(VeSession veSession);
    }

}