package ru.ya.rrmstu.mywallet.database;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import ru.ya.rrmstu.core.database.Initializer;

public class DbConnection {

    private static final String TAG = Context.class.getName();

    private static final String DB_NAME = "wallet.db";
    private static final String DRIVER_CLASS = "org.sqldroid.SQLDroidDriver";

    private static String dbFolder;
    private static String dbPath;

    public static void initConnection(Context context) {
        checkDbExist(context);
        Initializer.load(DRIVER_CLASS, "jdbc:sqldroid:" + dbPath);
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