/*
 * @(#) NhatKyAuditDAO.java  1.0  [4:34 PM] 5/1/2026
 *
 * Copyright (c) 2026 IUH. All rights reserved.
 */

/*
 * @description
 * @author: Yen
 * @date: 5/1/2026
 * @version: 1.0
 */

package dao.impl;

import dao.INhatKyAuditDAO;
import entity.NhatKyAudit;

import java.time.LocalDate;
import java.util.List;

public class NhatKyAuditDAO extends AbstractGenericDAO<NhatKyAudit, String> implements INhatKyAuditDAO {

    public NhatKyAuditDAO(){
        super(NhatKyAudit.class);
    }


    @Override
    public void ghiNhatKyAudit(NhatKyAudit nhatKy) {
        try {
            create(nhatKy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<NhatKyAudit> layDanhSachNhatKy() {
        return doInTransaction(em ->
                em.createQuery("SELECT n FROM NhatKyAudit n ORDER BY n.thoiDiemThaoTac DESC", NhatKyAudit.class)
                        .getResultList()
        );
    }

    @Override
    public String maNhatKyMoi() {
        return doInTransaction(em -> {
            String maxID = em.createQuery("SELECT MAX(n.nhatKyAuditID) FROM NhatKyAudit n", String.class).getSingleResult();
            if (maxID == null || maxID.isBlank()) {
                return "NK00001";
            }
            int number = Integer.parseInt(maxID.substring(2));
            return "NK" + String.format("%05d", number + 1);
        });
    }

    @Override
    public List<NhatKyAudit> timKiemNhatKy(LocalDate tuNgay, LocalDate denNgay, String nhanVienID, String loaiThaoTac, String doiTuongID) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT n FROM NhatKyAudit n WHERE 1=1");

            if (tuNgay != null && denNgay != null) {
                // Sử dụng hàm CAST trong JPQL để so sánh chính xác theo ngày
                jpql.append(" AND CAST(n.thoiDiemThaoTac AS date) BETWEEN :tuNgay AND :denNgay");
            }
            if (nhanVienID != null && !nhanVienID.equals("TẤT CẢ")) {
                jpql.append(" AND n.nhanVienID = :nhanVienID");
            }
            if (loaiThaoTac != null && !loaiThaoTac.isBlank() && !loaiThaoTac.equals("TẤT CẢ")) {
                jpql.append(" AND n.loaiThaoTac = :loaiThaoTac");
            }
            if (doiTuongID != null && !doiTuongID.isBlank()) {
                jpql.append(" AND n.doiTuongID LIKE :doiTuongID");
            }

            jpql.append(" ORDER BY n.thoiDiemThaoTac DESC");

            var query = em.createQuery(jpql.toString(), NhatKyAudit.class);

            // Gán các tham số động
            if (tuNgay != null && denNgay != null) {
                query.setParameter("tuNgay", tuNgay);
                query.setParameter("denNgay", denNgay);
            }
            if (nhanVienID != null && !nhanVienID.equals("TẤT CẢ")) {
                query.setParameter("nhanVienID", nhanVienID);
            }
            if (loaiThaoTac != null && !loaiThaoTac.isBlank() && !loaiThaoTac.equals("TẤT CẢ")) {
                // So sánh theo chuỗi String đại diện cho Enum
                query.setParameter("loaiThaoTac", entity.type.NhatKyAudit.valueOf(loaiThaoTac));
            }
            if (doiTuongID != null && !doiTuongID.isBlank()) {
                query.setParameter("doiTuongID", "%" + doiTuongID + "%");
            }

            return query.getResultList();
        });
    }

    @Override
    public String layTenNhanVienTheoMaNV(String nhanVienID) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT n.hoTen FROM NhanVien n WHERE n.nhanVienID = :id", String.class)
                            .setParameter("id", nhanVienID)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<NhatKyAudit> locNhatKyTheoKhoangThoiGian(LocalDate ngayBatDau, LocalDate ngayKetThuc) {
        return doInTransaction(em ->
                em.createQuery(
                                "SELECT n FROM NhatKyAudit n " +
                                        "WHERE CAST(n.thoiDiemThaoTac AS date) BETWEEN :ngayBD AND :ngayKT " +
                                        "ORDER BY n.thoiDiemThaoTac DESC", NhatKyAudit.class)
                        .setParameter("ngayBD", ngayBatDau)
                        .setParameter("ngayKT", ngayKetThuc)
                        .getResultList()
        );
    }

    @Override
    public List<NhatKyAudit> layDanhSachPhanTrang(int page, int pageSize, LocalDate tu, LocalDate den, String maNV, String loai) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT a FROM NhatKyAudit a WHERE 1=1");
            if (tu != null) jpql.append(" AND a.thoiDiemThaoTac >= :tu");
            if (den != null) jpql.append(" AND a.thoiDiemThaoTac <= :den");
            if (maNV != null && !maNV.isEmpty()) jpql.append(" AND a.nhanVienID = :maNV");
            if (loai != null && !loai.isEmpty()) jpql.append(" AND a.loaiThaoTac = :loai");
            jpql.append(" ORDER BY a.thoiDiemThaoTac DESC");

            var query = em.createQuery(jpql.toString(), NhatKyAudit.class);
            if (tu != null) query.setParameter("tu", tu.atStartOfDay());
            if (den != null) query.setParameter("den", den.atTime(23, 59, 59));
            if (maNV != null && !maNV.isEmpty()) query.setParameter("maNV", maNV);
            if (loai != null && !loai.isEmpty()) query.setParameter("loai", entity.type.NhatKyAudit.valueOf(loai));

            return query.setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        });
    }

    @Override
    public long demTongSoDong(LocalDate tu, LocalDate den, String maNV, String loai) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT COUNT(a) FROM NhatKyAudit a WHERE 1=1");
            if (tu != null) jpql.append(" AND a.thoiDiemThaoTac >= :tu");
            if (den != null) jpql.append(" AND a.thoiDiemThaoTac <= :den");
            if (maNV != null && !maNV.isEmpty()) jpql.append(" AND a.nhanVienID = :maNV");
            if (loai != null && !loai.isEmpty()) jpql.append(" AND a.loaiThaoTac = :loai");

            var query = em.createQuery(jpql.toString(), Long.class);
            if (tu != null) query.setParameter("tu", tu.atStartOfDay());
            if (den != null) query.setParameter("den", den.atTime(23, 59, 59));
            if (maNV != null && !maNV.isEmpty()) query.setParameter("maNV", maNV);
            if (loai != null && !loai.isEmpty()) query.setParameter("loai", entity.type.NhatKyAudit.valueOf(loai));

            return query.getSingleResult();
        });
    }
}