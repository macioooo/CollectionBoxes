package org.maciejszuwarowski.domain.currencyexchange;

import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateTable;

public interface ExchangeRateFetchable {
    ExchangeRateTable fetchCurrencyExchangeRateTable();
}
