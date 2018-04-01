package ru.ya.rrmstu.core.interfaces;

import ru.ya.rrmstu.core.objects.OperationType;

public interface Source {
    String getName();

    long getId();

    OperationType getOperationType();
}
