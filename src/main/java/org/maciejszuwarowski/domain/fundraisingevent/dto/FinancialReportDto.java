package org.maciejszuwarowski.domain.fundraisingevent.dto;

import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;

public record FinancialReportDto(String fundraisingEventName, BigDecimal amount, Currency currency) {
}
