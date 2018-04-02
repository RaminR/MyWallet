package ru.ya.rrmstu.core;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Currency;

import ru.ya.rrmstu.core.dao.impls.SourceDAOImpl;
import ru.ya.rrmstu.core.dao.impls.StorageDAOImpl;
import ru.ya.rrmstu.core.database.SQLiteConnection;
import ru.ya.rrmstu.core.decorator.SourceSync;
import ru.ya.rrmstu.core.decorator.StorageSynchronizer;
import ru.ya.rrmstu.core.exceptions.CurrencyException;
import ru.ya.rrmstu.core.impls.DefaultStorage;

public class Main {

    public static void main(String[] args) {
        SourceSync sourceSync = new SourceSync(new SourceDAOImpl());
        sourceSync.getAll();
    }
}
