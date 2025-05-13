package org.maciejszuwarowski.domain.collectionbox.dto;

import lombok.Builder;

import java.math.BigDecimal;
import org.maciejszuwarowski.domain.collectionbox.Currency;
@Builder
public record TransferResultDto(String message, boolean success, BigDecimal amountTransfered, Currency targetCurrency) {
}
