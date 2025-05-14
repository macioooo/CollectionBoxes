package org.maciejszuwarowski.domain.currencyexchange;

import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateTable;
import org.maciejszuwarowski.domain.currencyexchange.dto.Rate;

import java.math.BigDecimal;
import java.util.List;

import static org.maciejszuwarowski.domain.shared.Currency.*;

public class ExchangeRateFetcherTestImpl implements ExchangeRateFetchable {

    private List<Rate> rates;


    public ExchangeRateFetcherTestImpl() {
        this.rates = List.of(new Rate(null, USD.name(), new BigDecimal("3.86")),
                new Rate(null, PLN.name(), new BigDecimal("1.00")),
                new Rate(null, EUR.name(), new BigDecimal("4.32"))
        );
    }

    @Override
    public ExchangeRateTable fetchCurrencyExchangeRateTable() {


        return new ExchangeRateTable(null, null, null, this.rates);
    }
}
