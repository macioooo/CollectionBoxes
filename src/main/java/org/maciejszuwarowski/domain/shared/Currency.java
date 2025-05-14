package org.maciejszuwarowski.domain.shared;

import java.util.Arrays;

public enum Currency {
    USD,
    EUR,
    PLN;
    public static Currency fromCode(String code) {
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown currency code: " + code));
    }
}
