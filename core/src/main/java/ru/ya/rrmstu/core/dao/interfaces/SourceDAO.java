package ru.ya.rrmstu.core.dao.interfaces;

import java.util.List;

import ru.ya.rrmstu.core.enums.OperationType;
import ru.ya.rrmstu.core.interfaces.Source;

public interface SourceDAO extends CommonDAO<Source> {
    /**
     * Получить список корневыъ элементов деревьев для определенного типа операции
     *
     * @param operationType
     * @return
     */
    List<Source> getList(OperationType operationType);

}
