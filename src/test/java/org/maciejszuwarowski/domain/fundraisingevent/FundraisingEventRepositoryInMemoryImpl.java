package org.maciejszuwarowski.domain.fundraisingevent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class FundraisingEventRepositoryInMemoryImpl implements FundraisingEventRepository{

    Map<String, FundraisingEvent> inMemoryDb = new ConcurrentHashMap<>();


    @Override
    public FundraisingEvent save(FundraisingEvent fundraisingEvent) {
        return inMemoryDb.put(fundraisingEvent.id(), fundraisingEvent);
    }

    @Override
    public List<FundraisingEvent> findAll() {
        return inMemoryDb.values().stream().toList();
    }

    @Override
    public Optional<FundraisingEvent> findById(String id) {
        return Optional.ofNullable(inMemoryDb.get(id));
    }
}
