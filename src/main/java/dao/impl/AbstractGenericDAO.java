/**
 * File: null.java
 * Created by: Nguyen Thi Huynh Nhu
 * Date: 4/8/2026
 */

package dao.impl;

import dao.IGenericDAO;
import db.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
public abstract class AbstractGenericDAO<T, ID extends Serializable> implements IGenericDAO<T, ID> {
    protected final Class<T> entityClass;

    //Lam gi do tren EntityManager va tra ve R
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
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    @Override
    public T create(T entity) {
        return doInTransaction(em -> {
            em.merge(entity);
            return entity;
        });
    }

    @Override
    public T update(T entity) {
        return doInTransaction(em -> em.merge(entity));
    }

    @Override
    public boolean delete(ID id) {
        return doInTransaction(em -> {
            T entity = em.find(entityClass, id);
            em.remove(entity);
            return true;
        });
    }

    @Override
    public T findById(ID id) {
        return doInTransaction(em -> em.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        String query = "select e from " + entityClass.getSimpleName() + " e";
        return doInTransaction(em -> em.createQuery(query, entityClass).getResultList());
    }
}