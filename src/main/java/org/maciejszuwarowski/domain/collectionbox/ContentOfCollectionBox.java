package org.maciejszuwarowski.domain.collectionbox;


import jakarta.persistence.*;
import org.maciejszuwarowski.domain.collectionbox.exceptions.AmountOfMoneyLessThanZeroException;
import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Embeddable
class ContentOfCollectionBox {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "collection_box_entries", joinColumns =  @JoinColumn(name = "collection_box_id"))
    @MapKeyColumn(name = "currency_code")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    private Map<Currency, BigDecimal> contents;


    public ContentOfCollectionBox() {
        this.contents = new EnumMap<>(Currency.class);
        for (Currency currency : Currency.values()) {
            this.contents.put(currency, BigDecimal.ZERO);
        }
    }

    BigDecimal getAmount(Currency currency) {
        if (currency == null) {
            throw new IllegalStateException("Currency cannot be null");
        }
        return this.contents.getOrDefault(currency, BigDecimal.ZERO);
    }

    void addMoney(Currency currency, BigDecimal amountOfMoney) {
        Objects.requireNonNull(currency, "Currency cannot be null when adding money.");
        Objects.requireNonNull(amountOfMoney, "Amount of money cannot be null when adding money.");
        if (amountOfMoney.compareTo(BigDecimal.ZERO) < 0) {
            throw new AmountOfMoneyLessThanZeroException("You can't add negative numbers to Collection box");
        }
        BigDecimal currentAmount = this.contents.getOrDefault(currency, BigDecimal.ZERO);
        this.contents.put(currency, currentAmount.add(amountOfMoney));
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
