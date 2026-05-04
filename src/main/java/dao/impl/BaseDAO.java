package dao.impl;

import db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.function.Function;

public class BaseDAO {
    public <R> R doInTransaction(Function<EntityManager, R> function) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            R result = function.apply(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
}