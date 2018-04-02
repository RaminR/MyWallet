package ru.ya.rrmstu.core.dao.interfaces;

import java.util.List;

import ru.ya.rrmstu.core.enums.OperationType;
import ru.ya.rrmstu.core.interfaces.Source;

public interface SourceDAO extends CommonDAO<Source> {

    List<Source> getList(OperationType operationType);// получить список корневых элементов деревьев для определенного типа операции

}
