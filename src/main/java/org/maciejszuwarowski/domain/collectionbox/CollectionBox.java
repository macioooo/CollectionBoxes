package org.maciejszuwarowski.domain.collectionbox;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.maciejszuwarowski.domain.collectionbox.exceptions.CollectionBoxCannotBeAssigned;
import org.maciejszuwarowski.domain.collectionbox.exceptions.MoneyTransferException;
import org.maciejszuwarowski.domain.fundraisingevent.FundraisingEvent;
import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "collection_boxes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectionBox {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_fundraising_event_id")
    private FundraisingEvent assignedFundraisingEvent;
    @Embedded
    private ContentOfCollectionBox contents;

    public CollectionBox(String id, FundraisingEvent assignedFundraisingEvent) {
        this.id = id;
        this.contents = new ContentOfCollectionBox();
        this.assignedFundraisingEvent = assignedFundraisingEvent;
    }

    public CollectionBox(String id) {
        this.id = id;
        this.contents = new ContentOfCollectionBox();
        this.assignedFundraisingEvent = null;
    }


    public String getAssignedFundraisingEventIdAsString() {
        return this.assignedFundraisingEvent != null ? this.assignedFundraisingEvent.getId() : null;
    }

    public void assignToFundraisingEvent(FundraisingEvent eventToAssign) {
        Objects.requireNonNull(eventToAssign, "Fundraising event to assign cannot be null.");
        if (this.isAssigned()) {
            throw new CollectionBoxCannotBeAssigned("Box " + getId() + " is already assigned to event " + getAssignedFundraisingEventIdAsString());
        }
        if (!this.isEmpty()) {
            throw new CollectionBoxCannotBeAssigned("Box " + getId() + " is not empty and cannot be assigned. Please empty it first.");
        }
        this.assignedFundraisingEvent = eventToAssign;
    }

    public boolean isEmpty() {
        if (this.contents == null) {
            return true;
        }
        Map<Currency, BigDecimal> currentContentsMap = this.contents.getContents();
        return currentContentsMap.values().stream()
                .allMatch(amount -> amount.compareTo(BigDecimal.ZERO) == 0);
    }

    public boolean isAssigned() {
        return this.assignedFundraisingEvent != null;
    }

    public Map<Currency, BigDecimal> getContentDetails() {
        return this.contents != null ? this.contents.getContents() : Collections.emptyMap();
    }

    public BigDecimal getBalance(Currency currency) {
        return this.contents != null ? this.contents.getAmount(currency) : BigDecimal.ZERO;
    }

    public void addFunds(Currency currency, BigDecimal amount) {
        if (currency == null) {
            throw new MoneyTransferException("Currency cannot be null");
        }
        if (amount == null) {
            throw new MoneyTransferException("Amount cannot be null");
        }
        this.contents.addMoney(currency, amount);
    }

    public Map<Currency, BigDecimal> drainContents() {
        Map<Currency, BigDecimal> drainedMoney = this.contents.getContents();
        if (this.contents != null) {
            this.contents.clearAll();
        }
        return drainedMoney;
    }

    public void emptyContentPermanently() {

        if (this.contents != null) {
            this.contents.clearAll();
        }

    }
}
