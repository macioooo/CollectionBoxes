package org.maciejszuwarowski.domain.currencyexchange;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateDto;
import org.maciejszuwarowski.domain.shared.Currency;

@AllArgsConstructor
public class CurrencyExchangeFacade {
    private final CurrencyExchangeService currencyExchangeService;

    public ExchangeRateDto getCurrencyRatesForUsdPlnAndEur(Currency baseCurrency) {
        return currencyExchangeService.getEuroUsdAndPlnExchangeRates(baseCurrency);
    }
}
