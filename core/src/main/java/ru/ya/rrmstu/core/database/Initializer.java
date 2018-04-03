package ru.ya.rrmstu.core.database;

import ru.ya.rrmstu.core.dao.impls.OperationDAOImpl;
import ru.ya.rrmstu.core.dao.impls.SourceDAOImpl;
import ru.ya.rrmstu.core.dao.impls.StorageDAOImpl;
import ru.ya.rrmstu.core.decorator.OperationSync;
import ru.ya.rrmstu.core.decorator.SourceSync;
import ru.ya.rrmstu.core.decorator.StorageSync;

public class Initializer {

    private static OperationSync operationSync;
    private static SourceSync sourceSync;
    private static StorageSync storageSync;


    public static OperationSync getOperationSync() {
        return operationSync;
    }


    public static SourceSync getSourceSync() {
        return sourceSync;
    }


    public static StorageSync getStorageSync() {
        return storageSync;
    }


    public static void load(String driverName, String url) {

        SQLiteConnection.init(driverName, url);

        // последовательность создания объектов - важна (сначала справочные значения, потом операции)
        sourceSync = new SourceSync(new SourceDAOImpl());
        storageSync = new StorageSync(new StorageDAOImpl());
        operationSync = new OperationSync(new OperationDAOImpl(sourceSync.getIdentityMap(), storageSync.getIdentityMap()), sourceSync, storageSync);
    }

}
