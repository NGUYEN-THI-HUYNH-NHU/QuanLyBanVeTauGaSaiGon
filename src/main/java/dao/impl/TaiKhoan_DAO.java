package dao.impl;
/*
 * @(#) TaiKhoan_DAO.java  1.0  [4:09:04 PM] Sep 25, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Sep 25, 2025
 * @version: 1.0
 */

import dao.ITaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class TaiKhoan_DAO extends AbstractGenericDao<TaiKhoan, String> implements ITaiKhoan_DAO {

    public TaiKhoan_DAO() {
        super(TaiKhoan.class);
    }

    /**
     * TÌM TÀI KHOẢN THEO TÊN ĐĂNG NHẬP
     */
    public TaiKhoan getTaiKhoanVoiTenDangNhap(String tenDangNhap) {
        return doInTransaction(em -> {
            try {
                String jpql = "SELECT t FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
                return em.createQuery(jpql, TaiKhoan.class)
                        .setParameter("ten", tenDangNhap)
                        .getSingleResult();
            } catch (Exception e) {
                return null; // Trả về null nếu không tìm thấy (NoResultException)
            }
        });
    }

    /**
     * TẠO TÀI KHOẢN (Đã có sẵn trong lớp cha, ta chỉ gọi lại)
     */
    public boolean taoTaiKhoan(TaiKhoan taiKhoan) {
        try {
            create(taiKhoan);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CẬP NHẬT MẬT KHẨU
     */
    public boolean capNhatMatKhau(String nhanVienID, String newMatKhau) {
        return doInTransaction(em -> {
            // JPQL xuyên qua quan hệ: t.nhanVien.id
            String jpql = "UPDATE TaiKhoan t SET t.matKhauHash = :pass WHERE t.nhanVien.id = :nvId";
            int rowsUpdated = em.createQuery(jpql)
                    .setParameter("pass", newMatKhau)
                    .setParameter("nvId", nhanVienID)
                    .executeUpdate();
            return rowsUpdated > 0;
        });
    }

    /**
     * KIỂM TRA TÊN ĐĂNG NHẬP CÓ TỒN TẠI KHÔNG
     */
    public boolean isTaiKhoanTonTai(String tenDangNhap) {
        return doInTransaction(em -> {
            String jpql = "SELECT COUNT(t) FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("ten", tenDangNhap)
                    .getSingleResult();
            return count > 0;
        });
    }

    public boolean kiemTraTenDangNhap(String tenDN) {
        return isTaiKhoanTonTai(tenDN); // Dùng chung logic cho gọn
    }

    /**
     * TÌM TÀI KHOẢN THEO MÃ NHÂN VIÊN
     */
    public TaiKhoan getTaiKhoanVoiNhanVienID(String nhanVienIDtim) {
        return doInTransaction(em -> {
            try {
                String jpql = "SELECT t FROM TaiKhoan t WHERE t.nhanVien.id = :nvId";
                return em.createQuery(jpql, TaiKhoan.class)
                        .setParameter("nvId", nhanVienIDtim)
                        .getSingleResult();
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * LẤY NHÂN VIÊN BẰNG TÊN ĐĂNG NHẬP
     * Sức mạnh của JPA: Chỉ cần SELECT t.nhanVien, Hibernate tự động truy vấn thông tin nhân viên
     */
    public NhanVien getNhanVienByTenDangNhap(String tenDangNhap, boolean xacThuc) {
        if (!xacThuc) return null; // Nếu chưa xác thực mật khẩu (tầng BUS), trả về null luôn

        return doInTransaction(em -> {
            try {
                String jpql = "SELECT t.nhanVien FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
                return em.createQuery(jpql, NhanVien.class)
                        .setParameter("ten", tenDangNhap)
                        .getSingleResult();
            } catch (Exception e) {
                return null;
            }
        });
    }

    /**
     * LẤY DANH SÁCH TÀI KHOẢN
     */
    public List<TaiKhoan> getDanhSachTaiKhoan() {
        return findAll(); // Hàm cha đã có sẵn
    }

    /**
     * CẬP NHẬT THÔNG TIN TÀI KHOẢN
     */
    public boolean capNhatTaiKhoan(TaiKhoan tkMoi) {
        return doInTransaction(em -> {
            String jpql = "UPDATE TaiKhoan t SET t.tenDangNhap = :tenDN, t.matKhauHash = :matKhau, t.trangThai = :trangThai WHERE t.id = :id";
            int n = em.createQuery(jpql)
                    .setParameter("tenDN", tkMoi.getTenDangNhap())
                    .setParameter("matKhau", tkMoi.getMatKhauHash())
                    .setParameter("trangThai", tkMoi.isTrangThai())
                    .setParameter("id", tkMoi.getId())
                    .executeUpdate();
            return n > 0;
        });
    }

    /**
     * PHÁT SINH MÃ TÀI KHOẢN TỰ ĐỘNG
     */
    public String taoMaTaiKhoanMoi() {
        return doInTransaction(em -> {
            String jpql = "SELECT MAX(t.id) FROM TaiKhoan t";
            String maCuoi = em.createQuery(jpql, String.class).getSingleResult();
            if (maCuoi == null) {
                return "TK001";
            }
            int so = Integer.parseInt(maCuoi.substring(2)) + 1;
            return String.format("TK%03d", so);
        });
    }

    /**
     * GỢI Ý TÊN ĐĂNG NHẬP (Lấy tên có số thứ tự lớn nhất rồi + 1)
     */
    public String goiYTenDangNhap(String tenDN) {
        return doInTransaction(em -> {
            try {
                // Lấy tên đăng nhập gần nhất (DESC) để gợi ý số tiếp theo
                String jpql = "SELECT t.tenDangNhap FROM TaiKhoan t WHERE t.tenDangNhap LIKE :prefix ORDER BY t.tenDangNhap DESC";
                String maxTenDN = em.createQuery(jpql, String.class)
                        .setParameter("prefix", tenDN + "%")
                        .setMaxResults(1)
                        .getSingleResult();

                if (maxTenDN != null && maxTenDN.length() > tenDN.length()) {
                    int soThuTu = Integer.parseInt(maxTenDN.substring(tenDN.length())) + 1;
                    return tenDN + soThuTu;
                }
                return tenDN + "1"; // Nếu chưa có số nào, bắt đầu từ 1
            } catch (Exception e) {
                return tenDN; // Chưa bị trùng thì lấy luôn tên gốc
            }
        });
    }

    /**
     * TÌM KIẾM TỔNG HỢP (Dynamic Query)
     */
    public List<TaiKhoan> timKiemTongHop(String maNV, String tenDN, String vaiTro, Boolean trangThai) {
        return doInTransaction(em -> {
            StringBuilder jpql = new StringBuilder("SELECT t FROM TaiKhoan t WHERE 1=1");

            if (maNV != null && !maNV.isEmpty()) jpql.append(" AND t.nhanVien.id LIKE :maNV");
            if (tenDN != null && !tenDN.isEmpty()) jpql.append(" AND t.tenDangNhap LIKE :tenDN");
            if (vaiTro != null && !vaiTro.isEmpty()) jpql.append(" AND t.vaiTroTaiKhoan.id = :vaiTro");
            if (trangThai != null) jpql.append(" AND t.trangThai = :trangThai");

            TypedQuery<TaiKhoan> query = em.createQuery(jpql.toString(), TaiKhoan.class);

            if (maNV != null && !maNV.isEmpty()) query.setParameter("maNV", "%" + maNV + "%");
            if (tenDN != null && !tenDN.isEmpty()) query.setParameter("tenDN", "%" + tenDN + "%");
            if (vaiTro != null && !vaiTro.isEmpty()) query.setParameter("vaiTro", vaiTro);
            if (trangThai != null) query.setParameter("trangThai", trangThai);

            return query.getResultList();
        });
    }

    /**
     * KIỂM TRA THÔNG TIN QUÊN MẬT KHẨU
     * Ghi chú: Tham số là cccd nhưng đối chiếu với soDienThoai để giữ đúng logic SQL cũ của bạn
     */
    public boolean checkForgotPasswordInfo(String nhanVienID, String cccd, String email) {
        return doInTransaction(em -> {
            String jpql = "SELECT COUNT(n) FROM NhanVien n WHERE n.id = :id AND n.soDienThoai = :sdt AND n.email = :email";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("id", nhanVienID)
                    .setParameter("sdt", cccd)
                    .setParameter("email", email)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * KIỂM TRA MẬT KHẨU TRÙNG LẶP
     */
    public boolean checkDuplicatingPasswords(String nhanVienID, String newPass) {
        return doInTransaction(em -> {
            String jpql = "SELECT COUNT(t) FROM TaiKhoan t WHERE t.nhanVien.id = :nvId AND t.matKhauHash = :pass";
            Long count = em.createQuery(jpql, Long.class)
                    .setParameter("nvId", nhanVienID)
                    .setParameter("pass", newPass)
                    .getSingleResult();
            return count > 0;
        });
    }

    /**
     * TÌM TÀI KHOẢN THEO ID
     */
    public TaiKhoan timTaiKhoanTheoID(String taiKhoanID) {
        return findById(taiKhoanID); // Hàm cha đã lo
    }
}