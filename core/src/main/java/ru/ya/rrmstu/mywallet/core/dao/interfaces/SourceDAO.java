package ru.ya.rrmstu.mywallet.core.dao.interfaces;

import java.util.List;

import ru.ya.rrmstu.mywallet.core.enums.OperationType;
import ru.ya.rrmstu.mywallet.core.interfaces.Source;

public interface SourceDAO extends CommonDAO<Source> {

    List<Source> getList(OperationType operationType);// получить список корневых элементов деревьев для определенного типа операции

}
