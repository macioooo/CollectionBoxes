package org.maciejszuwarowski.domain.fundraisingevent.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.maciejszuwarowski.domain.shared.Currency;

import java.math.BigDecimal;

public record FinancialReportDto(String fundraisingEventName,
                                 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#,##0.00")
                                 BigDecimal amount,
                                 Currency currency) {
}
