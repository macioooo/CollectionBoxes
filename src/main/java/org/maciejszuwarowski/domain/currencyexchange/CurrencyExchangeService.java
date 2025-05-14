package org.maciejszuwarowski.domain.currencyexchange;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateDto;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateTable;
import org.maciejszuwarowski.domain.currencyexchange.dto.Rate;
import org.maciejszuwarowski.domain.currencyexchange.exceptions.CurrencyNotAvailableException;
import org.maciejszuwarowski.domain.currencyexchange.exceptions.MissingExchangeRateException;
import org.maciejszuwarowski.domain.shared.Currency;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.maciejszuwarowski.domain.shared.Currency.*;

@AllArgsConstructor
@Service
public class CurrencyExchangeService {

    private static final Set<Currency> AVAILABLE_CURRENCIES = Set.of(EUR, PLN, USD);
    private static final int EXCHANGE_RATE_SCALE = 6;
    private final ExchangeRateFetchable exchangeRateFetcher;

    public ExchangeRateDto getEuroUsdAndPlnExchangeRates(Currency baseCurrency) {
        if (baseCurrency == null) {
            throw new CurrencyNotAvailableException("Base currency cannot be null");
        }

        if (!AVAILABLE_CURRENCIES.contains(baseCurrency)) {
            throw new CurrencyNotAvailableException("Base currency " + baseCurrency + " is not one of the directly available target currencies " + AVAILABLE_CURRENCIES +
                    "Ensure base currency is sensible for the context of getEuroUsdAndPlnExchangeRates.");
        }

        ExchangeRateTable table = exchangeRateFetcher.fetchCurrencyExchangeRateTable();

        Map<Currency, BigDecimal> mapOfExchangeRatesToPln = table.rates().stream()
                .filter(rate -> AVAILABLE_CURRENCIES.contains(Currency.fromCode(rate.code())))
                .collect(Collectors.toMap(rate -> Currency.fromCode(rate.code()), Rate::mid));

        mapOfExchangeRatesToPln.put(PLN, BigDecimal.ONE);

        for (Currency availableCurrency : AVAILABLE_CURRENCIES) {
            if (!mapOfExchangeRatesToPln.containsKey(availableCurrency)) {
                throw new MissingExchangeRateException("Missing PLN-based exchange rate for an available currency: " + availableCurrency);
            }
        }

        Map<Currency, BigDecimal> resultExchangeRates = new HashMap<>();

        for (Currency targetCurrency : AVAILABLE_CURRENCIES) {
            resultExchangeRates.put(targetCurrency, convert(targetCurrency, baseCurrency, mapOfExchangeRatesToPln));
        }

        return new ExchangeRateDto(resultExchangeRates);
    }

    private BigDecimal convert(Currency fromCurrency, Currency toBaseCurrency, Map<Currency, BigDecimal> ratesToPln) {
        if (fromCurrency.equals(toBaseCurrency)) {
            return BigDecimal.ONE;
        }

        BigDecimal fromRateToPln = ratesToPln.get(fromCurrency);
        BigDecimal toRateToPln = ratesToPln.get(toBaseCurrency);

        if (fromRateToPln == null) {
            throw new CurrencyNotAvailableException("Missing PLN exchange rate for 'from' currency: " + fromCurrency);
        }
        if (toRateToPln == null) {
            throw new CurrencyNotAvailableException("Missing PLN exchange rate for 'to' (base) currency: " + toBaseCurrency);
        }

        return fromRateToPln.divide(toRateToPln, EXCHANGE_RATE_SCALE, RoundingMode.HALF_DOWN);
    }

}

