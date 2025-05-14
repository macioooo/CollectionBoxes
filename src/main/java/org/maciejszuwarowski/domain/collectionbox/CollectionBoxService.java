package org.maciejszuwarowski.domain.collectionbox;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.collectionbox.dto.*;
import org.maciejszuwarowski.domain.collectionbox.exceptions.CollectionBoxCannotBeAssigned;
import org.maciejszuwarowski.domain.collectionbox.exceptions.CollectionBoxNotAssignedException;
import org.maciejszuwarowski.domain.collectionbox.exceptions.CollectionBoxNotFoundException;
import org.maciejszuwarowski.domain.collectionbox.exceptions.MoneyTransferException;
import org.maciejszuwarowski.domain.fundraisingevent.FundraisingEvent;
import org.maciejszuwarowski.domain.fundraisingevent.FundraisingEventFacade;
import org.maciejszuwarowski.domain.shared.Currency;
import org.maciejszuwarowski.domain.shared.HashGenerable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_UNREGISTERED_SUCCESSFULLY;

@AllArgsConstructor
@Service
class CollectionBoxService {
    private final CollectionBoxRepository repository;
    private final FundraisingEventFacade fundraisingEventFacade;
    private final HashGenerable hashGenerator;

    @Transactional
    CollectionBox createCollectionBox() {
        CollectionBox newCollectionBox = new CollectionBox(hashGenerator.getHash());
        return repository.save(newCollectionBox);
    }

    @Transactional
    CollectionBox assignCollectionBox(String collectionBoxId, String fundraisingEventId) {
        CollectionBox collectionBox = findBoxById(collectionBoxId);
        FundraisingEvent fundraisingEvent = fundraisingEventFacade.getFundraisingEventById(fundraisingEventId);
        try {
            collectionBox.assignToFundraisingEvent(fundraisingEvent);
        } catch (IllegalStateException e) {
            throw new CollectionBoxCannotBeAssigned(e.getMessage());
        }

        return repository.save(collectionBox);
    }

    @Transactional
    CollectionBoxInfoMessage unregisterCollectionBox(String collectionBoxId) {
        CollectionBox collectionBox = findBoxById(collectionBoxId);
        collectionBox.emptyContentPermanently();
        repository.save(collectionBox);
        repository.deleteById(collectionBoxId);
        return CollectionBoxInfoMessage.builder()
                .message(COLLECTION_BOX_UNREGISTERED_SUCCESSFULLY.message)
                .build();

    }

    @Transactional
    EmptiedCollectionBoxDto emptyCollectionBoxAndGetDataForTransfer(String collectionBoxId) {
        CollectionBox collectionBox = findBoxById(collectionBoxId);
        if (!collectionBox.isAssigned()) {
            throw new CollectionBoxNotAssignedException("Collection box is not assigned. Cannot get data for transfer.");
        }
        Map<Currency, BigDecimal> contents = collectionBox.drainContents();
        repository.save(collectionBox);
        return EmptiedCollectionBoxDto.builder()
                .collectedAmount(contents)
                .fundraisingEventId(collectionBox.getAssignedFundraisingEventIdAsString())
                .build();
    }

    @Transactional
    TransferResultDto addMoneyToCollectionBox(String collectionBoxId, MoneyDto money) {
        CollectionBox collectionBox = findBoxById(collectionBoxId);
        BigDecimal moneyAddedToBalance = money.amount();
        Currency currency = money.currency();
        BigDecimal balanceBeforeAddition = collectionBox.getBalance(currency);
        collectionBox.addFunds(currency, moneyAddedToBalance);
        CollectionBox savedCollectionBox = repository.save(collectionBox);
        BigDecimal balanceAfterAddition = savedCollectionBox.getBalance(currency);
        if (isMoneyTransferedCorrectly(moneyAddedToBalance, balanceBeforeAddition, balanceAfterAddition)) {
            return TransferResultDto.builder()
                    .success(true)
                    .amountTransfered(moneyAddedToBalance)
                    .targetCurrency(currency)
                    .build();
        }
        throw new MoneyTransferException("Couldnt transfer the money to the collection box");

    }
    @Transactional(readOnly = true)
    List<CollectionBoxPublicInfoDto> findAllCollectionBoxes() {
        return repository.findAll()
                .stream()
                .map(CollectionBoxMapper::mapFromCollectionBoxToPublicInfoDto)
                .collect(Collectors.toList());
    }

    private boolean isMoneyTransferedCorrectly(BigDecimal moneyAddedToBalance, BigDecimal balanceBeforeAddition, BigDecimal balanceAfterAddition) {
        BigDecimal expectedBalance = balanceBeforeAddition.add(moneyAddedToBalance);
        return balanceAfterAddition.compareTo(expectedBalance) == 0;
    }

    private CollectionBox findBoxById(String collectionBoxId) {
        CollectionBox collectionBox = repository.findById(collectionBoxId)
                .orElseThrow(() -> new CollectionBoxNotFoundException("Collection box not found"));
        return collectionBox;
    }


}
