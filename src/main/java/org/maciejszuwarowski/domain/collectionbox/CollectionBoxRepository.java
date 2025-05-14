package org.maciejszuwarowski.domain.collectionbox;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
interface CollectionBoxRepository extends JpaRepository {
    CollectionBox save(CollectionBox collectionBox);
    Optional<CollectionBox> findById(String id);

    List<CollectionBox> findAll();
    Optional<CollectionBox> deleteById(String id);

}
