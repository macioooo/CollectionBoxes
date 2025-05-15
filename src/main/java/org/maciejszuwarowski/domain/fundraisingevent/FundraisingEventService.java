package org.maciejszuwarowski.domain.fundraisingevent;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacade;
import org.maciejszuwarowski.domain.collectionbox.dto.EmptiedCollectionBoxDto;
import org.maciejszuwarowski.domain.currencyexchange.CurrencyExchangeFacade;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateDto;
import org.maciejszuwarowski.domain.currencyexchange.exceptions.MissingExchangeRateException;
import org.maciejszuwarowski.domain.fundraisingevent.dto.CreateFundraisingEventDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FinancialReportDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FundraisingEventMessageDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.TransferResultDto;
import org.maciejszuwarowski.domain.fundraisingevent.exceptions.FundraisingEventNotFoundException;
import org.maciejszuwarowski.domain.shared.Currency;
import org.maciejszuwarowski.domain.shared.HashGenerable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.maciejszuwarowski.domain.fundraisingevent.FundraisingEventMessages.FUNDRAISING_EVENT_CREATED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.fundraisingevent.FundraisingEventMessages.MONEY_TRANSFERRED_TO_FUNDRAISING_EVENT_ACCOUNT_SUCCESSFULLY;

@AllArgsConstructor
@Service
class FundraisingEventService {
    private final FundraisingEventRepository fundraisingEventRepository;
    private final HashGenerable hashGenerator;
    private final CollectionBoxFacade collectionBoxFacade;
    private final CurrencyExchangeFacade currencyExchangeFacade;

    @Transactional
    CreateFundraisingEventDto createFundraisingEvent(String nameOfTheFundraisingEvent, Currency currency) {

        FundraisingEvent newFundraisingEvent = FundraisingEvent.builder()
                .id(hashGenerator.getHash())
                .nameOfFundraisingEvent(nameOfTheFundraisingEvent)
                .currencyOfTheMoneyAccount(currency)
                .amountOfMoney(BigDecimal.ZERO)
                .build();
        fundraisingEventRepository.save(newFundraisingEvent);
        return new CreateFundraisingEventDto(FUNDRAISING_EVENT_CREATED_SUCCESSFULLY.message, newFundraisingEvent.getId(), nameOfTheFundraisingEvent, currency);
    }
    @Transactional(readOnly = true)
    List<FinancialReportDto> createFinancialReport() {
        List<FundraisingEvent> fundraisingEvents = fundraisingEventRepository.findAll();
        List<FinancialReportDto> financialReportOfAllFundraisingEvents = fundraisingEvents.stream().map(
                fundraisingEvent -> new FinancialReportDto(
                        fundraisingEvent.getNameOfFundraisingEvent(),
                        fundraisingEvent.getAmountOfMoney(),
                        fundraisingEvent.getCurrencyOfTheMoneyAccount()
                )
        ).toList();
        return financialReportOfAllFundraisingEvents;
    }
    @Transactional
    TransferResultDto fetchMoneyFromCollectionBoxAndTransferItToFundraisingEvent(String collectionBoxId) {
        //getting data from colleciton box facade

        EmptiedCollectionBoxDto emptiedCollectionBoxDto = collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(collectionBoxId);
        String fundraisingEventId = emptiedCollectionBoxDto.fundraisingEventId();
        //prep variable for adding money to fundraising event account
        FundraisingEvent fundraisingEvent = findFundraisingEventById(fundraisingEventId);
        Currency targetCurrency = fundraisingEvent.getCurrencyOfTheMoneyAccount(); //fetching the currency that we will be exchanging money to
        BigDecimal collectedMoneyInTargetCurrency = calculateAndExchangeCurrencyRatesForTargetCurrency(targetCurrency, emptiedCollectionBoxDto);
        BigDecimal newAccountBalance = fundraisingEvent.getAmountOfMoney().add(collectedMoneyInTargetCurrency);
        FundraisingEvent fundraisingEventAfterTransferingMoney = FundraisingEvent.builder()
                .id(fundraisingEvent.getId())
                .currencyOfTheMoneyAccount(targetCurrency)
                .nameOfFundraisingEvent(fundraisingEvent.getNameOfFundraisingEvent())
                .amountOfMoney(newAccountBalance)
                .build();
        fundraisingEventRepository.save(fundraisingEventAfterTransferingMoney);
        return new TransferResultDto(MONEY_TRANSFERRED_TO_FUNDRAISING_EVENT_ACCOUNT_SUCCESSFULLY.message, collectionBoxId, collectedMoneyInTargetCurrency);
    }

    private BigDecimal calculateAndExchangeCurrencyRatesForTargetCurrency(Currency targetCurrency, EmptiedCollectionBoxDto emptiedCollectionBoxDto) {
        BigDecimal amountOfMoneyToTransferInProperCurrency = BigDecimal.ZERO;
        ExchangeRateDto currencyRatesForUsdPlnAndEur = currencyExchangeFacade.getCurrencyRatesForUsdPlnAndEur(targetCurrency);
        for (Currency sourceCurrency : emptiedCollectionBoxDto.collectedAmount().keySet()) {
            BigDecimal rateForSourceCurrency = currencyRatesForUsdPlnAndEur.exchangeRate().get(sourceCurrency);
            if (rateForSourceCurrency == null) {
                throw new MissingExchangeRateException("Exchange rate not found for currency:" + sourceCurrency + " to " + targetCurrency);
            }
            BigDecimal money = emptiedCollectionBoxDto.collectedAmount().get(sourceCurrency).multiply(rateForSourceCurrency);
            amountOfMoneyToTransferInProperCurrency = amountOfMoneyToTransferInProperCurrency.add(money);
        }
        return amountOfMoneyToTransferInProperCurrency;
    }

    FundraisingEvent findFundraisingEventById(String fundraisingEventId) {

        return fundraisingEventRepository.findById(fundraisingEventId).orElseThrow(() -> new FundraisingEventNotFoundException("Fundraising event not found"));
    }


}
