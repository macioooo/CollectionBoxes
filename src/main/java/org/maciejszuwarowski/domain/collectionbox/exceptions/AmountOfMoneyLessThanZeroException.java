package org.maciejszuwarowski.domain.collectionbox.exceptions;

public class AmountOfMoneyLessThanZeroException extends IllegalArgumentException{
    public AmountOfMoneyLessThanZeroException(String message) {
        super(message);
    }
}
