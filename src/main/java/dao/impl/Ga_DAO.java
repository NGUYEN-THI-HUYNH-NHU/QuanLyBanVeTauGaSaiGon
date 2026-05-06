package dao.impl;

import entity.Ga;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;

public class Ga_DAO extends AbstractGenericDAO<Ga, String> implements dao.IGaDAO {

    public Ga_DAO() {
        super(Ga.class);
    }

    @Override
    public List<Ga> searchGaByPrefix(String prefix, int limit) {
        return doInTransaction(em -> {
            String sql = "SELECT gaID, tenGa FROM Ga WHERE tenGa LIKE ?1 ORDER BY tenGa";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, prefix + "%");
            query.setMaxResults(limit);

            List<Object[]> results = query.getResultList();
            List<Ga> gaList = new ArrayList<>();
            for (Object[] row : results) {
                gaList.add(new Ga((String) row[0], (String) row[1]));
            }
            return gaList;
        });
    }

    @Override
    public Ga getGaByTenGa(String tenGa) {
        return doInTransaction(em -> {
            String sql = "SELECT gaID, tenGa FROM Ga WHERE tenGa = ?1";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, tenGa);

            List<Object[]> results = query.getResultList();
            if (!results.isEmpty()) {
                Object[] row = results.get(0);
                return new Ga((String) row[0], (String) row[1]);
            }
            return null;
        });
    }

    @Override
    public List<Ga> getGaByTenGaList(String tenGaTim) {
        return doInTransaction(em -> {
            String sql = "SELECT * FROM Ga WHERE LOWER(tenGa) LIKE ?1";

            Query query = em.createNativeQuery(sql, Ga.class);
            query.setParameter(1, "%" + tenGaTim.toLowerCase() + "%");

            @SuppressWarnings("unchecked")
            List<Ga> dsGa = query.getResultList();
            return dsGa;
        });
    }


    @Override
    public List<Ga> searchGaDenKhaThiByGaDi(String gaDiID, String prefixGaDen, int limit) {
        return doInTransaction(em -> {
            String sql = "SELECT DISTINCT "
                    + " cg2.gaID, g2.tenGa "
                    + " FROM ChuyenGa cg1 "
                    + " JOIN Chuyen c ON c.chuyenID = cg1.chuyenID "
                    + " JOIN ChuyenGa cg2 ON cg2.chuyenID = cg1.chuyenID AND cg2.thuTu > cg1.thuTu "
                    + " JOIN Ga g2 ON g2.gaID = cg2.gaID "
                    + " WHERE cg1.gaID = ?1 "
                    + " AND g2.tenGa LIKE ?2 "
                    + " AND cg2.gaID != ?3 "
                    + " ORDER BY g2.tenGa";

            Query query = em.createNativeQuery(sql);
            query.setParameter(1, gaDiID);
            query.setParameter(2, prefixGaDen + "%");
            query.setParameter(3, gaDiID);
            query.setMaxResults(limit);

            List<Object[]> results = query.getResultList();
            List<Ga> gaList = new ArrayList<>();
            for (Object[] row : results) {
                gaList.add(new Ga((String) row[0], (String) row[1]));
            }
            return gaList;
        });
    }

    @Override
    public List<Ga> getAllGa() {
        return doInTransaction(em -> {
            String sql = "SELECT * FROM Ga";
            return em.createNativeQuery(sql, Ga.class).getResultList();
        });
    }

    @Override
    public Ga getGaByIDTim(String gaIDTim) {
        return doInTransaction(em -> {
            String sql = "SELECT * FROM Ga WHERE gaID = ?1";

            Query query = em.createNativeQuery(sql, Ga.class);
            query.setParameter(1, gaIDTim);

            @SuppressWarnings("unchecked")
            List<Ga> results = query.getResultList();
            return results.isEmpty() ? null : results.get(0);
        });
    }

    @Override
    public boolean themGa(Ga gaMoi) {
        try {
            return doInTransaction(em -> {
                String insertSQL = "INSERT INTO Ga (gaID, tenGa, tinhThanh) VALUES (?1, ?2, ?3)";
                int hangAnhuong = em.createNativeQuery(insertSQL)
                        .setParameter(1, gaMoi.getGaID())
                        .setParameter(2, gaMoi.getTenGa())
                        .setParameter(3, gaMoi.getTinhThanh())
                        .executeUpdate();
                System.out.println(hangAnhuong + " hàng đã được thêm vào thành công!");
                return hangAnhuong > 0;
            });
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int capNhatGa(String gaIDSua, Ga gaCapNhat) {
        try {
            return doInTransaction(em -> {
                String updateSQL = "UPDATE Ga SET tenGa = ?1, tinhThanh = ?2 WHERE gaID = ?3";
                int hangAnhHuong = em.createNativeQuery(updateSQL)
                        .setParameter(1, gaCapNhat.getTenGa())
                        .setParameter(2, gaCapNhat.getTinhThanh())
                        .setParameter(3, gaIDSua)
                        .executeUpdate();
                System.out.println(hangAnhHuong + " hàng đã được cập nhật thành công!");
                return hangAnhHuong;
            });
        } catch (Exception e) {
            System.out.println("Dữ liệu bị trùng, vui lòng kiểm tra lại!");
            e.printStackTrace();
            return 0;
        }
    }
}
