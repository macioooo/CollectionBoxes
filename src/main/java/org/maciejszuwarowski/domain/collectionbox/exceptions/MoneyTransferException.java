package org.maciejszuwarowski.domain.collectionbox.exceptions;

public class MoneyTransferException extends IllegalArgumentException{
    public MoneyTransferException(String message) {
        super(message);
    }
}
