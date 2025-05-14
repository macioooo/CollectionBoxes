package org.maciejszuwarowski.domain.collectionbox;

import lombok.AllArgsConstructor;
import org.maciejszuwarowski.domain.collectionbox.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_ASSIGNED_SUCCESSFULLY;
import static org.maciejszuwarowski.domain.collectionbox.CollectionBoxFacadeMessages.COLLECTION_BOX_CREATED_SUCCESSFULLY;

@AllArgsConstructor
@Component
public class CollectionBoxFacade {

    private final CollectionBoxService service;

    public CollectionBoxInfoMessage createAndAssignCollectionBox(String fundraisingEventId) {
        CollectionBox box = service.createCollectionBox();
        if (fundraisingEventId != null) {
            if (!fundraisingEventId.trim().isEmpty()) {
                service.assignCollectionBox(box.getId(), fundraisingEventId);
            }
        }
        return CollectionBoxInfoMessage.builder()
                .message(COLLECTION_BOX_CREATED_SUCCESSFULLY.message)
                .collectionBoxId(box.getId())
                .build();
    }

    public CollectionBoxInfoMessage createCollectionBoxWithoutAssigning() {
        return createAndAssignCollectionBox(null);
    }


    public CollectionBoxInfoMessage assignCollectionBox(String collectionBoxId, String fundraisingEventId) {
        CollectionBox box = service.assignCollectionBox(collectionBoxId, fundraisingEventId);
        return CollectionBoxInfoMessage.builder()
                .message(COLLECTION_BOX_ASSIGNED_SUCCESSFULLY.message)
                .collectionBoxId(box.getId())
                .build();
    }

    public CollectionBoxInfoMessage unregisterCollectionBox(String collectionBoxId) {
        String message = service.unregisterCollectionBox(collectionBoxId).message();
        return new CollectionBoxInfoMessage(message, collectionBoxId);

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
