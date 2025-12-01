package gui.application.form.banVe;
/*
 * @(#) HanhKhachTableModel.java  1.0  [7:25:41 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import entity.KhuyenMai;

public class HanhKhachTableModel extends AbstractTableModel {
	private final String[] cols = { "Hành khách", "Vé", "Giá", "Phòng chờ", "Giá dịch vụ", "Giảm đối tượng",
			"Khuyến mãi", "Giảm KM", "Thành tiền", "" };

	public static final int COL_HANH_KHACH = 0;
	public static final int COL_VE = 1;
	public static final int COL_GIA = 2;
	public static final int COL_PHONG_CHO = 3;
	public static final int COL_GIA_DV = 4;
	public static final int COL_GIAM_DT = 5;
	public static final int COL_KHUYEN_MAI = 6;
	public static final int COL_GIAM_KM = 7;
	public static final int COL_THANH_TIEN = 8;

	private final List<PassengerRow> rows = new ArrayList<>();

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return cols.length;
	}

	@Override
	public String getColumnName(int column) {
		return cols[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		if (columnIndex == 3) {
			return Boolean.class;
		}
		if (columnIndex == 6) {
			return KhuyenMai.class;
		}
		if (columnIndex == 0) {
			return PassengerRow.class;
		}
		if (columnIndex == 2 || columnIndex == 4 || columnIndex == 5 || columnIndex == 7 || columnIndex == 8) {
			return Double.class;
		}
		return Object.class;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PassengerRow p = rows.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return p;
		case 1:
			return p.getVeSession().prettyString();
		case 2:
			return p.getVeSession().getVe().getGia();
		case 3:
			return p.getVeSession().getPhiPhieuDungPhongChoVIP() > 0;
		case 4:
			return p.getVeSession().getPhiPhieuDungPhongChoVIP();
		case 5:
			return p.getVeSession().getGiamDoiTuong();
		case 6:
			return p.getVeSession().getKhuyenMaiApDung();
		case 7:
			return p.getVeSession().getGiamKM();
		case 8:
			return p.getVeSession().getVe().getGia() + p.getVeSession().getPhiPhieuDungPhongChoVIP()
					- p.getVeSession().getGiamKM() - p.getVeSession().getGiamDoiTuong();
		case 9:
			return "Xóa";
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 0 || columnIndex == 6 || columnIndex == 3;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		PassengerRow p = rows.get(rowIndex);
		VeSession v = p.getVeSession();

		if (columnIndex == COL_HANH_KHACH && aValue instanceof PassengerRow) {
			PassengerRow src = (PassengerRow) aValue;
			p.setHoTen(src.getHoTen());
			p.setLoaiDoiTuong(src.getLoaiDoiTuong());
			p.setSoGiayTo(src.getSoGiayTo());
			fireTableRowsUpdated(rowIndex, rowIndex);
		} else if (columnIndex == COL_PHONG_CHO) {
			// Nhận giá trị true/false từ JCheckBox
			Boolean isSelected = (Boolean) aValue;

			if (isSelected) {
				p.getVeSession().setPhiPhieuDungPhongChoVIP(20000);
			} else {
				p.getVeSession().setPhiPhieuDungPhongChoVIP(0);
			}

			// Thông báo cho bảng cập nhật lại các ô bị ảnh hưởng (Giá dịch vụ & Thành tiền)
			fireTableCellUpdated(rowIndex, COL_GIA_DV);
			fireTableCellUpdated(rowIndex, COL_THANH_TIEN);
		} else if (columnIndex == COL_KHUYEN_MAI) {
			KhuyenMai km = (KhuyenMai) aValue;
			v.setKhuyenMaiApDung(km);

			// TÍNH TOÁN LẠI TIỀN GIẢM KM
			int tienGiam = 0;
			if (km != null) {
				if (km.getTyLeGiamGia() > 0) {
					tienGiam = (int) (v.getVe().getGia() * (km.getTyLeGiamGia()));
				} else if (km.getTienGiamGia() > 0) {
					tienGiam = (int) km.getTienGiamGia();
				}
				// (Có thể thêm logic giới hạn tiền giảm tối đa nếu cần)
			}
			v.setGiamKM(tienGiam);
			// Cập nhật cả dòng để tính lại Thành tiền
			fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	public void setRows(List<PassengerRow> list) {
		rows.clear();
		if (list != null) {
			rows.addAll(list);
		}
		fireTableDataChanged();
	}

	public void addRow(PassengerRow r) {
		rows.add(r);
		int idx = rows.size() - 1;
		fireTableRowsInserted(idx, idx);
	}

	public PassengerRow getRowAt(int idx) {
		return rows.get(idx);
	}

	public List<PassengerRow> getRowsCopy() {
		return new ArrayList<>(rows);
	}

	public void clear() {
		rows.clear();
		fireTableDataChanged();
	}
}