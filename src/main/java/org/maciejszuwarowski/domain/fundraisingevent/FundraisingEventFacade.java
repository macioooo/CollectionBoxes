package org.maciejszuwarowski.domain.fundraisingevent;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.fundraisingevent.dto.CreateFundraisingEventDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FinancialReportDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FundraisingEventMessageDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.TransferResultDto;
import org.maciejszuwarowski.domain.shared.Currency;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@Component
public class FundraisingEventFacade {
    private final FundraisingEventService fundraisingEventService;

    public CreateFundraisingEventDto createFundraisingEvent(String nameOfTheFundraisingEvent, Currency currency) {
        return fundraisingEventService.createFundraisingEvent(nameOfTheFundraisingEvent, currency);
    }

    public List<FinancialReportDto> displayFinancialReport() {
        return fundraisingEventService.createFinancialReport();
    }

    public TransferResultDto transferMoneyFromCollectionBox(String collectionBoxId) {
        return fundraisingEventService.fetchMoneyFromCollectionBoxAndTransferItToFundraisingEvent(collectionBoxId);
    }

    public FundraisingEvent getFundraisingEventById(String fundraisingEventId) {
        return fundraisingEventService.findFundraisingEventById(fundraisingEventId);

    }


}
