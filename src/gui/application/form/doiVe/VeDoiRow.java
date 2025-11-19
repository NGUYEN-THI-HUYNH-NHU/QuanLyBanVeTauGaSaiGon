package gui.application.form.doiVe;
/*
 * @(#) VeDoiRow.java  1.0  [11:23:41 AM] Nov 13, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 13, 2025
 * @version: 1.0
 */

import entity.Ve;

public class VeDoiRow {
	private Ve ve;
	private String hanhKhach;
	private String thongTinVe;
	private double thanhTien;
	private String loaiDoiVe;
	private double lePhiDoiVe;
	private double tienDoiLai;
	private String thongTinPhiDoi;
	private String lyDo;
	private boolean isSelected;

	// Constructor (Bạn sẽ tính toán phí và điền thông tin ở đây)
	public VeDoiRow(Ve ve) {
		this.ve = ve;

		// 1. Lấy thông tin cơ bản
		this.hanhKhach = String.format("<html><b>%s</b><br/>%s<br/>Số giấy tờ: %s</html>", ve.getKhachHang().getHoTen(),
				ve.getKhachHang().getLoaiDoiTuong().getDescription(), ve.getKhachHang().getSoGiayTo());
		this.thanhTien = ve.getGia();
		this.thongTinVe = ve.thongTinVeDoi();

		// 2. Tính toán phí hoàn
		// Đây là nghiệp vụ quan trọng, bạn cần định nghĩa rõ
		this.lePhiDoiVe = 10000.0;
		this.loaiDoiVe = "Đổi thường";
		this.thongTinPhiDoi = "Hoàn/Đổi vé bình thường năm 2025, áp dụng phí 10.000 VNĐ/vé";
		this.lyDo = "Không còn nhu cầu";

		// 3. Tính tiền hoàn lại
		this.tienDoiLai = this.thanhTien - this.lePhiDoiVe;
		if (this.tienDoiLai < 0) {
			this.tienDoiLai = 0;
		}

		// 4. Mặc định là không chọn
		this.isSelected = false;
	}

	// Getters và Setters
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

	public String getLoaiDoiVe() {
		return loaiDoiVe;
	}

	public double getLePhiDoiVe() {
		return lePhiDoiVe;
	}

	public double getTienDoiLai() {
		return tienDoiLai;
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
}