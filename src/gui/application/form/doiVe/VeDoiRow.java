package gui.application.form.doiVe;

/*
 * @(#) veDoiRow.java  1.0  [11:23:41 AM] Nov 13, 2025
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

import entity.PhieuDungPhongVIP;
import entity.Ve;

public class VeDoiRow {
	private Ve ve;
	private PhieuDungPhongVIP phieuDungPhongVIP;
	private String hanhKhach;
	private String loaiDoiVe;
	private double lePhiDoiVe;
	private String thongTinPhiDoi;
	private String lyDo;
	private boolean isSelected;
	private String thoiGianConLai;
	private boolean isDuDieuKien;
	private String lyDoKhongDuDieuKien;

	public VeDoiRow(Ve ve, PhieuDungPhongVIP phieuDungPhongVIP) {
		this.ve = ve;
		this.phieuDungPhongVIP = phieuDungPhongVIP;

		this.hanhKhach = String.format("<html><b>%s</b><br/>%s<br/>Số giấy tờ: %s</html>", ve.getKhachHang().getHoTen(),
				ve.getKhachHang().getLoaiDoiTuong().getDescription(), ve.getKhachHang().getSoGiayTo());
		calcThoiGianConLaiVaPhiDoi();

		this.lyDo = "Không còn nhu cầu";

		this.isSelected = false;
	}

	private void calcThoiGianConLaiVaPhiDoi() {
		LocalDateTime gioTauChay = ve.getNgayGioDi();
		LocalDateTime now = LocalDateTime.now();

		Duration duration = Duration.between(now, gioTauChay);
		long seconds = duration.getSeconds();

		if (seconds <= 0) {
			thoiGianConLai = "Đã khởi hành";
			isDuDieuKien = false;
			lyDoKhongDuDieuKien = "Tàu đã khởi hành, không thể đổi vé.";
			lePhiDoiVe = 0;
			thongTinPhiDoi = "Không thể đổi vé.";
			isSelected = false;
		} else {
			long hours = seconds / 3600;
			long minutes = (seconds % 3600) / 60;
			thoiGianConLai = String.format("%dg %02dp", hours, minutes);

			// Quy định: Phải trước 24 tiếng
			if (hours >= 24) {
				isDuDieuKien = true;
				lyDoKhongDuDieuKien = "";
				lePhiDoiVe = 20000;
				thongTinPhiDoi = "Đổi vé bình thường năm 2025, áp dụng phí 20.000VNĐ/vé";
			} else {
				isDuDieuKien = false;
				lyDoKhongDuDieuKien = "Thời gian còn lại dưới 24 giờ (Quy định đổi vé).";
				lePhiDoiVe = 0;
				thongTinPhiDoi = "Không đủ điều kiện đổi vé.";
				isSelected = false;
			}
		}
	}

	public Ve getVe() {
		return ve;
	}

	public String getHanhKhach() {
		return hanhKhach;
	}

	public String getLoaiDoiVe() {
		return loaiDoiVe;
	}

	public double getLePhiDoiVe() {
		return lePhiDoiVe;
	}

	public String getThongTinPhiDoi() {
		return thongTinPhiDoi;
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

	public PhieuDungPhongVIP getPhieuDungPhongVIP() {
		return phieuDungPhongVIP;
	}

	public void setPhieuDungPhongVIP(PhieuDungPhongVIP phieuDungPhongVIP) {
		this.phieuDungPhongVIP = phieuDungPhongVIP;
	}
}