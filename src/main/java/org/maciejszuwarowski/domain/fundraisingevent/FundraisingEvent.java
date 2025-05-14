package org.maciejszuwarowski.domain.fundraisingevent;

import lombok.Builder;
import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;
@Builder
record FundraisingEvent(String id, String nameOfFundraisingEvent, String collectionBoxId, Currency currencyOfTheMoneyAccount, BigDecimal amountOfMoney) {
}
