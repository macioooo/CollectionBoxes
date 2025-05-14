package org.maciejszuwarowski.domain.collectionbox.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Builder;
import org.maciejszuwarowski.domain.shared.Currency;
@Builder
public record EmptiedCollectionBoxDto(String fundraisingEventId, Map<Currency, BigDecimal> collectedAmount) {
}
