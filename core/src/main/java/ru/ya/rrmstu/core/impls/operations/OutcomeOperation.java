package ru.ya.rrmstu.core.impls.operations;


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import ru.ya.rrmstu.core.abstracts.AbstractOperation;
import ru.ya.rrmstu.core.interfaces.Source;
import ru.ya.rrmstu.core.interfaces.Storage;

// TODO для всех классов создать конструкторы без поля id, т.к. оно будет autoincrement
public class OutcomeOperation extends AbstractOperation {

    public OutcomeOperation(long id, Calendar dateTime, String addInfo, Storage fromStorage, Source toSource, BigDecimal amount, Currency currency) {
        super(id, dateTime, addInfo);
        this.fromStorage = fromStorage;
        this.toSource = toSource;
        this.amount = amount;
        this.currency = currency;
    }

    public OutcomeOperation(long id, Storage fromStorage, Source toSource, BigDecimal amount, Currency currency) {
        super(id);
        this.fromStorage = fromStorage;
        this.toSource = toSource;
        this.amount = amount;
        this.currency = currency;
    }

    public OutcomeOperation(Storage fromStorage, Source toSource, BigDecimal amount, Currency currency) {
        this.fromStorage = fromStorage;
        this.toSource = toSource;
        this.amount = amount;
        this.currency = currency;
    }

    private Storage fromStorage;
    private Source toSource;
    private BigDecimal amount;
    private Currency currency;


    public Storage getFromStorage() {
        return fromStorage;
    }

    public void setFromStorage(Storage fromStorage) {
        this.fromStorage = fromStorage;
    }

    public Source getToSource() {
        return toSource;
    }

    public void setToSource(Source toSource) {
        this.toSource = toSource;
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
