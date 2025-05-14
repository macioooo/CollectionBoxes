package org.maciejszuwarowski.domain.collectionbox;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.maciejszuwarowski.domain.HashGeneratorTestImpl;
import org.maciejszuwarowski.domain.collectionbox.dto.*;
import org.maciejszuwarowski.domain.collectionbox.exceptions.*;
import org.maciejszuwarowski.domain.shared.Currency;
import org.maciejszuwarowski.domain.shared.HashGenerable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_ASSIGNED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_CREATED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.shared.Currency.*;

public class CollectionBoxFacadeTest {

    private CollectionBoxFacade collectionBoxFacade;
    private CollectionBoxRepository collectionBoxRepository;
    private CollectionBoxService collectionBoxService;
    private HashGenerable hashGenerator;

    @BeforeEach
    void setUp() {
        collectionBoxRepository = new InMemoryCollectionBoxRepositoryImpl();
        hashGenerator = new HashGeneratorTestImpl();
        collectionBoxService = new CollectionBoxService(collectionBoxRepository, hashGenerator);
        collectionBoxFacade = new CollectionBoxFacade(collectionBoxService);

    }


    @Nested
    class CreatingCollectionBoxTests {
        @Test
        void shouldCreateAndAssignCollectionBoxSuccessfully() {
            //given
            String fundraisingEventId = "event-123";
            //when
            CollectionBoxInfoMessage result = collectionBoxFacade.createAndAssignCollectionBox(fundraisingEventId);
            List<CollectionBox> collectionBoxes = collectionBoxRepository.findAll();
            CollectionBox createdCollectionBox = collectionBoxes.getFirst();
            //then
            assertNotNull(result);
            assertEquals(COLLECTION_BOX_CREATED_SUCCESSFULLY.message, result.message());
            assertEquals(collectionBoxes.size(), 1);
            assertEquals(createdCollectionBox.getFundraisingEventId(), fundraisingEventId);
            assertTrue(createdCollectionBox.isAssigned());
            assertTrue(createdCollectionBox.isEmpty());
        }

        @Test
        void shouldCreateCollectionBoxWithoutFundraisingEventId() {
            //given
            //when
            CollectionBoxInfoMessage result = collectionBoxFacade.createCollectionBoxWithoutAssigning();
            List<CollectionBox> collectionBoxes = collectionBoxRepository.findAll();
            CollectionBox createdCollectionBox = collectionBoxes.getFirst();
            //then
            assertNotNull(result);
            assertEquals(COLLECTION_BOX_CREATED_SUCCESSFULLY.message, result.message());
            assertEquals(collectionBoxes.size(), 1);
            assertEquals(createdCollectionBox.getFundraisingEventId(), null);
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
            assertEquals(createdCollectionBox.getFundraisingEventId(), "");
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
            String fixedCollectionBoxId = "123";
            //when
            collectionBoxFacade.createCollectionBoxWithoutAssigning();
            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById(fixedCollectionBoxId);
            CollectionBox collectionBox = optionalCollectionBox.orElseThrow(() -> new AssertionError("CollectionBox not found"));
            assertFalse(collectionBox.isAssigned());
            //then
            CollectionBoxInfoMessage actualMessage = collectionBoxFacade.assignCollectionBox(fixedCollectionBoxId, fundraisingEventId);
            assertEquals(COLLECTION_BOX_ASSIGNED_SUCCESSFULLY.message, actualMessage.message());
            assertTrue(collectionBox.isAssigned());

        }

        @Test
        void shouldThrowCollectionBoxCannotBeAssignedWhenEventIdIsNull() {
            //given
            String fixedCollectionBoxId = "123";
            //when
            collectionBoxFacade.createCollectionBoxWithoutAssigning();
            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById(fixedCollectionBoxId);
            CollectionBox collectionBox = optionalCollectionBox.orElseThrow(() -> new AssertionError("CollectionBox not found"));
            assertFalse(collectionBox.isAssigned());
            //then
            assertThrows(CollectionBoxCannotBeAssigned.class, () -> collectionBoxFacade.assignCollectionBox("123", null), "Fundraising event ID cannot be null or empty.");

        }

        @Test
        void shouldThrowCollectionBoxCannotBeAssignedWhenEventIdIsEmpty() {
            //given
            String fixedCollectionBoxId = "123";
            String emptyFundraisingEventId = "";
            //when
            collectionBoxFacade.createCollectionBoxWithoutAssigning();
            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById(fixedCollectionBoxId);
            CollectionBox collectionBox = optionalCollectionBox.orElseThrow(() -> new AssertionError("CollectionBox not found"));
            assertFalse(collectionBox.isAssigned());
            //then
            assertThrows(CollectionBoxCannotBeAssigned.class, () -> collectionBoxFacade.assignCollectionBox("123", emptyFundraisingEventId), "Fundraising event ID cannot be null or empty.");

        }

        @Test
        void shouldThrowCollectionBoxCannotBeAssignedWhenCollectionBoxIsNotEmpty() {
            //given
            String fixedCollectionBoxId = "123";
            String emptyFundraisingEventId = "testtest123";
            //when
            collectionBoxFacade.createCollectionBoxWithoutAssigning();
            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById(fixedCollectionBoxId);
            CollectionBox collectionBox = optionalCollectionBox.orElseThrow(() -> new AssertionError("CollectionBox not found"));
            assertFalse(collectionBox.isAssigned());
            collectionBox.addFunds(USD, new BigDecimal("10"));
            //then
            assertThrows(CollectionBoxCannotBeAssigned.class, () -> collectionBoxFacade.assignCollectionBox("123", emptyFundraisingEventId), "Box 123 is not empty and cannot be assigned");

        }

        @Test
        void shouldThrowCollectionBoxCannotBeAssignedWhenCollectionBoxIsAlreadyAssigned() {
            //given
            String fundraisingEventId = "testevent123";
            String fixedCollectionBoxId = "123";
            //when
            collectionBoxFacade.createAndAssignCollectionBox(fundraisingEventId);
            Optional<CollectionBox> optionalCollectionBox = collectionBoxRepository.findById(fixedCollectionBoxId);
            CollectionBox collectionBox = optionalCollectionBox.orElseThrow(() -> new AssertionError("CollectionBox not found"));
            assertTrue(collectionBox.isAssigned());
            //then
            assertThrows(CollectionBoxCannotBeAssigned.class, () -> collectionBoxFacade.assignCollectionBox("123", fundraisingEventId), "Box 123 is already assigned");
        }

        @Test
        void shouldThrowCollectionBoxNotFoundExceptionWhenCollectionBoxCannotBeFoundInDatabase() {
            //given
            String fundraisingEventId = "213132";
            //when//then
            assertThrows(CollectionBoxNotFoundException.class, () -> collectionBoxFacade.assignCollectionBox("testtest", fundraisingEventId));
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
        private String fundraisingEventId = "eventtest123";

        @BeforeEach
        void addMoneySetup() {
            this.existingBoxId = UUID.randomUUID().toString();
            CollectionBox box = new CollectionBox(this.existingBoxId, this.fundraisingEventId);
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
            assertEquals(0, BigDecimal.ZERO.compareTo(boxOptional.get().getBalance(Currency.PLN)));
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
            CollectionBox box1 = new CollectionBox(UUID.randomUUID().toString(), "testEvent1");
            box1.addFunds(PLN, BigDecimal.TEN);
            collectionBoxRepository.save(box1);

            CollectionBox box2 = new CollectionBox(UUID.randomUUID().toString());
            collectionBoxRepository.save(box2);

            CollectionBox box3 = new CollectionBox(UUID.randomUUID().toString(), "testEvent3");
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
        private String eventId = "eventIdTest";

        @BeforeEach
        void emptySetup() {
            this.boxId = UUID.randomUUID().toString();
            CollectionBox box = new CollectionBox(this.boxId, this.eventId);
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
            assertEquals(eventId, resultDto.fundraisingEventId());
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
            assertEquals(eventId, emptiedBox.getFundraisingEventId());
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

        @Test
        void shouldReturnEmptyColectionBoxWhenEmptyingAnAlreadyEmptyBox() {
            //given
            String emptyBoxId = UUID.randomUUID().toString();
            String emptyBoxEventId = "emptyBoxEventIdTest";
            CollectionBox emptyBox = new CollectionBox(emptyBoxId, emptyBoxEventId);
            collectionBoxRepository.save(emptyBox);

            //when
            EmptiedCollectionBoxDto resultDto = collectionBoxFacade.emptyCollectionBoxAndGetDataTransfer(emptyBoxId);

            //then
            assertNotNull(resultDto);
            assertEquals(emptyBoxEventId, resultDto.fundraisingEventId());
            assertNotNull(resultDto.collectedAmount());
            for (Currency currency : Currency.values()) {
                BigDecimal amount = resultDto.collectedAmount().get(currency);
                assertNotNull(amount);
                assertEquals(0, BigDecimal.ZERO.compareTo(amount));
            }

            //CollectionBox should still be empty
            Optional<CollectionBox> boxOptional = collectionBoxRepository.findById(emptyBoxId);
            assertTrue(boxOptional.isPresent());
            assertTrue(boxOptional.get().isEmpty());
        }
    }

}
