package ru.ya.rrmstu.mywallet.core.dao.interfaces;

import java.sql.SQLException;
import java.util.List;

// описывает общие действия с БД для всех объектов
public interface CommonDAO<T> {

    List<T> getAll();
    T get(long id);
    boolean update(T object) throws SQLException;// boolean - чтобы удостовериться, что операция прошла успешно
    boolean delete(T object) throws SQLException;
    boolean add(T object) throws SQLException;

}
