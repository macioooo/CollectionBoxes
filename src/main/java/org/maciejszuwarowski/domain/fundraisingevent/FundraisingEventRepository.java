package org.maciejszuwarowski.domain.fundraisingevent;

import java.util.List;
import java.util.Optional;

interface FundraisingEventRepository {

    FundraisingEvent save(FundraisingEvent fundraisingEvent);

    List<FundraisingEvent> findAll();

    Optional<FundraisingEvent> findById(String id);
}
