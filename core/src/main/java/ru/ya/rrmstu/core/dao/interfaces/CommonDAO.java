package ru.ya.rrmstu.core.dao.interfaces;

import java.util.List;

/**
 * Данный класс описывает общие действия с БД для всех объектов
 */
public interface CommonDAO<T> {

    List<T> getAll();

    T get(long id);

    /**
     * boolean - чтобы удостовериться, что операция прошла успешно
     **/
    boolean update(T storage);

    boolean delete(T storage);
}
