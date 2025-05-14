package org.maciejszuwarowski.domain.fundraisingevent;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class FundraisingEventRepositoryInMemoryImpl implements FundraisingEventRepository {

    Map<String, FundraisingEvent> inMemoryDb = new ConcurrentHashMap<>();

    @Override
    public FundraisingEvent save(FundraisingEvent fundraisingEvent) {
        return inMemoryDb.put(fundraisingEvent.getId(), fundraisingEvent);
    }

    @Override
    public List<FundraisingEvent> findAll() {
        return inMemoryDb.values().stream().toList();
    }

    @Override
    public Optional<FundraisingEvent> findById(String id) {
        return Optional.ofNullable(inMemoryDb.get(id));
    }


    @Override
    public <S extends FundraisingEvent> List<S> saveAll(Iterable<S> entities) {
        return null;
    }


    @Override
    public List<FundraisingEvent> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(FundraisingEvent entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends FundraisingEvent> entities) {

    }

    @Override
    public void deleteAll() {

    }


    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends FundraisingEvent> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends FundraisingEvent> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<FundraisingEvent> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public FundraisingEvent getOne(String s) {
        return null;
    }

    @Override
    public FundraisingEvent getById(String s) {
        return null;
    }

    @Override
    public FundraisingEvent getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends FundraisingEvent> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends FundraisingEvent> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends FundraisingEvent> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends FundraisingEvent> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends FundraisingEvent> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends FundraisingEvent> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends FundraisingEvent, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<FundraisingEvent> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<FundraisingEvent> findAll(Pageable pageable) {
        return null;
    }
}
