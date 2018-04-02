package ru.ya.rrmstu.core.dao.interfaces;

import java.util.List;

// описывает общие действия с БД для всех объектов
public interface CommonDAO<T> {

    List<T> getAll();
    T get(long id);
    boolean update(T object);// boolean - чтобы удостовериться, что операция прошла успешно
    boolean delete(T object);
    boolean add(T object);

}
