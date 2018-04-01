package ru.ya.rrmstu.core.impls.operations;


import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Currency;

import ru.ya.rrmstu.core.abstracts.AbstractOperation;
import ru.ya.rrmstu.core.interfaces.Storage;

public class ConvertOperation extends AbstractOperation {

    public ConvertOperation(long id, Calendar dateTime, String addInfo, Storage fromStorage, Storage toStorage, Currency fromCurrency, Currency toCurrency, BigDecimal fromAmount, BigDecimal toAmount) {
        super(id, dateTime, addInfo);
        this.fromStorage = fromStorage;
        this.toStorage = toStorage;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    public ConvertOperation(long id, Storage fromStorage, Storage toStorage, Currency fromCurrency, Currency toCurrency, BigDecimal fromAmount, BigDecimal toAmount) {
        super(id);
        this.fromStorage = fromStorage;
        this.toStorage = toStorage;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    public ConvertOperation(Storage fromStorage, Storage toStorage, Currency fromCurrency, Currency toCurrency, BigDecimal fromAmount, BigDecimal toAmount) {
        this.fromStorage = fromStorage;
        this.toStorage = toStorage;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
    }

    private Storage fromStorage;
    private Storage toStorage;
    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal fromAmount;
    private BigDecimal toAmount;

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

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public BigDecimal getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(BigDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public BigDecimal getToAmount() {
        return toAmount;
    }

    public void setToAmount(BigDecimal toAmount) {
        this.toAmount = toAmount;
    }
}
