package ru.ya.rrmstu.core;

import java.math.BigDecimal;
import java.util.Currency;

import ru.ya.rrmstu.core.exceptions.CurrencyException;
import ru.ya.rrmstu.core.impls.DefaultStorage;

public class Main {

    public static void main(String[] args) {
        try {
            DefaultStorage defaultStorage = new DefaultStorage();

            Currency USD = Currency.getInstance("USD");
            Currency RUB = Currency.getInstance("RUB");

            defaultStorage.addCurrency(RUB);
            defaultStorage.addAmount(new BigDecimal(2000), RUB);

            System.out.println(defaultStorage.getAmount(RUB));

        } catch (CurrencyException e) {
            e.printStackTrace();
        }
    }
}
