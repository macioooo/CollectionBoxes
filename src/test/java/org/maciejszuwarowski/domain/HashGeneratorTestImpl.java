package org.maciejszuwarowski.domain;

import org.maciejszuwarowski.domain.shared.HashGenerable;

public class HashGeneratorTestImpl implements HashGenerable {

    private final String hash;

    HashGeneratorTestImpl(String hash) {
        this.hash = hash;
    }
    public HashGeneratorTestImpl() {
        this.hash = "123";
    }
    @Override
    public String getHash() {
        return this.hash;
    }
}
