package org.maciejszuwarowski.domain.currencyexchange;

import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateTable;

interface ExchangeRateFetchable {
    ExchangeRateTable fetchCurrencyExchangeRateTable();
}
