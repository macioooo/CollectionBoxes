package org.maciejszuwarowski.domain.fundraisingevent;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FinancialReportDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FundraisingEventMessageDto;
import org.maciejszuwarowski.domain.shared.Currency;

import java.util.List;

@AllArgsConstructor
public class FundraisingEventFacade {
    private final FundraisingEventService fundraisingEventService;

    public FundraisingEventMessageDto createFundraisingEvent(String nameOfTheFundraisingEvent, Currency currency) {
        return fundraisingEventService.createFundraisingEvent(nameOfTheFundraisingEvent, currency);
    }

    public List<FinancialReportDto> displayFinancialReport() {
        return fundraisingEventService.createFinancialReport();
    }

    public FundraisingEventMessageDto transferMoneyFromCollectionBox(String collectionBoxId) {
        return fundraisingEventService.fetchMoneyFromCollectionBoxAndTransferItToFundraisingEvent(collectionBoxId);
    }


}
