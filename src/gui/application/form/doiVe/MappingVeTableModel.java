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

import entity.KhuyenMai;
import entity.Ve;
import gui.application.form.banVe.VeSession;

public class MappingVeTableModel extends AbstractTableModel {
	public static final int COL_STT = 0;
	public static final int COL_HANH_KHACH = 1;
	public static final int COL_VE_CU_INFO = 2;
	public static final int COL_VE_CU_GIA = 3;
	public static final int COL_CHON_VE_MOI = 4;
	public static final int COL_VE_MOI_INFO = 5;
	public static final int COL_VE_MOI_GIA = 6;
	public static final int COL_KHUYEN_MAI = 7;
	public static final int COL_GIAM_KM = 8;
	public static final int COL_CHON_PHIEU_VIP = 9;
	public static final int COL_PHIEU_VIP_GIA = 10;
	public static final int COL_LE_PHI = 11;
	public static final int COL_CHENH_LECH = 12;

	private final String[] columnNames = { "STT", "Hành khách", "Thông tin vé cũ", "Giá vé cũ", "Chọn vé mới",
			"Thông tin vé mới", "Giá vé mới", "Chọn KM", "Giảm KM", "Phòng chờ", "Giá dịch vụ", "Lệ phí đổi",
			"Chênh lệch" };

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
				|| columnIndex == COL_GIAM_KM || columnIndex == COL_PHIEU_VIP_GIA || columnIndex == COL_CHENH_LECH) {
			return Double.class;
		}
		if (columnIndex == COL_CHON_VE_MOI) {
			return VeSession.class;
		}

		if (columnIndex == COL_KHUYEN_MAI) {
			return KhuyenMai.class;
		}
		if (columnIndex == COL_CHON_PHIEU_VIP) {
			return Boolean.class;
		}

		return String.class;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == COL_CHON_VE_MOI || columnIndex == COL_KHUYEN_MAI || columnIndex == COL_CHON_PHIEU_VIP;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MappingRow row = rows.get(rowIndex);
		Ve veDoi = row.getVeDoiRow().getVe();

		switch (columnIndex) {
		case COL_STT:
			return rowIndex + 1;
		case COL_HANH_KHACH:
			return row.getVeDoiRow().getHanhKhach();
		case COL_VE_CU_INFO:
			return veDoi.thongTinVeDoi(row.getVeDoiRow().getPhieuDungPhongVIP());
		case COL_VE_CU_GIA:
			return veDoi.getGia();
		case COL_CHON_VE_MOI:
			return row.getVeSessionMoi(); // Trả về object VeSession để ComboBox hiển thị
		case COL_VE_MOI_INFO:
			if (row.getVeSessionMoi() != null) {
				VeSession v = row.getVeSessionMoi();
				return v.prettyString();
			}
			return "Chưa chọn vé";
		case COL_VE_MOI_GIA:
			return (row.getVeSessionMoi() != null) ? (double) row.getVeSessionMoi().getVe().getGia() : 0.0;
		case COL_KHUYEN_MAI:
			// 1. Kiểm tra VeSessionMoi có tồn tại không
			VeSession vMoi = row.getVeSessionMoi();
			if (vMoi == null) {
				return null; // Nếu chưa chọn vé mới thì chắc chắn không có KM
			}

			// 2. Lấy KM và kiểm tra Ghost Object
			KhuyenMai km = vMoi.getKhuyenMaiApDung();
			if (km != null && (km.getKhuyenMaiID() == null || km.getKhuyenMaiID().isEmpty())) {
				return null;
			}
			return km;
		case COL_GIAM_KM:
			if (row.getVeSessionMoi() != null) {
				return row.getVeSessionMoi().getGiamKM();
			}
			return 0;
		case COL_CHON_PHIEU_VIP:
			if (row.getVeSessionMoi() != null) {
				return row.getVeSessionMoi().getPhiPhieuDungPhongChoVIP() > 0;
			}
			return false;
		case COL_PHIEU_VIP_GIA:
			if (row.getVeSessionMoi() != null) {
				return row.getVeSessionMoi().getPhiPhieuDungPhongChoVIP();
			}
			return 0;
		case COL_LE_PHI:
			return row.getVeDoiRow().getLePhiDoiVe();
		case COL_CHENH_LECH:
			return row.getChenhLech();
		default:
			return null;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == COL_CHON_VE_MOI) {
			MappingRow currentRow = rows.get(rowIndex);
			VeSession newSelectedVe = (VeSession) aValue; // Giá trị mới được chọn (có thể là null)
			// LOGIC THÔNG MINH: Kiểm tra xem vé này đã được dòng nào khác chọn chưa
			if (newSelectedVe != null) {
				for (int i = 0; i < rows.size(); i++) {
					// Không kiểm tra dòng chính nó
					if (i == rowIndex) {
						continue;
					}

					MappingRow otherRow = rows.get(i);
					VeSession otherVe = otherRow.getVeSessionMoi();

					// Nếu tìm thấy dòng khác đang giữ vé này
					if (otherVe != null && otherVe.equals(newSelectedVe)) {
						// 1. Gỡ vé khỏi dòng kia (Set về null)
						otherRow.setVeSessionMoi(null);
						// 2. Thông báo cập nhật giao diện dòng kia
						fireTableRowsUpdated(i, i);
						break; // Mỗi vé chỉ xuất hiện 1 lần nên break luôn
					}
				}
			}
			// Cập nhật cho dòng hiện tại
			currentRow.setVeSessionMoi(newSelectedVe);
			// Thông báo cập nhật giao diện dòng hiện tại
			fireTableRowsUpdated(rowIndex, rowIndex);
		} else if (columnIndex == COL_KHUYEN_MAI) {
			VeSession veMoi = (VeSession) getValueAt(rowIndex, COL_CHON_VE_MOI);
			KhuyenMai km = (KhuyenMai) aValue;
			veMoi.setKhuyenMaiApDung(km);

			if (km != null && (km.getKhuyenMaiID() == null || km.getKhuyenMaiID().isEmpty())) {
				km = null;
			}

			// TÍNH TOÁN LẠI TIỀN GIẢM KM
			int tienGiam = 0;
			if (km != null) {
				if (km.getTyLeGiamGia() > 0) {
					tienGiam = (int) (veMoi.getVe().getGia() * (km.getTyLeGiamGia()));
				} else if (km.getTienGiamGia() > 0) {
					tienGiam = (int) km.getTienGiamGia();
				}
				// (Có thể thêm logic giới hạn tiền giảm tối đa nếu cần)
			}
			veMoi.setGiamKM(tienGiam);
			// Cập nhật cả dòng để tính lại Thành tiền
			fireTableRowsUpdated(rowIndex, rowIndex);
		} else if (columnIndex == COL_CHON_PHIEU_VIP) {
			MappingRow currentRow = rows.get(rowIndex);

			// Nhận giá trị true/false từ JCheckBox
			Boolean isSelected = (Boolean) aValue;
			if (isSelected) {
				currentRow.getVeSessionMoi().setPhiPhieuDungPhongChoVIP(20000);
			} else {
				currentRow.getVeSessionMoi().setPhiPhieuDungPhongChoVIP(0);
			}

			// Thông báo cho bảng cập nhật lại các ô bị ảnh hưởng (Giá dịch vụ & Chênh lệch)
			fireTableCellUpdated(rowIndex, COL_PHIEU_VIP_GIA);
			fireTableCellUpdated(rowIndex, COL_CHENH_LECH);
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