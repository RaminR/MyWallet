package ru.ya.rrmstu.mywallet.app;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DbConnection.initConnection(this);
    }

}