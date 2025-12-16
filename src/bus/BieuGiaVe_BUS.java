package bus;
/*
 * @(#) BieuGiaVe_BUS.java  1.0  [8:27:54 PM] Nov 27, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 27, 2025
 * @version: 1.0
 */
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.BieuGiaVe_DAO;
import entity.BieuGiaVe;

public class BieuGiaVe_BUS {
	private final BieuGiaVe_DAO dao = new BieuGiaVe_DAO();

	public List<BieuGiaVe> layDanhSachBieuGia() {
		return dao.getAllBieuGia();
	}

	public List<BieuGiaVe> timKiem(String tuKhoa, String tuyenID, String loaiTauID) {
		return dao.getBieuGiaTheoTieuChi(tuKhoa, tuyenID, loaiTauID);
	}

	public String themBieuGia(BieuGiaVe bg) {
		// Validation
		String loi = kiemTraHopLe(bg);
		if (loi != null) {
			return loi;
		}

		String newID = taoMaBieuGia(bg.getNgayBatDau(), bg.getNgayKetThuc());
		bg.setBieuGiaVeID(newID);

		return dao.themBieuGia(bg) ? "Thêm thành công" : "Thêm thất bại (Lỗi DB)";
	}

	private String taoMaBieuGia(LocalDate ngayBD, LocalDate ngayKT) {

		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");

		String start = ngayBD.format(fmt);
		String end = (ngayKT != null)
				? ngayKT.format(fmt)
				: "VOHIEULUC";

		return "BGV_" + start + "_" + end;
	}


	public String capNhatBieuGia(BieuGiaVe bg) {
		String loi = kiemTraHopLe(bg);
		if (loi != null) {
			return loi;
		}

		return dao.capNhatBieuGia(bg) ? "Cập nhật thành công" : "Cập nhật thất bại";
	}

	public boolean xoaBieuGia(String id) {
		return dao.xoaBieuGia(id);
	}

	// Logic kiểm tra dữ liệu đầu vào
	private String kiemTraHopLe(BieuGiaVe bg) {
		if (bg.getMinKm() < 0 || bg.getMaxKm() < 0) {
			return "Khoảng cách Km không được âm.";
		}
		if (bg.getMinKm() >= bg.getMaxKm()) {
			return "Min Km phải nhỏ hơn Max Km.";
		}

		// Kiểm tra logic ngày
		if (bg.getNgayBatDau() == null) {
			return "Ngày bắt đầu không được để trống.";
		}
		if (bg.getNgayKetThuc() != null && bg.getNgayBatDau().isAfter(bg.getNgayKetThuc())) {
			return "Ngày kết thúc phải sau ngày bắt đầu.";
		}

		// Kiểm tra giá: phải có 1 trong 2 loại giá
		boolean hasKmPrice = bg.getDonGiaTrenKm() > 0;
		boolean hasFixPrice = bg.getGiaCoBan() > 0;

		if (!hasKmPrice && !hasFixPrice) {
			return "Phải nhập Đơn giá/Km hoặc Giá cố định (>0).";
		}
		if (hasKmPrice && hasFixPrice) {
			// Logic DB cho phép 1 cái null, nên ta ưu tiên chọn 1 cái trong UI,
			// nhưng ở BUS nên clear cái kia về 0 để DAO xử lý đúng.
			// (Ở Form đã xử lý disable input, nhưng ở đây check cho chắc)
		}

		return null;
	}
}