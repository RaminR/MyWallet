package ru.ya.rrmstu.core.interfaces;

import java.util.Calendar;

import ru.ya.rrmstu.core.enums.OperationType;

public interface Operation extends Comparable<Operation> {

    long getId();

    void setId(long id);

    OperationType getOperationType();

    Calendar getDateTime();

    String getDescription();
}
