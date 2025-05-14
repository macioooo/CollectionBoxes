package org.maciejszuwarowski.domain.fundraisingevent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface FundraisingEventRepository extends JpaRepository {

    FundraisingEvent save(FundraisingEvent fundraisingEvent);

    List<FundraisingEvent> findAll();

    Optional<FundraisingEvent> findById(String id);
}
