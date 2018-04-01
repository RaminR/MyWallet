package ru.ya.rrmstu.core.interfaces;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import ru.ya.rrmstu.core.exceptions.AmountException;
import ru.ya.rrmstu.core.exceptions.CurrencyException;

public interface Storage {

    String getName();

    /**
     * Получение баланса (Остаток)
     * getCurrencyAmounts()     - Остаток по каждой доступной валюте в хранилище
     * getAmount()              - Остаток по определенной валюте
     * getApproxAmount()        - Примерный остаток в переводе всех денег в одну валюту
     */
    Map<Currency, BigDecimal> getCurrencyAmounts();

    BigDecimal getAmount(Currency currency) throws CurrencyException;

    BigDecimal getApproxAmount(Currency currency) throws CurrencyException;

    /**
     * Изменение баланса
     * changeAmount()           - Изменение баланса по определеной валюте
     * addAmount()              - Добавить сумму в валюте
     * expenseAmount()          - Отнять сумму в валюте
     */
    void changeAmount(BigDecimal amount, Currency currency) throws CurrencyException;

    void addAmount(BigDecimal amount, Currency currency) throws CurrencyException;

    void expenseAmount(BigDecimal amount, Currency currency) throws CurrencyException, AmountException;


    /**
     * Работа с валютой
     * addCurrency()            - Добавить новую валюту в хранилище
     * deleteCurrency()         - Удалить валюту из хранилища
     * getCurrency()            - Получить валюту по коду
     * getAvailableCurrencies() - Получить все доступные валюты хранилища в отдельной коллекции
     */
    Currency getCurrency(String code) throws CurrencyException;

    void addCurrency(Currency currency) throws CurrencyException;

    void deleteCurrency(Currency currency) throws CurrencyException;

    List<Currency> getAvailableCurrencies();
}
