package org.maciejszuwarowski.domain.shared;

import java.util.Arrays;
import java.util.Optional;

public enum Currency {
    USD,
    EUR,
    PLN;
    public static Optional<Currency> fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(code.trim()))
                .findFirst();
    }
}
