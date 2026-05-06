package dao.impl;/*
 * @ (#) TuyenChiTiet_DAO.java   1.0     21/10/2025
package dao;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 21/10/2025
 */

import entity.Ga;
import entity.Tuyen;
import entity.TuyenChiTiet;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class TuyenChiTiet_DAO extends AbstractGenericDAO<TuyenChiTiet, String> implements dao.ITuyenChiTietDAO {

    public TuyenChiTiet_DAO() {
        super(TuyenChiTiet.class);
    }

    /**
     * Lấy danh sách TuyenChiTiet (các Ga trên tuyến) dựa trên TuyenID.
     * Dữ liệu trả về chứa đủ thông tin Ga và Mô tả Tuyen cần thiết cho nghiệp vụ.
     *
     * @param tuyenID ID của tuyến.
     * @return List<TuyenChiTiet> chứa chi tiết các ga trên tuyến, sắp xếp theo thứ tự.
     */
    @Override
    public List<TuyenChiTiet> layDanhSachTheoTuyenID(String tuyenID) {
        return doInTransaction(em -> {
            String sql = "SELECT tct.tuyenID, tct.gaID, tct.thuTu, tct.khoangCachTuGaXuatPhatKm, " +
                    "t.moTa, ga.tenGa " +
                    "FROM TuyenChiTiet tct " +
                    "JOIN Tuyen t ON tct.tuyenID = t.tuyenID " +
                    "JOIN Ga ga ON tct.gaID = ga.gaID " +
                    "WHERE tct.tuyenID = ?1 " +
                    "ORDER BY tct.thuTu ASC";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, tuyenID);

            List<Object[]> results = query.getResultList();
            List<TuyenChiTiet> danhSach = new ArrayList<>();

            for (Object[] rs : results) {
                Tuyen tuyen = new Tuyen((String) rs[0], (String) rs[4]);
                Ga ga = new Ga((String) rs[1], (String) rs[5]);

                int thuTu = rs[2] != null ? ((Number) rs[2]).intValue() : 0;
                int khoangCach = rs[3] != null ? ((Number) rs[3]).intValue() : 0;

                TuyenChiTiet tct = new TuyenChiTiet(tuyen, ga, thuTu, khoangCach);
                danhSach.add(tct);
            }
            return danhSach;
        });
    }

    /**
     * Thêm danh sách TuyenChiTiet vào CSDL bằng Batch Insert trong một giao dịch (Transaction).
     *
     * @param danhSachChiTiet Danh sách các chi tiết tuyến cần thêm.
     * @return boolean true nếu thêm thành công.
     */
    @Override
    public boolean themDanhSachChiTiet(List<TuyenChiTiet> danhSachChiTiet) {
        try {
            return doInTransaction(em -> {
                String sql = "INSERT INTO TuyenChiTiet (tuyenID, gaID, thuTu, khoangCachTuGaXuatPhatKm) VALUES (?1, ?2, ?3, ?4)";

                for (TuyenChiTiet chiTiet : danhSachChiTiet) {
                    em.createNativeQuery(sql)
                            .setParameter(1, chiTiet.getTuyen().getTuyenID())
                            .setParameter(2, chiTiet.getGa().getGaID())
                            .setParameter(3, chiTiet.getThuTu())
                            .setParameter(4, chiTiet.getKhoangCachTuGaXuatPhatKm())
                            .executeUpdate();
                }
                return true;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa tất cả TuyenChiTiet của một Tuyen. Thường được dùng trong nghiệp vụ Cập nhật.
     *
     * @param tuyenID ID của tuyến cần xóa chi tiết.
     * @return boolean true nếu xóa thành công ít nhất một bản ghi.
     */
    @Override
    public boolean xoaChiTietTheoTuyenID(String tuyenID) {
        try {
            return doInTransaction(em -> {
                String sql = "DELETE FROM TuyenChiTiet WHERE tuyenID = ?1";
                int affectedRows = em.createNativeQuery(sql)
                        .setParameter(1, tuyenID)
                        .executeUpdate();
                return affectedRows > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
