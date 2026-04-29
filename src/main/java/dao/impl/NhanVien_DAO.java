package dao.impl;
/*
 * @(#) Nhan.java  1.0  [4:10:00 PM] Sep 25, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 25, 2025
 * @version: 1.0
 */

import dao.INhanVien_DAO;
import entity.CaLam;
import entity.NhanVien;
import entity.VaiTroNhanVien;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class NhanVien_DAO extends AbstractGenericDao<NhanVien, String> implements INhanVien_DAO {

    public NhanVien_DAO() {
        super(NhanVien.class);
    }

    /**
     * Tìm nhân viên theo họ tên (Dùng JPQL)
     */
    @Override
    public List<NhanVien> getNhanVienVoiHoTen(String hoTenTim) {
        return doInTransaction(em -> {
            String jpql = "SELECT n FROM NhanVien n WHERE n.hoTen LIKE :hoTen";
            return em.createQuery(jpql, NhanVien.class)
                    .setParameter("hoTen", "%" + hoTenTim + "%")
                    .getResultList();
        });
    }

    /**
     * Tạo mã nhân viên tự động (Dùng JPQL để lấy mã lớn nhất)
     */
    @Override
    public String taoMaNhanVienTuDong() {
        return doInTransaction(em -> {
            String jpql = "SELECT MAX(n.id) FROM NhanVien n";
            String maCuoi = em.createQuery(jpql, String.class).getSingleResult();

            if (maCuoi == null) {
                return "NV001";
            }

            int so = Integer.parseInt(maCuoi.substring(2)) + 1;
            return String.format("NV%03d", so);
        });
    }

    /**
     * Tìm kiếm nhân viên đa tiêu chí (Dùng Dynamic JPQL)
     */
    @Override
    public List<NhanVien> timKiemNhanVien(String ten, String sdt, String vaiTroID, Boolean isHoatDong) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT n FROM NhanVien n WHERE 1=1");

            if (ten != null && !ten.isEmpty()) jpql.append(" AND n.hoTen LIKE :ten");
            if (sdt != null && !sdt.isEmpty()) jpql.append(" AND n.soDienThoai LIKE :sdt");
            if (vaiTroID != null) jpql.append(" AND n.vaiTroNhanVien.id = :vaiTroID");
            if (isHoatDong != null) jpql.append(" AND n.isHoatDong = :isHoatDong");

            TypedQuery<NhanVien> query = em.createQuery(jpql.toString(), NhanVien.class);

            if (ten != null && !ten.isEmpty()) query.setParameter("ten", "%" + ten + "%");
            if (sdt != null && !sdt.isEmpty()) query.setParameter("sdt", "%" + sdt + "%");
            if (vaiTroID != null) query.setParameter("vaiTroID", vaiTroID);
            if (isHoatDong != null) query.setParameter("isHoatDong", isHoatDong);

            return query.getResultList();
        });
    }

    /**
     * Lấy VaiTroNhanVien theo mã nhân viên
     */
    @Override
    public VaiTroNhanVien layVaiTroNhanVienTheoMaNV(String maNV) {
        return doInTransaction(em -> {
            // JPQL tự động duyệt qua quan hệ ManyToOne để lấy đúng Entity VaiTroNhanVien
            String jpql = "SELECT n.vaiTroNhanVien FROM NhanVien n WHERE n.id = :maNV";
            try {
                return em.createQuery(jpql, VaiTroNhanVien.class)
                        .setParameter("maNV", maNV)
                        .getSingleResult();
            } catch (Exception e) {
                // Trả về null nếu không tìm thấy nhân viên hoặc không có vai trò
                return null;
            }
        });
    }

    /**
     * Cập nhật riêng ảnh đại diện (Dùng JPQL Update để tối ưu hiệu suất)
     */
    public boolean capNhatAvatar(String nhanVienID, byte[] avatarData) {
        return doInTransaction(em -> {
            String jpql = "UPDATE NhanVien n SET n.avatar = :avatar WHERE n.id = :id";
            int result = em.createQuery(jpql)
                    .setParameter("avatar", avatarData)
                    .setParameter("id", nhanVienID)
                    .executeUpdate();
            return result > 0;
        });
    }

    /**
     * Lấy danh sách tất cả mã nhân viên
     */
    public List<String> layDanhSachMaNhanVien() {
        return doInTransaction(em -> {
            String jpql = "SELECT n.id FROM NhanVien n";
            return em.createQuery(jpql, String.class).getResultList();
        });
    }

    // --- PHẦN XỬ LÝ CA LÀM (Nếu bạn chưa tạo CaLamRepository) ---

    public List<CaLam> getAllCaLam() {
        return doInTransaction(em -> {
            return em.createQuery("SELECT c FROM CaLam c", CaLam.class).getResultList();
        });
    }

    public CaLam getCaLamById(String caLamID) {
        return doInTransaction(em -> em.find(CaLam.class, caLamID));
    }
}