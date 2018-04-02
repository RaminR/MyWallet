package ru.ya.rrmstu.core.dao.interfaces;

import java.util.List;

import ru.ya.rrmstu.core.enums.OperationType;
import ru.ya.rrmstu.core.interfaces.Operation;

public interface OperationDAO extends CommonDAO<Operation> {

    List<Operation> getList(OperationType operationType);// получить список операций определенного типа

}
