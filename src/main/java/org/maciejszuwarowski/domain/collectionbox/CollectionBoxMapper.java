package org.maciejszuwarowski.domain.collectionbox;

import org.maciejszuwarowski.domain.collectionbox.dto.CollectionBoxDetailsDto;

class CollectionBoxMapper {
    static CollectionBoxDetailsDto mapFromCollectionBoxToDetailsDto(CollectionBox collectionBox) {
        return CollectionBoxDetailsDto.builder()
                .id(collectionBox.id())
                .isEmpty(collectionBox.isEmpty())
                .isAssigned(collectionBox.isAssigned())
                .build();
    }
}
