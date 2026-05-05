package dao.impl;
/*
 * @(#) PhieuGiuChoChiTiet_DAO.java  1.0  [2:54:06 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

import dao.IPhieuGiuChoChiTietDAO;
import entity.PhieuGiuChoChiTiet;
import entity.Ve;
import entity.type.TrangThaiPhieuGiuCho;
import jakarta.persistence.Query;

import java.time.LocalDateTime;
import java.util.List;

public class PhieuGiuChoChiTietDAO extends AbstractGenericDAO<PhieuGiuChoChiTiet, String> implements IPhieuGiuChoChiTietDAO {
    // Thời gian giữ chỗ mặc định là 10 phút
    private static final int HOLD_DURATION_MINUTES = 10;

    public PhieuGiuChoChiTietDAO() {
        super(PhieuGiuChoChiTiet.class);
    }

    /**
     * Phương thức kiểm tra xung đột đã được cập nhật. Nhận đầu vào là thông tin từ
     * "vé session".
     *
     * @param chuyenID ID của chuyến (đã có)
     * @param tenGaDi  Tên ga đi (ví dụ: 'Sài Gòn')
     * @param tenGaDen Tên ga đến (ví dụ: 'Đà Nẵng')
     * @param soToa    Số thứ tự của toa (ví dụ: 3)
     * @param soGhe    Số thứ tự của ghế (ví dụ: 5)
     * @return true nếu CÓ XUNG ĐỘT (bị chiếm), false nếu GHẾ TRỐNG.
     */
    @Override
    public boolean checkConflict(String chuyenID, String tenGaDi, String tenGaDen, int soToa,
                                 int soGhe) {
        return doInTransaction(em -> {
            String sqlCheck = "-- 1. Khai báo tham số từ session\n"
                    + "DECLARE @chuyenID VARCHAR(50) = ?1 ;\n"
                    + "DECLARE @tenGaDi NVARCHAR(255) = ?2 ;\n"
                    + "DECLARE @tenGaDen NVARCHAR(255) = ?3 ;\n"
                    + "DECLARE @soToa INT = ?4 ;\n"
                    + "DECLARE @soGhe INT = ?5 ;\n"
                    + "DECLARE @holdMinutes INT = ?6 ;\n" + "\n"
                    + "-- 2. Truy vấn các ID cần thiết từ thông tin session\n"
                    + "DECLARE @gaDiID VARCHAR(50), @gaDenID VARCHAR(50), @gheID VARCHAR(50);\n"
                    + "DECLARE @thuTuGaDi_Moi INT, @thuTuGaDen_Moi INT;\n" + "\n"
                    + "SELECT @gaDiID = gaID FROM Ga WHERE tenGa = @tenGaDi;\n"
                    + "SELECT @gaDenID = gaID FROM Ga WHERE tenGa = @tenGaDen;\n" + "\n" + "SELECT @gheID = g.gheID\n"
                    + "FROM Ghe g\n" + "JOIN Toa t ON g.toaID = t.toaID\n" + "JOIN Chuyen c ON t.tauID = c.tauID\n"
                    + "WHERE c.chuyenID = @chuyenID\n" + "  AND t.soToa = @soToa\n" + "  AND g.soGhe = @soGhe;\n" + "\n"
                    + "IF @gheID IS NULL OR @gaDiID IS NULL OR @gaDenID IS NULL\n" + "BEGIN\n"
                    + "    RAISERROR('Không tìm thấy thông tin Ga ID hoặc Ghế ID từ dữ liệu đầu vào.', 16, 1);\n"
                    + "    RETURN;\n" + "END\n" + "\n" + "-- 3. Lấy thứ tự của ga đi/ga đến MỚI\n"
                    + "SELECT @thuTuGaDi_Moi = thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDiID;\n"
                    + "SELECT @thuTuGaDen_Moi = thuTu FROM ChuyenGa WHERE chuyenID = @chuyenID AND gaID = @gaDenID;\n"
                    + "\n" + "IF @thuTuGaDi_Moi IS NULL OR @thuTuGaDen_Moi IS NULL\n" + "BEGIN\n"
                    + "    RAISERROR('Không tìm thấy thông tin ga đi/ga đến trong lịch trình của chuyến.', 16, 1);\n"
                    + "    RETURN;\n" + "END\n" + "\n" + "-- 4. Phần logic kiểm tra xung đột\n" + "SELECT v.veID\n"
                    + "FROM Ve v\n" + "JOIN ChuyenGa cgDi ON v.chuyenID = cgDi.chuyenID AND v.gaDiID = cgDi.gaID\n"
                    + "JOIN ChuyenGa cgDen ON v.chuyenID = cgDen.chuyenID AND v.gaDenID = cgDen.gaID\n"
                    + "WHERE v.chuyenID = @chuyenID\n" + "  AND v.gheID = @gheID\n"
                    + "  AND v.trangThai IN ('DA_BAN', 'DA_DUNG')\n" + "  AND cgDi.thuTu < @thuTuGaDen_Moi\n"
                    + "  AND cgDen.thuTu > @thuTuGaDi_Moi\n" + "\n" + "UNION ALL\n" + "\n"
                    + "SELECT pgcct.phieuGiuChoChiTietID\n" + "FROM PhieuGiuChoChiTiet pgcct\n"
                    + "JOIN PhieuGiuCho pgc ON pgcct.phieuGiuChoID = pgc.phieuGiuChoID\n"
                    + "JOIN ChuyenGa cgDi ON pgcct.chuyenID = cgDi.chuyenID AND pgcct.gaDiID = cgDi.gaID\n"
                    + "JOIN ChuyenGa cgDen ON pgcct.chuyenID = cgDen.chuyenID AND pgcct.gaDenID = cgDen.gaID\n"
                    + "WHERE pgcct.chuyenID = @chuyenID\n" + "  AND pgcct.gheID = @gheID\n"
                    + "  AND pgcct.trangThai = 'DANG_GIU'\n" + "  AND pgc.trangThai = 'DANG_GIU'\n"
                    + "  AND pgc.thoiDiemTao > DATEADD(minute, -@holdMinutes, SYSUTCDATETIME())\n"
                    + "  AND cgDi.thuTu < @thuTuGaDen_Moi\n";

            Query query = em.createNativeQuery(sqlCheck);
            query.setParameter(1, chuyenID);
            query.setParameter(2, tenGaDi);
            query.setParameter(3, tenGaDen);
            query.setParameter(4, soToa);
            query.setParameter(5, soGhe);
            query.setParameter(6, HOLD_DURATION_MINUTES);

            try {
                List<?> results = query.getResultList();
                return !results.isEmpty();
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            }
        });
    }

    /**
     *
     * @param phieuGiuChoID ID của phiếu cha
     * @param newTrangThai  Trạng thái mới ('XAC_NHAN', 'HET_GIU')
     * @return true nếu cập nhật thành công
     */
    @Override
    public boolean updateTrangThaiPhieuGiuChoChiTietByPhieuGiuChoID(String phieuGiuChoID,
                                                                    String newTrangThai) {
        return doInTransaction(em -> {
            String jpql = "UPDATE PhieuGiuChoChiTiet p SET p.trangThai = :newTrangThai " +
                    "WHERE p.phieuGiuCho.phieuGiuChoID = :phieuGiuChoID";
            Query query = em.createQuery(jpql);
            query.setParameter("newTrangThai", TrangThaiPhieuGiuCho.valueOf(newTrangThai));
            query.setParameter("phieuGiuChoID", phieuGiuChoID);

            return query.executeUpdate() > 0;
        });
    }

    /**
     *
     * @param expiryMinutes Số phút quy định hết hạn
     * @return Số lượng chi tiết đã được cập nhật
     */
    @Override
    public int cleanUpExpiredPhieuGiuChoChiTiet(int expiryMinutes) {
        return doInTransaction(em -> {
            LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(expiryMinutes);

            String jpql = "UPDATE PhieuGiuChoChiTiet pgcct SET pgcct.trangThai = :hetGiu " +
                    "WHERE pgcct.trangThai = :dangGiu AND " +
                    "pgcct.phieuGiuCho.phieuGiuChoID IN ( " +
                    "   SELECT p.phieuGiuChoID FROM PhieuGiuCho p " +
                    "   WHERE p.trangThai IN (:dangGiu, :hetHan) AND p.thoiDiemTao < :expiryTime " +
                    ")";

            Query query = em.createQuery(jpql);
            query.setParameter("hetGiu", TrangThaiPhieuGiuCho.HET_GIU);
            query.setParameter("dangGiu", TrangThaiPhieuGiuCho.DANG_GIU);
            query.setParameter("hetHan", TrangThaiPhieuGiuCho.HET_HAN);
            query.setParameter("expiryTime", expiryTime);

            return query.executeUpdate();
        });
    }

    /**
     * @param phieuGiuChoID
     * @return
     */
    @Override
    public boolean deletePhieuGiuChoChiTietByPgcID(String phieuGiuChoID) {
        return doInTransaction(em -> {
            String jpql = "DELETE FROM PhieuGiuChoChiTiet p WHERE p.phieuGiuCho.phieuGiuChoID = :phieuGiuChoID";
            Query query = em.createQuery(jpql);
            query.setParameter("phieuGiuChoID", phieuGiuChoID);
            return query.executeUpdate() > 0;
        });
    }

    /**
     * @param ve
     * @param trangThai
     */
    @Override
    public boolean updateTrangThaiPhieuGiuChoChiTietByVe(Ve ve, TrangThaiPhieuGiuCho trangThai) {
        return doInTransaction(em -> {
            String jpql = "UPDATE PhieuGiuChoChiTiet p SET p.trangThai = :trangThaiMoi " +
                    "WHERE p.chuyen.chuyenID = :chuyenID " +
                    "AND p.ghe.gheID = :gheID " +
                    "AND p.gaDi.gaID = :gaDiID " +
                    "AND p.gaDen.gaID = :gaDenID " +
                    "AND p.trangThai = :trangThaiCu";

            Query query = em.createQuery(jpql);
            query.setParameter("trangThaiMoi", trangThai);
            query.setParameter("chuyenID", ve.getChuyen().getChuyenID());
            query.setParameter("gheID", ve.getGhe().getGheID());
            query.setParameter("gaDiID", ve.getGaDi().getGaID());
            query.setParameter("gaDenID", ve.getGaDen().getGaID());
            query.setParameter("trangThaiCu", TrangThaiPhieuGiuCho.XAC_NHAN);

            return query.executeUpdate() > 0;
        });
    }
}
