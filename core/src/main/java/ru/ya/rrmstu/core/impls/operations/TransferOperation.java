package ru.ya.rrmstu.core.impls.operations;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import ru.ya.rrmstu.core.abstracts.AbstractOperation;
import ru.ya.rrmstu.core.interfaces.Storage;

public class TransferOperation extends AbstractOperation {

    public TransferOperation(long id, Calendar dateTime, String addInfo, Storage fromStorage, Storage toStorage, BigDecimal amount, Currency currency) {
        super(id, dateTime, addInfo);
        this.fromStorage = fromStorage;
        this.toStorage = toStorage;
        this.amount = amount;
        this.currency = currency;
    }

    public TransferOperation(long id, Storage fromStorage, Storage toStorage, BigDecimal amount, Currency currency) {
        super(id);
        this.fromStorage = fromStorage;
        this.toStorage = toStorage;
        this.amount = amount;
        this.currency = currency;
    }

    public TransferOperation(Storage fromStorage, Storage toStorage, BigDecimal amount, Currency currency) {
        this.fromStorage = fromStorage;
        this.toStorage = toStorage;
        this.amount = amount;
        this.currency = currency;
    }

    private Storage fromStorage;
    private Storage toStorage;
    private BigDecimal amount;
    private Currency currency;

    public Storage getFromStorage() {
        return fromStorage;
    }

    public void setFromStorage(Storage fromStorage) {
        this.fromStorage = fromStorage;
    }

    public Storage getToStorage() {
        return toStorage;
    }

    public void setToStorage(Storage toStorage) {
        this.toStorage = toStorage;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
}
