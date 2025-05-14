package org.maciejszuwarowski.domain.collectionbox;

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

public class InMemoryCollectionBoxRepositoryImpl implements CollectionBoxRepository {

    Map<String, CollectionBox> database = new ConcurrentHashMap<>();

    @Override
    public CollectionBox save(CollectionBox collectionBox) {
        database.put(collectionBox.getId(), collectionBox);
        return collectionBox;
    }

    @Override
    public Optional<CollectionBox> findById(String id) {
        CollectionBox foundBox = database.get(id);
        return Optional.ofNullable(foundBox);
    }

    @Override
    public List<CollectionBox> findAll() {
        return database.values().stream().toList();
    }

    @Override
    public void deleteById(String id) {
        database.remove(id);
    }
    @Override
    public void flush() {

    }

    @Override
    public <S extends CollectionBox> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends CollectionBox> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<CollectionBox> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public CollectionBox getOne(String s) {
        return null;
    }

    @Override
    public CollectionBox getById(String s) {
        return null;
    }

    @Override
    public CollectionBox getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends CollectionBox> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends CollectionBox> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends CollectionBox> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends CollectionBox> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends CollectionBox> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends CollectionBox> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends CollectionBox, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }



    @Override
    public <S extends CollectionBox> List<S> saveAll(Iterable<S> entities) {
        return null;
    }


    @Override
    public boolean existsById(String s) {
        return false;
    }


    @Override
    public List<CollectionBox> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }



    @Override
    public void delete(CollectionBox entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends CollectionBox> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<CollectionBox> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<CollectionBox> findAll(Pageable pageable) {
        return null;
    }
}
