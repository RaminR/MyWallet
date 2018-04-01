package ru.ya.rrmstu.core;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface Storage {

    void changeAmount(BigDecimal amount, Currency currency);

    void addAmount(BigDecimal amount, Currency currency);

    void expenseAmount(BigDecimal amount, Currency currency);

    void addCurrency(Currency currency);

    void deleteCurrency(Currency currency);

    Currency getCurrency(String code);

    List<Currency> getAvailableCurrencies();
}
