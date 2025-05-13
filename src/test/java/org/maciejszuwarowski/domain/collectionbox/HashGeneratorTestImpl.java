package org.maciejszuwarowski.domain.collectionbox;

public class HashGeneratorTestImpl implements HashGenerable{

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
