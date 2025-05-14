package org.maciejszuwarowski.domain.collectionbox.dto;

import java.math.BigDecimal;
import org.maciejszuwarowski.domain.shared.Currency;

public record MoneyDto(
        Currency currency,
        BigDecimal amount

) {
}
