package org.maciejszuwarowski.domain.collectionbox.dto;

import lombok.Builder;

@Builder
public record CollectionBoxPublicInfoDto(String id, boolean isAssigned, boolean isEmpty, String message) {
}
