package ru.ya.rrmstu.mywallet.core.dao.interfaces;

import java.util.List;

import ru.ya.rrmstu.mywallet.core.enums.OperationType;
import ru.ya.rrmstu.mywallet.core.interfaces.Operation;

public interface OperationDAO extends CommonDAO<Operation> {

    List<Operation> getList(OperationType operationType);// получить список операций определенного типа

}
