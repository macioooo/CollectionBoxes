package org.maciejszuwarowski.domain.currencyexchange.exceptions;

public class CurrencyNotAvailableException extends IllegalArgumentException{
    public CurrencyNotAvailableException(String message) {
        super(message);
    }
}
