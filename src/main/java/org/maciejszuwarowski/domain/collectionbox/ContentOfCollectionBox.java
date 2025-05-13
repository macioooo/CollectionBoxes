package org.maciejszuwarowski.domain.collectionbox;


import org.maciejszuwarowski.domain.collectionbox.exceptions.AmountOfMoneyLessThanZeroException;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;

class ContentOfCollectionBox {

    private final Map<Currency, BigDecimal> contents;

    ContentOfCollectionBox() {
        this.contents = new EnumMap<>(Currency.class);
        for (Currency currency : Currency.values()) {
            this.contents.put(currency, BigDecimal.ZERO);
        }
    }

    BigDecimal getAmount(Currency currency) {
        if (currency == null) {
            throw new IllegalStateException("Currency cannot be null");
        }
        return this.contents.get(currency);
    }

    void addMoney(Currency currency, BigDecimal amountOfMoney) {
        if (amountOfMoney.compareTo(BigDecimal.ZERO) < 0) {
            throw new AmountOfMoneyLessThanZeroException("You can't add negative numbers to Collection box");
        }
        this.contents.put(currency, this.contents.get(currency).add(amountOfMoney));
    }


    Map<Currency, BigDecimal> getContents() {
        return Map.copyOf(this.contents);
    }

    void clearAll() {
        for (Currency currency : Currency.values()) {
            this.contents.put(currency, BigDecimal.ZERO);
        }
    }
}
