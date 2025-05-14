package org.maciejszuwarowski.domain.currencyexchange.dto;


import java.util.List;

public record ExchangeRateTable(String table, String no, String effectiveDate, List<Rate> rates) {
}
