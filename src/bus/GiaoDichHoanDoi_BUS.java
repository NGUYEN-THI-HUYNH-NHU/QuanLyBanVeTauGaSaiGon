package bus;

/*
 * @(#) GiaoDichHoanDoi_BUS.java  1.0  [2:20:33 PM] Nov 16, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 16, 2025
 * @version: 1.0
 */
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import dao.GiaoDichHoanDoi_DAO;
import entity.GiaoDichHoanDoi;
import entity.HoaDon;
import entity.NhanVien;
import entity.type.LoaiGiaoDich;
import gui.application.form.hoanVe.VeHoanRow;

public class GiaoDichHoanDoi_BUS {
	private final GiaoDichHoanDoi_DAO giaoDichHoanDoiDAO = new GiaoDichHoanDoi_DAO();

	/**
	 * @param hoaDon
	 * @param nhanVien
	 * @param listVeHoanRow
	 * @return
	 */
	public List<GiaoDichHoanDoi> taoCacGiaoDichHoanDoi(HoaDon hoaDon, NhanVien nhanVien,
			List<VeHoanRow> listVeHoanRow) {
		List<GiaoDichHoanDoi> dsGiaoDichHoanDoi = new ArrayList<GiaoDichHoanDoi>();
		for (VeHoanRow r : listVeHoanRow) {
			String gdhdID = "GDHV-" + r.getVe().getVeID();
			GiaoDichHoanDoi gdhd = new GiaoDichHoanDoi(gdhdID, nhanVien, hoaDon, r.getVe(), LoaiGiaoDich.HOAN_VE,
					r.getLyDo(), hoaDon.getThoiDiemTao(), r.getLePhiHoanVe(), r.getVe().getGia() - r.getLePhiHoanVe());
			dsGiaoDichHoanDoi.add(gdhd);
		}
		return dsGiaoDichHoanDoi;
	}

	/**
	 * @param conn
	 * @param dsGdhd
	 */
	public void themCacGiaoDichHoanDoi(Connection conn, List<GiaoDichHoanDoi> dsGdhd) {
		for (GiaoDichHoanDoi gd : dsGdhd) {
			giaoDichHoanDoiDAO.insertGiaoDichHoanDoi(conn, gd);
		}
	}

}
