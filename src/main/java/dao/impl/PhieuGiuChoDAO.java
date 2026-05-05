package dao.impl;
/*
 * @(#) PhieuGiuCho_DAO.java  1.0  [2:03:36 PM] Oct 26, 2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

/*
 * @description
 * @author: NguyenThiHuynhNhu
 * @date: Oct 26, 2025
 * @version: 1.0
 */

import dao.IPhieuGiuChoDAO;
import entity.PhieuGiuCho;
import entity.type.TrangThaiPhieuGiuCho;
import jakarta.persistence.Query;

import java.time.LocalDateTime;

public class PhieuGiuChoDAO extends AbstractGenericDAO<PhieuGiuCho, String> implements IPhieuGiuChoDAO {

    public PhieuGiuChoDAO() {
        super(PhieuGiuCho.class);
    }

    /**
     * Cập nhật trạng thái của một PhieuGiuCho. Dùng khi xác nhận (XAC_NHAN) hoặc
     * hủy (HET_HAN).
     *
     * @param phieuGiuChoID ID của phiếu cần cập nhật.
     * @param newTrangThai  Trạng thái mới ('XAC_NHAN' hoặc 'HET_HAN').
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateTrangThaiPhieuGiuCho(String phieuGiuChoID, String newTrangThai) {
        return doInTransaction(em -> {
            String jpql = "UPDATE PhieuGiuCho p SET p.trangThai = :newTrangThai WHERE p.phieuGiuChoID = :phieuGiuChoID";
            Query query = em.createQuery(jpql);
            query.setParameter("newTrangThai", TrangThaiPhieuGiuCho.valueOf(newTrangThai));
            query.setParameter("phieuGiuChoID", phieuGiuChoID);
            return query.executeUpdate() > 0;
        });
    }

    /**
     * Chạy định kỳ để "dọn dẹp" các phiếu giữ chỗ (bảng cha) đã hết hạn. Chỉ cập
     * nhật các phiếu 'DANG_GIU' đã quá thời gian quy định.
     *
     * @param expiryMinutes Số phút mà một phiếu được coi là hết hạn.
     * @return Số lượng phiếu (bảng cha) đã được cập nhật sang 'HET_HAN'.
     */
    public int cleanUpExpiredPhieuGiuCho(int expiryMinutes) {
        return doInTransaction(em -> {
            LocalDateTime expiryTime = LocalDateTime.now().minusMinutes(expiryMinutes);
            String jpql = "UPDATE PhieuGiuCho p "
                    + "SET p.trangThai = :hetHan "
                    + "WHERE p.trangThai = :dangGiu "
                    + "  AND p.thoiDiemTao < :expiryTime";
            Query query = em.createQuery(jpql);
            query.setParameter("hetHan", TrangThaiPhieuGiuCho.HET_HAN);
            query.setParameter("dangGiu", TrangThaiPhieuGiuCho.DANG_GIU);
            query.setParameter("expiryTime", expiryTime);
            return query.executeUpdate();
        });
    }
}