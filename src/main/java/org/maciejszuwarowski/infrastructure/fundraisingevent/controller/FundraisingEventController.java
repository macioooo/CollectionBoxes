package org.maciejszuwarowski.infrastructure.fundraisingevent.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.maciejszuwarowski.domain.fundraisingevent.FundraisingEventFacade;
import org.maciejszuwarowski.domain.fundraisingevent.dto.CreateFundraisingEventDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FinancialReportDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FundraisingEventMessageDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.TransferResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fundraising-events")
@RequiredArgsConstructor
public class FundraisingEventController {
    private final FundraisingEventFacade fundraisingEventFacade;
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateFundraisingEventDto createFundraisingEvent(
            @Valid @RequestBody CreateFundraisingEventDto requestDto) {
        return fundraisingEventFacade.createFundraisingEvent(
                requestDto.nameOfTheFundraisingEvent(),
                requestDto.currencyOfTheMoneyAccount()
        );
    }
    @GetMapping("/report")
    public ResponseEntity<List<FinancialReportDto>> getFinancialReport() {
        List<FinancialReportDto> report = fundraisingEventFacade.displayFinancialReport();
        return ResponseEntity.ok(report);
    }
    @PostMapping("/moneyTransfer")
    public ResponseEntity<TransferResultDto> transferFromCollectionBox(@RequestParam String collectionBoxId) {
        TransferResultDto message = fundraisingEventFacade.transferMoneyFromCollectionBox(collectionBoxId);
        return ResponseEntity.ok(message);
    }
}
