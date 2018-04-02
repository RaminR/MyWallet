package ru.ya.rrmstu.core;

import java.sql.ResultSet;
import java.sql.Statement;

import ru.ya.rrmstu.core.database.SQLiteConnection;
import ru.ya.rrmstu.core.exceptions.CurrencyException;
import ru.ya.rrmstu.core.impls.DefaultStorage;

public class Main {

    public static void main(String[] args) {
        try (Statement stmt = SQLiteConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery("select * from depository")){

            while (rs.next()){
                System.out.println(rs.getString("name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
