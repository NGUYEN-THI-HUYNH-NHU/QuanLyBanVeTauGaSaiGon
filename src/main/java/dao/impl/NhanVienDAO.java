/*
 * @(#) NhanVienDAO.java  1.0  [3:55 PM] 5/1/2026
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

import dao.INhanVienDAO;
import entity.CaLam;
import entity.NhanVien;
import entity.VaiTroNhanVien;

import java.util.List;

public class NhanVienDAO extends AbstractGenericDAO<NhanVien, String> implements INhanVienDAO {

    public NhanVienDAO() {
        super(NhanVien.class);
    }

    @Override
    public List<NhanVien> getNhanVienVoiHoTen(String hoTenTim) {
        return doInTransaction(em ->
                em.createQuery("SELECT n FROM NhanVien n " +
                                "LEFT JOIN FETCH n.caLam " +
                                "LEFT JOIN FETCH n.vaiTroNhanVien " +
                                "WHERE n.hoTen LIKE :hoTen", NhanVien.class)
                        .setParameter("hoTen", "%" + hoTenTim + "%")
                        .getResultList()
        );
    }

    @Override
    public String taoMaNhanVienTuDong() {
        return doInTransaction(em -> {
            String jpql = "SELECT MAX(n.nhanVienID) FROM NhanVien n";
            String maCuoi = em.createQuery(jpql, String.class).getSingleResult();
            if (maCuoi != null && !maCuoi.isBlank()) {
                int so = Integer.parseInt(maCuoi.substring(2)) + 1;
                return String.format("NV%03d", so);
            }
            return "NV001";
        });
    }


    @Override
    public List<NhanVien> timKiemNhanVien(String tuKhoa, String vaiTroID, Boolean isHoatDong) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder(
                    "SELECT n FROM NhanVien n " +
                            "LEFT JOIN FETCH n.caLam " +
                            "LEFT JOIN FETCH n.vaiTroNhanVien " +
                            "WHERE 1=1"
            );

            // Chỉ tìm kiếm theo Tên HOẶC Số điện thoại
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                jpql.append(" AND (n.hoTen LIKE :tuKhoa OR n.soDienThoai LIKE :tuKhoa)");
            }

            // Kết hợp AND với vai trò (nếu có chọn)
            if (vaiTroID != null && !vaiTroID.trim().isEmpty()) {
                jpql.append(" AND n.vaiTroNhanVien.vaiTroNhanVienID = :vaiTroID");
            }

            // Kết hợp AND với trạng thái (nếu có chọn)
            if (isHoatDong != null) {
                jpql.append(" AND n.isHoatDong = :isHoatDong");
            }

            var query = em.createQuery(jpql.toString(), NhanVien.class);

            // Gán tham số
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                query.setParameter("tuKhoa", "%" + tuKhoa.trim() + "%");
            }
            if (vaiTroID != null && !vaiTroID.trim().isEmpty()) {
                query.setParameter("vaiTroID", vaiTroID);
            }
            if (isHoatDong != null) {
                query.setParameter("isHoatDong", isHoatDong);
            }

            return query.getResultList();
        });
    }

    @Override
    public VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT n.vaiTroNhanVien FROM NhanVien n WHERE n.nhanVienID = :maNV", VaiTroNhanVien.class)
                            .setParameter("maNV", maNV)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean capNhatAvatar(String nhanVienID, byte[] avatarData) {
        try {
            return doInTransaction(em -> {
                int updated = em.createQuery("UPDATE NhanVien n SET n.avatar = :avatar WHERE n.nhanVienID = :id")
                        .setParameter("avatar", avatarData)
                        .setParameter("id", nhanVienID)
                        .executeUpdate();
                return updated > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<String> layDanhSachMaNhanVien() {
        return doInTransaction(em ->
                em.createQuery("SELECT n.nhanVienID FROM NhanVien n", String.class).getResultList()
        );
    }

    @Override
    public List<CaLam> getAllCaLam() {
        return doInTransaction(em ->
                em.createQuery("SELECT c FROM CaLam c", CaLam.class).getResultList()
        );
    }

    @Override
    public CaLam getCaLamById(String caLamID) {
        return doInTransaction(em -> em.find(CaLam.class, caLamID));
    }

    @Override
    public NhanVien getNhanVienByTenDangNhap(String tenDangNhap) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT n FROM TaiKhoan t JOIN t.nhanVien n " +
                                    "LEFT JOIN FETCH n.caLam " +
                                    "LEFT JOIN FETCH n.vaiTroNhanVien " +
                                    "WHERE t.tenDangNhap = :tenDangNhap", NhanVien.class)
                            .setParameter("tenDangNhap", tenDangNhap)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

}