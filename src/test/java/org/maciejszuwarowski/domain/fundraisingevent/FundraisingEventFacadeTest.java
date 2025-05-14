package org.maciejszuwarowski.domain.fundraisingevent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maciejszuwarowski.domain.HashGeneratorTestImpl;
import org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacade;
import org.maciejszuwarowski.domain.collectionbox.dto.EmptiedCollectionBoxDto;
import org.maciejszuwarowski.domain.currencyexchange.CurrencyExchangeFacade;
import org.maciejszuwarowski.domain.currencyexchange.dto.ExchangeRateDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FinancialReportDto;
import org.maciejszuwarowski.domain.fundraisingevent.dto.FundraisingEventMessageDto;
import org.maciejszuwarowski.domain.fundraisingevent.exceptions.FundraisingEventNotFoundException;
import org.maciejszuwarowski.domain.shared.Currency;
import org.maciejszuwarowski.domain.shared.HashGenerable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FundraisingEventFacadeTest {

    // mocked
    @Mock
    private CollectionBoxFacade collectionBoxFacade;
    @Mock
    private CurrencyExchangeFacade currencyExchangeFacade;

    // not mocked
    private FundraisingEventRepository fundraisingEventRepository;
    private HashGenerable hashGenerator;

    private FundraisingEventService actualFundraisingEventService;
    private FundraisingEventFacade fundraisingEventFacade;

    private String defaultEventName;
    private Currency defaultCurrency;

    @BeforeEach
    void setUp() {
        fundraisingEventRepository = new FundraisingEventRepositoryInMemoryImpl();
        hashGenerator = new HashGeneratorTestImpl(); // default hash - 123

        actualFundraisingEventService = new FundraisingEventService(
                fundraisingEventRepository,
                hashGenerator,
                collectionBoxFacade,
                currencyExchangeFacade
        );
        fundraisingEventFacade = new FundraisingEventFacade(actualFundraisingEventService);

        defaultEventName = "Annual Charity Drive";
        defaultCurrency = Currency.PLN;
    }

    @Nested
    class CreateFundraisingEventTests {

        @Test
        void shouldCreateAndStoreEventWithDefaultHash() {
            //given
            String expectedHash = "123"; //default

            //when
            FundraisingEventMessageDto resultDto = fundraisingEventFacade.createFundraisingEvent(defaultEventName, defaultCurrency);

            //then
            assertEquals(FundraisingEventMessages.FUNDRAISING_EVENT_CREATED_SUCCESSFULLY.message, resultDto.message());

            Optional<FundraisingEvent> eventOptional = fundraisingEventRepository.findById(expectedHash);
            assertTrue(eventOptional.isPresent(), "Event should be stored in the repository.");
            FundraisingEvent storedEvent = eventOptional.get();
            assertEquals(expectedHash, storedEvent.id());
            assertEquals(defaultEventName, storedEvent.nameOfFundraisingEvent());
            assertEquals(defaultCurrency, storedEvent.currencyOfTheMoneyAccount());
            assertEquals(0, BigDecimal.ZERO.compareTo(storedEvent.amountOfMoney()), "Initial amount should be zero.");
        }

        @Test
        void shouldCreateAndStoreEventWithSpecificHash() {
            // given
            String specificHash = "eventSpecificHash789";
            hashGenerator = new HashGeneratorTestImpl(specificHash);
            actualFundraisingEventService = new FundraisingEventService(
                    fundraisingEventRepository, hashGenerator, collectionBoxFacade, currencyExchangeFacade);
            fundraisingEventFacade = new FundraisingEventFacade(actualFundraisingEventService);

            String eventName = "Special Event";
            Currency eventCurrency = Currency.USD;

            // When
            FundraisingEventMessageDto resultDto = fundraisingEventFacade.createFundraisingEvent(eventName, eventCurrency);

            // Then
            assertEquals(FundraisingEventMessages.FUNDRAISING_EVENT_CREATED_SUCCESSFULLY.message, resultDto.message());
            Optional<FundraisingEvent> eventOptional = fundraisingEventRepository.findById(specificHash);
            assertTrue(eventOptional.isPresent());
            assertEquals(eventName, eventOptional.get().nameOfFundraisingEvent());
        }
    }

    @Nested
    class DisplayFinancialReportTests {

        @Test
        void shouldReturnEmptyListWhenRepositoryEmpty() {
            //given

            //when
            List<FinancialReportDto> report = fundraisingEventFacade.displayFinancialReport();

            //then
            assertNotNull(report);
            assertTrue(report.isEmpty());
        }

        @Test
        void shouldReturnReportsForAllEvents() {
            //given
            FundraisingEvent event1 = FundraisingEvent.builder().id("ev1").nameOfFundraisingEvent("Event One").currencyOfTheMoneyAccount(Currency.PLN).amountOfMoney(new BigDecimal("1000.00")).build();
            FundraisingEvent event2 = FundraisingEvent.builder().id("ev2").nameOfFundraisingEvent("Event Two").currencyOfTheMoneyAccount(Currency.EUR).amountOfMoney(new BigDecimal("500.50")).build();
            fundraisingEventRepository.save(event1);
            fundraisingEventRepository.save(event2);

            //when
            List<FinancialReportDto> report = fundraisingEventFacade.displayFinancialReport();

            //then
            assertEquals(2, report.size());
            assertTrue(report.stream().anyMatch(r -> r.fundraisingEventName().equals("Event One") && r.amount().compareTo(new BigDecimal("1000.00")) == 0));
            assertTrue(report.stream().anyMatch(r -> r.fundraisingEventName().equals("Event Two") && r.amount().compareTo(new BigDecimal("500.50")) == 0));
        }
    }

    @Nested
    class TransferMoneyFromCollectionBoxTests {
        private final Currency eventCurrency = Currency.PLN;
        private final BigDecimal initialEventBalance = new BigDecimal("50.00");
        private String existingEventId;

        @BeforeEach
        void setUpTransferTest() {
            existingEventId = "transferTargetEvent456";
            hashGenerator = new HashGeneratorTestImpl(existingEventId);
            actualFundraisingEventService = new FundraisingEventService(
                    fundraisingEventRepository, hashGenerator, collectionBoxFacade, currencyExchangeFacade);
            fundraisingEventFacade = new FundraisingEventFacade(actualFundraisingEventService);

            FundraisingEvent event = FundraisingEvent.builder()
                    .id(existingEventId)
                    .nameOfFundraisingEvent("Event for Transfers")
                    .currencyOfTheMoneyAccount(eventCurrency)
                    .amountOfMoney(initialEventBalance)
                    .build();
            fundraisingEventRepository.save(event);
        }

        @Test
        void shouldUpdateBalanceForMixedCurrencies() {
            // Given
            String collectionBoxId = "cbtess";

            Map<Currency, BigDecimal> collectedAmounts = new HashMap<>();
            collectedAmounts.put(Currency.USD, new BigDecimal("10.00"));
            collectedAmounts.put(Currency.EUR, new BigDecimal("10.00"));
            collectedAmounts.put(Currency.PLN, new BigDecimal("10.00"));
            EmptiedCollectionBoxDto emptiedBoxDto = EmptiedCollectionBoxDto.builder()
                    .collectedAmount(collectedAmounts)
                    .fundraisingEventId(existingEventId)
                    .build();
            when(collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(collectionBoxId)).thenReturn(emptiedBoxDto);

            //Random exchange rates
            Map<Currency, BigDecimal> exchangeRates = new HashMap<>();
            exchangeRates.put(Currency.USD, new BigDecimal("3.96"));
            exchangeRates.put(Currency.EUR, new BigDecimal("4.26"));
            exchangeRates.put(Currency.PLN, BigDecimal.ONE);
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(exchangeRates);
            when(currencyExchangeFacade.getCurrencyRatesForUsdPlnAndEur(eventCurrency)).thenReturn(exchangeRateDto);

            //when
            FundraisingEventMessageDto resultDto = fundraisingEventFacade.transferMoneyFromCollectionBox(collectionBoxId);

            //then
            assertEquals(FundraisingEventMessages.MONEY_TRANSFERRED_TO_FUNDRAISING_EVENT_ACCOUNT_SUCCESSFULLY.message, resultDto.message());

            Optional<FundraisingEvent> eventOptional = fundraisingEventRepository.findById(existingEventId);
            assertTrue(eventOptional.isPresent());
            FundraisingEvent updatedEvent = eventOptional.get();

            // Amount of PLN after Exchange:
            // 10 USD * 3.96 = 39.60 PLN
            // 10 EUR * 4.26 = 42.60 PLN
            // 10 PLN * 1.00 = 10.00 PLN
            // 39.60 + 42.60 + 10.00 = 92.20 PLN
            BigDecimal transferredInPLN = new BigDecimal("92.20");
            BigDecimal expectedFinalBalance = initialEventBalance.add(transferredInPLN); // 50.00 + 92.20 = 142.20

            assertEquals(0, expectedFinalBalance.compareTo(updatedEvent.amountOfMoney()),
                    "Final balance is incorrect. Expected: " + expectedFinalBalance + ", Got: " + updatedEvent.amountOfMoney());
        }

        @Test
        void shouldThrowNotFoundIfEventMissing() {
            //given
            String collectionBoxId = "testtes1";
            String nonExistentEventId = "event-does-not-exist";
            EmptiedCollectionBoxDto emptiedBoxDto = EmptiedCollectionBoxDto.builder()
                    .collectedAmount(Collections.singletonMap(Currency.PLN, BigDecimal.TEN))
                    .fundraisingEventId(nonExistentEventId)
                    .build();
            when(collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(collectionBoxId)).thenReturn(emptiedBoxDto);


            //when//then
            FundraisingEventNotFoundException exception = assertThrows(FundraisingEventNotFoundException.class, () -> {
                fundraisingEventFacade.transferMoneyFromCollectionBox(collectionBoxId);
            });
            assertEquals("Fundraising event not found", exception.getMessage());

            Optional<FundraisingEvent> originalEventOptional = fundraisingEventRepository.findById(existingEventId);
            assertTrue(originalEventOptional.isPresent());
            assertEquals(0, initialEventBalance.compareTo(originalEventOptional.get().amountOfMoney()), "Original event balance should not change.");
        }
    }
}