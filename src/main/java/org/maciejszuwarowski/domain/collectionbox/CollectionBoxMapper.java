package org.maciejszuwarowski.domain.collectionbox;

import org.maciejszuwarowski.domain.collectionbox.dto.CollectionBoxPublicInfoDto;

class CollectionBoxMapper {
    static CollectionBoxPublicInfoDto mapFromCollectionBoxToPublicInfoDto(CollectionBox collectionBox) {
        return CollectionBoxPublicInfoDto.builder()
                .id(collectionBox.getId())
                .isEmpty(collectionBox.isEmpty())
                .isAssigned(collectionBox.isAssigned())
                .build();
    }
}
