package ru.ya.rrmstu.mywallet.core.interfaces;


import ru.ya.rrmstu.mywallet.core.enums.OperationType;

public interface Source extends TreeNode {

    OperationType getOperationType();

    void setOperationType(OperationType type);

}
