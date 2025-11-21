package gui.application.form.hoanVe;
/*
 * @(#) VeHoanRow.java  1.0  [11:23:41 AM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */
import java.time.Duration;
import java.time.LocalDateTime;

import entity.Ve;

public class VeHoanRow {
	private Ve ve;
	private String hanhKhach;
	private String thongTinVe;
	private double thanhTien;
	private double lePhiHoanVe;
	private double tienHoan;
	private String thongTinPhiHoan;
	private String lyDo;
	private boolean isSelected;

	private String thoiGianConLai;
	private boolean isDuDieuKien;
	private String lyDoKhongDuDieuKien;

	public VeHoanRow(Ve ve) {
		this.ve = ve;

		this.hanhKhach = String.format("<html><b>%s</b><br/>%s<br/>Số giấy tờ: %s</html>", ve.getKhachHang().getHoTen(),
				ve.getKhachHang().getLoaiDoiTuong().getDescription(), ve.getKhachHang().getSoGiayTo());
		this.thanhTien = ve.getGia();
		this.thongTinVe = ve.thongTinVeHoan();

		calcThoiGianConLaiVaPhiHoan();

		this.lyDo = "Không còn nhu cầu";

		this.isSelected = false;
	}

	private void calcThoiGianConLaiVaPhiHoan() {
		LocalDateTime gioTauChay = ve.getNgayGioDi();
		LocalDateTime now = LocalDateTime.now();

		Duration duration = Duration.between(now, gioTauChay);
		long seconds = duration.getSeconds();

		if (seconds <= 0) {
			thoiGianConLai = "Đã khởi hành";
			isDuDieuKien = false;
			lyDoKhongDuDieuKien = "Tàu đã khởi hành, không thể hoàn vé.";
			lePhiHoanVe = 0;
			thongTinPhiHoan = "Không thể hoàn vé.";
			tienHoan = 0;
		} else {
			long hours = seconds / 3600;
			long minutes = (seconds % 3600) / 60;
			thoiGianConLai = String.format("%dg %02dp", hours, minutes);

			// Quy định: Phải trước 4 tiếng
			if (hours >= 4) {
				isDuDieuKien = true;
				lyDoKhongDuDieuKien = "";
				if (hours >= 24) {
					lePhiHoanVe = thanhTien * 0.2;
					thongTinPhiHoan = "Hoàn vé bình thường năm 2025, áp dụng phí 20% giá vé";
				} else {
					lePhiHoanVe = thanhTien * 0.1;
					thongTinPhiHoan = "Hoàn vé bình thường năm 2025, áp dụng phí 10% giá vé";
				}
				if (lePhiHoanVe < 10000) {
					lePhiHoanVe = 10000;
				}
				tienHoan = thanhTien - lePhiHoanVe;
			} else {
				isDuDieuKien = false;
				lyDoKhongDuDieuKien = "Thời gian còn lại dưới 4 giờ (Quy định hoàn vé).";
				lePhiHoanVe = 0;
				thongTinPhiHoan = "Không đủ điều kiện hoàn vé.";
				tienHoan = 0;
			}
		}
	}

	public Ve getVe() {
		return ve;
	}

	public String getHanhKhach() {
		return hanhKhach;
	}

	public String getThongTinVe() {
		return thongTinVe;
	}

	public double getThanhTien() {
		return thanhTien;
	}

	public double getLePhiHoanVe() {
		return lePhiHoanVe;
	}

	public double getTienHoan() {
		return tienHoan;
	}

	public String getThongTinPhiHoan() {
		return thongTinPhiHoan;
	}

	public String getLyDo() {
		return lyDo;
	}

	public void setLyDo(String lyDo) {
		this.lyDo = lyDo;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getThoiGianConLai() {
		return thoiGianConLai;
	}

	public boolean isDuDieuKien() {
		return isDuDieuKien;
	}

	public String getLyDoKhongDuDieuKien() {
		return lyDoKhongDuDieuKien;
	}
}