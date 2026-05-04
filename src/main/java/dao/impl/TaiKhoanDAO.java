/*
 * @(#) TaiKhoanDAO.java  1.0  [4:29 PM] 5/1/2026
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

import dao.ITaiKhoanDAO;
import entity.NhanVien;
import entity.TaiKhoan;

import java.util.List;

public class TaiKhoanDAO extends AbstractGenericDAO<TaiKhoan, String> implements ITaiKhoanDAO {

    public TaiKhoanDAO() {
        super(TaiKhoan.class);
    }

    @Override
    public TaiKhoan getTaiKhoanVoiTenDangNhap(String tenDangNhap) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT t FROM TaiKhoan t " +
                                    "LEFT JOIN FETCH t.vaiTroTaiKhoan " +
                                    "LEFT JOIN FETCH t.nhanVien " +
                                    "WHERE t.tenDangNhap = :ten", TaiKhoan.class)
                            .setParameter("ten", tenDangNhap)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean capNhatMatKhau(String nhanVienID, String newMatKhau) {
        try {
            return doInTransaction(em -> {
                int updated = em.createQuery("UPDATE TaiKhoan t SET t.matKhauHash = :mk WHERE t.nhanVien.nhanVienID = :nvID")
                        .setParameter("mk", newMatKhau)
                        .setParameter("nvID", nhanVienID)
                        .executeUpdate();
                return updated > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isTaiKhoanTonTai(String tenDangNhap) {
        return getTaiKhoanVoiTenDangNhap(tenDangNhap) != null;
    }

    @Override
    public TaiKhoan getTaiKhoanVoiNhanVienID(String nhanVienID) {
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT t FROM TaiKhoan t " +
                                    "LEFT JOIN FETCH t.vaiTroTaiKhoan " +
                                    "LEFT JOIN FETCH t.nhanVien " +
                                    "WHERE t.nhanVien.nhanVienID = :nvID", TaiKhoan.class)
                            .setParameter("nvID", nhanVienID)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public NhanVien getNhanVienByTenDangNhap(String tenDangNhap, boolean xacThuc) {
        if (!xacThuc || getTaiKhoanVoiTenDangNhap(tenDangNhap) == null) {
            return null;
        }
        try {
            return doInTransaction(em ->
                    em.createQuery("SELECT t.nhanVien FROM TaiKhoan t WHERE t.tenDangNhap = :ten", NhanVien.class)
                            .setParameter("ten", tenDangNhap)
                            .getSingleResult()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<TaiKhoan> getDanhSachTaiKhoan() {
        return doInTransaction(em ->
                em.createQuery("SELECT t FROM TaiKhoan t " +
                                "LEFT JOIN FETCH t.vaiTroTaiKhoan " +
                                "LEFT JOIN FETCH t.nhanVien", TaiKhoan.class)
                        .getResultList()
        );
    }

    @Override
    public boolean kiemTraTenDangNhap(String tenDN) {
        return getTaiKhoanVoiTenDangNhap(tenDN) != null;
    }

    @Override
    public String taoMaTaiKhoanMoi() {
        return doInTransaction(em -> {
            Long count = em.createQuery("SELECT COUNT(t) FROM TaiKhoan t", Long.class)
                    .getSingleResult();

            long nextNumber = count + 1;
            String nextID = String.format("TK%03d", nextNumber);

            // Vòng lặp phòng thủ mã trùng
            while (true) {
                Long exists = em.createQuery("SELECT COUNT(t) FROM TaiKhoan t WHERE t.taiKhoanID = :id", Long.class)
                        .setParameter("id", nextID)
                        .getSingleResult();

                if (exists == 0) {
                    break;
                }
                nextNumber++;
                nextID = String.format("TK%03d", nextNumber);
            }

            return nextID;
        });
    }

    @Override
    public String goiYTenDangNhap(String tenDN) {
        try {
            return doInTransaction(em -> {
                List<String> list = em.createQuery(
                                "SELECT t.tenDangNhap FROM TaiKhoan t WHERE t.tenDangNhap LIKE :ten ORDER BY t.tenDangNhap DESC", String.class)
                        .setParameter("ten", tenDN + "%")
                        .getResultList();
                if (!list.isEmpty()) {
                    String lastUsername = list.get(0);
                    String sub = lastUsername.substring(tenDN.length());
                    if (!sub.isEmpty() && sub.matches("\\d+")) {
                        int soThuTu = Integer.parseInt(sub) + 1;
                        return tenDN + soThuTu;
                    }
                }
                return tenDN + "1";
            });
        } catch (Exception e) {
            return tenDN;
        }
    }

    @Override
    public List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT t FROM TaiKhoan t " +
                    "LEFT JOIN FETCH t.vaiTroTaiKhoan " +
                    "LEFT JOIN FETCH t.nhanVien WHERE 1=1");

            if (maNV != null && !maNV.isBlank()) {
                jpql.append(" AND t.nhanVien.nhanVienID LIKE :maNV");
            }
            if (tenDN != null && !tenDN.isBlank()) {
                jpql.append(" AND t.tenDangNhap LIKE :tenDN");
            }
            if (vaiTro != null && !vaiTro.isBlank()) {
                jpql.append(" AND t.vaiTroTaiKhoan.vaiTroTaiKhoanID = :vaiTro");
            }
            if (trangThai != null) {
                jpql.append(" AND t.trangThai = :trangThai");
            }

            var query = em.createQuery(jpql.toString(), TaiKhoan.class);

            if (maNV != null && !maNV.isBlank()) query.setParameter("maNV", "%" + maNV + "%");
            if (tenDN != null && !tenDN.isBlank()) query.setParameter("tenDN", "%" + tenDN + "%");
            if (vaiTro != null && !vaiTro.isBlank()) query.setParameter("vaiTro", vaiTro);
            if (trangThai != null) query.setParameter("trangThai", trangThai);

            return query.getResultList();
        });
    }

    @Override
    public boolean checkForgotPasswordInfo(String nhanVienID, String soDienThoai, String email) {
        return doInTransaction(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(n) FROM NhanVien n WHERE n.nhanVienID = :id AND n.soDienThoai = :sdt AND n.email = :email", Long.class)
                    .setParameter("id", nhanVienID)
                    .setParameter("sdt", soDienThoai)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        });
    }

    @Override
    public boolean checkDuplicatingPasswords(String nhanVienID, String newPass) {
        return doInTransaction(em -> {
            Long count = em.createQuery(
                            "SELECT COUNT(t) FROM TaiKhoan t WHERE t.nhanVien.nhanVienID = :id AND t.matKhauHash = :pass", Long.class)
                    .setParameter("id", nhanVienID)
                    .setParameter("pass", newPass)
                    .getSingleResult();
            return count > 0;
        });
    }
}