package ru.ya.rrmstu.core;

import java.sql.ResultSet;
import java.sql.Statement;

import ru.ya.rrmstu.core.dao.impls.StorageDAOImpl;
import ru.ya.rrmstu.core.database.SQLiteConnection;

public class Main {

    public static void main(String[] args) {
        new StorageDAOImpl().getAll();
    }
}
