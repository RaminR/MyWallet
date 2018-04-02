package ru.ya.rrmstu.core.dao.interfaces;

import java.math.BigDecimal;
import java.util.Currency;

import ru.ya.rrmstu.core.interfaces.Storage;

public interface StorageDAO extends CommonDAO<Storage> {

    boolean addCurrency(Storage storage, Currency currency);

    boolean deleteCurrency(Storage storage, Currency currency);

    boolean updateAmount(Storage storage, BigDecimal amount);

}
