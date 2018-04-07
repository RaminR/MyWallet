package ru.ya.rrmstu.mywallet.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLiteConnection {

    private static Connection con;
    private static String urlConnection;
    private static String driverClassName;

    //TODO реализовать передачу датасорса, если ядро например будет использоваться для веб приложения

    public static void init(String driverName, String url) {
        urlConnection = url;
        driverClassName = driverName;
        createConnection();
    }

    private static void createConnection() {
        try {
            Class.forName(driverClassName).newInstance();

            if (con == null) {
                con = DriverManager.getConnection(urlConnection);
                con.createStatement().execute("PRAGMA foreign_keys = ON");
                con.createStatement().execute("PRAGMA encoding = \"UTF-8\"");
            }

        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(SQLiteConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                createConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
}