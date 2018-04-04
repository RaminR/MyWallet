package ru.ya.rrmstu.mywallet.utils;

import android.app.Application;

import ru.ya.rrmstu.mywallet.database.DbConnection;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DbConnection.initConnection(this);
    }

}