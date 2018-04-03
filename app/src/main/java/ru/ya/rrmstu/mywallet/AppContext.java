package ru.ya.rrmstu.mywallet;

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

    private static final String TAG = Context.class.getName();

    private static final String DB_NAME = "wallet.db";
    private static String dbFolder;
    private static String dbPath;

    @Override
    public void onCreate() {
        super.onCreate();

        checkDbExist(this);
    }


    /**
     * Если нет файла БД - скопировать его из папки assets
     *
     * @param context
     */
    private static void checkDbExist(Context context) {

        dbFolder = context.getApplicationInfo().dataDir + "/" + "database/";

        File databaseFolder = new File(dbFolder);
        if (!databaseFolder.exists()) {
            databaseFolder.mkdir();
        }

        dbPath = dbFolder + DB_NAME;

        if (!checkDataBaseExists()) {
            copyDataBase(context);
        }
    }

    private static void copyDataBase(Context context) {

        try (InputStream sourceFile = context.getAssets().open(DB_NAME); OutputStream destinationFolder = new FileOutputStream(dbPath)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = sourceFile.read(buffer)) > 0) {
                destinationFolder.write(buffer, 0, length);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private static boolean checkDataBaseExists() {
        File dbFile = new File(dbPath);
        return dbFile.exists();
    }
}