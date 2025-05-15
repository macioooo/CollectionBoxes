package org.maciejszuwarowski.infrastructure.currencyexchange.http;

import org.maciejszuwarowski.domain.currencyexchange.ExchangeRateFetchable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
@Configuration
public class CurrencyExchangeClientConfig {
    @Bean
    public RestTemplateResponseErrorHandler restTemplateResponseErrorHandler() {
        return new RestTemplateResponseErrorHandler();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateResponseErrorHandler restTemplateResponseErrorHandle) {
        return new RestTemplateBuilder()
                .errorHandler(restTemplateResponseErrorHandle)
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(5000))
                .build();
    }

    @Bean
    public ExchangeRateFetchable remoteNumberGeneratorClient(RestTemplate restTemplate) {
        return new ExchangeRateRestTemplate(restTemplate, "https://api.nbp.pl");
    }
}
