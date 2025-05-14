package org.maciejszuwarowski.domain.currencyexchange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateDto;
import org.maciejszuwarowski.domain.currencyexchange.exceptions.CurrencyNotAvailableException;
import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.maciejszuwarowski.domain.shared.Currency.*;

public class CurrencyExchangeFacadeTest {
    private CurrencyExchangeFacade currencyExchangeFacade;
    private CurrencyExchangeService currencyExchangeService;
    private ExchangeRateFetchable exchangeRateFetcher;

    @BeforeEach
    void setUp() {
        exchangeRateFetcher = new ExchangeRateFetcherTestImpl();
        currencyExchangeService = new CurrencyExchangeService(exchangeRateFetcher);
        currencyExchangeFacade = new CurrencyExchangeFacade(currencyExchangeService);

    }

    @Nested
    class ExchangingCurrencyTests {

        @Test
        void shouldReturnDollarExchangeRateForUsdPlnAndEur() {
            //given
            Currency usdCurrency = USD;
            BigDecimal expectedUsdToUsdRate = BigDecimal.ONE;
            BigDecimal expectedPlnToUsd = new BigDecimal("0.259067");
            BigDecimal expectedEurToUsd = new BigDecimal("1.119171");
            //when
            ExchangeRateDto exchangeRateForUsdCurr = currencyExchangeFacade.getCurrencyRatesForUsdPlnAndEur(usdCurrency);
            BigDecimal usdToUsd = exchangeRateForUsdCurr.exchangeRate().get(USD);
            BigDecimal plnToUsd = exchangeRateForUsdCurr.exchangeRate().get(PLN);
            BigDecimal eurToUsd = exchangeRateForUsdCurr.exchangeRate().get(EUR);
            //then
            assertEquals(expectedUsdToUsdRate, usdToUsd);
            assertEquals(expectedPlnToUsd, plnToUsd);
            assertEquals(expectedEurToUsd, eurToUsd);
        }

        @Test
        void shouldReturnZlotyExchangeRateForUsdPlnAndEur() {
            //given
            Currency plnCurrency = PLN;
            BigDecimal expectedPlnToPln = BigDecimal.ONE;
            BigDecimal expectedUsdToPln = new BigDecimal("3.860000");
            BigDecimal expectedEurToPln = new BigDecimal("4.320000");
            //when
            ExchangeRateDto exchangeRateForUsdCurr = currencyExchangeFacade.getCurrencyRatesForUsdPlnAndEur(plnCurrency);
            BigDecimal usdToPln = exchangeRateForUsdCurr.exchangeRate().get(USD);
            BigDecimal plnToPln = exchangeRateForUsdCurr.exchangeRate().get(PLN);
            BigDecimal plnToEuro = exchangeRateForUsdCurr.exchangeRate().get(EUR);
            //then
            assertEquals(expectedPlnToPln, plnToPln);
            assertEquals(expectedUsdToPln, usdToPln);
            assertEquals(expectedEurToPln, plnToEuro);
        }

        @Test
        void shouldReturnEuroExchangeRateForUsdPlnAndEur() {
            Currency eurCurrency = EUR;
            BigDecimal expectedEurToEur = BigDecimal.ONE;
            BigDecimal expectedUsdToEur = new BigDecimal("0.893519");
            BigDecimal expectedPlnToEur = new BigDecimal("0.231481");

            // when
            ExchangeRateDto exchangeRateForEurCurr = currencyExchangeFacade.getCurrencyRatesForUsdPlnAndEur(eurCurrency);
            BigDecimal eurToEur = exchangeRateForEurCurr.exchangeRate().get(EUR);
            BigDecimal usdToEur = exchangeRateForEurCurr.exchangeRate().get(USD);
            BigDecimal plnToEur = exchangeRateForEurCurr.exchangeRate().get(PLN);

            // then
            assertEquals(expectedEurToEur, eurToEur);
            assertEquals(expectedUsdToEur, usdToEur);
            assertEquals(expectedPlnToEur, plnToEur);
        }
    }

    @Nested
    class ExceptionHandlingTests {

        @Test
        void shouldThrowCurrencyNotAvailableExceptionWhenBaseCurrencyIsNull() {
            //given
            Currency nullBaseCurrency = null;

            //when
            CurrencyNotAvailableException exception = assertThrows(CurrencyNotAvailableException.class, () -> {
                currencyExchangeFacade.getCurrencyRatesForUsdPlnAndEur(nullBaseCurrency);
            });

            //then
            assertEquals("Base currency cannot be null", exception.getMessage());
        }
    }
}
