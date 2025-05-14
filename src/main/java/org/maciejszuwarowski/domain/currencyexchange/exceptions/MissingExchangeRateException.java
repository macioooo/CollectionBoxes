package org.maciejszuwarowski.domain.currencyexchange.exceptions;

public class MissingExchangeRateException extends IllegalStateException{
    public MissingExchangeRateException(String message) {
        super(message);
    }
}
