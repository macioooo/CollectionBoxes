package org.maciejszuwarowski.domain.collectionbox.dto;

import java.math.BigDecimal;
import org.maciejszuwarowski.domain.collectionbox.Currency;

public record MoneyDto(
        Currency currency,
        BigDecimal amount

) {
}
