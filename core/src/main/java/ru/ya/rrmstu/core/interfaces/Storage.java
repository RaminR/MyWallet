package ru.ya.rrmstu.core.interfaces;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import ru.ya.rrmstu.core.exceptions.AmountException;
import ru.ya.rrmstu.core.exceptions.CurrencyException;

public interface Storage extends TreeNode {

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
     * updateAmount()           - Изменение баланса по определеной валюте
     */
    void updateAmount(BigDecimal amount, Currency currency) throws CurrencyException, AmountException;

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
