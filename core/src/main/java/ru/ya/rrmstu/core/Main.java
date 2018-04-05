package ru.ya.rrmstu.core;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Currency;

import ru.ya.rrmstu.core.dao.impls.OperationDAOImpl;
import ru.ya.rrmstu.core.dao.impls.SourceDAOImpl;
import ru.ya.rrmstu.core.dao.impls.StorageDAOImpl;
import ru.ya.rrmstu.core.decorator.OperationSync;
import ru.ya.rrmstu.core.decorator.SourceSync;
import ru.ya.rrmstu.core.decorator.StorageSync;
import ru.ya.rrmstu.core.enums.OperationType;
import ru.ya.rrmstu.core.exceptions.CurrencyException;
import ru.ya.rrmstu.core.impls.DefaultSource;
import ru.ya.rrmstu.core.impls.DefaultStorage;
import ru.ya.rrmstu.core.impls.operations.ConvertOperation;
import ru.ya.rrmstu.core.impls.operations.IncomeOperation;
import ru.ya.rrmstu.core.impls.operations.OutcomeOperation;
import ru.ya.rrmstu.core.impls.operations.TransferOperation;
import ru.ya.rrmstu.core.interfaces.Source;
import ru.ya.rrmstu.core.interfaces.Storage;

public class Main {

    private static StorageSync storageSync = new StorageSync(new StorageDAOImpl());
    private static SourceSync sourceSync = new SourceSync(new SourceDAOImpl());
    private static OperationSync operationSync = new OperationSync(new OperationDAOImpl(sourceSync.getIdentityMap(), storageSync.getIdentityMap()), sourceSync, storageSync);


    public static void main(String[] args) {
        sourceSync.getAll();
    }

    private static void testOutcome() throws CurrencyException, SQLException {
        OutcomeOperation operation = new OutcomeOperation();
        operation.setFromCurrency(storageSync.get(9).getCurrency("RUB"));
        operation.setFromAmount(new BigDecimal(900));
        operation.setFromStorage(storageSync.get(9));
        operation.setToSource(sourceSync.get(13));
        operation.setDateTime(Calendar.getInstance());
        operation.setDescription("test desc");

        operationSync.add(operation);
    }

    private static void testIncome() throws CurrencyException, SQLException {
        IncomeOperation operation = new IncomeOperation();
        operation.setFromCurrency(storageSync.get(9).getCurrency("RUB"));
        operation.setFromAmount(new BigDecimal(10));
        operation.setToStorage(storageSync.get(9));
        operation.setFromSource(sourceSync.get(10));
        operation.setDateTime(Calendar.getInstance());
        operation.setDescription("test desc income");

        operationSync.add(operation);

    }

    private static void testTransfer() throws CurrencyException, SQLException {
        TransferOperation operation = new TransferOperation();
        operation.setFromCurrency(storageSync.get(9).getCurrency("RUB"));
        operation.setFromAmount(new BigDecimal(3));
        operation.setFromStorage(storageSync.get(9));
        operation.setToStorage(storageSync.get(10));
        operation.setDateTime(Calendar.getInstance());
        operation.setDescription("test desc transfer");

        operationSync.add(operation);

    }

    private static void testConvert() throws CurrencyException, SQLException {
        Storage s1 = storageSync.get(9);
        Storage s2 = storageSync.get(10);

        ConvertOperation operation = new ConvertOperation();

        operation.setFromCurrency(s1.getCurrency("RUB"));
        operation.setFromAmount(new BigDecimal(800));
        operation.setFromStorage(s1);

        operation.setToCurrency(s2.getCurrency("USD"));
        operation.setToAmount(new BigDecimal(200));
        operation.setToStorage(s2);


        operation.setDateTime(Calendar.getInstance());
        operation.setDescription("test desc transfer");

        operationSync.add(operation);


    }

    private static void testStorage() throws SQLException {
        Storage parentStorage = storageSync.get(10);


        DefaultStorage storage = new DefaultStorage("def store");


        try {
            storage.addCurrency(Currency.getInstance("USD"), new BigDecimal(145));
            storage.addCurrency(Currency.getInstance("RUB"), new BigDecimal(100));

            storage.setParent(parentStorage);

            storageSync.add(storage);


            //storageSync.deleteCurrency(storage, Currency.getInstance("USD"));


            storage.setName("test 2");

            storageSync.update(storage);


        } catch (CurrencyException e) {
            e.printStackTrace();
        }
    }

    private static DefaultSource testSource() throws SQLException {
        Source parentSource = sourceSync.get(4);

        DefaultSource s = new DefaultSource("test source 2");
        s.setOperationType(OperationType.OUTCOME);
        s.setParent(parentSource);

        sourceSync.add(s);
        System.out.println("sourceSync = " + sourceSync.getAll());
        return s;
    }

}
