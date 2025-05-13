package org.maciejszuwarowski.domain.collectionbox;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCollectionBoxRepositoryImpl implements CollectionBoxRepository {

    Map<String, CollectionBox> database = new ConcurrentHashMap<>();

    @Override
    public CollectionBox save(CollectionBox collectionBox) {
        database.put(collectionBox.getId(), collectionBox);
        return collectionBox;
    }

    @Override
    public Optional<CollectionBox> findById(String id) {
        CollectionBox foundBox = database.get(id);
        return Optional.ofNullable(foundBox);
    }

    @Override
    public List<CollectionBox> findAll() {
        return database.values().stream().toList();
    }

    @Override
    public Optional<CollectionBox> deleteCollectionBoxById(String id) {
        return Optional.ofNullable(database.remove(id));
    }
}
