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

/**
 * Lớp POJO đại diện cho một dòng trong bảng chọn vé để hoàn.
 */
import entity.Ve;

public class VeHoanRow {
	private Ve ve;
	private String hanhKhach;
	private String thongTinVe;
	private double thanhTien;
	private String loaiHoanVe;
	private double lePhiHoanVe;
	private double tienHoanLai;
	private String thongTinPhiHoan;
	private String lyDo;
	private boolean isSelected;

	// Constructor (Bạn sẽ tính toán phí và điền thông tin ở đây)
	public VeHoanRow(Ve ve) {
		this.ve = ve;

		// 1. Lấy thông tin cơ bản
		this.hanhKhach = String.format("<html><b>%s</b><br/>%s<br/>Số giấy tờ: %s</html>", ve.getKhachHang().getHoTen(),
				ve.getKhachHang().getLoaiDoiTuong().getDescription(), ve.getKhachHang().getSoGiayTo());
		this.thanhTien = ve.getGia();
		this.thongTinVe = ve.thongTinVeHoan();

		// 2. Tính toán phí hoàn
		// Đây là nghiệp vụ quan trọng, bạn cần định nghĩa rõ
		this.lePhiHoanVe = 10000.0;
		this.loaiHoanVe = "Trả thường";
		this.thongTinPhiHoan = "Hoàn/Đổi vé bình thường năm 2025, áp dụng phí 10.000 VNĐ/vé";
		this.lyDo = "Không còn nhu cầu";

		// 3. Tính tiền hoàn lại
		this.tienHoanLai = this.thanhTien - this.lePhiHoanVe;
		if (this.tienHoanLai < 0) {
			this.tienHoanLai = 0;
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

	public String getLoaiHoanVe() {
		return loaiHoanVe;
	}

	public double getLePhiHoanVe() {
		return lePhiHoanVe;
	}

	public double getTienHoanLai() {
		return tienHoanLai;
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
}