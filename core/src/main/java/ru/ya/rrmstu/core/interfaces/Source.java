package ru.ya.rrmstu.core.interfaces;

import com.sun.xml.internal.ws.wsdl.writer.document.BindingOperationType;

public interface Source {

    String getName();

    long getId();

    BindingOperationType getOperationType();
}
