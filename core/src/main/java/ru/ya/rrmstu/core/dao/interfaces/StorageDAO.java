package ru.ya.rrmstu.core.dao.interfaces;

import java.math.BigDecimal;
import java.util.Currency;

import ru.ya.rrmstu.core.exceptions.CurrencyException;
import ru.ya.rrmstu.core.interfaces.Storage;

public interface StorageDAO extends CommonDAO<Storage> {

    boolean addCurrency(Storage storage, Currency currency) throws CurrencyException;

    boolean deleteCurrency(Storage storage, Currency currency) throws CurrencyException;

    boolean updateAmount(Storage storage, Currency currency, BigDecimal amount);


}
