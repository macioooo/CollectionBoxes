package org.maciejszuwarowski.domain.fundraisingevent.dto;

import org.maciejszuwarowski.domain.shared.Currency;

public record CreateFundraisingEventDto(String message, String idOfTheFundraisingEvent ,String nameOfTheFundraisingEvent, Currency currencyOfTheMoneyAccount) {
}
