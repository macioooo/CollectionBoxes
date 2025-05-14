package org.maciejszuwarowski.domain.shared;

import org.maciejszuwarowski.domain.shared.HashGenerable;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class HashGenerator implements HashGenerable {

    @Override
    public String getHash() {
        return UUID.randomUUID().toString();
    }
}
