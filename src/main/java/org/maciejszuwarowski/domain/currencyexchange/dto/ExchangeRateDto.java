package org.maciejszuwarowski.domain.currencyexchange.dto;

import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;
import java.util.Map;

public record ExchangeRateDto(Map<Currency, BigDecimal> exchangeRate) {
}
