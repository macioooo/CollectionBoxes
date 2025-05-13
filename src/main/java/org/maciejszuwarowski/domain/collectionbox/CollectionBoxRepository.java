package org.maciejszuwarowski.domain.collectionbox;

import java.util.List;
import java.util.Optional;

interface CollectionBoxRepository {
    CollectionBox save(CollectionBox collectionBox);
    Optional<CollectionBox> findById(String id);

    List<CollectionBox> findAll();
    Optional<CollectionBox> deleteCollectionBoxById(String id);

}
