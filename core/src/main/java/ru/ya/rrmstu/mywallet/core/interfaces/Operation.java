package ru.ya.rrmstu.mywallet.core.interfaces;

import java.util.Calendar;

import ru.ya.rrmstu.mywallet.core.enums.OperationType;

public interface Operation extends Comparable<Operation> {

    long getId();

    void setId(long id);

    OperationType getOperationType();

    Calendar getDateTime();

    String getDescription();
}
