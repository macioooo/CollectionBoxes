package org.maciejszuwarowski.domain.fundraisingevent;

import jakarta.persistence.*;
import lombok.Builder;
import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;

@Builder
@Table(name = "fundraising_events")
@Entity
public record FundraisingEvent(
        @Id
        String id,
        @Column(nullable = false)
        String nameOfFundraisingEvent,
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        Currency currencyOfTheMoneyAccount,
        @Column(nullable = false, precision = 19, scale = 4)
        BigDecimal amountOfMoney) {
}
