package org.maciejszuwarowski.domain.currencyexchange.dto;

import java.math.BigDecimal;

public record Rate(String currency, String code, BigDecimal mid) {

}
