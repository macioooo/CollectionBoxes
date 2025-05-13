package org.maciejszuwarowski.domain.collectionbox;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.maciejszuwarowski.domain.collectionbox.exceptions.CollectionBoxCannotBeAssigned;
import org.maciejszuwarowski.domain.collectionbox.exceptions.MoneyTransferException;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
class CollectionBox {
    private final String id;
    private String fundraisingEventId;
    private ContentOfCollectionBox contents;

    CollectionBox(String id, String fundraisingEventId) {
        this.id = id;
        this.fundraisingEventId = fundraisingEventId;
        this.contents = new ContentOfCollectionBox();
    }

    CollectionBox(String id) {
        this.id = id;
        this.contents = new ContentOfCollectionBox();
    }

    String getId() {
        return this.id;
    }

    String getFundraisingEventId() {
        return this.fundraisingEventId;
    }

    void setFundraisingEventId(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            throw new CollectionBoxCannotBeAssigned("Fundraising event ID cannot be null or empty.");
        }
        if (isAssigned()) {
            throw new CollectionBoxCannotBeAssigned("Box " + getId() + " is already assigned");
        }
        if (!isEmpty()) {
            throw new CollectionBoxCannotBeAssigned("Box " + getId() + " is not empty and cannot be assigned");
        }
        this.fundraisingEventId = id;
    }

    boolean isEmpty() {
        return !contents.getContents().values().stream()
                .anyMatch(amount -> amount.compareTo(BigDecimal.ZERO) > 0);
    }

    boolean isAssigned() {
        return fundraisingEventId != null && !fundraisingEventId.trim().isEmpty();
    }

    Map<Currency, BigDecimal> getContentDetails() {
        return this.contents.getContents();
    }

    BigDecimal getBalance(Currency currency) {
        return this.contents.getAmount(currency);
    }

    void addFunds(Currency currency, BigDecimal amount) {
        if (currency == null) {
            throw new MoneyTransferException("Currency cannot be null");
        }
        if (amount == null) {
            throw new MoneyTransferException("Amount cannot be null");
        }
        this.contents.addMoney(currency, amount);
    }

    Map<Currency, BigDecimal> drainContents() {
        Map<Currency, BigDecimal> drainedMoney = this.contents.getContents();
        this.contents.clearAll();
        return drainedMoney;
    }

    void emptyContentPermanently() {
        this.contents.clearAll();
    }
}
