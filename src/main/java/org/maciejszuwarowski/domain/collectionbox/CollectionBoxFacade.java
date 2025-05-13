package org.maciejszuwarowski.domain.collectionbox;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.collectionbox.dto.*;

import java.util.List;

import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_ASSIGNED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_CREATED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxMapper.mapFromCollectionBoxToPublicInfoDto;

@AllArgsConstructor
public class CollectionBoxFacade {

    private final CollectionBoxService service;

    public CollectionBoxInfoMessage createAndAssignCollectionBox(String fundraisingEventId) {
        service.createCollectionBox(fundraisingEventId);
        return CollectionBoxInfoMessage.builder()
                .message(COLLECTION_BOX_CREATED_SUCCESSFULLY.message)
                .build();
    }

    public CollectionBoxInfoMessage createCollectionBoxWithoutAssigning() {
        return createAndAssignCollectionBox(null);
    }


    public CollectionBoxInfoMessage assignCollectionBox(String collectionBoxId, String fundraisingEventId) {
        CollectionBox box = service.assignCollectionBox(collectionBoxId, fundraisingEventId);
        return CollectionBoxInfoMessage.builder()
                .message(COLLECTION_BOX_ASSIGNED_SUCCESSFULLY.message)
                .build();
    }

    public CollectionBoxInfoMessage unregisterCollectionBox(String collectionBoxId) {
        return service.unregisterCollectionBox(collectionBoxId);
    }

    public TransferResultDto addMoneyToCollectionBox(String collectionBoxId, MoneyDto money) {
        return service.addMoneyToCollectionBox(collectionBoxId, money);

    }

    public List<CollectionBoxPublicInfoDto> getAllCollectionBoxes() {
        return service.findAllCollectionBoxes();
    }

    public EmptiedCollectionBoxDto emptyCollectionBoxAndGetDataTransfer(String collectionBoxId) {
        return service.emptyCollectionBoxAndGetDataForTransfer(collectionBoxId);
    }

}
