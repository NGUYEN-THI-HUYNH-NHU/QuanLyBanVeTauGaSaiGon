package dao;

import java.io.Serializable;
import java.util.List;

public interface IGenericDAO<T, ID extends Serializable> {
    T create(T entity);

    T update(T entity);

    boolean delete(ID id);

    T findById(ID id);

    List<T> findAll();
}
