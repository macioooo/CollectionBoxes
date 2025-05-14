package org.maciejszuwarowski.domain.collectionbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.maciejszuwarowski.domain.HashGeneratorTestImpl;
import org.maciejszuwarowski.domain.collectionbox.dto.*;
import org.maciejszuwarowski.domain.collectionbox.exceptions.*;
import org.maciejszuwarowski.domain.fundraisingevent.FundraisingEvent;
import org.maciejszuwarowski.domain.fundraisingevent.FundraisingEventFacade;
import org.maciejszuwarowski.domain.shared.Currency;
import org.maciejszuwarowski.domain.shared.HashGenerable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_ASSIGNED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_CREATED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.shared.Currency.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CollectionBoxFacadeTest {

    private static final String DEFAULT_BOX_ID = "123";
    FundraisingEvent sampleEvent;
    private CollectionBoxFacade collectionBoxFacade;
    private CollectionBoxRepository collectionBoxRepository;
    private CollectionBoxService collectionBoxService;
    private HashGenerable hashGenerator;
    @Mock
    private FundraisingEventFacade fundraisingEventFacade;

    @BeforeEach
    void setUp() {
        collectionBoxRepository = new InMemoryCollectionBoxRepositoryImpl();
        hashGenerator = new HashGeneratorTestImpl();
        collectionBoxService = new CollectionBoxService(collectionBoxRepository, fundraisingEventFacade, hashGenerator);
        collectionBoxFacade = new CollectionBoxFacade(collectionBoxService);
        sampleEvent = FundraisingEvent.builder()
                .id("event-123")
                .nameOfFundraisingEvent("Sample Event")
                .currencyOfTheMoneyAccount(PLN)
                .amountOfMoney(BigDecimal.ZERO)
                .build();

    }


    @Test
    void shouldThrowCollectionBoxNotFoundExceptionWhenCollectionBoxCannotBeFoundInDatabase() {
        //given
        String fundraisingEventId = "213132";
        //when//then
        assertThrows(CollectionBoxNotFoundException.class, () -> collectionBoxFacade.assignCollectionBox("testtest", fundraisingEventId));
    }

    @Test
    void shouldReturnEmptyAmountsWhenEmptyingAnAlreadyEmptyAndAssignedBox() {
        //given
        String eventIdForBox = "eventForEmptyBoxTest";

        FundraisingEvent fundraisingEventForSetup = FundraisingEvent.builder()
                .id(eventIdForBox)
                .nameOfFundraisingEvent("testevent")
                .currencyOfTheMoneyAccount(PLN)
                .amountOfMoney(BigDecimal.ZERO)
                .build();

        when(fundraisingEventFacade.getFundraisingEventById(eventIdForBox)).thenReturn(fundraisingEventForSetup);

        CollectionBox box = collectionBoxService.createCollectionBox();
        String actualBoxId = box.getId();
        collectionBoxService.assignCollectionBox(actualBoxId, eventIdForBox);

        //when
        EmptiedCollectionBoxDto resultDto = collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(actualBoxId);
        //then
        assertNotNull(resultDto);
        assertEquals(eventIdForBox, resultDto.fundraisingEventId());
        assertNotNull(resultDto.collectedAmount());

        assertTrue(resultDto.collectedAmount().values().stream().allMatch(amount -> amount.compareTo(BigDecimal.ZERO) == 0));
        assertEquals(Currency.values().length, resultDto.collectedAmount().size());

        Optional<CollectionBox> boxOptionalAfterDrain = collectionBoxRepository.findById(actualBoxId);
        assertTrue(boxOptionalAfterDrain.isPresent());
        assertTrue(boxOptionalAfterDrain.get().isEmpty());
        assertTrue(boxOptionalAfterDrain.get().isAssigned());
        assertEquals(eventIdForBox, boxOptionalAfterDrain.get().getAssignedFundraisingEventIdAsString());
    }

    @Nested
    class CreatingCollectionBoxTests {
        @Test
        void shouldCreateAndAssignCollectionBoxSuccessfully() {
            //given
            String fundraisingEventId = "event-123";
            when(fundraisingEventFacade.getFundraisingEventById(fundraisingEventId)).thenReturn(sampleEvent);
            //when
            CollectionBoxInfoMessage result = collectionBoxFacade.createAndAssignCollectionBox(fundraisingEventId);
            List<CollectionBox> collectionBoxes = collectionBoxRepository.findAll();
            CollectionBox createdCollectionBox = collectionBoxes.get(0);
            //then
            assertNotNull(result);
            assertEquals(COLLECTION_BOX_CREATED_SUCCESSFULLY.message, result.message());
            assertEquals(collectionBoxes.size(), 1);
            assertNotNull(createdCollectionBox.getAssignedFundraisingEvent());
            assertEquals(fundraisingEventId, createdCollectionBox.getAssignedFundraisingEvent().getId());
            assertTrue(createdCollectionBox.isAssigned());
            assertTrue(createdCollectionBox.isEmpty());
        }

        @Test
        void shouldCreateCollectionBoxWithoutFundraisingEventId() {
            //given
            //when
            CollectionBoxInfoMessage result = collectionBoxFacade.createCollectionBoxWithoutAssigning();
            List<CollectionBox> collectionBoxes = collectionBoxRepository.findAll();
            CollectionBox createdCollectionBox = collectionBoxes.get(0);
            //then
            assertNotNull(result);
            assertEquals(COLLECTION_BOX_CREATED_SUCCESSFULLY.message, result.message());
            assertEquals(collectionBoxes.size(), 1);
            assertNull(createdCollectionBox.getAssignedFundraisingEventIdAsString());
            assertFalse(createdCollectionBox.isAssigned());
            assertTrue(createdCollectionBox.isEmpty());
        }

        @Test
        void shouldCreateCollectionBoxWithoutAssigningFundraisingEventIdAfterPassingEmptyString() {
            //given
            String fundraisingEventId = "";
            //when
            CollectionBoxInfoMessage result = collectionBoxFacade.createAndAssignCollectionBox(fundraisingEventId);
            List<CollectionBox> collectionBoxes = collectionBoxRepository.findAll();
            CollectionBox createdCollectionBox = collectionBoxes.getFirst();
            //then
            assertNotNull(result);
            assertEquals(COLLECTION_BOX_CREATED_SUCCESSFULLY.message, result.message());
            assertEquals(collectionBoxes.size(), 1);
            assertEquals(createdCollectionBox.getAssignedFundraisingEventIdAsString(), null);
            assertFalse(createdCollectionBox.isAssigned());
            assertTrue(createdCollectionBox.isEmpty());
        }

    }

    @Nested
    class AssigningCollectionBoxesTests {
        @Test
        void shouldAssignCollectionBoxToFundraisingEvent() {
            //given
            String fundraisingEventId = "testevent123";
            CollectionBox boxToAssign = new CollectionBox(DEFAULT_BOX_ID);
            collectionBoxRepository.save(boxToAssign);


            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById(DEFAULT_BOX_ID);
            CollectionBox collectionBox = optionalCollectionBox.orElseThrow(() -> new AssertionError("CollectionBox not found for setup"));
            assertFalse(collectionBox.isAssigned());

            FundraisingEvent event = FundraisingEvent.builder().id(fundraisingEventId).nameOfFundraisingEvent("Test Event").currencyOfTheMoneyAccount(PLN).amountOfMoney(BigDecimal.ZERO).build();
            when(fundraisingEventFacade.getFundraisingEventById(fundraisingEventId)).thenReturn(event);

            //when
            CollectionBoxInfoMessage actualMessage = collectionBoxFacade.assignCollectionBox(DEFAULT_BOX_ID, fundraisingEventId);

            //then
            assertEquals(COLLECTION_BOX_ASSIGNED_SUCCESSFULLY.message, actualMessage.message());
            CollectionBox assignedBox = collectionBoxRepository.findById(DEFAULT_BOX_ID).orElseThrow(); // Re-fetch to check persisted state
            assertTrue(assignedBox.isAssigned());
            assertNotNull(assignedBox.getAssignedFundraisingEvent());
            assertEquals(fundraisingEventId, assignedBox.getAssignedFundraisingEvent().getId());
        }

        @Test
        void shouldThrowCollectionBoxCannotBeAssignedWhenCollectionBoxIsNotEmpty() {
            //given
            String fundraisingEventId = "testtest123";

            CollectionBox collectionBox = new CollectionBox(DEFAULT_BOX_ID);
            collectionBox.addFunds(USD, new BigDecimal("10"));
            collectionBoxRepository.save(collectionBox);

            FundraisingEvent event = FundraisingEvent.builder().id(fundraisingEventId).nameOfFundraisingEvent("Test Event").currencyOfTheMoneyAccount(PLN).amountOfMoney(BigDecimal.ZERO).build();
            when(fundraisingEventFacade.getFundraisingEventById(fundraisingEventId)).thenReturn(event);

            //when
            //then
            assertThrows(CollectionBoxCannotBeAssigned.class, () -> collectionBoxFacade.assignCollectionBox(DEFAULT_BOX_ID, fundraisingEventId), "Box 123 is not empty and cannot be assigned");
        }

        @Test
        void shouldThrowCollectionBoxCannotBeAssignedWhenCollectionBoxIsAlreadyAssigned() {
            //given
            String fundraisingEventId1 = "testevent123";
            String fundraisingEventId2 = "anotherEvent456";

            FundraisingEvent firstEvent = FundraisingEvent.builder().id(fundraisingEventId1).nameOfFundraisingEvent("First Event").currencyOfTheMoneyAccount(PLN).amountOfMoney(BigDecimal.ZERO).build();
            when(fundraisingEventFacade.getFundraisingEventById(fundraisingEventId1)).thenReturn(firstEvent);

            collectionBoxFacade.createAndAssignCollectionBox(fundraisingEventId1);

            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById(DEFAULT_BOX_ID);
            CollectionBox collectionBox = optionalCollectionBox.orElseThrow(() -> new AssertionError("CollectionBox not found after initial assignment"));
            assertTrue(collectionBox.isAssigned());

            FundraisingEvent secondEvent = FundraisingEvent.builder().id(fundraisingEventId2).nameOfFundraisingEvent("Second Event").currencyOfTheMoneyAccount(EUR).amountOfMoney(BigDecimal.ZERO).build();
            when(fundraisingEventFacade.getFundraisingEventById(fundraisingEventId2)).thenReturn(secondEvent);

            //then
            assertThrows(CollectionBoxCannotBeAssigned.class, () -> collectionBoxFacade.assignCollectionBox(DEFAULT_BOX_ID, fundraisingEventId2), "Box 123 is already assigned");
        }

        @Test
        void shouldThrowCollectionBoxNotFoundExceptionWhenCollectionBoxCannotBeFoundInDatabase() {
            //given
            String fundraisingEventId = "213132";
            //when//then
            assertThrows(CollectionBoxNotFoundException.class, () -> collectionBoxFacade.assignCollectionBox("testtestNonExistent", fundraisingEventId));
        }
    }

    @Nested
    class UnregisterCollectionBoxTests {
        @Test
        void shouldClearTheMoneyAndDeleteCollectionBoxFromDatabaseWhenIsNotAssigned() {
            //given
            collectionBoxFacade.createCollectionBoxWithoutAssigning();
            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById("123");
            CollectionBox collectionBox = optionalCollectionBox.orElse(new CollectionBox(null));
            assertTrue(collectionBox != null);
            //when
            collectionBoxFacade.unregisterCollectionBox("123");
            //then
            assertFalse(collectionBoxRepository.findById("123").isPresent());
        }

        @Test
        void shouldClearTheMoneyAndDeleteCollectionBoxFromDatabaseWhenIsAssigned() {
            //given
            String fundraisingEventId = "Testtess";
            when(fundraisingEventFacade.getFundraisingEventById(fundraisingEventId)).thenReturn(new FundraisingEvent(fundraisingEventId, "test", USD, BigDecimal.ZERO));
            collectionBoxFacade.createAndAssignCollectionBox("Testtess");
            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById("123");
            CollectionBox collectionBox = optionalCollectionBox.orElse(new CollectionBox(null));
            assertTrue(collectionBox != null);
            //when
            collectionBoxFacade.unregisterCollectionBox("123");
            //then
            assertFalse(collectionBoxRepository.findById("123").isPresent());
        }

        @Test
        void shouldThrowCollectionBoxNotFoundExceptionWhenTryingToDeleteCollectionBoxThatDosentExists() {
            //given
            //when
            //then
            assertThrows(CollectionBoxNotFoundException.class, () -> collectionBoxFacade.unregisterCollectionBox("123"), "Collection box not found");
        }

        @Test
        void shouldThrowCollectionBoxNotFoundExceptionWhenTryingToDeleteCollectionBoxTwoTimes() {
            //given
            //when
            //then
            assertThrows(CollectionBoxNotFoundException.class, () -> collectionBoxFacade.unregisterCollectionBox("123"), "Collection box not found");
        }
    }

    @Nested
    class AddingMoneyToCollectionBoxTests {
        private String existingBoxId;
        private FundraisingEvent fundraisingEvent;

        @BeforeEach
        void addMoneySetup() {
            fundraisingEvent = new FundraisingEvent("testid", "testname", USD, BigDecimal.ZERO);
            this.existingBoxId = UUID.randomUUID().toString();
            CollectionBox box = new CollectionBox(this.existingBoxId, fundraisingEvent);
            collectionBoxRepository.save(box);
        }

        @Test
        void shouldAddMoneyToAssignedCollectionBox() {
            //given
            MoneyDto money = new MoneyDto(USD, new BigDecimal("10.00"));
            TransferResultDto expectedTransferResult = TransferResultDto.builder()
                    .targetCurrency(money.currency())
                    .amountTransfered(money.amount())
                    .success(true)
                    .message("Success!")
                    .build();
            //when
            TransferResultDto transferResult = collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, money);
            //then
            assertEquals(expectedTransferResult, transferResult);
        }

        @Test
        void shouldAddMoneyMultipleTimesInTheSameCurrency() {
            //given
            MoneyDto firstAddition = new MoneyDto(EUR, new BigDecimal("50.00"));
            MoneyDto secondAddition = new MoneyDto(EUR, new BigDecimal("30.25"));
            BigDecimal expectedFinalEurBalance = new BigDecimal("80.25");
            //when
            collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, firstAddition);
            TransferResultDto secondResult = collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, secondAddition);
            //then
            assertTrue(secondResult.success());
            assertEquals(0, secondAddition.amount().compareTo(secondResult.amountTransfered()));
            assertEquals(secondAddition.currency(), secondResult.targetCurrency());
            Optional<CollectionBox> boxOptional = collectionBoxRepository.findById(existingBoxId);
            assertTrue(boxOptional.isPresent());
            CollectionBox updatedBox = boxOptional.get();
            assertEquals(0, expectedFinalEurBalance.compareTo(updatedBox.getBalance(EUR)));
            assertEquals(0, BigDecimal.ZERO.compareTo(updatedBox.getBalance(USD)));

        }

        @Test
        void shouldAddMoneyToTwoDiffrentCurrencies() {
            //given
            MoneyDto firstAdditionInEur = new MoneyDto(EUR, new BigDecimal("50.00"));
            MoneyDto secondAdditionInUsd = new MoneyDto(USD, new BigDecimal("30.25"));
            //when
            collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, firstAdditionInEur);
            TransferResultDto secondResult = collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, secondAdditionInUsd);
            //then
            assertTrue(secondResult.success());
            assertEquals(0, secondAdditionInUsd.amount().compareTo(secondResult.amountTransfered()));
            assertEquals(secondAdditionInUsd.currency(), secondResult.targetCurrency());
            Optional<CollectionBox> boxOptional = collectionBoxRepository.findById(existingBoxId);
            assertTrue(boxOptional.isPresent());
            CollectionBox updatedBox = boxOptional.get();
            assertEquals(0, firstAdditionInEur.amount().compareTo(updatedBox.getBalance(EUR)));
            assertEquals(0, secondAdditionInUsd.amount().compareTo(updatedBox.getBalance(USD)));
        }

        @Test
        void shouldThrowCollectionBoxNotFoundWhenAddingToNonExistentBox() {
            //given
            String nonExistentBoxId = "boxidthatdosentexists";
            MoneyDto moneyToAdd = new MoneyDto(PLN, new BigDecimal("10.00"));

            //when//then
            assertThrows(CollectionBoxNotFoundException.class, () ->
                    collectionBoxFacade.addMoneyToCollectionBox(nonExistentBoxId, moneyToAdd), "Collection box not found"
            );
            //checking if money in collectionbox has changed
            Optional<CollectionBox> boxOptional = collectionBoxRepository.findById(existingBoxId);
            assertTrue(boxOptional.isPresent());
            assertEquals(0, BigDecimal.ZERO.compareTo(boxOptional.get().getBalance(PLN)));
        }

        @Test
        void shouldThrowAmountOfMoneyLessThanZeroExceptionWhenAddingNegativeAmount() {
            //given
            MoneyDto negativeMoney = new MoneyDto(Currency.PLN, new BigDecimal("-5.00"));
            //when//then
            assertThrows(AmountOfMoneyLessThanZeroException.class, () ->
                    collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, negativeMoney), "You can't add negative numbers to Collection box)"
            );
            //checking if money in collectionbox has changed
            Optional<CollectionBox> boxOptional = collectionBoxRepository.findById(existingBoxId);
            assertTrue(boxOptional.isPresent());
            assertEquals(0, BigDecimal.ZERO.compareTo(boxOptional.get().getBalance(Currency.PLN)));
        }

        @Test
        void shouldThrowMoneyTransferExceptionWhenAddingNullAmount() {
            //given
            MoneyDto moneyWithNullAmount = new MoneyDto(Currency.EUR, null);
            //when//then
            assertThrows(MoneyTransferException.class, () ->
                            collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, moneyWithNullAmount)
                    , "Amount cannot be null");
        }

        @Test
        void shouldThrowMoneyTransferExceptionWhenAddingNullCurrency() {
            //given
            MoneyDto moneyWithNullAmount = new MoneyDto(null, new BigDecimal("10.05"));
            //when//then
            assertThrows(IllegalStateException.class, () ->
                            collectionBoxFacade.addMoneyToCollectionBox(existingBoxId, moneyWithNullAmount)
                    , "Currency cannot be null");
        }


    }

    @Nested
    class GetAllCollectionBoxesTests {
        @Test
        void shouldReturnEmptyListWhenNoBoxesExist() {
            //given
            List<CollectionBoxPublicInfoDto> result = collectionBoxFacade.getAllCollectionBoxes();

            //then
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void shouldReturnListOfAllExistingBoxes() {
            //given
            CollectionBox box1 = new CollectionBox(UUID.randomUUID().toString(), new FundraisingEvent("one", "first", USD, BigDecimal.ZERO));
            box1.addFunds(PLN, BigDecimal.TEN);
            collectionBoxRepository.save(box1);

            CollectionBox box2 = new CollectionBox(UUID.randomUUID().toString());
            collectionBoxRepository.save(box2);

            CollectionBox box3 = new CollectionBox(UUID.randomUUID().toString(), new FundraisingEvent("two", "second", PLN, BigDecimal.ZERO));
            collectionBoxRepository.save(box3);

            //when
            List<CollectionBoxPublicInfoDto> resultList = collectionBoxFacade.getAllCollectionBoxes();
            //then
            assertNotNull(resultList);
            assertThat(resultList).hasSize(3);
            CollectionBoxPublicInfoDto dto1 = resultList.stream().filter(dto -> dto.id().equals(box1.getId())).findFirst().orElseThrow();
            assertEquals(box1.getId(), dto1.id());
            assertTrue(dto1.isAssigned());
            assertFalse(dto1.isEmpty());
            CollectionBoxPublicInfoDto dto2 = resultList.stream().filter(dto -> dto.id().equals(box2.getId())).findFirst().orElseThrow();
            assertEquals(box2.getId(), dto2.id());
            assertFalse(dto2.isAssigned());
            assertTrue(dto2.isEmpty());
            CollectionBoxPublicInfoDto dto3 = resultList.stream().filter(dto -> dto.id().equals(box3.getId())).findFirst().orElseThrow();
            assertEquals(box3.getId(), dto3.id());
            assertTrue(dto3.isAssigned());
            assertTrue(dto3.isEmpty());
        }
    }

    @Nested
    class EmptyCollectionBoxAndGetDataTransferTests {


        private String boxId;
        private FundraisingEvent fundraisingEvent;

        @BeforeEach
        void emptySetup() {
            this.boxId = UUID.randomUUID().toString();
            fundraisingEvent = new FundraisingEvent("id", "name", USD, BigDecimal.ZERO);
            CollectionBox box = new CollectionBox(this.boxId, fundraisingEvent);
            box.addFunds(Currency.PLN, new BigDecimal("150.75"));
            box.addFunds(Currency.USD, new BigDecimal("50.00"));
            collectionBoxRepository.save(box);
        }

        @Test
        void shouldEmptyBoxAndReturnDataSuccessfully() {
            // given
            BigDecimal expectedPln = new BigDecimal("150.75");
            BigDecimal expectedUsd = new BigDecimal("50.00");

            //when
            EmptiedCollectionBoxDto resultDto = collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(boxId);
            assertNotNull(resultDto);
            assertEquals(fundraisingEvent.getId(), resultDto.fundraisingEventId());
            assertNotNull(resultDto.collectedAmount());
            assertThat(resultDto.collectedAmount())
                    .containsEntry(PLN, expectedPln)
                    .containsEntry(USD, expectedUsd);
            //then
            assertEquals(0, expectedPln.compareTo(resultDto.collectedAmount().get(PLN)));
            assertEquals(0, expectedUsd.compareTo(resultDto.collectedAmount().get(USD)));


            // chceck if collectionbox is still in db
            Optional<CollectionBox> boxOptional = collectionBoxRepository.findById(boxId);
            assertTrue(boxOptional.isPresent());
            CollectionBox emptiedBox = boxOptional.get();
            assertTrue(emptiedBox.isEmpty());
            assertEquals(boxId, emptiedBox.getId());
        }

        @Test
        void shouldThrowCollectionBoxNotFoundExceptionWhenEmptyingNonExistentBox() {
            // given
            String nonExistentBoxId = "nonextistentboxidtest132123";

            // when//then
            assertThrows(CollectionBoxNotFoundException.class, () ->
                    collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(nonExistentBoxId), "Collection box not found"
            );
        }

        @Test
        void shouldThrowNotAssignedWhenEmptyingUnassignedBox() {
            //given
            String unassignedBoxId = UUID.randomUUID().toString();
            CollectionBox unassignedBox = new CollectionBox(unassignedBoxId);
            unassignedBox.addFunds(EUR, BigDecimal.TEN);
            collectionBoxRepository.save(unassignedBox);
            //when//then
            assertThrows(CollectionBoxNotAssignedException.class, () ->
                    collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(unassignedBoxId), "Collection box is not assigned. Cannot get data for transfer.");


            //check if collectionBox got emptied - it shouldnt
            Optional<CollectionBox> boxOptional = collectionBoxRepository.findById(unassignedBoxId);
            assertTrue(boxOptional.isPresent());
            assertFalse(boxOptional.get().isEmpty());
        }
    }
}


