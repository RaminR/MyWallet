package ru.ya.rrmstu.core;

import ru.ya.rrmstu.core.dao.impls.SourceDAOImpl;
import ru.ya.rrmstu.core.decorator.SourceSync;

public class Main {

    public static void main(String[] args) {
        SourceSync sourceSync = new SourceSync(new SourceDAOImpl());
        sourceSync.getAll();
    }
}
