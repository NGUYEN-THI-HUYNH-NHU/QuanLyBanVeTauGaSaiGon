package dao.impl;

/*
 * @(#) GiaoDichHoanDoi_DAO.java  1.0  [11:37:32 AM] Nov 1, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Nov 1, 2025
 * @version: 1.0
 */

import dao.IGiaoDichHoanDoiDAO;
import entity.GiaoDichHoanDoi;
import jakarta.persistence.Query;

import java.sql.Timestamp;

public class GiaoDichHoanDoiDAO extends AbstractGenericDAO<GiaoDichHoanDoi, String> implements IGiaoDichHoanDoiDAO {

    public GiaoDichHoanDoiDAO() {
        super(GiaoDichHoanDoi.class);
    }

    /**
     * @param giaoDichHoanDoi
     */
    @Override
    public boolean insertGiaoDichHoanDoi(GiaoDichHoanDoi giaoDichHoanDoi) throws Exception {
        return doInTransaction(em -> {
            String sql = "INSERT INTO GiaoDichHoanDoi (giaoDichHoanDoiID, nhanVienID, hoaDonID, veGocID, veMoiID, loaiGiaoDich, lyDo, thoiDiemGiaoDich, phiHoanDoi, soTienChenhLech) VALUES(?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9, ?10)";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, giaoDichHoanDoi.getGiaoDichHoanDoiID());
            query.setParameter(2, giaoDichHoanDoi.getNhanVien().getNhanVienID());
            query.setParameter(3, giaoDichHoanDoi.getHoaDon().getHoaDonID());
            query.setParameter(4, giaoDichHoanDoi.getVeGoc().getVeID());
            if (giaoDichHoanDoi.getVeMoi() != null) {
                query.setParameter(5, giaoDichHoanDoi.getVeMoi().getVeID());
            } else {
                query.setParameter(5, null);
            }
            query.setParameter(6, giaoDichHoanDoi.getLoaiGiaoDich().toString());
            query.setParameter(7, giaoDichHoanDoi.getLyDo());
            query.setParameter(8, Timestamp.valueOf(giaoDichHoanDoi.getThoiDiemGiaoDich()));
            query.setParameter(9, giaoDichHoanDoi.getPhiHoanDoi());
            query.setParameter(10, giaoDichHoanDoi.getSoTienChenhLech());

            return query.executeUpdate() > 0;
        });
    }
}