package org.maciejszuwarowski.domain.currencyexchange;

import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateTable;
import org.maciejszuwarowski.domain.currencyexchange.dto.Rate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
@Component
public class HardcodedExchangeRateFetcher implements ExchangeRateFetchable{
    @Override
    public ExchangeRateTable fetchCurrencyExchangeRateTable() {
        List<Rate> rates = Arrays.asList(
                new Rate("dolar amerykański", "USD", new BigDecimal("3.96")),
                new Rate("euro", "EUR", new BigDecimal("4.26"))
        );
        return new ExchangeRateTable("A", "example-table-001", "2025-05-14", rates);
    }
}
