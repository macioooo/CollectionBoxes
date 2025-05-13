package org.maciejszuwarowski.domain.collectionbox;

enum CollectionBoxFacadeMessages {
    COLLECTION_BOX_CREATED_SUCCESSFULLY("Collection box has been created succesfully"),
    COLLECTION_BOX_ASSIGNED_SUCCESSFULLY("Collection box has been assigned succesfully"),
    COLLECTION_BOX_UNREGISTERED_SUCCESSFULLY("Collection box has been unregistered succesfully"),
    MONEY_HAS_BEEN_TRANSFERED_SUCCESSFULLY("Money to collection box has been transfered succesfully");
    final String message;
    CollectionBoxFacadeMessages(String message) {
        this.message = message;
    }

}
