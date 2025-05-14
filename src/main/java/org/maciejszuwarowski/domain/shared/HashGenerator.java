package org.maciejszuwarowski.domain.shared;

import org.maciejszuwarowski.domain.shared.HashGenerable;

import java.util.UUID;

public class HashGenerator implements HashGenerable {

    @Override
    public String getHash() {
        return UUID.randomUUID().toString();
    }
}
