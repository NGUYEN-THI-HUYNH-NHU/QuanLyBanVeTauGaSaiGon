package dao.impl;

/*
 * @ (#) ChuyenGaDAO.java   1.0     09/12/2025
 *
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 09/12/2025
 */

import entity.ChuyenGa;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ChuyenGaDAO extends AbstractGenericDAO<ChuyenGa, ChuyenGa.ChuyenGaID> {

    public ChuyenGaDAO() {
        super(ChuyenGa.class);
    }

    public List<ChuyenGa> getChiTietHanhTrinh(String maChuyen) {
        return doInTransaction(em -> {
            String jpql = "SELECT cg FROM ChuyenGa cg " +
                    "JOIN FETCH cg.ga " +
                    "JOIN FETCH cg.chuyen c " +
                    "JOIN FETCH c.tau " +
                    "WHERE cg.chuyen.chuyenID = :maChuyen " +
                    "ORDER BY cg.thuTu ASC";

            TypedQuery<ChuyenGa> query = em.createQuery(jpql, ChuyenGa.class);
            query.setParameter("maChuyen", maChuyen);

            List<ChuyenGa> result = query.getResultList();


            for (ChuyenGa cg : result) {
                if (cg.getGa() != null) {
                    cg.getGa().getTenGa();
                }
            }
            return query.getResultList();
        });
    }
}
