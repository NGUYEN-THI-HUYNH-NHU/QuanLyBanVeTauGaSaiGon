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
import java.util.List;

import dao.BieuGiaVe_DAO;
import entity.BieuGiaVe;

public class BieuGiaVe_BUS {
	private final BieuGiaVe_DAO dao = new BieuGiaVe_DAO();

	public List<BieuGiaVe> layDanhSachBieuGia() {
		return dao.getAllBieuGia();
	}

	public String themBieuGia(BieuGiaVe bg) {
		// Validation
		String loi = kiemTraHopLe(bg);
		if (loi != null) {
			return loi;
		}

		// Sinh ID tự động (Nếu ID chưa có)
		if (bg.getBieuGiaVeID() == null || bg.getBieuGiaVeID().isEmpty()) {
			// Ví dụ sinh mã: BG + TimeMillis hoặc UUID
			bg.setBieuGiaVeID("BG" + System.currentTimeMillis());
		}

		return dao.themBieuGia(bg) ? "Thêm thành công" : "Thêm thất bại (Lỗi DB)";
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