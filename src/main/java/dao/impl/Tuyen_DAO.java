package dao.impl;

import connectDB.ConnectDB;
import entity.Ga;
import entity.Tuyen;
import entity.TuyenChiTiet;
import jakarta.persistence.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Tuyen_DAO extends AbstractGenericDAO<Tuyen, String> implements dao.ITuyenDAO {
    private Ga_DAO ga_dao = new Ga_DAO();

    public Tuyen_DAO() {
        super(Tuyen.class);
    }

    @Override
    public List<Tuyen> getAllTuyen() {
        return doInTransaction(em -> {
            String sql = "SELECT tuyenID, moTa, trangThai FROM Tuyen";

            Query query = em.createNativeQuery(sql);
            List<Object[]> results = query.getResultList();
            List<Tuyen> danhSachTuyen = new ArrayList<>();

            for (Object[] row : results) {
                Tuyen tuyen = new Tuyen((String) row[0], (String) row[1], (Boolean) row[2]);
                danhSachTuyen.add(tuyen);
            }
            return danhSachTuyen;
        });
    }

    @Override
    public List<Tuyen> getTuyenByID(String tuyenIDTim) {
        return doInTransaction(em -> {
            String sql = "SELECT * FROM Tuyen WHERE LOWER(tuyenID) LIKE ?1";

            Query query = em.createNativeQuery(sql, Tuyen.class);
            query.setParameter(1, "%" + tuyenIDTim.toLowerCase() + "%");

            @SuppressWarnings("unchecked")
            List<Tuyen> danhSachTuyen = query.getResultList();
            return danhSachTuyen;
        });
    }

    @Override
    public List<Tuyen> getTuyenTheoGa(String gaDi, String gaDen) {
        return doInTransaction(em -> {
            Map<String, Tuyen> tuyepMap = new LinkedHashMap<>();
            boolean hasBothGa = (gaDi != null && !gaDi.trim().isEmpty()) && (gaDen != null && !gaDen.trim().isEmpty());
            String sql;

            if (hasBothGa) {
                // Giữ nguyên câu truy vấn SQL phức tạp của bạn, chỉ đổi ? thành ?1, ?2, ?3
                sql = "SELECT DISTINCT t.tuyenID, t.moTa, t.trangThai " +
                        "FROM Tuyen t " +
                        "WHERE t.tuyenID IN ( "
                        + "    SELECT t1.tuyenID FROM TuyenChiTiet t1 JOIN Ga g1 ON t1.gaID = g1.gaID WHERE LOWER(g1.tenGa) LIKE LOWER(?1) "
                        + "    AND t1.tuyenID IN ( "
                        + "        SELECT t2.tuyenID FROM TuyenChiTiet t2 JOIN Ga g2 ON t2.gaID = g2.gaID WHERE LOWER(g2.tenGa) LIKE LOWER(?2) "
                        + "        AND t2.thuTu > ( "
                        + "            SELECT MIN(t3.thuTu) FROM TuyenChiTiet t3 JOIN Ga g3 ON t3.gaID = g3.gaID "
                        + "            WHERE LOWER(g3.tenGa) LIKE LOWER(?3) AND t3.tuyenID = t2.tuyenID "
                        + "        ) " + "    ) " + ") ORDER BY t.tuyenID";
            } else {
                sql = "SELECT DISTINCT t.tuyenID, t.moTa, t.trangThai "
                        + "FROM Tuyen t JOIN TuyenChiTiet t1 ON t.tuyenID = t1.tuyenID JOIN Ga g1 ON t1.gaID = g1.gaID "
                        + "WHERE 1=1 ";
                if (gaDi != null && !gaDi.trim().isEmpty()) {
                    sql += " AND LOWER(g1.tenGa) LIKE LOWER(?1) ";
                } else if (gaDen != null && !gaDen.trim().isEmpty()) {
                    sql += " AND LOWER(g1.tenGa) LIKE LOWER(?1) ";
                }
                sql += " ORDER BY t.tuyenID";
            }

            Query query = em.createNativeQuery(sql);

            if (hasBothGa) {
                query.setParameter(1, "%" + gaDi.trim() + "%");
                query.setParameter(2, "%" + gaDen.trim() + "%");
                query.setParameter(3, "%" + gaDi.trim() + "%");
            } else if (gaDi != null && !gaDi.trim().isEmpty()) {
                query.setParameter(1, "%" + gaDi.trim() + "%");
            } else if (gaDen != null && !gaDen.trim().isEmpty()) {
                query.setParameter(1, "%" + gaDen.trim() + "%");
            }

            List<Object[]> results = query.getResultList();
            for (Object[] row : results) {
                String tuyenID = (String) row[0];
                if (!tuyepMap.containsKey(tuyenID)) {
                    Tuyen tuyen = new Tuyen(tuyenID, (String) row[1], (Boolean) row[2]);
                    tuyepMap.put(tuyenID, tuyen);
                }
            }
            return new ArrayList<>(tuyepMap.values());
        });
    }

    @Override
    public boolean themTuyenMoi(Tuyen tuyenMoi) {
        if (tuyenMoi == null || tuyenMoi.getTuyenID() == null || tuyenMoi.getTuyenID().isEmpty()) {
            return false;
        }
        try {
            return doInTransaction(em -> {
                String sql = "INSERT INTO Tuyen(tuyenID, moTa, trangThai) VALUES(?1, ?2, ?3)";
                int affectedRows = em.createNativeQuery(sql)
                        .setParameter(1, tuyenMoi.getTuyenID())
                        .setParameter(2, tuyenMoi.getMoTa())
                        .setParameter(3, tuyenMoi.isTrangThai())
                        .executeUpdate();
                return affectedRows > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean xoaTuyen(String tuyenID) {
        try {
            return doInTransaction(em -> {
                String sql = "DELETE FROM Tuyen WHERE tuyenID = ?1";
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

    @Override
    public boolean capNhatTuyen(Tuyen tuyenCapNhat) {
        try {
            return doInTransaction(em -> {
                String sql = "UPDATE Tuyen SET moTa = ?1, trangThai = ?2 WHERE tuyenID = ?3";
                int affectedRows = em.createNativeQuery(sql)
                        .setParameter(1, tuyenCapNhat.getMoTa())
                        .setParameter(2, tuyenCapNhat.isTrangThai())
                        .setParameter(3, tuyenCapNhat.getTuyenID())
                        .executeUpdate();
                return affectedRows > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tuyến theo mã tuyến chính xác (không dùng like) Dùng để kiểm tra xem mã
     * đã tồn tại hay chưa.
     *
     * @param tuyenIDTim Mã tuyến cần tìm.
     * @return Tuyen object nếu tìm thấy, null nếu không.
     */
    @Override
    public Tuyen getTuyenByExactID(String tuyenIDTim) {
        return doInTransaction(em -> {
            String sql = "SELECT * FROM Tuyen WHERE tuyenID = ?1";
            Query query = em.createNativeQuery(sql, Tuyen.class);
            query.setParameter(1, tuyenIDTim);

            @SuppressWarnings("unchecked")
            List<Tuyen> results = query.getResultList();
            if (!results.isEmpty()) {
                return results.get(0);
            }
            return null;
        });
    }

    @Override
    public List<Tuyen> getTop10Tuyen(String keyword) {
        return doInTransaction(em -> {
            String sql = "SELECT TOP 10 tuyenID, moTa, trangThai FROM Tuyen WHERE tuyenID LIKE ?1 OR moTa LIKE ?2";
            Query query = em.createNativeQuery(sql);
            query.setParameter(1, "%" + keyword + "%");
            query.setParameter(2, "%" + keyword + "%");

            List<Object[]> results = query.getResultList();
            List<Tuyen> list = new ArrayList<>();
            for (Object[] row : results) {
                list.add(new Tuyen((String) row[0], (String) row[1], (Boolean) row[2]));
            }
            return list;
        });
    }

    @Override
    public Tuyen layTuyenTheoMa(String maTuyen) {
        return doInTransaction(em -> {
            String sql = "SELECT * FROM Tuyen WHERE tuyenID = ?1";
            Query query = em.createNativeQuery(sql, Tuyen.class);
            query.setParameter(1, maTuyen);

            @SuppressWarnings("unchecked")
            List<Tuyen> results = query.getResultList();
            if (!results.isEmpty()) {
                return results.get(0);
            }
            return null;
        });
    }

    /**
     * Lấy danh sách chi tiết các ga (TuyenChiTiet) thuộc về một Tuyến
     * Sắp xếp theo thứ tự (thuTu)
     */
    @Override
    public List<TuyenChiTiet> layDanhSachTuyenChiTiet(String maTuyen) {
        return doInTransaction (em -> {
            String sql = "SELECT tct.tuyenID, tct.gaID, tct.thuTu, tct.khoangCachTuGaXP, g.tenGa " +
                    "FROM TuyenChiTiet tct " +
                    "JOIN Ga g ON tct.gaID = g.gaID " +
                    "WHERE tct.tuyenID = ?1 " +
                    "ORDER BY tct.thuTu ASC";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, maTuyen);

            List<Object[]> results = query.getResultList();
            List<TuyenChiTiet> list = new ArrayList<>();

            Tuyen tuyen = getTuyenByExactID(maTuyen);
            if (tuyen == null) tuyen = new Tuyen(maTuyen);

            for (Object[] rs : results) {
                String gaID = (String) rs[1];
                int thuTu = rs[2] != null ? ((Number) rs[2]).intValue() : 0;
                int khoangCach = rs[3] != null ? ((Number) rs[3]).intValue() : 0;
                String tenGa = (String) rs[4];

                Ga ga = new Ga(gaID, tenGa);
                TuyenChiTiet ct = new TuyenChiTiet(tuyen, ga, thuTu, khoangCach);
                list.add(ct);
            }
            return list;
        });
    }
}
