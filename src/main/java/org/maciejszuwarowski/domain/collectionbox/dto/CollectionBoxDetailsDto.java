package org.maciejszuwarowski.domain.collectionbox.dto;

import lombok.Builder;

@Builder
public record CollectionBoxDetailsDto(String id, boolean isAssigned, boolean isEmpty) {
}
