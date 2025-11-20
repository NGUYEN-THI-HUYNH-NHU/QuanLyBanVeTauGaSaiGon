package gui.application.form.doiVe;
/*
 * @(#) MappingVeTableModel.java  1.0  [4:16:16 PM] Nov 20, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 20, 2025
 * @version: 1.0
 */

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import entity.Ve;
import gui.application.form.banVe.VeSession;

public class MappingVeTableModel extends AbstractTableModel {

	// Định nghĩa các cột
	public static final int COL_STT = 0;
	public static final int COL_HANH_KHACH = 1;
	public static final int COL_VE_CU_INFO = 2;
	public static final int COL_VE_CU_GIA = 3;
	public static final int COL_LE_PHI = 4;
	public static final int COL_CHON_VE_MOI = 5; // Cột ComboBox
	public static final int COL_VE_MOI_INFO = 6;
	public static final int COL_VE_MOI_GIA = 7;
	public static final int COL_CHENH_LECH = 8;

	private final String[] columnNames = { "STT", "Hành khách", "Thông tin vé cũ", "Giá vé cũ", "Lệ phí đổi",
			"Chọn vé mới", "Thông tin vé mới", "Giá vé mới", "Chênh lệch" };

	private List<MappingRow> rows;

	public MappingVeTableModel() {
		this.rows = new ArrayList<>();
	}

	/**
	 * Khởi tạo dữ liệu cho bảng. Tự động map 1-1 theo thứ tự index nếu số lượng
	 * bằng nhau.
	 */
	public void setData(List<VeDoiRow> listVeCu, List<VeSession> listVeMoi) {
		this.rows.clear();
		if (listVeCu != null) {
			for (int i = 0; i < listVeCu.size(); i++) {
				VeDoiRow old = listVeCu.get(i);
				VeSession ne = null;

				// Tự động gán vé mới tương ứng theo thứ tự (nếu có)
				if (listVeMoi != null && i < listVeMoi.size()) {
					ne = listVeMoi.get(i);
				}

				rows.add(new MappingRow(old, ne));
			}
		}
		fireTableDataChanged();
	}

	public List<MappingRow> getRows() {
		return rows;
	}

	public MappingRow getRowAt(int rowIndex) {
		if (rowIndex >= 0 && rowIndex < rows.size()) {
			return rows.get(rowIndex);
		}
		return null;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == COL_VE_CU_GIA || columnIndex == COL_LE_PHI || columnIndex == COL_VE_MOI_GIA
				|| columnIndex == COL_CHENH_LECH) {
			return Double.class;
		}
		if (columnIndex == COL_CHON_VE_MOI) {
			return VeSession.class; // Để ComboBox render object
		}
		return Object.class; // Các cột khác là String hoặc Object
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// Chỉ cho phép sửa cột Chọn vé mới (ComboBox)
		return columnIndex == COL_CHON_VE_MOI;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MappingRow row = rows.get(rowIndex);
		Ve veCuEntity = row.getVeCu().getVe();

		switch (columnIndex) {
		case COL_STT:
			return rowIndex + 1;
		case COL_HANH_KHACH:
			return row.getVeCu().getHanhKhach();
		case COL_VE_CU_INFO:
			return veCuEntity.thongTinVeDoi(); // Logic hiển thị ngắn gọn vé cũ
		case COL_VE_CU_GIA:
			return veCuEntity.getGia();
		case COL_LE_PHI:
			return row.getVeCu().getLePhiDoiVe();
		case COL_CHON_VE_MOI:
			return row.getVeMoi(); // Trả về object VeSession để ComboBox hiển thị
		case COL_VE_MOI_INFO:
			if (row.getVeMoi() != null) {
				VeSession v = row.getVeMoi();
				return v.prettyString();
			}
			return "Chưa chọn vé";
		case COL_VE_MOI_GIA:
			return (row.getVeMoi() != null) ? (double) row.getVeMoi().getGia() : 0.0;
		case COL_CHENH_LECH:
			return row.getChenhLech();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == COL_CHON_VE_MOI) {
			MappingRow row = rows.get(rowIndex);
			if (aValue instanceof VeSession) {
				row.setVeMoi((VeSession) aValue);

				// Khi thay đổi vé mới, các cột sau sẽ bị ảnh hưởng:
				// Info vé mới, Giá vé mới, Chênh lệch
				fireTableRowsUpdated(rowIndex, rowIndex);
			}
		}
	}

	/**
	 * Tính tổng tiền cần thanh toán (hoặc hoàn lại) > 0: Khách cần trả thêm < 0:
	 * Hoàn tiền cho khách
	 */
	public double getTongTienChenhLech() {
		double total = 0;
		for (MappingRow row : rows) {
			total += row.getChenhLech();
		}
		return total;
	}

	public void clear() {
		rows.clear();
		fireTableDataChanged();
	}

	public void setRows(List<MappingRow> list) {
		rows.clear();
		if (list != null) {
			rows.addAll(list);
		}
		fireTableDataChanged();
	}

	public List<MappingRow> getRowsCopy() {
		return new ArrayList<>(rows);
	}
}