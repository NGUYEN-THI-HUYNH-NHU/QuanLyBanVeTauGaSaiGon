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
import jakarta.persistence.Query;

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
    public boolean updateTrangThaiPhieuGiuCho(String phieuGiuChoID, String newTrangThai) throws Exception {
        return doInTransaction(em -> {
            String sql = "UPDATE PhieuGiuCho SET trangThai = ?1 WHERE phieuGiuChoID = ?2";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, newTrangThai);
            query.setParameter(2, phieuGiuChoID);
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
            String sql = "UPDATE PhieuGiuCho "
                    + "SET trangThai = 'HET_HAN' "
                    + "WHERE trangThai = 'DANG_GIU' "
                    + "  AND thoiDiemTao < DATEADD(minute, -?1, SYSUTCDATETIME())";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, expiryMinutes);
            return query.executeUpdate();
        });
    }
}