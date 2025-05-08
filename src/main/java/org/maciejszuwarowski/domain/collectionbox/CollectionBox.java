package org.maciejszuwarowski.domain.collectionbox;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;
@Builder
record CollectionBox(String id, String fundraisingEventId, Map<String, BigDecimal> contents) {

    boolean isEmpty() {
        return contents().isEmpty();
    }

    boolean isAssigned() {
        return fundraisingEventId != null && !fundraisingEventId.isEmpty();
    }
}
