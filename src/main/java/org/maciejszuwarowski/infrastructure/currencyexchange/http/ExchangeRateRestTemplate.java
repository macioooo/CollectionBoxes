package org.maciejszuwarowski.infrastructure.currencyexchange.http;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.currencyexchange.ExchangeRateFetchable;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateTable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

@AllArgsConstructor
public class ExchangeRateRestTemplate implements ExchangeRateFetchable {
    private final static String SERVICE = "/api/exchangerates/tables/A";
    private final RestTemplate restTemplate;
    private final String uri;

    @Override
    public ExchangeRateTable fetchCurrencyExchangeRateTable() {
        HttpHeaders headers = new HttpHeaders();
        final HttpEntity<HttpHeaders> requestEntity = new HttpEntity<>(headers);
        try {
            final ResponseEntity<ExchangeRateTable[]> response = makeGetRequest(requestEntity);
            ExchangeRateTable[] table = response.getBody();
            System.out.println(table[0]);
            return table[0];

        } catch (ResourceAccessException e) {
            return new ExchangeRateTable(null, null, null, Collections.emptyList());
        }

    }

    private ResponseEntity<ExchangeRateTable[]> makeGetRequest(HttpEntity<HttpHeaders> requestEntity) {
        String url = UriComponentsBuilder.fromHttpUrl(getUrlForService(SERVICE)).toUriString();
        ResponseEntity<ExchangeRateTable[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                ExchangeRateTable[].class);
        return response;
    }

    private String getUrlForService(String service) {
        return uri + service;
    }
}
