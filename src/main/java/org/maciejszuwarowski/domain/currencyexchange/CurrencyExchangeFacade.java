package org.maciejszuwarowski.domain.currencyexchange;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateDto;
import org.maciejszuwarowski.domain.shared.Currency;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class CurrencyExchangeFacade {
    private final CurrencyExchangeService currencyExchangeService;

    public ExchangeRateDto getCurrencyRatesForUsdPlnAndEur(Currency baseCurrency) {
        return currencyExchangeService.getEuroUsdAndPlnExchangeRates(baseCurrency);
    }
}
