package org.maciejszuwarowski.domain.fundraisingevent.dto;

import java.math.BigDecimal;

public record TransferResultDto(String message, String idOfCollectionBox, BigDecimal collectedMoney) {
}
